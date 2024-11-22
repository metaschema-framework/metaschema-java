/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static com.github.seregamorph.hamcrest.MoreMatchers.where;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression.ResultType;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.FailingErrorListener;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10Lexer;
import gov.nist.secauto.metaschema.core.metapath.cst.comparison.AbstractComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.comparison.GeneralComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.comparison.ValueComparison;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUuidItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.TooManyStaticImports")
class BuildCstVisitorTest {
  @NonNull
  private static final URI NS_URI = ObjectUtils.notNull(URI.create("http://example.com/ns"));
  @NonNull
  private static final String NS = ObjectUtils.notNull(NS_URI.toASCIIString());
  @NonNull
  private static final IEnhancedQName ROOT = EQNameFactory.of(NS, "root");
  @NonNull
  private static final IEnhancedQName FIELD1 = EQNameFactory.of(NS, "field1");
  @NonNull
  private static final IEnhancedQName FIELD2 = EQNameFactory.of(NS, "field2");
  @NonNull
  private static final IEnhancedQName UUID = EQNameFactory.of(NS, "uuid");
  @NonNull
  private static final IEnhancedQName FLAG = EQNameFactory.of("flag");

  @RegisterExtension
  Mockery context = new JUnit5Mockery();

  @NonNull
  private static IDocumentNodeItem newTestDocument() {
    MockNodeItemFactory factory = new MockNodeItemFactory();

    return factory.document(URI.create("http://example.com/content"), ROOT,
        List.of(
            factory.flag(UUID, IUuidItem.random())),
        List.of(
            factory.field(FIELD1, IStringItem.valueOf("field1")),
            factory.field(FIELD2, IStringItem.valueOf("field2"), // NOPMD
                List.of(factory.flag(FLAG, IStringItem.valueOf("field2-flag"))))));
  }

  @NonNull
  private static StaticContext newStaticContext() {
    return StaticContext.builder()
        .defaultModelNamespace(NS_URI)
        .build();
  }

  private static IExpression parseExpression(@NonNull String path) {

    Metapath10Lexer lexer = new Metapath10Lexer(CharStreams.fromString(path));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    Metapath10 parser = new Metapath10(tokens);
    parser.addErrorListener(new FailingErrorListener());

    ParseTree tree = parser.expr();
    // ParseTreePrinter cstPrinter = new ParseTreePrinter(System.out);
    // cstPrinter.print(tree, Arrays.asList(parser.getRuleNames()));

    return new BuildCSTVisitor(StaticContext.instance()).visit(tree);
  }

  @Test
  void testAbbreviatedParentAxis() {
    StaticContext staticContext = newStaticContext();
    // compile expression
    String path = "../field2";
    MetapathExpression expr = MetapathExpression.compile(path, staticContext);

    // select starting node
    IDocumentNodeItem document = newTestDocument();
    IFieldNodeItem field
        = MetapathExpression.compile("/root/field1", staticContext)
            .evaluateAs(document, ResultType.ITEM);
    assert field != null;

    // evaluate
    ISequence<IFieldNodeItem> result = expr.evaluate(field);
    assertThat(result.getValue(), contains(
        allOf(
            where(IFieldNodeItem::getQName, equalTo(FIELD2))))); // NOPMD
  }

  @Test
  void testParentAxisMatch() {
    StaticContext staticContext = newStaticContext();

    // select starting node
    IDocumentNodeItem document = newTestDocument();
    IFieldNodeItem field = MetapathExpression.compile("/root/field1", staticContext)
        .evaluateAs(document, ResultType.ITEM);
    assert field != null;

    // compile expression
    IItem result = MetapathExpression.compile("parent::root", staticContext)
        .evaluateAs(field, ResultType.ITEM);
    assert result != null;

    assertAll(
        () -> assertInstanceOf(IRootAssemblyNodeItem.class, result),
        () -> assertEquals(ROOT, ((IRootAssemblyNodeItem) result).getQName()));
  }

  @Test
  void testParentAxisNonMatch() {
    StaticContext staticContext = newStaticContext();

    // select starting node
    IDocumentNodeItem document = newTestDocument();
    IFieldNodeItem field = MetapathExpression.compile("/root/field1", staticContext)
        .evaluateAs(document, ResultType.ITEM);
    assert field != null;

    // compile expression
    String path = "parent::other";
    MetapathExpression expr = MetapathExpression.compile(path, staticContext);

    // evaluate
    ISequence<?> result = expr.evaluate(field);
    assertTrue(result.isEmpty());
  }

  @Test
  void testParentAxisDocument() {
    StaticContext staticContext = newStaticContext();

    // compile expression
    String path = "parent::other";
    MetapathExpression expr = MetapathExpression.compile(path, staticContext);

    // select starting node
    IDocumentNodeItem document = newTestDocument();

    // evaluate
    ISequence<?> result = expr.evaluate(document);
    assertTrue(result.isEmpty());
  }

  @Test
  void testAbbreviatedForwardAxisModelName() {
    StaticContext staticContext = newStaticContext();

    String path = "./root";
    MetapathExpression expr = MetapathExpression.compile(path, staticContext);

    // select starting node
    IDocumentNodeItem document = newTestDocument();

    // evaluate
    ISequence<IAssemblyNodeItem> result = expr.evaluate(document);
    assertThat(result.getValue(), contains(
        allOf(
            instanceOf(IRootAssemblyNodeItem.class),
            where(IAssemblyNodeItem::getQName, equalTo(ROOT))))); // NOPMD
  }

  @Test
  void testAbbreviatedForwardAxisFlagName() {
    StaticContext staticContext = newStaticContext();

    String path = "./@flag";
    MetapathExpression expr = MetapathExpression.compile(path, staticContext);

    // select starting node
    IDocumentNodeItem document = newTestDocument();
    IFieldNodeItem field = MetapathExpression.compile("/root/field2", staticContext)
        .evaluateAs(document, ResultType.ITEM);
    assert field != null;

    // evaluate
    ISequence<IFlagNodeItem> result = expr.evaluate(field);
    assertThat(result.getValue(), contains(
        allOf(
            instanceOf(IFlagNodeItem.class),
            where(IFlagNodeItem::getQName, equalTo(FLAG)))));
  }

  @Test
  void testForwardstepChild() {
    StaticContext staticContext = newStaticContext();

    String path = "child::*";
    MetapathExpression expr = MetapathExpression.compile(path, staticContext);

    // select starting node
    IDocumentNodeItem document = newTestDocument();
    IRootAssemblyNodeItem root = MetapathExpression.compile("/root", staticContext)
        .evaluateAs(document, ResultType.ITEM, new DynamicContext(staticContext));
    assert root != null;

    // evaluate
    ISequence<IFieldNodeItem> result = expr.evaluate(root);
    assertThat(result.getValue(), contains(
        allOf(
            instanceOf(IFieldNodeItem.class),
            where(IFieldNodeItem::getQName, equalTo(FIELD1))), // NOPMD
        allOf(
            instanceOf(IFieldNodeItem.class),
            where(IFieldNodeItem::getQName, equalTo(FIELD2))))); // NOPMD
  }

  static Stream<Arguments> testComparison() {
    return Stream.of(
        Arguments.of("A = B", GeneralComparison.class, ComparisonFunctions.Operator.EQ),
        Arguments.of("A != B", GeneralComparison.class, ComparisonFunctions.Operator.NE),
        Arguments.of("A < B", GeneralComparison.class, ComparisonFunctions.Operator.LT),
        Arguments.of("A <= B", GeneralComparison.class, ComparisonFunctions.Operator.LE),
        Arguments.of("A > B", GeneralComparison.class, ComparisonFunctions.Operator.GT),
        Arguments.of("A >= B", GeneralComparison.class, ComparisonFunctions.Operator.GE),
        Arguments.of("A eq B", ValueComparison.class, ComparisonFunctions.Operator.EQ),
        Arguments.of("A ne B", ValueComparison.class, ComparisonFunctions.Operator.NE),
        Arguments.of("A lt B", ValueComparison.class, ComparisonFunctions.Operator.LT),
        Arguments.of("A le B", ValueComparison.class, ComparisonFunctions.Operator.LE),
        Arguments.of("A gt B", ValueComparison.class, ComparisonFunctions.Operator.GT),
        Arguments.of("A ge B", ValueComparison.class, ComparisonFunctions.Operator.GE));
  }

  @ParameterizedTest
  @MethodSource
  void testComparison(
      @NonNull String metapath,
      @NonNull Class<?> expectedClass,
      @NonNull ComparisonFunctions.Operator operator) {
    IExpression ast = parseExpression(metapath);

    assertAll(
        () -> assertEquals(expectedClass, ast.getClass()),
        () -> assertEquals(operator, ((AbstractComparison) ast).getOperator()));
  }

  static Stream<Arguments> testAnd() {
    return Stream.of(
        Arguments.of("true() and false()", IBooleanItem.FALSE),
        Arguments.of("false() and false()", IBooleanItem.FALSE),
        Arguments.of("false() and true()", IBooleanItem.FALSE),
        Arguments.of("true() and true()", IBooleanItem.TRUE));
  }

  @ParameterizedTest
  @MethodSource
  void testAnd(@NonNull String metapath, @NonNull IBooleanItem expectedResult) {
    IExpression ast = parseExpression(metapath);

    IDocumentNodeItem document = newTestDocument();
    ISequence<?> result = ast.accept(new DynamicContext(), ISequence.of(document));
    IItem resultItem = result.getFirstItem(false);
    assertAll(
        () -> assertEquals(And.class, ast.getClass()),
        () -> assertNotNull(resultItem),
        () -> assertThat(resultItem, instanceOf(IBooleanItem.class)),
        () -> assertEquals(expectedResult, resultItem));
  }

  static Stream<Arguments> testIf() {
    return Stream.of(
        Arguments.of("if (true()) then true() else false()", IBooleanItem.TRUE),
        Arguments.of("if (false()) then true() else false()", IBooleanItem.FALSE),
        Arguments.of("if (()) then true() else false()", IBooleanItem.FALSE),
        Arguments.of("if (1) then true() else false()", IBooleanItem.TRUE),
        Arguments.of("if (0) then true() else false()", IBooleanItem.FALSE),
        Arguments.of("if (-1) then true() else false()", IBooleanItem.TRUE));
  }

  @ParameterizedTest
  @MethodSource
  void testIf(@NonNull String metapath, @NonNull IBooleanItem expectedResult) {
    IExpression ast = parseExpression(metapath);

    IDocumentNodeItem document = newTestDocument();
    ISequence<?> result = ast.accept(new DynamicContext(), ISequence.of(document));
    IItem resultItem = result.getFirstItem(false);
    assertAll(
        () -> assertEquals(If.class, ast.getClass()),
        () -> assertNotNull(resultItem),
        () -> assertThat(resultItem, instanceOf(IBooleanItem.class)),
        () -> assertEquals(expectedResult, resultItem));
  }

  static Stream<Arguments> testFor() {
    return Stream.of(
        Arguments.of(
            "for $num in (1,2,3) return $num+1",
            ISequence.of(
                integer(2),
                integer(3),
                integer(4))),
        Arguments.of(
            "for $num in (1,2,3), $bool in (true(),false()) return ($num,$bool)",
            ISequence.of(
                integer(1), bool(true), integer(1), bool(false),
                integer(2), bool(true), integer(2), bool(false),
                integer(3), bool(true), integer(3), bool(false))));
  }

  @ParameterizedTest
  @MethodSource
  void testFor(@NonNull String metapath, @NonNull ISequence<?> expectedResult) {
    IExpression ast = parseExpression(metapath);

    IDocumentNodeItem document = newTestDocument();
    ISequence<?> result = ast.accept(new DynamicContext(), ISequence.of(document));
    assertAll(
        () -> assertEquals(For.class, ast.getClass()),
        () -> assertNotNull(result),
        () -> assertThat(result, instanceOf(ISequence.class)),
        () -> assertEquals(expectedResult, result));
  }

  static Stream<Arguments> testSimpleMap() {
    return Stream.of(
        Arguments.of(
            "(1,2,1)!'*'",
            ISequence.of(string("*"), string("*"), string("*"))),
        Arguments.of(
            "(1,2,3) ! concat('id-',.) ! concat(.,'-suffix')",
            ISequence.of(
                string("id-1-suffix"),
                string("id-2-suffix"),
                string("id-3-suffix"))));
  }

  @ParameterizedTest
  @MethodSource
  void testSimpleMap(@NonNull String metapath, @NonNull ISequence<?> expectedResult) {
    IExpression ast = parseExpression(metapath);

    IDocumentNodeItem document = newTestDocument();
    ISequence<?> result = ast.accept(new DynamicContext(), ISequence.of(document));
    assertAll(
        () -> assertEquals(SimpleMap.class, ast.getClass()),
        () -> assertNotNull(result),
        () -> assertThat(result, instanceOf(ISequence.class)),
        () -> assertEquals(expectedResult, result));
  }
}
