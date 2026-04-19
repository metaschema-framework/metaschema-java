/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.modules.sarif;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import dev.harrel.jsonschema.Dialects;
import dev.harrel.jsonschema.JsonNode;
import dev.harrel.jsonschema.Validator;
import dev.harrel.jsonschema.ValidatorFactory;
import dev.harrel.jsonschema.providers.OrgJsonNode;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.IResourceLocation;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.ConstraintValidationFinding;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.ILet;
import dev.metaschema.core.model.constraint.TimingCollector;
import dev.metaschema.core.model.constraint.ValidationPhase;
import dev.metaschema.core.model.validation.IValidationFinding;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.IVersionInfo;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;

class SarifValidationHandlerTimingTest {
  @RegisterExtension
  public final JUnit5Mockery mockery = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  @Test
  void testTimingEnrichmentInSarifOutput() throws IOException {
    IVersionInfo versionInfo = mockery.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(mockery.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(mockery.mock(INodeItem.class));
    IResourceLocation location = mockery.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    mockery.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(versionInfo).getName();
        will(returnValue("test"));
        allowing(versionInfo).getVersion();
        will(returnValue("0.0.0"));

        allowing(constraintA).getLevel();
        will(returnValue(IConstraint.Level.ERROR));
        allowing(constraintA).getId();
        will(returnValue(null));
        allowing(constraintA).getFormalName();
        will(returnValue("a formal name"));
        allowing(constraintA).getDescription();
        will(returnValue(MarkupLine.fromMarkdown("a description")));
        allowing(constraintA).getProperties();
        will(returnValue(Map.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getInternalIdentifier();
        will(returnValue("test-constraint-id"));

        allowing(node).getLocation();
        will(returnValue(location));
        allowing(node).getBaseUri();
        will(returnValue(sourceFile.toUri()));
        allowing(node).getMetapath();
        will(returnValue("/node/child"));

        allowing(location).getLine();
        will(returnValue(10));
        allowing(location).getColumn();
        will(returnValue(0));
        allowing(location).getByteOffset();
        will(returnValue(-1L));
        allowing(location).getCharOffset();
        will(returnValue(-1L));
      }
    });

    // Create and populate TimingCollector with realistic events
    TimingCollector collector = new TimingCollector();
    URI docUri = ObjectUtils.notNull(sourceFile.toUri());

    collector.beforeValidation(docUri);
    collector.beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
    collector.beforeConstraintEvaluation(constraintA, node);
    busyWait();
    collector.afterConstraintEvaluation(constraintA, node);
    collector.afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);
    collector.afterValidation(docUri);

    // Create handler with timing collector
    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);
    handler.setTimingCollector(collector);

    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());

    String sarifOutput = handler.writeToString(IBindingContext.newInstance());
    JSONObject sarif = new JSONObject(sarifOutput);

    // Verify invocations exist in the run
    JSONArray runs = sarif.getJSONArray("runs");
    JSONObject run = runs.getJSONObject(0);
    assertTrue(run.has("invocations"), "Run should have invocations when timing is enabled");

    JSONArray invocations = run.getJSONArray("invocations");
    assertEquals(1, invocations.length(), "Should have exactly one invocation");

    JSONObject invocation = invocations.getJSONObject(0);
    assertTrue(invocation.has("startTimeUtc"), "Invocation should have startTimeUtc");
    assertTrue(invocation.has("endTimeUtc"), "Invocation should have endTimeUtc");
    assertTrue(invocation.getBoolean("executionSuccessful"), "executionSuccessful should be true");

    // Verify phase timing as notifications
    assertTrue(invocation.has("toolExecutionNotifications"),
        "Invocation should have toolExecutionNotifications for phase timing");
    JSONArray notifications = invocation.getJSONArray("toolExecutionNotifications");
    assertTrue(notifications.length() > 0, "Should have at least one phase timing notification");

    boolean foundConstraintPhase = false;
    for (int i = 0; i < notifications.length(); i++) {
      JSONObject notification = notifications.getJSONObject(i);
      JSONObject message = notification.getJSONObject("message");
      if (message.getString("text").contains("CONSTRAINT_VALIDATION")) {
        foundConstraintPhase = true;
        assertTrue(notification.has("properties"), "Phase notification should have properties");
        JSONObject phaseProps = notification.getJSONObject("properties");
        assertTrue(phaseProps.has("timing"), "Phase properties should have timing");
        JSONObject phaseTiming = phaseProps.getJSONObject("timing");
        assertTrue(phaseTiming.has("totalMs"), "Phase timing should have totalMs");
        assertTrue(phaseTiming.has("count"), "Phase timing should have count");
        break;
      }
    }
    assertTrue(foundConstraintPhase, "Should have a notification for CONSTRAINT_VALIDATION phase");

    // Verify constraint timing in rule properties
    JSONObject tool = run.getJSONObject("tool");
    JSONObject driver = tool.getJSONObject("driver");
    JSONArray rules = driver.getJSONArray("rules");
    assertTrue(rules.length() > 0, "Should have at least one rule");

    boolean foundTimingOnRule = false;
    for (int i = 0; i < rules.length(); i++) {
      JSONObject rule = rules.getJSONObject(i);
      if (rule.has("properties")) {
        JSONObject props = rule.getJSONObject("properties");
        if (props.has("timing")) {
          foundTimingOnRule = true;
          JSONObject timing = props.getJSONObject("timing");
          assertTrue(timing.has("totalMs"), "Rule timing should have totalMs");
          assertTrue(timing.has("count"), "Rule timing should have count");
          assertEquals(1, timing.getInt("count"), "Constraint was evaluated once");
          break;
        }
      }
    }
    assertTrue(foundTimingOnRule, "Should have timing data on constraint rule properties");
  }

  @Test
  void testNoTimingWhenCollectorNotSet() throws IOException {
    IVersionInfo versionInfo = mockery.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(mockery.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(mockery.mock(INodeItem.class));
    IResourceLocation location = mockery.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    mockery.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(versionInfo).getName();
        will(returnValue("test"));
        allowing(versionInfo).getVersion();
        will(returnValue("0.0.0"));

        allowing(constraintA).getLevel();
        will(returnValue(IConstraint.Level.ERROR));
        allowing(constraintA).getId();
        will(returnValue(null));
        allowing(constraintA).getFormalName();
        will(returnValue("a formal name"));
        allowing(constraintA).getDescription();
        will(returnValue(MarkupLine.fromMarkdown("a description")));
        allowing(constraintA).getProperties();
        will(returnValue(Map.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(Set.of()));

        allowing(node).getLocation();
        will(returnValue(location));
        allowing(node).getBaseUri();
        will(returnValue(sourceFile.toUri()));
        allowing(node).getMetapath();
        will(returnValue("/node/child"));

        allowing(location).getLine();
        will(returnValue(10));
        allowing(location).getColumn();
        will(returnValue(0));
        allowing(location).getByteOffset();
        will(returnValue(-1L));
        allowing(location).getCharOffset();
        will(returnValue(-1L));
      }
    });

    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);
    // No timing collector set

    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());

    String sarifOutput = handler.writeToString(IBindingContext.newInstance());
    JSONObject sarif = new JSONObject(sarifOutput);

    JSONArray runs = sarif.getJSONArray("runs");
    JSONObject run = runs.getJSONObject(0);

    // Always-on: invocations should always be present with timestamps
    assertTrue(run.has("invocations"), "Run should always have invocations (always-on timing)");
    JSONArray invocations = run.getJSONArray("invocations");
    assertEquals(1, invocations.length());
    JSONObject invocation = invocations.getJSONObject(0);
    assertTrue(invocation.has("startTimeUtc"), "Invocation should always have startTimeUtc");
    assertTrue(invocation.has("endTimeUtc"), "Invocation should always have endTimeUtc");

    // Without timing collector, should NOT have phase/let timing notifications
    assertFalse(invocation.has("toolExecutionNotifications"),
        "Invocation should not have timing notifications when collector is not set");

    // Rules should not have timing properties
    JSONObject tool = run.getJSONObject("tool");
    JSONObject driver = tool.getJSONObject("driver");
    JSONArray rules = driver.getJSONArray("rules");
    for (int i = 0; i < rules.length(); i++) {
      JSONObject rule = rules.getJSONObject(i);
      if (rule.has("properties")) {
        JSONObject props = rule.getJSONObject("properties");
        assertFalse(props.has("timing"), "Rule should not have timing when collector is not set");
      }
    }
  }

  @Test
  void testPerResultEvaluationTiming() throws IOException {
    IVersionInfo versionInfo = mockery.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(mockery.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(mockery.mock(INodeItem.class));
    IResourceLocation location = mockery.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    mockery.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(versionInfo).getName();
        will(returnValue("test"));
        allowing(versionInfo).getVersion();
        will(returnValue("0.0.0"));

        allowing(constraintA).getLevel();
        will(returnValue(IConstraint.Level.ERROR));
        allowing(constraintA).getId();
        will(returnValue("per-result-constraint"));
        allowing(constraintA).getFormalName();
        will(returnValue("per-result test"));
        allowing(constraintA).getDescription();
        will(returnValue(MarkupLine.fromMarkdown("a description")));
        allowing(constraintA).getProperties();
        will(returnValue(Map.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getInternalIdentifier();
        will(returnValue("per-result-id"));

        allowing(node).getLocation();
        will(returnValue(location));
        allowing(node).getBaseUri();
        will(returnValue(sourceFile.toUri()));
        allowing(node).getMetapath();
        will(returnValue("/node/child"));

        allowing(location).getLine();
        will(returnValue(10));
        allowing(location).getColumn();
        will(returnValue(0));
        allowing(location).getByteOffset();
        will(returnValue(-1L));
        allowing(location).getCharOffset();
        will(returnValue(-1L));
      }
    });

    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);

    // Fire constraint evaluation events on the handler - finding added during
    // evaluation
    handler.beforeConstraintEvaluation(constraintA, node);
    busyWait();
    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());
    handler.afterConstraintEvaluation(constraintA, node);

    String sarifOutput = handler.writeToString(IBindingContext.newInstance());
    JSONObject sarif = new JSONObject(sarifOutput);

    JSONArray results = sarif.getJSONArray("runs").getJSONObject(0).getJSONArray("results");
    assertTrue(results.length() > 0, "Should have at least one result");

    JSONObject result = results.getJSONObject(0);
    assertTrue(result.has("properties"), "Result should have properties with per-evaluation timing");

    JSONObject props = result.getJSONObject("properties");
    assertTrue(props.has("timing"), "Result properties should have timing data");

    JSONObject timing = props.getJSONObject("timing");
    assertTrue(timing.has("totalMs"), "Per-result timing should have totalMs");
  }

  @Test
  void testPerResultLetTiming() throws IOException {
    IVersionInfo versionInfo = mockery.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(mockery.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(mockery.mock(INodeItem.class));
    IResourceLocation location = mockery.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    ILet let = ILet.of(
        IEnhancedQName.of("test-var"),
        IMetapathExpression.compile("count(//item)"),
        ISource.externalSource("https://example.com/module"),
        null);

    mockery.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(versionInfo).getName();
        will(returnValue("test"));
        allowing(versionInfo).getVersion();
        will(returnValue("0.0.0"));

        allowing(constraintA).getLevel();
        will(returnValue(IConstraint.Level.ERROR));
        allowing(constraintA).getId();
        will(returnValue("let-timing-constraint"));
        allowing(constraintA).getFormalName();
        will(returnValue("let timing test"));
        allowing(constraintA).getDescription();
        will(returnValue(MarkupLine.fromMarkdown("a description")));
        allowing(constraintA).getProperties();
        will(returnValue(Map.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getInternalIdentifier();
        will(returnValue("let-timing-id"));

        allowing(node).getLocation();
        will(returnValue(location));
        allowing(node).getBaseUri();
        will(returnValue(sourceFile.toUri()));
        allowing(node).getMetapath();
        will(returnValue("/node/child"));

        allowing(location).getLine();
        will(returnValue(10));
        allowing(location).getColumn();
        will(returnValue(0));
        allowing(location).getByteOffset();
        will(returnValue(-1L));
        allowing(location).getCharOffset();
        will(returnValue(-1L));
      }
    });

    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);

    // Simulate constraint evaluation with let evaluation inside
    handler.beforeConstraintEvaluation(constraintA, node);
    handler.beforeLetEvaluation(let);
    busyWait();
    handler.afterLetEvaluation(let);
    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());
    handler.afterConstraintEvaluation(constraintA, node);

    String sarifOutput = handler.writeToString(IBindingContext.newInstance());
    JSONObject sarif = new JSONObject(sarifOutput);

    JSONArray results = sarif.getJSONArray("runs").getJSONObject(0).getJSONArray("results");
    assertTrue(results.length() > 0, "Should have at least one result");

    JSONObject result = results.getJSONObject(0);
    assertTrue(result.has("properties"), "Result should have properties");

    JSONObject props = result.getJSONObject("properties");
    assertTrue(props.has("letTimings"), "Result properties should have letTimings");

    JSONArray letTimings = props.getJSONArray("letTimings");
    assertTrue(letTimings.length() > 0, "Should have at least one let timing entry");

    JSONObject letEntry = letTimings.getJSONObject(0);
    assertEquals("test-var", letEntry.getString("name"), "Let timing entry should have correct name");
    assertTrue(letEntry.has("timing"), "Let timing entry should have timing data");
  }

  @Test
  void testTimingOutputPassesSarifSchemaValidation() throws IOException {
    IVersionInfo versionInfo = mockery.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(mockery.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(mockery.mock(INodeItem.class));
    IResourceLocation location = mockery.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    mockery.checking(new Expectations() {
      { // NOPMD - intentional
        allowing(versionInfo).getName();
        will(returnValue("test"));
        allowing(versionInfo).getVersion();
        will(returnValue("0.0.0"));

        allowing(constraintA).getLevel();
        will(returnValue(IConstraint.Level.ERROR));
        allowing(constraintA).getId();
        will(returnValue("test-rule-1"));
        allowing(constraintA).getFormalName();
        will(returnValue("a formal name"));
        allowing(constraintA).getDescription();
        will(returnValue(MarkupLine.fromMarkdown("a description")));
        allowing(constraintA).getProperties();
        will(returnValue(Map.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getInternalIdentifier();
        will(returnValue("test-constraint-id"));

        allowing(node).getLocation();
        will(returnValue(location));
        allowing(node).getBaseUri();
        will(returnValue(sourceFile.toUri()));
        allowing(node).getMetapath();
        will(returnValue("/node/child"));

        allowing(location).getLine();
        will(returnValue(42));
        allowing(location).getColumn();
        will(returnValue(0));
        allowing(location).getByteOffset();
        will(returnValue(1024L));
        allowing(location).getCharOffset();
        will(returnValue(2048L));
      }
    });

    // Populate timing data
    TimingCollector collector = new TimingCollector();
    URI docUri = ObjectUtils.notNull(sourceFile.toUri());

    collector.beforeValidation(docUri);
    collector.beforePhase(ValidationPhase.SCHEMA_VALIDATION);
    busyWait();
    collector.afterPhase(ValidationPhase.SCHEMA_VALIDATION);
    collector.beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
    collector.beforeConstraintEvaluation(constraintA, node);
    busyWait();
    collector.afterConstraintEvaluation(constraintA, node);
    collector.afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);
    collector.afterValidation(docUri);

    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);
    handler.setTimingCollector(collector);

    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());

    String sarifOutput = handler.writeToString(IBindingContext.newInstance());

    // Validate against SARIF 2.1.0 schema
    Path sarifSchema = Paths.get("modules/sarif/sarif-schema-2.1.0.json");

    try (Reader schemaReader = Files.newBufferedReader(sarifSchema, StandardCharsets.UTF_8)) {
      JsonNode schemaNode = new OrgJsonNode(new JSONObject(new JSONTokener(schemaReader)));
      JsonNode instanceNode = new OrgJsonNode(new JSONObject(sarifOutput));

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
          () -> "SARIF output with timing failed schema validation. Errors:\n" + sj.toString());
    }
  }

  /**
   * A brief busy-wait to ensure System.nanoTime() advances measurably.
   */
  @SuppressWarnings("PMD.EmptyWhileStmt")
  private static void busyWait() {
    long start = System.nanoTime();
    while (System.nanoTime() - start < 1_000) {
      // spin until at least 1 microsecond has elapsed
    }
  }
}
