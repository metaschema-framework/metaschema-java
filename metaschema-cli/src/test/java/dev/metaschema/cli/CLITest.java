/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import dev.harrel.jsonschema.Dialects;
import dev.harrel.jsonschema.JsonNode;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import dev.harrel.jsonschema.providers.OrgJsonNode;
import dev.metaschema.cli.processor.ExitCode;
import dev.metaschema.cli.processor.ExitStatus;
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
        add(Arguments.of(new String[] { "list-allowed-values", "--help" }, ExitCode.OK,
            NO_EXCEPTION_CLASS));
        add(Arguments.of(
            new String[] { "list-allowed-values",
                "src/test/resources/content/schema-validation-module.xml"
            },
            ExitCode.OK, NO_EXCEPTION_CLASS));
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
        // Test --sarif-timing without -o produces an error
        add(Arguments.of(
            new String[] { "validate-content",
                "-m",
                "../databind/src/test/resources/metaschema/simple/metaschema.xml",
                "../databind/src/test/resources/metaschema/simple/example.json",
                "--as=json",
                "--sarif-timing"
            },
            ExitCode.INVALID_ARGUMENTS, NO_EXCEPTION_CLASS));
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

  @Test
  void testSarifTimingOutput() throws IOException {
    Path sarifOutput = Paths.get("target/test-sarif-timing.sarif");

    // Use a module with inline let statements and constraints, plus external
    // constraints with additional let statements to exercise all timing features
    String[] cliArgs = {
        "validate-content",
        "-m",
        "src/test/resources/content/timing-test-module.xml",
        "src/test/resources/content/timing-test-content.json",
        "--as=json",
        "-c", "src/test/resources/content/timing-test-constraints.xml",
        "-o", sarifOutput.toString(),
        "--sarif-timing",
        "--sarif-include-pass",
        "--show-stack-trace"
    };

    ExitStatus status = CLI.runCli(NULL_STREAM, cliArgs);
    evaluateResult(status, ExitCode.OK, cliArgs);

    // Verify SARIF output file was created and contains timing data
    assertTrue(Files.exists(sarifOutput), "SARIF output file should exist");

    String sarifContent = new String(Files.readAllBytes(sarifOutput), StandardCharsets.UTF_8);
    JSONObject sarif = new JSONObject(sarifContent);

    JSONArray runs = sarif.getJSONArray("runs");
    JSONObject run = runs.getJSONObject(0);

    // Verify invocations with timing are present
    assertTrue(run.has("invocations"), "Run should have invocations when --sarif-timing is enabled");
    JSONArray invocations = run.getJSONArray("invocations");
    assertEquals(1, invocations.length(), "Should have exactly one invocation");

    JSONObject invocation = invocations.getJSONObject(0);
    assertTrue(invocation.has("startTimeUtc"), "Invocation should have startTimeUtc");
    assertTrue(invocation.has("endTimeUtc"), "Invocation should have endTimeUtc");
    assertTrue(invocation.getBoolean("executionSuccessful"), "executionSuccessful should be true");
    assertTrue(invocation.has("toolExecutionNotifications"),
        "Invocation should have toolExecutionNotifications for phase timing");

    // Verify phase timing notifications exist
    JSONArray notifications = invocation.getJSONArray("toolExecutionNotifications");
    assertTrue(notifications.length() > 0, "Should have at least one phase timing notification");

    // Verify let-statement timing is captured (module has 2 inline lets +
    // constraints has 2 external lets)
    boolean foundLetTiming = false;
    for (int idx = 0; idx < notifications.length(); idx++) {
      JSONObject notification = notifications.getJSONObject(idx);
      String text = notification.getJSONObject("message").getString("text");
      if (text.startsWith("$") && text.contains(" := ")) {
        foundLetTiming = true;
        break;
      }
    }
    assertTrue(foundLetTiming, "Should have let-statement timing notifications");

    // Verify per-result timing on at least one result (using --sarif-include-pass
    // since test content passes all constraints)
    JSONArray results = run.getJSONArray("results");
    boolean foundPerResultTiming = false;
    for (int idx = 0; idx < results.length(); idx++) {
      JSONObject result = results.getJSONObject(idx);
      if (result.has("properties")) {
        JSONObject props = result.getJSONObject("properties");
        if (props.has("timing")) {
          foundPerResultTiming = true;
          JSONObject timing = props.getJSONObject("timing");
          assertTrue(timing.has("totalMs"), "Per-result timing should have totalMs");
          break;
        }
      }
    }
    assertTrue(foundPerResultTiming,
        "At least one result should have per-result timing when --sarif-timing is used");

    // Validate against official SARIF 2.1.0 schema
    Path sarifSchema = Paths.get("../databind-modules/modules/sarif/sarif-schema-2.1.0.json");

    try (Reader schemaReader = Files.newBufferedReader(sarifSchema, StandardCharsets.UTF_8)) {
      JsonNode schemaNode = new OrgJsonNode(new JSONObject(new JSONTokener(schemaReader)));
      JsonNode instanceNode = new OrgJsonNode(new JSONObject(sarifContent));

      Validator.Result result = new ValidatorFactory()
          .withJsonNodeFactory(new OrgJsonNode.Factory())
          .withDialect(new Dialects.Draft2020Dialect())
          .validate(schemaNode, instanceNode);
      StringJoiner sj = new StringJoiner("\n");
      for (dev.harrel.jsonschema.Error finding : result.getErrors()) {
        sj.add(String.format("[%s]%s %s for schema '%s'",
            finding.getInstanceLocation(),
            finding.getKeyword() == null ? "" : " " + finding.getKeyword() + ":",
            finding.getError(),
            finding.getSchemaLocation()));
      }
      assertTrue(result.isValid(),
          () -> "SARIF timing output failed schema validation. Errors:\n" + sj.toString());
    }
  }

  @Test
  void testSarifAlwaysOnInvocations() throws IOException {
    Path sarifOutput = Paths.get("target/test-sarif-always-on.sarif");

    // Run CLI with SARIF output but WITHOUT --sarif-timing
    String[] cliArgs = {
        "validate-content",
        "-m",
        "src/test/resources/content/timing-test-module.xml",
        "src/test/resources/content/timing-test-content.json",
        "--as=json",
        "-o", sarifOutput.toString(),
        "--show-stack-trace"
    };

    ExitStatus status = CLI.runCli(NULL_STREAM, cliArgs);
    evaluateResult(status, ExitCode.OK, cliArgs);

    assertTrue(Files.exists(sarifOutput), "SARIF output file should exist");

    String sarifContent = new String(Files.readAllBytes(sarifOutput), StandardCharsets.UTF_8);
    JSONObject sarif = new JSONObject(sarifContent);

    JSONObject run = sarif.getJSONArray("runs").getJSONObject(0);

    // Always-on: invocations should always be present
    assertTrue(run.has("invocations"), "Run should always have invocations (always-on timing)");
    JSONArray invocations = run.getJSONArray("invocations");
    assertEquals(1, invocations.length());

    JSONObject invocation = invocations.getJSONObject(0);
    assertTrue(invocation.has("startTimeUtc"), "Invocation should always have startTimeUtc");
    assertTrue(invocation.has("endTimeUtc"), "Invocation should always have endTimeUtc");
    assertTrue(invocation.getBoolean("executionSuccessful"));

    // Without --sarif-timing, should NOT have timing notifications
    assertFalse(invocation.has("toolExecutionNotifications"),
        "Invocation should not have timing notifications without --sarif-timing");
  }
}
