/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.MetapathEvaluationFeature;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.function.InvalidTypeFunctionException;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.IModelNodeItem;
import dev.metaschema.core.metapath.item.node.IModuleNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.testsupport.MockedModelTestSupport;
import dev.metaschema.core.testsupport.builder.IModuleBuilder;

/**
 * Targeted tests for the {@code METAPATH_ATOMIZE_NO_DATA_AS_EMPTY} evaluation
 * feature. These exercise the function argument conversion layer directly,
 * independent of any visitor, so a regression that narrows or broadens the
 * caught exception surface in
 * {@link dev.metaschema.core.metapath.function.impl.AbstractFunction#convertSequence}
 * is caught here.
 */
class AtomizeNoDataAsEmptyTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/atomize-no-data-test";

  private static IModule buildSingleFlagModule() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    return IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("no-data-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root")
            .rootName("root")
            .flags(List.of(mocking.flag().name("href"))))
        .toModule();
  }

  private static IAssemblyNodeItem rootAssemblyNode(IModule module) {
    IModuleNodeItem moduleNode = INodeItemFactory.instance().newModuleNodeItem(module);
    List<? extends IModelNodeItem<?, ?>> children = moduleNode.modelItems().collect(Collectors.toList());
    return (IAssemblyNodeItem) children.iterator().next();
  }

  private static DynamicContext newContext(IModule module) {
    DynamicContext context = new DynamicContext(
        StaticContext.builder()
            .defaultModelNamespace(module.getXmlNamespace())
            .build());
    context.disablePredicateEvaluation();
    return context;
  }

  @Test
  void featureDefaultsToDisabled() {
    DynamicContext context = new DynamicContext(StaticContext.instance());
    assertFalse(context.getConfiguration()
        .isFeatureEnabled(MetapathEvaluationFeature.METAPATH_ATOMIZE_NO_DATA_AS_EMPTY),
        "METAPATH_ATOMIZE_NO_DATA_AS_EMPTY must default to disabled on a fresh DynamicContext");
  }

  @Test
  void enableAndDisableRoundTripToggleFeatureState() {
    DynamicContext context = new DynamicContext(StaticContext.instance());
    context.enableAtomizeNoDataAsEmpty();
    assertTrue(context.getConfiguration()
        .isFeatureEnabled(MetapathEvaluationFeature.METAPATH_ATOMIZE_NO_DATA_AS_EMPTY),
        "enableAtomizeNoDataAsEmpty must set the feature to enabled");

    context.disableAtomizeNoDataAsEmpty();
    assertFalse(context.getConfiguration()
        .isFeatureEnabled(MetapathEvaluationFeature.METAPATH_ATOMIZE_NO_DATA_AS_EMPTY),
        "disableAtomizeNoDataAsEmpty must restore the disabled state");
  }

  @Test
  void withoutFeatureFlagFunctionArgAtomizationOnNoDataFlagThrows() {
    IModule module = buildSingleFlagModule();
    IAssemblyNodeItem rootNode = rootAssemblyNode(module);
    DynamicContext context = newContext(module);

    IMetapathExpression expr = IMetapathExpression.compile("upper-case(@href)", context.getStaticContext());
    assertThrows(InvalidTypeFunctionException.class,
        () -> expr.evaluate(rootNode, context),
        "With the feature disabled, atomizing a no-data flag inside a function argument must raise FOTY");
  }

  @Test
  void withFeatureFlagFunctionArgAtomizationOnNoDataFlagDoesNotThrow() {
    IModule module = buildSingleFlagModule();
    IAssemblyNodeItem rootNode = rootAssemblyNode(module);
    DynamicContext context = newContext(module);
    context.enableAtomizeNoDataAsEmpty();

    // The feature controls argument-atomization behavior only: function-arg
    // conversion yields an empty sequence for the no-data @href instead of
    // raising FOTY. The function body then handles the empty input according
    // to its own semantics (for upper-case, per the XPath spec, empty input
    // returns the zero-length string). The contract being exercised here is
    // that no FOTY error propagates out of evaluation.
    IMetapathExpression expr = IMetapathExpression.compile("upper-case(@href)", context.getStaticContext());
    assertDoesNotThrow(() -> expr.evaluate(rootNode, context),
        "With the feature enabled, atomizing a no-data flag inside a function argument"
            + " must not raise FOTY");
  }

  @Test
  void featureFlagDoesNotSwallowUnrelatedTypeErrors() {
    IModule module = buildSingleFlagModule();
    IAssemblyNodeItem rootNode = rootAssemblyNode(module);
    DynamicContext context = newContext(module);
    context.enableAtomizeNoDataAsEmpty();

    // Concatenating two strings with '+' requires numeric operands; the atomic
    // string items cannot be promoted, so a type error must still be raised
    // even when no-data-atomization tolerance is enabled.
    IMetapathExpression expr = IMetapathExpression.compile("'a' + 'b'", context.getStaticContext());
    assertThrows(InvalidTypeMetapathException.class,
        () -> expr.evaluate(rootNode, context),
        "The feature must only tolerate NODE_HAS_NO_TYPED_VALUE / DATA_ITEM_IS_FUNCTION,"
            + " not generic type errors");
  }

  @Test
  void featureFlagPropagatesIntoRecurseDepthRecursion() {
    IModule module = buildSingleFlagModule();
    IAssemblyNodeItem rootNode = rootAssemblyNode(module);
    // Deliberately create a context WITHOUT disabling predicate evaluation
    // because recurse-depth below relies on predicate evaluation to prove the
    // inner expression actually ran.
    DynamicContext context = new DynamicContext(
        StaticContext.builder()
            .defaultModelNamespace(module.getXmlNamespace())
            .build());
    context.enableAtomizeNoDataAsEmpty();

    // recurse-depth compiles its string argument and evaluates it against the
    // same DynamicContext. The inner expression filters the current context
    // item by a predicate containing a function call on a no-data flag. If
    // the feature did not propagate, the upper-case argument atomization
    // would raise FOTY during recursion and tear the whole expression down.
    // With the feature propagated, upper-case(@href) yields the empty string,
    // the predicate is false, and recurse-depth terminates.
    IMetapathExpression expr = IMetapathExpression.compile(
        "recurse-depth('.[upper-case(@href)]')",
        context.getStaticContext());
    assertDoesNotThrow(() -> expr.evaluate(rootNode, context),
        "The feature must propagate into recurse-depth's inner expression evaluation");
  }
}
