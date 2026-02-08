/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import dev.metaschema.core.metapath.IErrorCode;

/**
 * Unit tests for {@link FormatDateTimeFunctionException}.
 */
class FormatDateTimeFunctionExceptionTest {

  @Test
  void testConstructWithInvalidPictureStringCodeAndMessage() {
    String message = "Invalid picture string component";
    FormatDateTimeFunctionException ex
        = new FormatDateTimeFunctionException(
            FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
            message);

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FOFD", errorCode.getPrefix());
    assertEquals(1340, errorCode.getCode());
    assertEquals("FOFD1340", errorCode.getCodeAsString());
    assertEquals("FOFD1340: " + message, ex.getMessage());
  }

  @Test
  void testConstructWithComponentNotAvailableCodeAndMessage() {
    String message = "Component not available for formatting";
    FormatDateTimeFunctionException ex
        = new FormatDateTimeFunctionException(
            FormatDateTimeFunctionException.COMPONENT_NOT_AVAILABLE,
            message);

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FOFD", errorCode.getPrefix());
    assertEquals(1350, errorCode.getCode());
    assertEquals("FOFD1350", errorCode.getCodeAsString());
    assertEquals("FOFD1350: " + message, ex.getMessage());
  }

  @Test
  void testErrorCodePrefixIsFofd() {
    FormatDateTimeFunctionException ex
        = new FormatDateTimeFunctionException(
            FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
            "test");

    assertEquals("FOFD", ex.getErrorCode().getPrefix());
  }

  @Test
  void testConstructWithCodeMessageAndCause() {
    String message = "Something went wrong";
    Throwable cause = new IllegalArgumentException("root cause");
    FormatDateTimeFunctionException ex
        = new FormatDateTimeFunctionException(
            FormatDateTimeFunctionException.INVALID_PICTURE_STRING,
            message,
            cause);

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FOFD", errorCode.getPrefix());
    assertEquals(1340, errorCode.getCode());
    assertEquals("FOFD1340: " + message, ex.getMessage());
    assertSame(cause, ex.getCause());
  }

  @Test
  void testConstructWithCodeAndCauseOnly() {
    Throwable cause = new IllegalStateException("underlying error");
    FormatDateTimeFunctionException ex
        = new FormatDateTimeFunctionException(
            FormatDateTimeFunctionException.COMPONENT_NOT_AVAILABLE,
            cause);

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FOFD", errorCode.getPrefix());
    assertEquals(1350, errorCode.getCode());
    assertSame(cause, ex.getCause());
    // getMessage() will include the error code prefix but no custom message text
    assertEquals("FOFD1350", ex.getMessage());
  }
}
