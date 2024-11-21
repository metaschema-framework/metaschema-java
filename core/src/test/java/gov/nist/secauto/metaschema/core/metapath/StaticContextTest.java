/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class StaticContextTest {
  private static final String MODEL_NS = "http://example.com/ns/model";
  private static final StaticContext STATIC_CONTEXT = StaticContext.builder()
      .namespace("prefix", MetapathConstants.NS_METAPATH_FUNCTIONS)
      .namespace("model-prefix", MODEL_NS)
      .defaultFunctionNamespace(MetapathConstants.NS_METAPATH_FUNCTIONS)
      .defaultModelNamespace(MODEL_NS)
      .build();
  private static final IEnhancedQName COUNT_QNAME = IEnhancedQName.of(
      MetapathConstants.NS_METAPATH_FUNCTIONS,
      "count");
  private static final IEnhancedQName LOCAL_NAME_QNAME = IEnhancedQName.of("local-name");
  private static final IEnhancedQName MODEL_QNAME = IEnhancedQName.of(MODEL_NS, "local-name");

  static Stream<Arguments> provideFunctionValues() {
    return Stream.of(
        // prefixed lexical name
        Arguments.of("prefix:count", COUNT_QNAME),
        // qualified name
        Arguments.of(
            "Q{" + MetapathConstants.NS_METAPATH_FUNCTIONS + "}count",
            COUNT_QNAME),
        // defaulted namespace using just local name
        Arguments.of("count", COUNT_QNAME));
  }

  @ParameterizedTest
  @MethodSource("provideFunctionValues")
  void testFunctions(
      @NonNull String eqname,
      @NonNull IEnhancedQName expected) {
    assertDoesNotThrow(() -> {
      IFunction function = STATIC_CONTEXT.lookupFunction(eqname, 1);
      assertAll(
          () -> assertNotNull(function, "Expected function to be non-null"),
          () -> assertEquals(expected, function.getQName()));

    });
  }

  static Stream<Arguments> provideFlagValues() {
    return Stream.of(
        // prefixed lexical name
        Arguments.of(
            "model-prefix:local-name",
            MODEL_QNAME),
        // qualified name
        Arguments.of(
            "Q{" + MODEL_NS + "}local-name",
            MODEL_QNAME),
        // just local name
        Arguments.of(
            "local-name",
            LOCAL_NAME_QNAME));
  }

  @ParameterizedTest
  @MethodSource("provideFlagValues")
  void testFlagValue(
      @NonNull String eqname,
      @NonNull IEnhancedQName expected) {

    IEnhancedQName qname = STATIC_CONTEXT.parseFlagName(eqname);
    assertEquals(expected, qname);
  }

  static Stream<Arguments> provideModelValues() {
    return Stream.of(
        // prefixed lexical name
        Arguments.of(
            "model-prefix:local-name",
            MODEL_QNAME),
        // qualified name
        Arguments.of("Q{" + MODEL_NS + "}local-name",
            MODEL_QNAME),
        // defaulted namespace using just local name
        Arguments.of(
            "local-name",
            MODEL_QNAME));
  }

  @ParameterizedTest
  @MethodSource("provideModelValues")
  void testModelValues(
      @NonNull String eqname,
      @NonNull IEnhancedQName expected) {

    IEnhancedQName qname = STATIC_CONTEXT.parseModelName(eqname);
    assertEquals(expected, qname);
  }

  static Stream<Arguments> provideVariableValues() {
    return Stream.of(
        // prefixed lexical name
        Arguments.of(
            "model-prefix:local-name",
            MODEL_QNAME),
        // qualified name
        Arguments.of("Q{" + MODEL_NS + "}local-name",
            MODEL_QNAME),
        // just local name
        Arguments.of(
            "local-name",
            LOCAL_NAME_QNAME));
  }

  @ParameterizedTest
  @MethodSource("provideVariableValues")
  void testVariableValues(
      @NonNull String eqname,
      @NonNull IEnhancedQName expected) {

    IEnhancedQName qname = STATIC_CONTEXT.parseVariableName(eqname);
    assertEquals(expected, qname);
  }

  @Test
  void lookupNonExistantDataType() {
    StaticMetapathException ex = assertThrows(StaticMetapathException.class, () -> {
      StaticContext.instance().lookupAtomicType("xs:string");
    });

    assertEquals(StaticMetapathException.PREFIX_NOT_EXPANDABLE, ex.getCode());
  }

  @Test
  void lookupExistingDataType() {
    assertAll(
        () -> assertNotNull(StaticContext.instance().lookupAtomicType("string")),
        () -> assertNotNull(StaticContext.instance().lookupAtomicType("meta:string")));
  }
}
