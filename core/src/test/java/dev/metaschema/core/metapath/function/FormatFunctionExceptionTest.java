/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import dev.metaschema.core.metapath.IErrorCode;

class FormatFunctionExceptionTest {

  @Test
  void testConstructionWithCodeAndMessage() {
    String message = "invalid format token '~'";
    FormatFunctionException ex
        = new FormatFunctionException(FormatFunctionException.INVALID_FORMAT_TOKEN, message);

    IErrorCode errorCode = ex.getErrorCode();
    assertNotNull(errorCode);
    assertEquals("FODF", errorCode.getPrefix());
    assertEquals(1310, errorCode.getCode());
    assertEquals("FODF1310: " + message, ex.getMessage());
    assertNull(ex.getCause());
  }

  @Test
  void testErrorCodePrefix() {
    FormatFunctionException ex
        = new FormatFunctionException(FormatFunctionException.INVALID_FORMAT_TOKEN, "test");

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FODF", errorCode.getPrefix());
    assertEquals(1310, errorCode.getCode());
    assertEquals("FODF1310", errorCode.getCodeAsString());
  }

  @Test
  void testConstructionWithCodeMessageAndCause() {
    String message = "invalid format token";
    Throwable cause = new IllegalArgumentException("bad token");
    FormatFunctionException ex
        = new FormatFunctionException(FormatFunctionException.INVALID_FORMAT_TOKEN, message, cause);

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FODF", errorCode.getPrefix());
    assertEquals(1310, errorCode.getCode());
    assertEquals("FODF1310: " + message, ex.getMessage());
    assertSame(cause, ex.getCause());
  }

  @Test
  void testConstructionWithCodeAndCause() {
    Throwable cause = new IllegalArgumentException("bad token");
    FormatFunctionException ex
        = new FormatFunctionException(FormatFunctionException.INVALID_FORMAT_TOKEN, cause);

    IErrorCode errorCode = ex.getErrorCode();
    assertEquals("FODF", errorCode.getPrefix());
    assertEquals(1310, errorCode.getCode());
    assertSame(cause, ex.getCause());
  }
}
