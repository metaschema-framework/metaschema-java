/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

class StepTest
    extends ExpressionTestBase {

  @NonNull
  private static final IEnhancedQName ROOT = IEnhancedQName.of(NS, "root");

  IDocumentNodeItem getTestNodeItem() {
    MockNodeItemFactory factory = new MockNodeItemFactory();

    return factory.document(URI.create("http://example.com/content"), ROOT, List.of(), List.of(
        factory.assembly(IEnhancedQName.of(NS, "node-1"),
            List.of(
                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-v1")),
                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-v2")),
                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-v3"))),
            List.of(
                factory.assembly(IEnhancedQName.of(NS, "a"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-a-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-a-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-a-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-a-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-a-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-a-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-a-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-a-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-a-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-a-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-a-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-a-z-v3"))),
                            List.of()))),
                factory.assembly(IEnhancedQName.of(NS, "b"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-b-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-b-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-b-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-b-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-b-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-b-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-b-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-b-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-b-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-b-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-b-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-b-z-v3"))),
                            List.of()))),
                factory.assembly(IEnhancedQName.of(NS, "c"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-c-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-c-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-c-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-c-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-c-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-c-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-c-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-c-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-c-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-1-c-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-1-c-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-1-c-z-v3"))),
                            List.of()))))),
        factory.assembly(IEnhancedQName.of(NS, "node-2"),
            List.of(
                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-v1")),
                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-v2")),
                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-v3"))),
            List.of(
                factory.assembly(IEnhancedQName.of(NS, "a"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-a-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-a-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-a-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-a-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-a-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-a-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-a-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-a-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-a-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-a-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-a-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-a-z-v3"))),
                            List.of()))),
                factory.assembly(IEnhancedQName.of(NS, "b"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-b-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-b-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-b-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-b-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-b-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-b-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-b-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-b-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-b-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-b-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-b-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-b-z-v3"))),
                            List.of()))),
                factory.assembly(IEnhancedQName.of(NS, "c"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-c-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-c-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-c-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-c-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-c-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-c-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-c-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-c-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-c-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-2-c-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-2-c-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-2-c-z-v3"))),
                            List.of()))))),
        factory.assembly(IEnhancedQName.of(NS, "node-3"),
            List.of(
                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-v1")),
                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-v2")),
                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-v3"))),
            List.of(
                factory.assembly(IEnhancedQName.of(NS, "a"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-a-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-a-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-a-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-a-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-a-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-a-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-a-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-a-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-a-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-a-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-a-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-a-z-v3"))),
                            List.of()))),
                factory.assembly(IEnhancedQName.of(NS, "b"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-b-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-b-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-b-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-b-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-b-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-b-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-b-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-b-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-b-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-b-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-b-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-b-z-v3"))),
                            List.of()))),
                factory.assembly(IEnhancedQName.of(NS, "c"),
                    List.of(
                        factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-c-v1")),
                        factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-c-v2")),
                        factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-c-v3"))),
                    List.of(
                        factory.assembly(IEnhancedQName.of(NS, "x"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-c-x-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-c-x-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-c-x-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "y"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-c-y-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-c-y-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-c-y-v3"))),
                            List.of()),
                        factory.assembly(IEnhancedQName.of(NS, "z"),
                            List.of(
                                factory.flag(IEnhancedQName.of("flag-v1"), IStringItem.valueOf("flag-3-c-z-v1")),
                                factory.flag(IEnhancedQName.of("flag-v2"), IStringItem.valueOf("flag-3-c-z-v2")),
                                factory.flag(IEnhancedQName.of("flag-v3"), IStringItem.valueOf("flag-3-c-z-v3"))),
                            List.of())))))));
  }

  @Test
  void testSelfAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IModelNodeItem<?, ?> nodeB = IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
        .evaluateAs(getTestNodeItem(), IMetapathExpression.ResultType.ITEM, dynamicContext);

    INodeItem actual = IMetapathExpression.compile("self::*", dynamicContext.getStaticContext())
        .evaluateAs(nodeB, IMetapathExpression.ResultType.ITEM, dynamicContext);

    Assertions.assertThat(actual)
        .isEqualTo(nodeB)
        .isNotNull();
  }

  @Test
  void testParentAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IModelNodeItem<?, ?> nodeB = ObjectUtils.requireNonNull(
        IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
            .evaluateAs(getTestNodeItem(), IMetapathExpression.ResultType.ITEM, dynamicContext));

    INodeItem actual = IMetapathExpression.compile("parent::*", dynamicContext.getStaticContext())
        .evaluateAs(nodeB, IMetapathExpression.ResultType.ITEM, dynamicContext);

    Assertions.assertThat(actual)
        .isEqualTo(nodeB.getParentNodeItem())
        .isNotNull();
  }

  @Test
  void testFlagAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IModelNodeItem<?, ?> nodeB = IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
        .evaluateAs(getTestNodeItem(), IMetapathExpression.ResultType.ITEM, dynamicContext);

    ISequence<?> actual = IMetapathExpression.compile("flag::*", dynamicContext.getStaticContext())
        .evaluate(nodeB, dynamicContext);

    Assertions.assertThat(actual.getValue())
        .hasOnlyElementsOfType(IFlagNodeItem.class)
        .map(flag -> ObjectUtils.requireNonNull(flag).toAtomicItem().asString())
        .containsExactly("flag-2-b-v1", "flag-2-b-v2", "flag-2-b-v3");
  }

  @Test
  void testAncestorAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB = IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
        .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext);

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("ancestor::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    Assertions.assertThat(actual.getValue()).isEqualTo(List.of(
        document,
        document.getRootAssemblyNodeItem(),
        document.getRootAssemblyNodeItem()
            .getModelItemsByName(IEnhancedQName.of(NS, "node-2"))
            .iterator().next()));
  }

  @Test
  void testAncestorOrSelfAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB = IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
        .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext);

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("ancestor-or-self::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    Assertions.assertThat(actual.getValue()).isEqualTo(List.of(
        document,
        document.getRootAssemblyNodeItem(),
        document.getRootAssemblyNodeItem()
            .getModelItemsByName(IEnhancedQName.of(NS, "node-2"))
            .iterator().next(),
        nodeB));
  }

  @Test
  void testChildrenAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("child::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next()));
  }

  @Test
  void testDescendantAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> node2
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("descendant::*", dynamicContext.getStaticContext())
            .evaluate(node2, dynamicContext);

    IModelNodeItem<?, ?> nodeA
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "a"))).iterator().next();
    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "b"))).iterator().next();
    IModelNodeItem<?, ?> nodeC
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "c"))).iterator().next();

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(
            nodeA,
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next(),
            nodeB,
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next(),
            nodeC,
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next()));
  }

  @Test
  void testDescendantOrSelfAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> node2
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("descendant-or-self::*", dynamicContext.getStaticContext())
            .evaluate(node2, dynamicContext);

    IModelNodeItem<?, ?> nodeA
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "a"))).iterator().next();
    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "b"))).iterator().next();
    IModelNodeItem<?, ?> nodeC
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "c"))).iterator().next();

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(
            node2,
            nodeA,
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next(),
            nodeB,
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeB.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next(),
            nodeC,
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next()));
  }

  @Test
  void testFollowingSiblingAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("following-sibling::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    IModelNodeItem<?, ?> node2 = document.getRootAssemblyNodeItem()
        .getModelItemsByName(IEnhancedQName.of(NS, "node-2"))
        .iterator().next();

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "c"))).iterator().next()));
  }

  @Test
  void testPrecedingSiblingAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("preceding-sibling::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    IModelNodeItem<?, ?> node2 = document.getRootAssemblyNodeItem()
        .getModelItemsByName(IEnhancedQName.of(NS, "node-2"))
        .iterator().next();

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "a"))).iterator().next()));
  }

  @Test
  void testFollowingAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("following::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    IModelNodeItem<?, ?> node2 = document.getRootAssemblyNodeItem()
        .getModelItemsByName(IEnhancedQName.of(NS, "node-2"))
        .iterator().next();

    IModelNodeItem<?, ?> nodeC
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "c"))).iterator().next();

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(
            nodeC,
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeC.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next()));
  }

  @Test
  void testPrecedingAxis() {
    DynamicContext dynamicContext = newDynamicContext();

    IDocumentNodeItem document = getTestNodeItem();

    IModelNodeItem<?, ?> nodeB
        = ObjectUtils.requireNonNull(IMetapathExpression.compile("/root/node-2/b", dynamicContext.getStaticContext())
            .evaluateAs(document, IMetapathExpression.ResultType.ITEM, dynamicContext));

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("preceding::*", dynamicContext.getStaticContext())
            .evaluate(nodeB, dynamicContext);

    IModelNodeItem<?, ?> node2 = document.getRootAssemblyNodeItem()
        .getModelItemsByName(IEnhancedQName.of(NS, "node-2"))
        .iterator().next();

    IModelNodeItem<?, ?> nodeA
        = ObjectUtils.requireNonNull(node2.getModelItemsByName(IEnhancedQName.of(NS, "a"))).iterator().next();

    Assertions.assertThat(actual.getValue()).isEqualTo(
        List.of(
            nodeA,
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "x"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "y"))).iterator().next(),
            ObjectUtils.requireNonNull(nodeA.getModelItemsByName(IEnhancedQName.of(NS, "z"))).iterator().next()));
  }
}
