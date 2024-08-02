/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaContentValidator
    extends AbstractContentValidator {
  @NonNull
  private final Schema schema;

  /**
   * Construct a new JSON schema validator using the provided reader to load the
   * JSON schema.
   *
   * @param reader
   *          the JSON schema reader
   */
  public JsonSchemaContentValidator(@NonNull Reader reader) {
    this(new JSONTokener(reader));
  }

  /**
   * Construct a new JSON schema validator using the provided input stream to load
   * the JSON schema.
   *
   * @param is
   *          the JSON schema input source
   */
  public JsonSchemaContentValidator(@NonNull InputStream is) {
    this(new JSONTokener(is));
  }

  /**
   * Construct a new JSON schema validator using the provided JSON object for the
   * JSON schema.
   *
   * @param jsonSchema
   *          the JSON schema
   */
  public JsonSchemaContentValidator(@NonNull JSONObject jsonSchema) {
    this(ObjectUtils.notNull(SchemaLoader.load(jsonSchema)));
  }

  /**
   * Construct a new JSON schema validator using the provided JSON tokenizer to
   * load the schema.
   *
   * @param tokenizer
   *          the JSON schema token stream
   */
  protected JsonSchemaContentValidator(@NonNull JSONTokener tokenizer) {
    this(new JSONObject(tokenizer));
  }

  /**
   * Construct a new JSON schema validator using the preloaded JSON schema.
   *
   * @param schema
   *          the preloaded JSON schema
   */
  protected JsonSchemaContentValidator(@NonNull Schema schema) {
    this.schema = ObjectUtils.requireNonNull(schema, "schema");
  }

  @Override
  public IValidationResult validate(InputStream is, URI resourceUri) throws IOException {
    JSONObject json;
    try {
      json = new JSONObject(new JSONTokener(is));
    } catch (JSONException ex) {
      throw new IOException(String.format("Unable to parse JSON from '%s'", resourceUri), ex);
    }
    return validate(json, resourceUri);
  }

  /**
   * Validate the provided JSON.
   *
   * @param json
   *          the JSON to validate
   * @param resourceUri
   *          the source URI for the JSON to validate
   * @return the validation results
   */
  @SuppressWarnings("null")
  @NonNull
  public IValidationResult validate(@NonNull JSONObject json, @NonNull URI resourceUri) {
    IValidationResult retval;
    try {
      schema.validate(json);
      retval = IValidationResult.PASSING_RESULT;
    } catch (ValidationException ex) {
      retval = new JsonValidationResult(handleValidationException(ex, resourceUri).collect(Collectors.toList()));
    }

    return retval;
  }

  /**
   * Build validation findings from a validation exception.
   *
   * @param exception
   *          the JSON schema validation exception generated during schema
   *          validation representing the issue
   * @param resourceUri
   *          the resource the issue was found in
   * @return the stream of findings
   */
  @SuppressWarnings("null")
  @NonNull
  protected Stream<JsonValidationFinding> handleValidationException(
      @NonNull ValidationException exception,
      @NonNull URI resourceUri) {
    JsonValidationFinding finding = new JsonValidationFinding(exception, resourceUri);
    Stream<JsonValidationFinding> childFindings = exception.getCausingExceptions().stream()
        .flatMap(ex -> {
          return handleValidationException(ex, resourceUri);
        });
    return Stream.concat(Stream.of(finding), childFindings);
  }

  /**
   * Records an identified individual validation result found during JSON schema
   * validation.
   */
  public static class JsonValidationFinding implements IValidationFinding {
    @NonNull
    private final ValidationException exception;
    @NonNull
    private final URI documentUri;

    /**
     * Construct a new XML schema validation finding, which represents an issue
     * identified during XML schema validation.
     *
     * @param exception
     *          the JSON schema validation exception generated during schema
     *          validation representing the issue
     * @param resourceUri
     *          the resource the issue was found in
     */
    public JsonValidationFinding(
        @NonNull ValidationException exception,
        @NonNull URI resourceUri) {
      this.exception = ObjectUtils.requireNonNull(exception, "exception");
      this.documentUri = ObjectUtils.requireNonNull(resourceUri, "documentUri");
    }

    @Override
    public String getIdentifier() {
      // always null
      return null;
    }

    @Override
    public Kind getKind() {
      return IValidationFinding.Kind.FAIL;
    }

    @Override
    public IConstraint.Level getSeverity() {
      return IConstraint.Level.ERROR;
    }

    @Override
    public URI getDocumentUri() {
      return documentUri;
    }

    @Override
    public IResourceLocation getLocation() {
      // not known
      return null;
    }

    @Override
    public String getPathKind() {
      return "JSON-pointer";
    }

    @Override
    public String getPath() {
      return getCause().getPointerToViolation();
    }

    @SuppressWarnings("null")
    @Override
    public String getMessage() {
      return getCause().getLocalizedMessage();
    }

    @NonNull
    @Override
    public ValidationException getCause() {
      return exception;
    }
  }

  private static class JsonValidationResult implements IValidationResult {
    @NonNull
    private final List<JsonValidationFinding> findings;

    @SuppressWarnings("null")
    public JsonValidationResult(@NonNull List<JsonValidationFinding> findings) {
      this.findings = Collections.unmodifiableList(Objects.requireNonNull(findings, "findings"));
    }

    @Override
    public IConstraint.Level getHighestSeverity() {
      return findings.isEmpty() ? IConstraint.Level.INFORMATIONAL : IConstraint.Level.ERROR;
    }

    @Override
    public List<? extends IValidationFinding> getFindings() {
      return findings;
    }

  }
}
