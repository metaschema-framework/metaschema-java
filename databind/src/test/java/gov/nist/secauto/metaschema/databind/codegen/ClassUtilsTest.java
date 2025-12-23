/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Unit tests for {@link ClassUtils}.
 */
class ClassUtilsTest {

  /**
   * Test that toPropertyName converts various inputs to proper Java property
   * names.
   *
   * @param input
   *          the input name
   * @param expected
   *          the expected property name
   */
  @ParameterizedTest
  @CsvSource({
      // Basic hyphen-separated
      "my-property, MyProperty",
      "some-long-name, SomeLongName",
      // Underscore-separated
      "my_property, MyProperty",
      "some_long_name, SomeLongName",
      // Already camelCase - preserves boundaries
      "myProperty, MyProperty",
      "MyProperty, MyProperty",
      // Single word
      "property, Property",
      "PROPERTY, Property",
      // Reserved word mapping
      "class, Clazz",
      "Class, Clazz"
  })
  void testToPropertyName(String input, String expected) {
    assertEquals(expected, ClassUtils.toPropertyName(input));
  }

  /**
   * Test that toVariableName converts various inputs to lowerCamelCase.
   *
   * @param input
   *          the input name
   * @param expected
   *          the expected variable name
   */
  @ParameterizedTest
  @CsvSource({
      // Basic hyphen-separated
      "my-property, myProperty",
      "some-long-name, someLongName",
      // Underscore-separated
      "my_property, myProperty",
      "some_long_name, someLongName",
      // Already camelCase - preserves boundaries
      "myProperty, myProperty",
      // Single word
      "property, property",
      "Property, property",
      "PROPERTY, property"
  })
  void testToVariableName(String input, String expected) {
    assertEquals(expected, ClassUtils.toVariableName(input));
  }

  /**
   * Test that toClassName converts various inputs to UpperCamelCase.
   *
   * @param input
   *          the input name
   * @param expected
   *          the expected class name
   */
  @ParameterizedTest
  @CsvSource({
      // Basic hyphen-separated
      "my-class, MyClass",
      "some-long-name, SomeLongName",
      // Underscore-separated
      "my_class, MyClass",
      // Single word
      "myclass, Myclass",
      // Already camelCase - preserves boundaries
      "MyClass, MyClass"
  })
  void testToClassName(String input, String expected) {
    assertEquals(expected, ClassUtils.toClassName(input));
  }

  /**
   * Test that toPackageName converts namespace URIs to valid Java package names.
   *
   * @param input
   *          the input namespace URI
   * @param expected
   *          the expected package name
   */
  @ParameterizedTest
  @CsvSource({
      // Standard namespace URIs
      "https://example.com/ns/test, com.example.ns.test",
      "http://www.example.org/schema, org.example.www.schema",
      // Namespace with version numbers
      "https://example.com/ns/1.0, com.example.ns._1_0",
      // Real-world example
      "https://csrc.nist.gov/ns/metaschema-binding/1.0, gov.nist.csrc.ns.metaschema_binding._1_0",
      // Edge case: separators-only path component produces underscores
      "https://example.com/---/test, com.example.___.test"
  })
  void testToPackageName(String input, String expected) {
    assertEquals(expected, ClassUtils.toPackageName(input));
  }

  /**
   * Test edge case: empty or whitespace inputs for property name.
   */
  @Test
  void testToPropertyNameWithEmptyInput() {
    // Empty string should return the input unchanged
    assertEquals("", ClassUtils.toPropertyName(""));
  }

  /**
   * Test edge case: names starting with digits. The implementation preserves
   * numeric prefixes as-is for property names.
   */
  @ParameterizedTest
  @CsvSource({
      "123-test, 123Test",
      "1st-item, 1stItem"
  })
  void testToPropertyNameWithDigitPrefix(String input, String expected) {
    assertEquals(expected, ClassUtils.toPropertyName(input));
  }

  /**
   * Test edge case: class names starting with digits. Note that Java class names
   * cannot start with digits, but the current implementation preserves them
   * as-is. Callers should validate or prefix the result if used as an actual
   * class name.
   */
  @ParameterizedTest
  @CsvSource({
      "123-test, 123Test",
      "1st-item, 1stItem"
  })
  void testToClassNameWithDigitPrefix(String input, String expected) {
    assertEquals(expected, ClassUtils.toClassName(input));
  }

  /**
   * Test that reserved Java keywords are handled in package names.
   */
  @Test
  void testToPackageNameWithReservedWord() {
    // Package name with 'class' component should be prefixed
    String result = ClassUtils.toPackageName("https://example.com/class/test");
    assertEquals("com.example._class.test", result);
  }

  /**
   * Test that Java contextual keywords (module, var, yield, record, sealed,
   * permits) are handled in package names for forward compatibility.
   */
  @ParameterizedTest
  @CsvSource({
      "https://example.com/module/test, com.example._module.test",
      "https://example.com/var/test, com.example._var.test",
      "https://example.com/yield/test, com.example._yield.test",
      "https://example.com/record/test, com.example._record.test",
      "https://example.com/sealed/test, com.example._sealed.test",
      "https://example.com/permits/test, com.example._permits.test"
  })
  void testToPackageNameWithContextualKeyword(String input, String expected) {
    assertEquals(expected, ClassUtils.toPackageName(input));
  }
}
