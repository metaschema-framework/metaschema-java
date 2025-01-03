/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUuidItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class InstanceOfTest
    extends ExpressionTestBase {

  @NonNull
  private static final IEnhancedQName ROOT = IEnhancedQName.of(NS, "root");
  @NonNull
  private static final IEnhancedQName FIELD1 = IEnhancedQName.of(NS, "field1");
  @NonNull
  private static final IEnhancedQName FIELD2 = IEnhancedQName.of(NS, "field2");
  @NonNull
  private static final IEnhancedQName UUID = IEnhancedQName.of(NS, "uuid");
  @NonNull
  private static final IEnhancedQName FLAG1 = IEnhancedQName.of("flag");
  @NonNull
  private static final IEnhancedQName FLAG2 = IEnhancedQName.of(NS, "flag");

  // FIXME: Use test vectors from
  // https://www.w3.org/TR/xpath-31/#id-sequencetype-syntax
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        // data type tests
        Arguments.of("'a' instance of string", true),
        Arguments.of("'a' instance of meta:string", true),
        Arguments.of("'a' instance of meta:uuid", false),
        Arguments.of("'a' instance of node()", false),
        Arguments.of("'a' instance of item()", true),
        Arguments.of("'a' instance of meta:string?", true),
        // sequence tests
        Arguments.of("() instance of string", false),
        Arguments.of("('a', 1) instance of meta:string+", false),
        Arguments.of("('a', 1) instance of item()+", true),
        Arguments.of("() instance of item()+", false),
        // array tests
        Arguments.of("[ 1, 2 ] instance of array(integer+)", true),
        Arguments.of("[ 1, 'not an integer' ] instance of array(integer+)", false),
        // map tests
        Arguments.of("$M instance of map(*)", true),
        Arguments.of("$M instance of map(meta:integer, meta:string)", true),
        Arguments.of("$M instance of map(meta:decimal, meta:any-atomic-type)", true),
        Arguments.of("not($M instance of map(meta:uuid, meta:string))", true),
        Arguments.of("not($M instance of map(meta:integer, meta:token))", true));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testMatch(@NonNull String test, boolean expected) {
    DynamicContext dynamicContext = new DynamicContext();
    dynamicContext.bindVariableValue(
        IEnhancedQName.of("M"),
        IMapItem.of(integer(0), string("no"), integer(1), string("yes")).toSequence());

    Boolean result = IMetapathExpression.compile(test)
        .evaluateAs(null, IMetapathExpression.ResultType.BOOLEAN, dynamicContext);

    assertEquals(
        expected,
        result,
        String.format("Expected `%s` to evaluate to '%s'",
            test,
            expected));
  }

  private static Stream<Arguments> provideValuesNode() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(". instance of document-node()", true),
        Arguments.of(". instance of document-node(assembly(*))", true),
        Arguments.of(". instance of document-node(assembly(x:root))", true),
        Arguments.of("/x:root instance of assembly()", true),
        Arguments.of("/x:root instance of assembly(x:root)", true),
        Arguments.of("/x:root instance of assembly(x:root,x:root)", true),
        Arguments.of("/x:root instance of assembly(x:other,x:root)", false),
        Arguments.of("/x:root instance of assembly(*,x:root)", true),
        Arguments.of("/x:root/x:field1 instance of field()", true),
        Arguments.of("/x:root/x:field1 instance of field(x:field1)", true),
        Arguments.of("/x:root/x:field2 instance of field(x:field2,x:field2)", true),
        Arguments.of("/x:root/x:field2 instance of field(x:field2,meta:integer)", true),
        Arguments.of("/x:root/x:field1 instance of field(x:other,x:uuid)", false),
        Arguments.of("/x:root/x:field1 instance of field(x:field1,meta:string)", true),
        Arguments.of("/x:root/x:field1 instance of field(x:field1,meta:integer)", false),
        Arguments.of("/x:root/@x:uuid instance of flag()", true),
        Arguments.of("/x:root/@x:uuid instance of flag(x:uuid)", true),
        Arguments.of("/x:root/@x:uuid instance of flag(x:uuid,x:uuid)", true),
        Arguments.of("/x:root/@x:uuid instance of flag(x:other,x:uuid)", false),
        Arguments.of("/x:root/@x:uuid instance of flag(x:uuid,meta:string)", true),
        Arguments.of("/x:root/@x:uuid instance of flag(x:uuid,meta:integer)", false),
        Arguments.of("/x:root/@x:uuid instance of flag(x:uuid,meta:uuid)", true));
  }

  @ParameterizedTest
  @MethodSource("provideValuesNode")
  void testMatchNodeItem(@NonNull String test, boolean expected) {
    MockNodeItemFactory factory = new MockNodeItemFactory();

    IDocumentNodeItem documentNodeItem = factory.document(URI.create("http://example.com/content"), ROOT,
        List.of(
            factory.flag(UUID, IUuidItem.random())),
        List.of(
            factory.field(FIELD1, IStringItem.valueOf("field1")),
            factory.field(FIELD2, IIntegerItem.valueOf(2),
                List.of(
                    factory.flag(FLAG1, IStringItem.valueOf("field2-flag")),
                    factory.flag(FLAG2, IUuidItem.random())))));

    StaticContext staticContext = StaticContext.builder()
        .namespace("x", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.bindVariableValue(
        IEnhancedQName.of("M"),
        IMapItem.of(integer(0), string("no"), integer(1), string("yes")).toSequence());

    Boolean result = IMetapathExpression.compile(test, staticContext)
        .evaluateAs(documentNodeItem, IMetapathExpression.ResultType.BOOLEAN, dynamicContext);

    assertEquals(
        expected,
        result,
        String.format("Expected `%s` to evaluate to '%s'",
            test,
            expected));
  }
}
