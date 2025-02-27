/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.modules.sarif;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.model.constraint.ConstraintValidationFinding;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.model.validation.IValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator.JsonValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator.XmlValidationFinding;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.IVersionInfo;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.SerializationFeature;

import org.schemastore.json.sarif.x210.Artifact;
import org.schemastore.json.sarif.x210.ArtifactLocation;
import org.schemastore.json.sarif.x210.Location;
import org.schemastore.json.sarif.x210.LogicalLocation;
import org.schemastore.json.sarif.x210.Message;
import org.schemastore.json.sarif.x210.MultiformatMessageString;
import org.schemastore.json.sarif.x210.PhysicalLocation;
import org.schemastore.json.sarif.x210.Region;
import org.schemastore.json.sarif.x210.ReportingDescriptor;
import org.schemastore.json.sarif.x210.Result;
import org.schemastore.json.sarif.x210.Run;
import org.schemastore.json.sarif.x210.Sarif;
import org.schemastore.json.sarif.x210.SarifModule;
import org.schemastore.json.sarif.x210.Tool;
import org.schemastore.json.sarif.x210.ToolComponent;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Supports building a Static Analysis Results Interchange Format (SARIF)
 * document based on a set of validation findings.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class SarifValidationHandler {
  private enum Kind {
    NOT_APPLICABLE("notApplicable"),
    PASS("pass"),
    FAIL("fail"),
    REVIEW("review"),
    OPEN("open"),
    INFORMATIONAL("informational");

    @NonNull
    private final String label;

    Kind(@NonNull String label) {
      this.label = label;
    }

    @NonNull
    public String getLabel() {
      return label;
    }
  }

  private enum SeverityLevel {
    NONE("none"),
    NOTE("note"),
    WARNING("warning"),
    ERROR("error");

    @NonNull
    private final String label;

    SeverityLevel(@NonNull String label) {
      this.label = label;
    }

    @NonNull
    public String getLabel() {
      return label;
    }
  }

  @NonNull
  static final String SARIF_NS = "https://docs.oasis-open.org/sarif/sarif/v2.1.0";
  @NonNull
  public static final IAttributable.Key SARIF_HELP_URL_KEY
      = IAttributable.key("help-url", SARIF_NS);
  @NonNull
  public static final IAttributable.Key SARIF_HELP_TEXT_KEY
      = IAttributable.key("help-text", SARIF_NS);
  @NonNull
  public static final IAttributable.Key SARIF_HELP_MARKDOWN_KEY
      = IAttributable.key("help-markdown", SARIF_NS);

  @NonNull
  private final URI source;
  @Nullable
  private final IVersionInfo toolVersion;
  private final AtomicInteger artifactIndex = new AtomicInteger(-1);
  private final AtomicInteger ruleIndex = new AtomicInteger(-1);

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<URI, ArtifactRecord> artifacts = new LinkedHashMap<>();
  @NonNull
  private final List<AbstractRuleRecord> rules = new LinkedList<>();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<IConstraint, ConstraintRuleRecord> constraintRules = new LinkedHashMap<>();
  @NonNull
  private final List<IResult> results = new LinkedList<>();
  @NonNull
  private final SchemaRuleRecord schemaRule = new SchemaRuleRecord();
  private boolean schemaValid = true;

  /**
   * Construct a new validation handler.
   *
   * @param source
   *          the URI of the content that was validated
   * @param toolVersion
   *          the version information for the tool producing the validation
   *          results
   */
  public SarifValidationHandler(
      @NonNull URI source,
      @Nullable IVersionInfo toolVersion) {
    if (!source.isAbsolute()) {
      throw new IllegalArgumentException(String.format("The source URI '%s' is not absolute.", source.toASCIIString()));
    }

    this.source = source;
    this.toolVersion = toolVersion;
  }

  @NonNull
  private URI getSource() {
    return source;
  }

  private IVersionInfo getToolVersion() {
    return toolVersion;
  }

  /**
   * Register a collection of validation finding.
   *
   * @param findings
   *          the findings to register
   */
  public void addFindings(@NonNull Collection<? extends IValidationFinding> findings) {
    for (IValidationFinding finding : findings) {
      assert finding != null;
      addFinding(finding);
    }
  }

  /**
   * Register a validation finding.
   *
   * @param finding
   *          the finding to register
   */
  public void addFinding(@NonNull IValidationFinding finding) {
    if (finding instanceof JsonValidationFinding) {
      addJsonValidationFinding((JsonValidationFinding) finding);
    } else if (finding instanceof XmlValidationFinding) {
      addXmlValidationFinding((XmlValidationFinding) finding);
    } else if (finding instanceof ConstraintValidationFinding) {
      addConstraintValidationFinding((ConstraintValidationFinding) finding);
    } else {
      throw new IllegalStateException();
    }
  }

  private ConstraintRuleRecord getRuleRecord(@NonNull IConstraint constraint) {
    ConstraintRuleRecord retval = constraintRules.get(constraint);
    if (retval == null) {
      retval = new ConstraintRuleRecord(constraint);
      constraintRules.put(constraint, retval);
      rules.add(retval);
    }
    return retval;
  }

  private ArtifactRecord getArtifactRecord(@NonNull URI artifactUri) {
    ArtifactRecord retval = artifacts.get(artifactUri);
    if (retval == null) {
      retval = new ArtifactRecord(artifactUri);
      artifacts.put(artifactUri, retval);
    }
    return retval;
  }

  private void addJsonValidationFinding(@NonNull JsonValidationFinding finding) {
    results.add(new SchemaResult(finding));
    if (schemaValid && IValidationFinding.Kind.FAIL.equals(finding.getKind())) {
      schemaValid = false;
    }
  }

  private void addXmlValidationFinding(@NonNull XmlValidationFinding finding) {
    results.add(new SchemaResult(finding));
    if (schemaValid && IValidationFinding.Kind.FAIL.equals(finding.getKind())) {
      schemaValid = false;
    }
  }

  private void addConstraintValidationFinding(@NonNull ConstraintValidationFinding finding) {
    results.add(new ConstraintResult(finding));
  }

  /**
   * Generate a SARIF document based on the collected findings.
   *
   * @param outputUri
   *          the URI to use as the base for relative paths in the SARIF document
   * @return the generated SARIF document
   * @throws IOException
   *           if an error occurred while generating the SARIF document
   */
  @NonNull
  private Sarif generateSarif(@NonNull URI outputUri) throws IOException {
    Sarif sarif = new Sarif();
    sarif.setVersion("2.1.0");

    Run run = new Run();
    sarif.addRun(run);

    Artifact artifact = new Artifact();
    artifact.setLocation(getArtifactRecord(getSource()).generateArtifactLocation(outputUri));
    run.addArtifact(artifact);

    for (IResult result : results) {
      result.generateResults(outputUri).forEach(run::addResult);
    }

    IVersionInfo toolVersion = getToolVersion();
    if (!rules.isEmpty() || toolVersion != null) {
      Tool tool = new Tool();
      ToolComponent driver = new ToolComponent();

      if (toolVersion != null) {
        driver.setName(toolVersion.getName());
        driver.setVersion(toolVersion.getVersion());
      }

      for (AbstractRuleRecord rule : rules) {
        driver.addRule(rule.generate());
      }

      tool.setDriver(driver);
      run.setTool(tool);
    }

    return sarif;
  }

  /**
   * Write the collection of findings to a string in SARIF format.
   *
   * @param bindingContext
   *          the context used to access Metaschema module information based on
   *          Java class bindings
   * @return the SARIF document as a string
   * @throws IOException
   *           if an error occurred while generating the SARIF document
   */
  @NonNull
  public String writeToString(@NonNull IBindingContext bindingContext) throws IOException {
    bindingContext.registerModule(SarifModule.class);
    try (StringWriter writer = new StringWriter()) {
      bindingContext.newSerializer(Format.JSON, Sarif.class)
          .disableFeature(SerializationFeature.SERIALIZE_ROOT)
          .serialize(generateSarif(getSource()), writer);
      return ObjectUtils.notNull(writer.toString());
    }
  }

  /**
   * Write the collection of findings to the provided output file.
   *
   * @param outputFile
   *          the path to the output file to write to
   * @param bindingContext
   *          the context used to access Metaschema module information based on
   *          Java class bindings
   * @throws IOException
   *           if an error occurred while writing the SARIF file
   */
  public void write(
      @NonNull Path outputFile,
      @NonNull IBindingContext bindingContext) throws IOException {

    URI output = ObjectUtils.notNull(outputFile.toUri());
    Sarif sarif = generateSarif(output);

    bindingContext.registerModule(SarifModule.class);
    bindingContext.newSerializer(Format.JSON, Sarif.class)
        .disableFeature(SerializationFeature.SERIALIZE_ROOT)
        .serialize(
            sarif,
            outputFile,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);
  }

  private interface IResult {
    @NonNull
    IValidationFinding getFinding();

    @NonNull
    List<Result> generateResults(@NonNull URI output) throws IOException;
  }

  private abstract class AbstractResult<T extends IValidationFinding> implements IResult {
    @NonNull
    private final T finding;

    protected AbstractResult(@NonNull T finding) {
      this.finding = finding;
    }

    @Override
    public T getFinding() {
      return finding;
    }

    @NonNull
    protected Kind kind(@NonNull IValidationFinding finding) {
      IValidationFinding.Kind kind = finding.getKind();

      Kind retval;
      switch (kind) {
      case FAIL:
        retval = Kind.FAIL;
        break;
      case INFORMATIONAL:
        retval = Kind.INFORMATIONAL;
        break;
      case NOT_APPLICABLE:
        retval = Kind.NOT_APPLICABLE;
        break;
      case PASS:
        retval = Kind.PASS;
        break;
      default:
        throw new IllegalArgumentException(String.format("Invalid finding kind '%s'.", kind));
      }
      return retval;
    }

    @NonNull
    protected SeverityLevel level(@NonNull Level severity) {
      SeverityLevel retval;
      switch (severity) {
      case CRITICAL:
      case ERROR:
        retval = SeverityLevel.ERROR;
        break;
      case INFORMATIONAL:
      case DEBUG:
        retval = SeverityLevel.NOTE;
        break;
      case WARNING:
        retval = SeverityLevel.WARNING;
        break;
      case NONE:
        retval = SeverityLevel.NONE;
        break;
      default:
        throw new IllegalArgumentException(String.format("Invalid severity '%s'.", severity));
      }
      return retval;
    }

    protected void message(@NonNull IValidationFinding finding, @NonNull Result result) {
      String message = finding.getMessage();
      if (message == null) {
        message = "";
      }

      Message msg = new Message();
      msg.setText(message);
      result.setMessage(msg);
    }

    protected void location(@NonNull IValidationFinding finding, @NonNull Result result, @NonNull URI base)
        throws IOException {
      IResourceLocation location = finding.getLocation();
      if (location != null) {
        // region
        Region region = new Region();

        if (location.getLine() > -1) {
          region.setStartLine(BigInteger.valueOf(location.getLine()));
          region.setEndLine(BigInteger.valueOf(location.getLine()));
        }
        if (location.getColumn() > -1) {
          region.setStartColumn(BigInteger.valueOf(location.getColumn() + 1));
          region.setEndColumn(BigInteger.valueOf(location.getColumn() + 1));
        }
        if (location.getByteOffset() > -1) {
          region.setByteOffset(BigInteger.valueOf(location.getByteOffset()));
          region.setByteLength(BigInteger.ZERO);
        }
        if (location.getCharOffset() > -1) {
          region.setCharOffset(BigInteger.valueOf(location.getCharOffset()));
          region.setCharLength(BigInteger.ZERO);
        }

        PhysicalLocation physical = new PhysicalLocation();

        URI documentUri = finding.getDocumentUri();
        if (documentUri != null) {
          physical.setArtifactLocation(getArtifactRecord(documentUri).generateArtifactLocation(base));
        }
        physical.setRegion(region);

        LogicalLocation logical = new LogicalLocation();

        logical.setDecoratedName(finding.getPath());

        Location loc = new Location();
        loc.setPhysicalLocation(physical);
        loc.setLogicalLocation(logical);
        result.addLocation(loc);
      }
    }
  }

  private final class SchemaResult
      extends AbstractResult<IValidationFinding> {

    protected SchemaResult(@NonNull IValidationFinding finding) {
      super(finding);
    }

    @Override
    public List<Result> generateResults(@NonNull URI output) throws IOException {
      IValidationFinding finding = getFinding();

      Result result = new Result();

      result.setRuleId(schemaRule.getId());
      result.setRuleIndex(BigInteger.valueOf(schemaRule.getIndex()));
      result.setGuid(schemaRule.getGuid());

      result.setKind(kind(finding).getLabel());
      result.setLevel(level(finding.getSeverity()).getLabel());
      message(finding, result);
      location(finding, result, output);

      return CollectionUtil.singletonList(result);
    }
  }

  private final class ConstraintResult
      extends AbstractResult<ConstraintValidationFinding> {

    protected ConstraintResult(@NonNull ConstraintValidationFinding finding) {
      super(finding);
    }

    @Override
    public List<Result> generateResults(@NonNull URI output) throws IOException {
      ConstraintValidationFinding finding = getFinding();

      List<Result> retval = new LinkedList<>();

      Kind kind = kind(finding);
      SeverityLevel level = level(finding.getSeverity());

      for (IConstraint constraint : finding.getConstraints()) {
        assert constraint != null;
        ConstraintRuleRecord rule = getRuleRecord(constraint);

        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        Result result = new Result();

        String id = constraint.getId();
        if (id != null) {
          result.setRuleId(id);
        }
        result.setRuleIndex(BigInteger.valueOf(rule.getIndex()));
        result.setGuid(rule.getGuid());
        result.setKind(kind.getLabel());
        result.setLevel(level.getLabel());
        message(finding, result);
        location(finding, result, output);

        retval.add(result);
      }
      return retval;
    }
  }

  private abstract class AbstractRuleRecord {
    private final int index;
    @NonNull
    private final UUID guid;

    private AbstractRuleRecord() {
      this.index = ruleIndex.addAndGet(1);
      this.guid = ObjectUtils.notNull(UUID.randomUUID());
    }

    public int getIndex() {
      return index;
    }

    @NonNull
    public UUID getGuid() {
      return guid;
    }

    @NonNull
    protected abstract ReportingDescriptor generate();
  }

  private final class SchemaRuleRecord
      extends AbstractRuleRecord {

    @Override
    protected ReportingDescriptor generate() {
      ReportingDescriptor retval = new ReportingDescriptor();
      retval.setId(getId());
      retval.setGuid(getGuid());
      return retval;
    }

    public String getId() {
      return "schema-valid";
    }
  }

  private final class ConstraintRuleRecord
      extends AbstractRuleRecord {
    @NonNull
    private final IConstraint constraint;

    public ConstraintRuleRecord(@NonNull IConstraint constraint) {
      this.constraint = constraint;
    }

    @NonNull
    public IConstraint getConstraint() {
      return constraint;
    }

    @Override
    protected ReportingDescriptor generate() {
      ReportingDescriptor retval = new ReportingDescriptor();
      IConstraint constraint = getConstraint();

      UUID guid = getGuid();

      String id = constraint.getId();
      if (id == null) {
        retval.setId(guid.toString());
      } else {
        retval.setId(id);
      }
      retval.setGuid(guid);
      String formalName = constraint.getFormalName();
      if (formalName != null) {
        MultiformatMessageString text = new MultiformatMessageString();
        text.setText(formalName);
        retval.setShortDescription(text);
      }
      MarkupLine description = constraint.getDescription();
      if (description != null) {
        MultiformatMessageString text = new MultiformatMessageString();
        text.setText(description.toText());
        text.setMarkdown(description.toMarkdown());
        retval.setFullDescription(text);
      }

      Set<String> helpUrls = constraint.getPropertyValues(SARIF_HELP_URL_KEY);
      if (!helpUrls.isEmpty()) {
        retval.setHelpUri(URI.create(helpUrls.stream().findFirst().get()));
      }

      Set<String> helpText = constraint.getPropertyValues(SARIF_HELP_TEXT_KEY);
      Set<String> helpMarkdown = constraint.getPropertyValues(SARIF_HELP_MARKDOWN_KEY);
      // if there is help text or markdown, produce a message
      if (!helpText.isEmpty() || !helpMarkdown.isEmpty()) {
        MultiformatMessageString help = new MultiformatMessageString();

        MarkupMultiline markdown = helpMarkdown.stream().map(MarkupMultiline::fromMarkdown).findFirst().orElse(null);
        if (markdown != null) {
          // markdown is provided
          help.setMarkdown(markdown.toMarkdown());
        }

        String text = helpText.isEmpty()
            ? ObjectUtils.requireNonNull(markdown).toText() // if text is empty, markdown must be provided
            : helpText.stream().findFirst().get(); // use the provided text
        help.setText(text);

        retval.setHelp(help);
      }

      return retval;
    }

  }

  private final class ArtifactRecord {
    @NonNull
    private final URI uri;
    private final int index;

    public ArtifactRecord(@NonNull URI uri) {
      this.uri = uri;
      this.index = artifactIndex.addAndGet(1);
    }

    @NonNull
    public URI getUri() {
      return uri;
    }

    public int getIndex() {
      return index;
    }

    public ArtifactLocation generateArtifactLocation(@NonNull URI baseUri) throws IOException {
      ArtifactLocation location = new ArtifactLocation();

      try {
        location.setUri(UriUtils.relativize(baseUri, getUri(), true));
      } catch (URISyntaxException ex) {
        throw new IOException(ex);
      }

      location.setIndex(BigInteger.valueOf(getIndex()));
      return location;
    }
  }
}
