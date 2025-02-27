/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.modules.sarif;

import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.constraint.ConstraintValidationFinding;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.validation.IValidationFinding;
import gov.nist.secauto.metaschema.core.util.IVersionInfo;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.Reader;
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

class SarifValidationHandlerTest {
  @RegisterExtension
  public final JUnit5Mockery context = new JUnit5Mockery();

  @Test
  void testWriteToString() throws IOException {
    IVersionInfo versionInfo = context.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(context.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(context.mock(INodeItem.class));
    IResourceLocation location = context.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    Set<String> helpUrls = Set.of("https://example.com/test");
    Set<String> helpMarkdown = Set.of("**help text**");

    context.checking(new Expectations() {
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
        will(returnValue(
            Map.ofEntries(
                Map.entry(SarifValidationHandler.SARIF_HELP_URL_KEY, helpUrls),
                Map.entry(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY, helpMarkdown))));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(helpUrls));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(helpMarkdown));

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

    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);

    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());

    String sarifOutput = handler.writeToString(IBindingContext.newInstance());

    Path sarifSchema = Paths.get("modules/sarif/sarif-schema-2.1.0.json");

    try (Reader schemaReader = Files.newBufferedReader(sarifSchema, StandardCharsets.UTF_8)) {
      JsonNode schemaNode = new OrgJsonNode(new JSONObject(new JSONTokener(schemaReader)));

      JsonNode instanceNode = new OrgJsonNode(new JSONObject(sarifOutput));

      Validator.Result result
          = new ValidatorFactory().withDialect(new Dialects.Draft2020Dialect()).validate(schemaNode, instanceNode);
      StringJoiner sj = new StringJoiner("\n");
      for (dev.harrel.jsonschema.Error finding : result.getErrors()) {
        sj.add(String.format("[%s]%s %s for schema '%s'",
            finding.getInstanceLocation(),
            finding.getKeyword() == null ? "" : " " + finding.getKeyword() + ":",
            finding.getError(),
            finding.getSchemaLocation()));
      }
      assertTrue(result.isValid(), () -> "SARIF output failed schema validation. Errors:\n" + sj.toString());
    }
  }

  @Test
  void testWriteToFile() throws IOException {

    IVersionInfo versionInfo = context.mock(IVersionInfo.class);
    IConstraint constraintA = ObjectUtils.notNull(context.mock(IConstraint.class, "constraintA"));
    INodeItem node = ObjectUtils.notNull(context.mock(INodeItem.class));
    IResourceLocation location = context.mock(IResourceLocation.class);

    Path sourceFile = ObjectUtils.requireNonNull(Paths.get(".", "source.json").toAbsolutePath());

    Set<String> helpUrls = Set.of("https://example.com/test");
    Set<String> helpMarkdown = Set.of("**help text**");

    context.checking(new Expectations() {
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
        will(returnValue(
            Map.ofEntries(
                Map.entry(SarifValidationHandler.SARIF_HELP_URL_KEY, helpUrls),
                Map.entry(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY, helpMarkdown))));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_URL_KEY);
        will(returnValue(helpUrls));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_TEXT_KEY);
        will(returnValue(Set.of()));
        allowing(constraintA).getPropertyValues(SarifValidationHandler.SARIF_HELP_MARKDOWN_KEY);
        will(returnValue(helpMarkdown));

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

    SarifValidationHandler handler
        = new SarifValidationHandler(ObjectUtils.notNull(sourceFile.toUri()), versionInfo);

    handler.addFinding(ConstraintValidationFinding.builder(constraintA, node)
        .kind(IValidationFinding.Kind.FAIL)
        .build());

    // no need to cleanup this file, since it is created in the target directory
    Path sarifFile = ObjectUtils.requireNonNull(Paths.get("target/test.sarif"));
    handler.write(sarifFile, IBindingContext.newInstance());

    Path sarifSchema = Paths.get("modules/sarif/sarif-schema-2.1.0.json");

    try (Reader schemaReader = Files.newBufferedReader(sarifSchema, StandardCharsets.UTF_8)) {
      JsonNode schemaNode = new OrgJsonNode(new JSONObject(new JSONTokener(schemaReader)));

      try (Reader instanceReader = Files.newBufferedReader(sarifFile, StandardCharsets.UTF_8)) {
        JsonNode instanceNode = new OrgJsonNode(new JSONObject(new JSONTokener(instanceReader)));

        Validator.Result result
            = new ValidatorFactory().withDialect(new Dialects.Draft2020Dialect()).validate(schemaNode, instanceNode);
        StringJoiner sj = new StringJoiner("\n");
        for (dev.harrel.jsonschema.Error finding : result.getErrors()) {
          sj.add(String.format("[%s]%s %s for schema '%s'",
              finding.getInstanceLocation(),
              finding.getKeyword() == null ? "" : " " + finding.getKeyword() + ":",
              finding.getError(),
              finding.getSchemaLocation()));
        }
        assertTrue(result.isValid(), () -> "SARIF output failed schema validation. Errors:\n" + sj.toString());
      }
    }
  }
}
