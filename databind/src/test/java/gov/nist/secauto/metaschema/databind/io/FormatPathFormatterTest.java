/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertSame;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;
import gov.nist.secauto.metaschema.core.metapath.format.PathFormatSelection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Format} path formatter methods.
 */
class FormatPathFormatterTest {

  @Nested
  @DisplayName("getPathFormatter()")
  class GetPathFormatterTests {

    @Test
    @DisplayName("XML format returns XPath formatter")
    void testXmlReturnsXPathFormatter() {
      assertSame(IPathFormatter.XPATH_PATH_FORMATTER, Format.XML.getPathFormatter());
    }

    @Test
    @DisplayName("JSON format returns JSON Pointer formatter")
    void testJsonReturnsJsonPointerFormatter() {
      assertSame(IPathFormatter.JSON_POINTER_PATH_FORMATTER, Format.JSON.getPathFormatter());
    }

    @Test
    @DisplayName("YAML format returns JSON Pointer formatter")
    void testYamlReturnsJsonPointerFormatter() {
      assertSame(IPathFormatter.JSON_POINTER_PATH_FORMATTER, Format.YAML.getPathFormatter());
    }
  }

  @Nested
  @DisplayName("resolvePathFormatter() with AUTO selection")
  class ResolveAutoTests {

    @Test
    @DisplayName("AUTO with XML format returns XPath formatter")
    void testAutoWithXmlReturnsXPath() {
      assertSame(
          IPathFormatter.XPATH_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.AUTO, Format.XML));
    }

    @Test
    @DisplayName("AUTO with JSON format returns JSON Pointer formatter")
    void testAutoWithJsonReturnsJsonPointer() {
      assertSame(
          IPathFormatter.JSON_POINTER_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.AUTO, Format.JSON));
    }

    @Test
    @DisplayName("AUTO with YAML format returns JSON Pointer formatter")
    void testAutoWithYamlReturnsJsonPointer() {
      assertSame(
          IPathFormatter.JSON_POINTER_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.AUTO, Format.YAML));
    }

    @Test
    @DisplayName("AUTO with null format returns Metapath formatter (fallback)")
    void testAutoWithNullReturnsFallback() {
      assertSame(
          IPathFormatter.METAPATH_PATH_FORMATER,
          Format.resolvePathFormatter(PathFormatSelection.AUTO, null));
    }
  }

  @Nested
  @DisplayName("resolvePathFormatter() with explicit selection")
  class ResolveExplicitTests {

    @Test
    @DisplayName("METAPATH selection returns Metapath formatter regardless of format")
    void testMetapathSelectionIgnoresFormat() {
      assertSame(
          IPathFormatter.METAPATH_PATH_FORMATER,
          Format.resolvePathFormatter(PathFormatSelection.METAPATH, Format.XML));
      assertSame(
          IPathFormatter.METAPATH_PATH_FORMATER,
          Format.resolvePathFormatter(PathFormatSelection.METAPATH, Format.JSON));
      assertSame(
          IPathFormatter.METAPATH_PATH_FORMATER,
          Format.resolvePathFormatter(PathFormatSelection.METAPATH, null));
    }

    @Test
    @DisplayName("XPATH selection returns XPath formatter regardless of format")
    void testXpathSelectionIgnoresFormat() {
      assertSame(
          IPathFormatter.XPATH_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.XPATH, Format.JSON));
      assertSame(
          IPathFormatter.XPATH_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.XPATH, Format.YAML));
      assertSame(
          IPathFormatter.XPATH_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.XPATH, null));
    }

    @Test
    @DisplayName("JSON_POINTER selection returns JSON Pointer formatter regardless of format")
    void testJsonPointerSelectionIgnoresFormat() {
      assertSame(
          IPathFormatter.JSON_POINTER_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.JSON_POINTER, Format.XML));
      assertSame(
          IPathFormatter.JSON_POINTER_PATH_FORMATTER,
          Format.resolvePathFormatter(PathFormatSelection.JSON_POINTER, null));
    }
  }
}
