/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IReportConstraint;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyConstraints;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedReportConstraint;
import gov.nist.secauto.metaschema.databind.model.metaschema.impl.ConstraintBindingSupport;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class BindingConstraintLoaderTest {
  private static final String NS = "http://csrc.nist.gov/ns/test/metaschema/meta-constraints";

  @Test
  void testValid() throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader loader = new BindingConstraintLoader(bindingContext);

    List<IConstraintSet> constraints = loader.load(
        ObjectUtils
            .notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-valid.yaml")));
    assertEquals(1, constraints.size());

    Path compileDir = ObjectUtils.notNull(Paths.get("target/generated-test-modules/meta-constraints/"));
    Files.createDirectories(compileDir);

    bindingContext = IBindingContext.builder()
        .compilePath(compileDir)
        .constraintSet(constraints)
        .build();

    IBindingMetaschemaModule metaschema = bindingContext.loadMetaschema(
        ObjectUtils.notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/metaschema.xml")));
    IBoundModule module = bindingContext.registerModule(metaschema);

    final IAssemblyDefinition level1
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());
    final IAssemblyDefinition level2
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level2").getIndexPosition());
    final IAssemblyDefinition level3
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level3").getIndexPosition());

    assert level1 != null;
    assert level2 != null;
    assert level3 != null;

    level1.getConstraints();
    level2.getConstraints();
    level3.getConstraints();

    assertAll(
        () -> assertNotNull(level1),
        () -> assertEquals(1, level1.getLetExpressions().size(), "level 1 let"),
        () -> assertEquals(1, level1.getExpectConstraints().size(), "level 1 expect"),
        () -> assertNotNull(level2),
        () -> assertEquals(1, level2.getLetExpressions().size(), "level 2 let"),
        () -> assertEquals(1, level2.getExpectConstraints().size(), "level 2 expect"),
        () -> assertNotNull(level3),
        () -> assertEquals(1, level3.getLetExpressions().size(), "level 3 let"),
        () -> assertEquals(1, level3.getExpectConstraints().size(), "level 3 expect"));
  }

  @Test
  void testReportConstraintLoading() throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader loader = new BindingConstraintLoader(bindingContext);

    List<IConstraintSet> constraints = loader.load(
        ObjectUtils.notNull(
            Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-report.yaml")));
    assertEquals(1, constraints.size(), "should load exactly one constraint set");

    // Get the constraint set and verify it has the context with report constraint
    IConstraintSet constraintSet = constraints.get(0);
    assertNotNull(constraintSet, "constraint set should not be null");

    // Now test that it applies to level1
    Path compileDir = ObjectUtils.notNull(Paths.get("target/generated-test-modules/meta-constraints-report/"));
    Files.createDirectories(compileDir);

    bindingContext = IBindingContext.builder()
        .compilePath(compileDir)
        .constraintSet(constraints)
        .build();

    IBindingMetaschemaModule metaschema = bindingContext.loadMetaschema(
        ObjectUtils.notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/metaschema.xml")));
    IBoundModule module = bindingContext.registerModule(metaschema);

    final IAssemblyDefinition level1
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());

    assertNotNull(level1, "level1 assembly should exist");

    // Debug: print all constraints for level1
    List<? extends IConstraint> allConstraints = level1.getConstraints();
    StringBuilder debug = new StringBuilder();
    debug.append("All constraints on level1: ").append(allConstraints.size()).append("\n");
    for (IConstraint c : allConstraints) {
      debug.append("  - ").append(c.getClass().getSimpleName())
          .append(": ").append(c.getId())
          .append(" (type: ").append(c.getType()).append(")\n");
    }

    // Verify report constraints were loaded
    List<? extends IReportConstraint> reportConstraints = level1.getReportConstraints();
    debug.append("Report constraints on level1: ").append(reportConstraints.size());

    // Use assertEquals with debug message to ensure we see the output
    assertEquals(1, reportConstraints.size(),
        "should have exactly one report constraint. Debug info:\n" + debug);
    assertAll(
        () -> assertNotNull(reportConstraints, "report constraints list should not be null"),
        () -> assertFalse(reportConstraints.isEmpty(), "should have at least one report constraint"),
        () -> assertEquals(1, reportConstraints.size(), "should have exactly one report constraint"),
        () -> assertEquals("level1-report", reportConstraints.get(0).getId(), "constraint should have correct id"));
  }

  /**
   * Diagnostic test to trace through each step of the constraint loading process.
   * This helps identify where report constraints might be lost.
   */
  @Test
  void testTraceReportConstraintLoading() throws IOException {
    StringBuilder trace = new StringBuilder();
    trace.append("=== Tracing Report Constraint Loading ===\n\n");

    // Step 1: Parse YAML directly to binding object
    trace.append("STEP 1: Parse YAML to binding object\n");
    IBindingContext bindingContext = IBindingContext.newInstance();
    IBoundLoader loader = bindingContext.newBoundLoader();
    URI resourceUri = ObjectUtils
        .notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-report.yaml")
            .toUri());

    Object constraintsDocument = loader.load(resourceUri);
    trace.append("  Document type: ").append(constraintsDocument.getClass().getName()).append("\n");
    assertEquals(MetaschemaMetaConstraints.class, constraintsDocument.getClass(),
        "Should parse to MetaschemaMetaConstraints");

    MetaschemaMetaConstraints metaConstraints = (MetaschemaMetaConstraints) constraintsDocument;

    // Step 2: Check contexts
    trace.append("\nSTEP 2: Check parsed contexts\n");
    List<gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathContext> contexts
        = metaConstraints.getContexts();
    trace.append("  Number of contexts: ").append(contexts.size()).append("\n");
    assertFalse(contexts.isEmpty(), "Should have at least one context");

    // Step 3: Check first context's constraints
    trace.append("\nSTEP 3: Check first context's constraints\n");
    gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathContext firstContext = contexts.get(0);
    trace.append("  First context metapaths: ");
    for (var mp : firstContext.getMetapaths()) {
      trace.append(mp.getTarget()).append(" ");
    }
    trace.append("\n");

    AssemblyConstraints assemblyConstraints = firstContext.getConstraints();
    trace.append("  Constraints object: ").append(assemblyConstraints == null ? "null" : "present").append("\n");
    assertNotNull(assemblyConstraints, "Constraints should not be null");

    // Step 4: Check rules in constraints
    trace.append("\nSTEP 4: Check rules in AssemblyConstraints\n");
    List<? extends ITargetedConstraintBase> rules = assemblyConstraints.getRules();
    trace.append("  Number of rules: ").append(rules.size()).append("\n");

    if (!rules.isEmpty()) {
      for (int i = 0; i < rules.size(); i++) {
        Object rule = rules.get(i);
        trace.append("  Rule ").append(i).append(": ");
        if (rule == null) {
          trace.append("null\n");
        } else {
          trace.append(rule.getClass().getName()).append("\n");
          if (rule instanceof TargetedReportConstraint) {
            TargetedReportConstraint report = (TargetedReportConstraint) rule;
            trace.append("    ID: ").append(report.getId()).append("\n");
            trace.append("    Test: ").append(report.getTest()).append("\n");
            trace.append("    Target: ").append(report.getTarget()).append("\n");
          } else if (rule instanceof IConstraintBase) {
            IConstraintBase constraint = (IConstraintBase) rule;
            trace.append("    ID: ").append(constraint.getId()).append("\n");
          }
        }
      }
    } else {
      trace.append("  WARNING: No rules found!\n");
    }

    // Step 5: Check if TargetedReportConstraint is in the rules
    trace.append("\nSTEP 5: Look for TargetedReportConstraint\n");
    boolean foundReport = false;
    for (Object rule : rules) {
      if (rule instanceof TargetedReportConstraint) {
        foundReport = true;
        trace.append("  FOUND TargetedReportConstraint!\n");
        break;
      }
    }
    if (!foundReport) {
      trace.append("  NOT FOUND - TargetedReportConstraint missing from rules\n");
    }

    // Output trace for debugging
    System.out.println(trace);

    // The assertion that matters
    assertEquals(1, rules.size(), "Should have exactly one rule. Trace:\n" + trace);
    assertEquals(TargetedReportConstraint.class, rules.get(0).getClass(),
        "Rule should be TargetedReportConstraint. Trace:\n" + trace);
  }

  /**
   * Test that ConstraintBindingSupport.parse() correctly converts
   * TargetedReportConstraint to IReportConstraint and adds it to the
   * AssemblyConstraintSet.
   */
  @Test
  void testConstraintBindingSupportParsesReportConstraint() throws IOException {
    StringBuilder trace = new StringBuilder();
    trace.append("=== Testing ConstraintBindingSupport.parse() ===\n\n");

    // Step 1: Parse YAML directly to binding object
    IBindingContext bindingContext = IBindingContext.newInstance();
    IBoundLoader loader = bindingContext.newBoundLoader();
    URI resourceUri = ObjectUtils
        .notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-report.yaml")
            .toUri());

    MetaschemaMetaConstraints metaConstraints = (MetaschemaMetaConstraints) loader.load(resourceUri);
    var contexts = metaConstraints.getContexts();
    assertFalse(contexts.isEmpty(), "Should have at least one context");

    var firstContext = contexts.get(0);
    AssemblyConstraints assemblyConstraints = firstContext.getConstraints();
    assertNotNull(assemblyConstraints, "Should have assembly constraints");

    trace.append("YAML parsed successfully. Rules count: ").append(assemblyConstraints.getRules().size()).append("\n");

    // Step 2: Create AssemblyConstraintSet and parse constraints into it
    StaticContext staticContext = StaticContext.builder()
        .baseUri(resourceUri)
        .useWildcardWhenNamespaceNotDefaulted(true)
        .build();
    ISource source = ISource.externalSource(staticContext, false);

    AssemblyConstraintSet constraintSet = new AssemblyConstraintSet(source);

    trace.append("Before ConstraintBindingSupport.parse():\n");
    trace.append("  expect constraints: ").append(constraintSet.getExpectConstraints().size()).append("\n");
    trace.append("  report constraints: ").append(constraintSet.getReportConstraints().size()).append("\n");
    trace.append("  all constraints: ").append(constraintSet.getConstraints().size()).append("\n");

    // Parse using ConstraintBindingSupport
    ConstraintBindingSupport.parse(constraintSet, assemblyConstraints, source);

    trace.append("\nAfter ConstraintBindingSupport.parse():\n");
    trace.append("  expect constraints: ").append(constraintSet.getExpectConstraints().size()).append("\n");
    trace.append("  report constraints: ").append(constraintSet.getReportConstraints().size()).append("\n");
    trace.append("  all constraints: ").append(constraintSet.getConstraints().size()).append("\n");

    // List all constraints with their types
    for (IConstraint c : constraintSet.getConstraints()) {
      trace.append("    - ").append(c.getClass().getSimpleName()).append(": id=").append(c.getId()).append("\n");
    }

    // Report constraints specifically
    trace.append("\nReport constraints details:\n");
    for (IReportConstraint rc : constraintSet.getReportConstraints()) {
      trace.append("    - id=").append(rc.getId())
          .append(", test=").append(rc.getTest().getPath())
          .append(", target=").append(rc.getTarget().getPath()).append("\n");
    }

    System.out.println(trace);

    // Assertions
    assertEquals(1, constraintSet.getReportConstraints().size(),
        "Should have exactly one report constraint. Trace:\n" + trace);
    assertEquals("level1-report", constraintSet.getReportConstraints().get(0).getId(),
        "Report constraint should have correct id");
  }

  /**
   * Test the full BindingConstraintLoader.load() path to verify report
   * constraints are in the returned IConstraintSet.
   */
  @Test
  void testBindingConstraintLoaderReturnsReportConstraints() throws MetaschemaException, IOException {
    StringBuilder trace = new StringBuilder();
    trace.append("=== Testing BindingConstraintLoader.load() ===\n\n");

    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader loader = new BindingConstraintLoader(bindingContext);

    List<IConstraintSet> constraintSets = loader.load(
        ObjectUtils.notNull(
            Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-report.yaml")));

    trace.append("Constraint sets loaded: ").append(constraintSets.size()).append("\n\n");
    assertEquals(1, constraintSets.size(), "Should have exactly one constraint set");

    IConstraintSet constraintSet = constraintSets.get(0);
    trace.append("Constraint set type: ").append(constraintSet.getClass().getName()).append("\n");

    // For MetaConstraintSet, we need to check the contexts
    if (constraintSet instanceof gov.nist.secauto.metaschema.core.model.constraint.MetaConstraintSet) {
      trace.append("MetaConstraintSet detected - need to check contexts\n");
      // Can't directly access contexts, but we can test the behavior
    }

    // Now test applying constraints to see if they work
    Path compileDir = ObjectUtils.notNull(Paths.get("target/generated-test-modules/meta-constraints-report-trace/"));
    Files.createDirectories(compileDir);

    bindingContext = IBindingContext.builder()
        .compilePath(compileDir)
        .constraintSet(constraintSets)
        .build();

    IBindingMetaschemaModule metaschema = bindingContext.loadMetaschema(
        ObjectUtils.notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/metaschema.xml")));

    trace.append("\nBefore registerModule:\n");
    IAssemblyDefinition level1Before
        = metaschema.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());
    if (level1Before != null) {
      trace.append("  level1 report constraints: ").append(level1Before.getReportConstraints().size()).append("\n");
      trace.append("  level1 expect constraints: ").append(level1Before.getExpectConstraints().size()).append("\n");
    }

    IBoundModule module = bindingContext.registerModule(metaschema);

    trace.append("\nAfter registerModule:\n");
    IAssemblyDefinition level1 = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());
    assertNotNull(level1, "level1 should exist");

    trace.append("  level1 all constraints: ").append(level1.getConstraints().size()).append("\n");
    trace.append("  level1 expect constraints: ").append(level1.getExpectConstraints().size()).append("\n");
    trace.append("  level1 report constraints: ").append(level1.getReportConstraints().size()).append("\n");

    // List all constraints
    for (IConstraint c : level1.getConstraints()) {
      trace.append("    - ").append(c.getClass().getSimpleName())
          .append(": id=").append(c.getId())
          .append(", type=").append(c.getType()).append("\n");
    }

    System.out.println(trace);

    assertEquals(1, level1.getReportConstraints().size(),
        "Should have exactly one report constraint on level1. Trace:\n" + trace);
  }

  /**
   * Test that expect constraints are preserved through the registerModule
   * process. This verifies that constraints defined in the metaschema XML are
   * properly embedded in generated code and available on the bound module.
   */
  @Test
  void testExpectConstraintPreservedThroughRegisterModule() throws MetaschemaException, IOException {
    IBindingContext bindingContext = IBindingContext.newInstance();
    IConstraintLoader loader = new BindingConstraintLoader(bindingContext);

    List<IConstraintSet> constraints = loader.load(
        ObjectUtils
            .notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-valid.yaml")));

    Path compileDir = ObjectUtils.notNull(Paths.get("target/generated-test-modules/meta-constraints-trace-expect/"));
    Files.createDirectories(compileDir);

    bindingContext = IBindingContext.builder()
        .compilePath(compileDir)
        .constraintSet(constraints)
        .build();

    IBindingMetaschemaModule metaschema = bindingContext.loadMetaschema(
        ObjectUtils.notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/metaschema.xml")));

    // Verify expect constraint exists before registerModule
    IAssemblyDefinition level1Before
        = metaschema.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());
    assertNotNull(level1Before, "level1 definition should exist before registerModule");
    assertEquals(1, level1Before.getExpectConstraints().size(),
        "level1 should have 1 expect constraint before registerModule");

    // Register the module
    IBoundModule module = bindingContext.registerModule(metaschema);

    // Verify expect constraint exists after registerModule
    IAssemblyDefinition level1After
        = module.getAssemblyDefinitionByName(IEnhancedQName.of(NS, "level1").getIndexPosition());
    assertNotNull(level1After, "level1 definition should exist after registerModule");
    assertEquals(1, level1After.getExpectConstraints().size(),
        "level1 should have 1 expect constraint after registerModule");
  }

  /**
   * Parallel comparison test: Trace both expect and report constraints through
   * the exact same loading path to identify where they diverge.
   */
  @Test
  void testCompareExpectVsReportYamlParsing() throws IOException {
    StringBuilder comparison = new StringBuilder();
    comparison.append("=== Comparing Expect vs Report YAML Parsing ===\n\n");

    IBindingContext bindingContext = IBindingContext.newInstance();
    IBoundLoader loader = bindingContext.newBoundLoader();

    // Load both YAML files
    URI expectUri = ObjectUtils
        .notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-valid.yaml")
            .toUri());
    URI reportUri = ObjectUtils
        .notNull(Paths.get("src/test/resources/content/constraints/meta-constraints/meta-constraints-report.yaml")
            .toUri());

    Object expectDoc = loader.load(expectUri);
    Object reportDoc = loader.load(reportUri);

    comparison.append("Document types:\n");
    comparison.append("  Expect: ").append(expectDoc.getClass().getName()).append("\n");
    comparison.append("  Report: ").append(reportDoc.getClass().getName()).append("\n\n");

    assertEquals(expectDoc.getClass(), reportDoc.getClass(),
        "Both should parse to same document type");

    MetaschemaMetaConstraints expectConstraints = (MetaschemaMetaConstraints) expectDoc;
    MetaschemaMetaConstraints reportConstraints = (MetaschemaMetaConstraints) reportDoc;

    // Compare contexts
    comparison.append("Contexts:\n");
    comparison.append("  Expect contexts: ").append(expectConstraints.getContexts().size()).append("\n");
    comparison.append("  Report contexts: ").append(reportConstraints.getContexts().size()).append("\n\n");

    assertFalse(expectConstraints.getContexts().isEmpty(), "Expect contexts should not be empty");
    assertFalse(reportConstraints.getContexts().isEmpty(), "Report contexts should not be empty");

    // Compare first context's constraints
    var expectFirstContext = expectConstraints.getContexts().get(0);
    var reportFirstContext = reportConstraints.getContexts().get(0);

    comparison.append("First context metapaths:\n");
    comparison.append("  Expect: ");
    for (var mp : expectFirstContext.getMetapaths()) {
      comparison.append(mp.getTarget()).append(" ");
    }
    comparison.append("\n  Report: ");
    for (var mp : reportFirstContext.getMetapaths()) {
      comparison.append(mp.getTarget()).append(" ");
    }
    comparison.append("\n\n");

    // Compare rules
    AssemblyConstraints expectAssemblyConstraints = expectFirstContext.getConstraints();
    AssemblyConstraints reportAssemblyConstraints = reportFirstContext.getConstraints();

    comparison.append("AssemblyConstraints:\n");
    comparison.append("  Expect constraints: ").append(
        expectAssemblyConstraints == null ? "null" : "present").append("\n");
    comparison.append("  Report constraints: ").append(
        reportAssemblyConstraints == null ? "null" : "present").append("\n\n");

    assertNotNull(expectAssemblyConstraints, "Expect AssemblyConstraints should not be null");
    assertNotNull(reportAssemblyConstraints, "Report AssemblyConstraints should not be null");

    // Compare rules lists
    List<? extends ITargetedConstraintBase> expectRules = expectAssemblyConstraints.getRules();
    List<? extends ITargetedConstraintBase> reportRules = reportAssemblyConstraints.getRules();

    comparison.append("Rules:\n");
    comparison.append("  Expect rules count: ").append(expectRules.size()).append("\n");
    comparison.append("  Report rules count: ").append(reportRules.size()).append("\n");

    if (!expectRules.isEmpty()) {
      comparison.append("  Expect rules types:\n");
      for (int i = 0; i < expectRules.size(); i++) {
        Object rule = expectRules.get(i);
        comparison.append("    [").append(i).append("] ").append(
            rule == null ? "null" : rule.getClass().getSimpleName()).append("\n");
      }
    }

    if (!reportRules.isEmpty()) {
      comparison.append("  Report rules types:\n");
      for (int i = 0; i < reportRules.size(); i++) {
        Object rule = reportRules.get(i);
        comparison.append("    [").append(i).append("] ").append(
            rule == null ? "null" : rule.getClass().getSimpleName()).append("\n");
      }
    } else {
      comparison.append("  Report rules: EMPTY - THIS IS THE PROBLEM!\n");
    }

    // Output comparison for debugging
    System.out.println(comparison);
    assertFalse(expectRules.isEmpty(), "Expect rules should not be empty");
    assertFalse(reportRules.isEmpty(),
        "Report rules should not be empty - YAML parsing failed! Comparison:\n" + comparison);
    assertEquals(1, reportRules.size(),
        "Should have exactly one report rule. Comparison:\n" + comparison);
    assertEquals(TargetedReportConstraint.class, reportRules.get(0).getClass(),
        "Rule should be TargetedReportConstraint. Comparison:\n" + comparison);
  }
}
