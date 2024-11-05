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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.altindag.log.LogCaptor;

/**
 * Unit test for simple CLI.
 */
public class CLITest {
  private static final ExitCode NO_EXCEPTION_CLASS = null;

  void evaluateResult(@NonNull ExitStatus status, @NonNull ExitCode expectedCode) {
    status.generateMessage(true);
    assertAll(() -> assertEquals(expectedCode, status.getExitCode(), "exit code mismatch"),
        () -> assertNull(status.getThrowable(), "expected null Throwable"));
  }

  void evaluateResult(@NonNull ExitStatus status, @NonNull ExitCode expectedCode,
      @NonNull Class<? extends Throwable> thrownClass) {
    status.generateMessage(true);
    Throwable thrown = status.getThrowable();
    assertAll(
        () -> assertEquals(expectedCode, status.getExitCode(), "exit code mismatch"),
        () -> assertEquals(thrownClass, thrown == null ? null : thrown.getClass(), "expected Throwable mismatch"));
  }

  private static Stream<Arguments> providesValues() {
    @SuppressWarnings("serial") List<Arguments> values = new LinkedList<>() {
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
      evaluateResult(CLI.runCli(fullArgs), expectedExitCode);
    } else {
      evaluateResult(CLI.runCli(fullArgs), expectedExitCode, expectedThrownClass);
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
      CLI.runCli(cliArgs);
      assertThat(captor.getErrorLogs().toString())
          .contains("expect-default-non-zero: Expect constraint '. > 0' did not match the data",
              "expect-custom-non-zero: No default message, custom error message for expect-custom-non-zero constraint.",
              "matches-default-regex-letters-only: Value '1' did not match the pattern",
              "matches-custom-regex-letters-only: No default message, custom error message for matches-custom-regex-letters-only constraint.",
              "cardinality-default-two-minimum: The cardinality '1' is below the required minimum '2' for items matching",
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
      CLI.runCli(cliArgs);
      assertThat(captor.getErrorLogs().toString())
          .contains("This constraint SHOULD be violated if test passes.");
    }
  }
}
