/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.TooManyStaticImports")
class DefaultConstraintValidatorTest {
  private static final String NS = URI.create("http://example.com/ns").toASCIIString();

  @RegisterExtension
  Mockery context = new JUnit5Mockery();

  @NonNull
  private static QName qname(@NonNull String name) {
    return new QName(NS, name);
  }

  @SuppressWarnings("null")
  @Test
  void testAllowedValuesAllowOther() {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory(context);

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("value"));

    IFlagDefinition flagDefinition = context.mock(IFlagDefinition.class);

    ISource source = context.mock(ISource.class);

    IAllowedValuesConstraint allowedValues = IAllowedValuesConstraint.builder()
        .source(source)
        .allowedValue(IAllowedValue.of(
            "other",
            MarkupLine.fromMarkdown("some documentation"),
            "1.0.0"))
        .allowsOther(true)
        .build();

    DynamicContext dynamicContext = new DynamicContext();

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(flag).getDefinition();
        will(returnValue(flagDefinition));
        allowing(flag).accept(with(any(DefaultConstraintValidator.Visitor.class)), with(dynamicContext));
        will(new FlagVisitorAction(dynamicContext));
        allowing(flag).toPath(with(any(IPathFormatter.class)));
        will(returnValue("flag/path"));

        allowing(flagDefinition).getLetExpressions();
        will(returnValue(CollectionUtil.emptyMap()));
        allowing(flagDefinition).getAllowedValuesConstraints();
        will(returnValue(CollectionUtil.singletonList(allowedValues)));
        allowing(flagDefinition).getExpectConstraints();
        will(returnValue(CollectionUtil.emptyList()));
        allowing(flagDefinition).getMatchesConstraints();
        will(returnValue(CollectionUtil.emptyList()));
        allowing(flagDefinition).getIndexHasKeyConstraints();
        will(returnValue(CollectionUtil.emptyList()));

        allowing(source).getStaticContext();
        will(returnValue(StaticContext.instance()));
      }
    });
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertTrue(handler.isPassing(), "doesn't pass");
  }

  @SuppressWarnings("null")
  @Test
  void testAllowedValuesMultipleAllowOther() {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory(context);

    IFlagNodeItem flag = itemFactory.flag(qname("value"), IStringItem.valueOf("value"));

    IFlagDefinition flagDefinition = context.mock(IFlagDefinition.class);

    ISource source = context.mock(ISource.class);

    IAllowedValuesConstraint allowedValues1 = IAllowedValuesConstraint.builder()
        .source(source)
        .allowedValue(IAllowedValue.of(
            "other",
            MarkupLine.fromMarkdown("some documentation"),
            "1.0.0"))
        .allowsOther(true)
        .build();
    IAllowedValuesConstraint allowedValues2 = IAllowedValuesConstraint.builder()
        .source(source)
        .allowedValue(IAllowedValue.of(
            "other2",
            MarkupLine.fromMarkdown("some documentation"), null))
        .allowsOther(true)
        .build();

    List<? extends IAllowedValuesConstraint> allowedValuesConstraints
        = List.of(allowedValues1, allowedValues2);

    DynamicContext dynamicContext = new DynamicContext();

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(flag).getDefinition();
        will(returnValue(flagDefinition));
        allowing(flag).accept(with(any(DefaultConstraintValidator.Visitor.class)), with(dynamicContext));
        will(new FlagVisitorAction(dynamicContext));
        allowing(flag).toPath(with(any(IPathFormatter.class)));
        will(returnValue("flag/path"));

        allowing(flagDefinition).getLetExpressions();
        will(returnValue(CollectionUtil.emptyMap()));
        allowing(flagDefinition).getAllowedValuesConstraints();
        will(returnValue(allowedValuesConstraints));
        allowing(flagDefinition).getExpectConstraints();
        will(returnValue(CollectionUtil.emptyList()));
        allowing(flagDefinition).getMatchesConstraints();
        will(returnValue(CollectionUtil.emptyList()));
        allowing(flagDefinition).getIndexHasKeyConstraints();
        will(returnValue(CollectionUtil.emptyList()));

        allowing(source).getStaticContext();
        will(returnValue(StaticContext.instance()));
      }
    });
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(flag, dynamicContext);
    validator.finalizeValidation(dynamicContext);

    assertTrue(handler.isPassing(), "doesn't pass");
  }

  @SuppressWarnings("null")
  @Test
  void testMultipleAllowedValuesConflictingAllowOther() {
    MockNodeItemFactory itemFactory = new MockNodeItemFactory(context);

    IFlagNodeItem flag1 = itemFactory.flag(qname("value"), IStringItem.valueOf("value"));
    IFlagNodeItem flag2 = itemFactory.flag(qname("other2"), IStringItem.valueOf("other2"));

    IFlagDefinition flagDefinition = context.mock(IFlagDefinition.class);

    ISource source = context.mock(ISource.class);

    IAllowedValuesConstraint allowedValues1 = IAllowedValuesConstraint.builder()
        .source(source)
        .allowedValue(IAllowedValue.of(
            "other",
            MarkupLine.fromMarkdown("some documentation"),
            null))
        .allowsOther(true)
        .build();
    IAllowedValuesConstraint allowedValues2 = IAllowedValuesConstraint.builder()
        .source(source)
        .allowedValue(IAllowedValue.of(
            "other2",
            MarkupLine.fromMarkdown("some documentation"),
            "1.0.0"))
        .allowsOther(false)
        .build();

    List<? extends IAllowedValuesConstraint> allowedValuesConstraints
        = List.of(allowedValues1, allowedValues2);

    DynamicContext dynamicContext = new DynamicContext();

    context.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(flag1).getDefinition();
        will(returnValue(flagDefinition));
        allowing(flag1).accept(with(any(DefaultConstraintValidator.Visitor.class)), with(dynamicContext));
        will(new FlagVisitorAction(dynamicContext));
        allowing(flag1).toPath(with(any(IPathFormatter.class)));
        will(returnValue("flag1/path"));

        allowing(flag2).getDefinition();
        will(returnValue(flagDefinition));
        allowing(flag2).accept(with(any(DefaultConstraintValidator.Visitor.class)), with(any(DynamicContext.class)));
        will(new FlagVisitorAction(dynamicContext));
        allowing(flag2).toPath(with(any(IPathFormatter.class)));
        will(returnValue("flag2/path"));

        allowing(flagDefinition).getLetExpressions();
        will(returnValue(CollectionUtil.emptyMap()));
        allowing(flagDefinition).getAllowedValuesConstraints();
        will(returnValue(allowedValuesConstraints));
        allowing(flagDefinition).getExpectConstraints();
        will(returnValue(CollectionUtil.emptyList()));
        allowing(flagDefinition).getMatchesConstraints();
        will(returnValue(CollectionUtil.emptyList()));
        allowing(flagDefinition).getIndexHasKeyConstraints();
        will(returnValue(CollectionUtil.emptyList()));

        allowing(source).getStaticContext();
        will(returnValue(StaticContext.instance()));
      }
    });
    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    DefaultConstraintValidator validator = new DefaultConstraintValidator(handler);
    validator.validate(flag1, dynamicContext);
    validator.validate(flag2, dynamicContext);
    validator.finalizeValidation(dynamicContext);
    assertAll(
        () -> assertFalse(handler.isPassing(), "must pass"),
        () -> assertThat("only 1 finding", handler.getFindings(), hasSize(1)),
        () -> assertThat("finding is for a flag node", handler.getFindings(), hasItem(hasProperty("node", is(flag1)))));
  }

  private static class FlagVisitorAction
      extends CustomAction {
    @NonNull
    private DynamicContext dynamicContext;

    public FlagVisitorAction(@NonNull DynamicContext dynamicContext) {
      super("return the flag");
      this.dynamicContext = dynamicContext;
    }

    @Override
    public Object invoke(Invocation invocation) {
      IFlagNodeItem thisFlag = (IFlagNodeItem) invocation.getInvokedObject();
      assert thisFlag != null;
      DefaultConstraintValidator.Visitor visitor = (DefaultConstraintValidator.Visitor) invocation.getParameter(0);
      return visitor.visitFlag(thisFlag, dynamicContext);
    }
  }
}
