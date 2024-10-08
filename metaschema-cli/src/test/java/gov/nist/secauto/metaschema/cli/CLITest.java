/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

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
    assert thrown != null;
    assertAll(() -> assertEquals(expectedCode, status.getExitCode(), "exit code mismatch"),
        () -> assertEquals(thrownClass, thrown.getClass(), "expected Throwable mismatch"));
  }

  private static Stream<Arguments> providesValues() {
    @SuppressWarnings("serial") List<Arguments> values = new LinkedList<>() {
      {
        add(Arguments.of(new String[] {}, ExitCode.INVALID_COMMAND,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "-h" }, ExitCode.OK, NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "generate-schema", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "validate", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(new String[] { "validate-content", "--help" }, ExitCode.OK,
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
            // fail due to constraint validation issue
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
}
