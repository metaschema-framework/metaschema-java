/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;

/**
 * Tests for choice validation during deserialization.
 * <p>
 * This class tests that choice elements are properly validated during
 * deserialization. When a choice contains mutually exclusive alternatives,
 * providing ONE alternative should satisfy the choice requirement - the other
 * alternatives should NOT be flagged as missing.
 * <p>
 * Related issues:
 * <ul>
 * <li>Issue #308 - Original choice regression</li>
 * <li>Issue #613 - Choice elements incorrectly require all alternatives</li>
 * </ul>
 */
class ChoiceValidationTest
    extends AbstractMetaschemaTest {

  private static final Path METASCHEMA_PATH
      = Paths.get("src/test/resources/metaschema/308-choice-regression/metaschema.xml");
  private static final Path EXAMPLE_JSON_PATH
      = Paths.get("src/test/resources/metaschema/308-choice-regression/example.json");
  private static final Path EXAMPLE_XML_PATH
      = Paths.get("src/test/resources/metaschema/308-choice-regression/example.xml");

  /**
   * Test that providing one choice alternative satisfies the choice requirement.
   * <p>
   * The test metaschema defines a choice between fields x and y, both with
   * min-occurs=1. The example provides only y, which should satisfy the choice.
   * With required field validation enabled, this should NOT throw an error.
   *
   * @param formatName
   *          the format to test ("JSON" or "XML")
   * @throws IOException
   *           if an I/O error occurs reading the test resources
   * @throws MetaschemaException
   *           if metaschema processing fails
   */
  @ParameterizedTest
  @ValueSource(strings = { "JSON", "XML" })
  void testChoiceAlternativeSatisfiesRequirement(String formatName) throws IOException, MetaschemaException {
    Format format = Format.valueOf(formatName);
    Path examplePath = format == Format.JSON ? EXAMPLE_JSON_PATH : EXAMPLE_XML_PATH;

    IBindingContext bindingContext = newBindingContext();
    bindingContext.loadMetaschema(ObjectUtils.notNull(METASCHEMA_PATH));

    IBoundLoader loader = bindingContext.newBoundLoader();
    // Enable required field validation - this is the key difference from the
    // regression test. With the fix, providing one choice alternative should
    // satisfy the choice requirement.
    loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    // This should NOT throw - providing y should satisfy the choice between x and y
    assertDoesNotThrow(() -> {
      Object result = loader.load(ObjectUtils.notNull(examplePath));
      assertNotNull(result, format + " example should parse successfully with required field validation");
    }, "Providing one choice alternative (y) should satisfy the choice requirement - "
        + "the other alternative (x) should NOT be flagged as missing");
  }

  /**
   * Test that choice validation works for constraint validation as well.
   *
   * @throws IOException
   *           if an I/O error occurs reading the test resources
   * @throws MetaschemaException
   *           if metaschema processing fails
   */
  @Test
  void testChoiceWithConstraintValidation() throws IOException, MetaschemaException {
    IBindingContext bindingContext = newBindingContext();
    bindingContext.loadMetaschema(ObjectUtils.notNull(METASCHEMA_PATH));

    IBoundLoader loader = bindingContext.newBoundLoader();
    loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
    loader.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    assertDoesNotThrow(() -> {
      Object result = loader.load(ObjectUtils.notNull(EXAMPLE_JSON_PATH));
      assertNotNull(result, "JSON example should parse successfully with both validations enabled");
    });
  }
}
