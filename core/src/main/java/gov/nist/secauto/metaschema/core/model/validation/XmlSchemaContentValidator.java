/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports validating an XML resource using an XML schema.
 */
public class XmlSchemaContentValidator
    extends AbstractContentValidator {
  private final Schema schema;

  @SuppressWarnings("null")
  @NonNull
  private static Schema toSchema(@NonNull List<? extends Source> schemaSources) throws SAXException {
    SchemaFactory schemafactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    // schemafactory.setResourceResolver(new ClasspathResourceResolver());
    Schema retval;
    if (schemaSources.isEmpty()) {
      retval = schemafactory.newSchema();
    } else {
      retval = schemafactory.newSchema(schemaSources.toArray(new Source[0]));
    }

    // TODO verify source input streams are closed
    return retval;
  }

  /**
   * Construct a new XML schema validator using the provided XML schema sources.
   *
   * @param schemaSources
   *          the XML schemas to use for validation
   * @throws SAXException
   *           if an error occurred while parsing the provided XML schemas
   */
  public XmlSchemaContentValidator(@NonNull List<? extends Source> schemaSources) throws SAXException {
    this(toSchema(ObjectUtils.requireNonNull(schemaSources, "schemaSources")));
  }

  /**
   * Construct a new XML schema validator using the provided pre-parsed XML
   * schema(s).
   *
   * @param schema
   *          the pre-parsed XML schema(s) to use for validation
   */
  protected XmlSchemaContentValidator(@NonNull Schema schema) {
    this.schema = ObjectUtils.requireNonNull(schema, "schema");
  }

  private Schema getSchema() {
    return schema;
  }

  @Override
  public IValidationResult validate(InputStream is, URI documentUri) throws IOException {
    Source xmlSource = new StreamSource(is, documentUri.toASCIIString());

    Validator validator = getSchema().newValidator();
    XmlValidationErrorHandler errorHandler = new XmlValidationErrorHandler(documentUri);
    validator.setErrorHandler(errorHandler);
    try {
      validator.validate(xmlSource);
    } catch (SAXParseException ex) {
      String location = ex.getLineNumber() > -1 && ex.getColumnNumber() > -1
          ? String.format("at %d:%d", ex.getLineNumber(), ex.getColumnNumber())
          : "";
      throw new IOException(
          String.format("Unexpected failure during validation of '%s'%s. %s",
              documentUri,
              location,
              ex.getLocalizedMessage()),
          ex);
    } catch (SAXException ex) {
      throw new IOException(
          String.format("Unexpected failure during validation of '%s'. %s",
              documentUri,
              ex.getLocalizedMessage()),
          ex);
    }
    return errorHandler;
  }

  /**
   * Records an identified individual validation result found during XML schema
   * validation.
   */
  public static class XmlValidationFinding implements IValidationFinding, IResourceLocation {
    @NonNull
    private final URI documentUri;
    @NonNull
    private final SAXParseException exception;
    @NonNull
    private final Level severity;

    /**
     * Construct a new XML schema validation finding, which represents an issue
     * identified during XML schema validation.
     *
     * @param severity
     *          the finding significance
     * @param exception
     *          the XML schema validation exception generated during schema
     *          validation representing the issue
     * @param resourceUri
     *          the resource the issue was found in
     */
    public XmlValidationFinding(
        @NonNull Level severity,
        @NonNull SAXParseException exception,
        @NonNull URI resourceUri) {
      this.severity = ObjectUtils.requireNonNull(severity, "severity");
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
      return Level.WARNING.equals(getSeverity()) ? Kind.PASS : Kind.FAIL;
    }

    @Override
    public Level getSeverity() {
      return severity;
    }

    @Override
    public URI getDocumentUri() {
      String systemId = getCause().getSystemId();
      return systemId == null ? documentUri : URI.create(systemId);
    }

    @Override
    public int getLine() {
      return getCause().getLineNumber();
    }

    @Override
    public int getColumn() {
      return getCause().getColumnNumber();
    }

    @Override
    public long getCharOffset() {
      // not known
      return -1;
    }

    @Override
    public long getByteOffset() {
      // not known
      return -1;
    }

    @Override
    public IResourceLocation getLocation() {
      return this;
    }

    @Override
    public String getPathKind() {
      // not known
      return null;
    }

    @Override
    public String getPath() {
      // not known
      return null;
    }

    @Override
    public String getMessage() {
      return getCause().getLocalizedMessage();
    }

    @NonNull
    @Override
    public SAXParseException getCause() {
      return exception;
    }
  }

  private static class XmlValidationErrorHandler implements ErrorHandler, IValidationResult {
    @NonNull
    private final URI documentUri;
    @NonNull
    private final List<XmlValidationFinding> findings = new LinkedList<>();
    @NonNull
    private Level highestSeverity = Level.INFORMATIONAL;

    public XmlValidationErrorHandler(@NonNull URI documentUri) {
      this.documentUri = ObjectUtils.requireNonNull(documentUri, "documentUri");
    }

    @NonNull
    public URI getDocumentUri() {
      return documentUri;
    }

    private void adjustHighestSeverity(@NonNull Level severity) {
      if (highestSeverity.ordinal() < severity.ordinal()) {
        highestSeverity = severity;
      }
    }

    @SuppressWarnings("null")
    @Override
    public void warning(SAXParseException ex) throws SAXException {
      findings.add(new XmlValidationFinding(Level.WARNING, ex, getDocumentUri()));
      adjustHighestSeverity(Level.WARNING);
    }

    @SuppressWarnings("null")
    @Override
    public void error(SAXParseException ex) throws SAXException {
      findings.add(new XmlValidationFinding(Level.ERROR, ex, getDocumentUri()));
      adjustHighestSeverity(Level.CRITICAL);
    }

    @SuppressWarnings("null")
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
      findings.add(new XmlValidationFinding(Level.CRITICAL, ex, getDocumentUri()));
      adjustHighestSeverity(Level.CRITICAL);
    }

    @SuppressWarnings("null")
    @Override
    @NonNull
    public List<XmlValidationFinding> getFindings() {
      return Collections.unmodifiableList(findings);
    }

    @Override
    public Level getHighestSeverity() {
      return highestSeverity;
    }
  }
}
