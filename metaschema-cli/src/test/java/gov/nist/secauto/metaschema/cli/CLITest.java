/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.altindag.log.LogCaptor;

/**
 * Unit test for simple CLI.
 */
@Execution(value = ExecutionMode.SAME_THREAD, reason = "Log capturing needs to be single threaded")
public class CLITest {
  private static final ExitCode NO_EXCEPTION_CLASS = null;

  /**
   * A PrintStream that discards all output, used to suppress CLI console output
   * during tests.
   */
  @SuppressWarnings("resource")
  private static final PrintStream NULL_STREAM = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {
      // discard
    }

    @Override
    public void write(byte[] b, int off, int len) {
      // discard
    }
  });

  void evaluateResult(@NonNull ExitStatus status, @NonNull ExitCode expectedCode, @NonNull String[] args) {
    status.generateMessage(true);
    Throwable thrown = status.getThrowable();
    assertAll(
        () -> assertEquals(expectedCode, status.getExitCode(),
            () -> buildExitCodeMismatchMessage(status, expectedCode, thrown, args)),
        () -> assertNull(thrown,
            () -> buildUnexpectedThrowableMessage(thrown, args)));
  }

  void evaluateResult(@NonNull ExitStatus status, @NonNull ExitCode expectedCode,
      @NonNull Class<? extends Throwable> thrownClass, @NonNull String[] args) {
    Throwable thrown = status.getThrowable();
    assertAll(
        () -> assertEquals(expectedCode, status.getExitCode(),
            () -> buildExitCodeMismatchMessage(status, expectedCode, thrown, args)),
        () -> assertEquals(thrownClass, thrown == null ? null : thrown.getClass(),
            () -> buildThrowableMismatchMessage(thrownClass, thrown, args)));
  }

  private static String buildExitCodeMismatchMessage(@NonNull ExitStatus status, @NonNull ExitCode expectedCode,
      Throwable thrown, @NonNull String[] args) {
    StringBuilder sb = new StringBuilder();
    sb.append("exit code mismatch: expected <").append(expectedCode).append("> but was <")
        .append(status.getExitCode()).append(">");
    sb.append("\nCommand args: ").append(String.join(" ", args));
    if (status.getMessage() != null) {
      sb.append("\nStatus message: ").append(status.getMessage());
    }
    if (thrown != null) {
      sb.append("\nThrowable: ").append(thrown.getClass().getName()).append(": ").append(thrown.getMessage());
      sb.append("\nStack trace:\n").append(getStackTraceAsString(thrown));
    }
    return sb.toString();
  }

  private static String buildUnexpectedThrowableMessage(Throwable thrown, @NonNull String[] args) {
    if (thrown == null) {
      return "expected null Throwable";
    }
    StringBuilder sb = new StringBuilder();
    sb.append("expected null Throwable but got: ").append(thrown.getClass().getName())
        .append(": ").append(thrown.getMessage());
    sb.append("\nCommand args: ").append(String.join(" ", args));
    sb.append("\nStack trace:\n").append(getStackTraceAsString(thrown));
    return sb.toString();
  }

  private static String buildThrowableMismatchMessage(Class<? extends Throwable> expectedClass, Throwable thrown,
      @NonNull String[] args) {
    StringBuilder sb = new StringBuilder();
    sb.append("expected Throwable mismatch: expected <")
        .append(expectedClass == null ? "null" : expectedClass.getName())
        .append("> but was <")
        .append(thrown == null ? "null" : thrown.getClass().getName())
        .append(">");
    sb.append("\nCommand args: ").append(String.join(" ", args));
    if (thrown != null) {
      sb.append("\nMessage: ").append(thrown.getMessage());
      sb.append("\nStack trace:\n").append(getStackTraceAsString(thrown));
    }
    return sb.toString();
  }

  private static String getStackTraceAsString(Throwable throwable) {
    java.io.StringWriter sw = new java.io.StringWriter();
    throwable.printStackTrace(new java.io.PrintWriter(sw));
    return sw.toString();
  }

  private static Stream<Arguments> providesValues() {
    List<Arguments> values = new LinkedList<>() {
      {
        add(Arguments.of(new String[] {}, ExitCode.INVALID_COMMAND,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "-h" }, ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "generate-schema", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "generate-diagram", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "validate", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "validate-content", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "convert", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "metapath", "list-functions", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "metapath", "eval", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate",
                "../databind/src/test/resources/metaschema/fields_with_flags/metaschema.xml"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "generate-schema", "--overwrite", "--as",
                "JSON",
                "../databind/src/test/resources/metaschema/fields_with_flags/metaschema.xml",
                "target/schema-test.json" },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content", "--as=xml",
                "-m=../databind/src/test/resources/metaschema/bad_index-has-key/metaschema.xml",
                "../databind/src/test/resources/metaschema/bad_index-has-key/example.xml",
                "--show-stack-trace" },
            ExitCode.FAIL, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content", "--as=json",
                "-m=../databind/src/test/resources/metaschema/bad_index-has-key/metaschema.xml",
                "../databind/src/test/resources/metaschema/bad_index-has-key/example.json", "--show-stack-trace" },
            ExitCode.FAIL, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "--show-stack-trace" },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "generate-schema",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "--as", "xml",
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "generate-schema",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "--as", "json",
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "generate-diagram",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "../databind/src/test/resources/metaschema/simple/example.json",
                "--as=json"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "../databind/src/test/resources/metaschema/simple/example.xml",
                "--as=xml"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "https://bad.domain.example.net/example.xml",
                "--as=xml"
            },
            ExitCode.IO_ERROR, java.net.UnknownHostException.class));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "https://github.com/no-example.xml",
                "--as=xml"
            },
            ExitCode.IO_ERROR, java.io.FileNotFoundException.class));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "src/test/resources/content/schema-validation-module.xml",
                "src/test/resources/content/schema-validation-module-missing-required.xml",
                "--as=xml"
            },
            // fail due to schema validation issue
            ExitCode.FAIL, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "src/test/resources/content/schema-validation-module.xml",
                "src/test/resources/content/schema-validation-module-missing-required.xml",
                "--as=xml",
                "--disable-schema-validation"
            },
            // fail due to missing element during parsing
            ExitCode.FAIL, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "src/test/resources/content/schema-validation-module.xml",
                "src/test/resources/content/schema-validation-module-missing-required.xml",
                "--as=xml",
                "--disable-schema-validation",
                "--disable-constraint-validation"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "metapath", "list-functions" },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "convert",
                "-m",
                "../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml",
                "--to=yaml",
                "../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml",
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
        // Test markup-line datatype validation with YAML module
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "src/test/resources/content/test-markup-line-module.yaml",
                "src/test/resources/content/test-markup-line-content.json",
                "--as=json"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
      }
    };
    return values.stream();
  }

  @ParameterizedTest
  @MethodSource("providesValues")
  void testAllCommands(@NonNull String[] args, @NonNull ExitCode expectedExitCode,
      Class<? extends Throwable> expectedThrownClass) {
    String[] defaultArgs = { "--show-stack-trace" };
    String[] fullArgs = Stream.of(args, defaultArgs).flatMap(Stream::of)
        .toArray(String[]::new);
    if (expectedThrownClass == null) {
      evaluateResult(CLI.runCli(NULL_STREAM, fullArgs), expectedExitCode, fullArgs);
    } else {
      evaluateResult(CLI.runCli(NULL_STREAM, fullArgs), expectedExitCode, expectedThrownClass, fullArgs);
    }
  }

  @Test
  void testValidateContent() {
    try (LogCaptor captor = LogCaptor.forRoot()) {
      String[] cliArgs = { "validate-content",
          "-m",
          "src/test/resources/content/215-module.xml",
          "src/test/resources/content/215.xml",
          "--disable-schema-validation"
      };
      CLI.runCli(NULL_STREAM, cliArgs);
      assertThat(captor.getErrorLogs().toString())
          .contains("expect-default-non-zero: Expect constraint '. > 0' did not match the data",
              "expect-custom-non-zero: No default message, custom error message for expect-custom-non-zero constraint.",
              "matches-default-regex-letters-only: Value '1' did not match the pattern",
              "matches-custom-regex-letters-only: No default message, custom error message for" +
                  " matches-custom-regex-letters-only constraint.",
              "cardinality-default-two-minimum: The cardinality '1' is below the required minimum '2' for items" +
                  " matching",
              "index-items-default: Index 'index-items-default' has duplicate key for items",
              "index-items-custom: No default message, custom error message for index-item-custom.",
              "is-unique-default: Unique constraint violation at paths",
              "is-unique-custom: No default message, custom error message for is-unique-custom.",
              "index-has-key-default: Key reference [2] not found in index 'index-items-default' for item",
              "index-has-key-custom: No default message, custom error message for index-has-key-custom.");
    }
  }

  @Test
  void testValidateConstraints() {
    try (LogCaptor captor = LogCaptor.forRoot()) {
      String[] cliArgs = { "validate",
          "src/test/resources/content/constraint-example.xml",
          "-c",
          "src/test/resources/content/constraint-constraints.xml",
          "--disable-schema-validation",
      };
      CLI.runCli(NULL_STREAM, cliArgs);
      assertThat(captor.getErrorLogs().toString())
          .contains("This constraint SHOULD be violated if test passes.");
    }
  }
}
