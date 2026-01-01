/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import gov.nist.secauto.metaschema.core.metapath.format.IPathFormatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LoggingValidationHandler} factory methods and configuration.
 * <p>
 * These tests verify the handler creation API that must be preserved when
 * migrating from Jansi to JLine. The actual ANSI output behavior is tested
 * through integration tests in {@link gov.nist.secauto.metaschema.cli.CLITest}.
 */
class LoggingValidationHandlerTest {

  @Nested
  @DisplayName("Factory Method Tests")
  class FactoryMethodTests {

    @Test
    @DisplayName("instance() returns non-null singleton")
    void testInstanceReturnsNonNull() {
      LoggingValidationHandler handler = LoggingValidationHandler.instance();
      assertNotNull(handler);
    }

    @Test
    @DisplayName("instance() returns same instance on repeated calls")
    void testInstanceReturnsSameInstance() {
      LoggingValidationHandler first = LoggingValidationHandler.instance();
      LoggingValidationHandler second = LoggingValidationHandler.instance();
      assertSame(first, second, "instance() should return the same singleton");
    }

    @Test
    @DisplayName("instance(false) returns handler that does not log exceptions")
    void testInstanceWithoutExceptionLogging() {
      LoggingValidationHandler handler = LoggingValidationHandler.instance(false);
      assertNotNull(handler);
      assertThat(handler.isLogExceptions()).isFalse();
    }

    @Test
    @DisplayName("instance(true) returns handler that logs exceptions")
    void testInstanceWithExceptionLogging() {
      LoggingValidationHandler handler = LoggingValidationHandler.instance(true);
      assertNotNull(handler);
      assertThat(handler.isLogExceptions()).isTrue();
    }

    @Test
    @DisplayName("instance(true) and instance(false) return different singletons")
    void testInstanceWithDifferentExceptionSettings() {
      LoggingValidationHandler withExceptions = LoggingValidationHandler.instance(true);
      LoggingValidationHandler withoutExceptions = LoggingValidationHandler.instance(false);

      assertThat(withExceptions.isLogExceptions()).isTrue();
      assertThat(withoutExceptions.isLogExceptions()).isFalse();
    }

    @Test
    @DisplayName("withPathFormatter creates handler with custom formatter")
    void testWithPathFormatter() {
      LoggingValidationHandler handler = LoggingValidationHandler.withPathFormatter(
          IPathFormatter.METAPATH_PATH_FORMATER);
      assertNotNull(handler);
      // New instance should not log exceptions by default
      assertThat(handler.isLogExceptions()).isFalse();
    }

    @Test
    @DisplayName("withSettings creates handler with custom settings - exceptions enabled")
    void testWithSettingsExceptionsEnabled() {
      LoggingValidationHandler handler = LoggingValidationHandler.withSettings(
          true,
          IPathFormatter.METAPATH_PATH_FORMATER);
      assertNotNull(handler);
      assertThat(handler.isLogExceptions()).isTrue();
    }

    @Test
    @DisplayName("withSettings creates handler with custom settings - exceptions disabled")
    void testWithSettingsExceptionsDisabled() {
      LoggingValidationHandler handler = LoggingValidationHandler.withSettings(
          false,
          IPathFormatter.METAPATH_PATH_FORMATER);
      assertNotNull(handler);
      assertThat(handler.isLogExceptions()).isFalse();
    }
  }

  @Nested
  @DisplayName("Configuration Tests")
  class ConfigurationTests {

    @Test
    @DisplayName("default instance does not log exceptions")
    void testDefaultInstanceDoesNotLogExceptions() {
      LoggingValidationHandler handler = LoggingValidationHandler.instance();
      assertThat(handler.isLogExceptions()).isFalse();
    }

    @Test
    @DisplayName("handler created with withPathFormatter uses default exception logging")
    void testWithPathFormatterUsesDefaultExceptionLogging() {
      LoggingValidationHandler handler = LoggingValidationHandler.withPathFormatter(
          IPathFormatter.METAPATH_PATH_FORMATER);
      // Default is to not log exceptions
      assertThat(handler.isLogExceptions()).isFalse();
    }
  }
}
