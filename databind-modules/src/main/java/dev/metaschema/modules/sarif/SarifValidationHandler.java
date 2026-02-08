/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.modules.sarif;

import org.schemastore.json.sarif.x210.Artifact;
import org.schemastore.json.sarif.x210.ArtifactLocation;
import org.schemastore.json.sarif.x210.Invocation;
import org.schemastore.json.sarif.x210.LetTimingEntry;
import org.schemastore.json.sarif.x210.Location;
import org.schemastore.json.sarif.x210.LogicalLocation;
import org.schemastore.json.sarif.x210.Message;
import org.schemastore.json.sarif.x210.MultiformatMessageString;
import org.schemastore.json.sarif.x210.Notification;
import org.schemastore.json.sarif.x210.PhysicalLocation;
import org.schemastore.json.sarif.x210.PropertyBag;
import org.schemastore.json.sarif.x210.Region;
import org.schemastore.json.sarif.x210.ReportingDescriptor;
import org.schemastore.json.sarif.x210.Result;
import org.schemastore.json.sarif.x210.Run;
import org.schemastore.json.sarif.x210.Sarif;
import org.schemastore.json.sarif.x210.SarifModule;
import org.schemastore.json.sarif.x210.TimingData;
import org.schemastore.json.sarif.x210.Tool;
import org.schemastore.json.sarif.x210.ToolComponent;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IResourceLocation;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.constraint.ConstraintValidationFinding;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.IConstraint.Level;
import dev.metaschema.core.model.constraint.ILet;
import dev.metaschema.core.model.constraint.TimingCollector;
import dev.metaschema.core.model.constraint.TimingRecord;
import dev.metaschema.core.model.constraint.ValidationEventListener;
import dev.metaschema.core.model.constraint.ValidationPhase;
import dev.metaschema.core.model.validation.IValidationFinding;
import dev.metaschema.core.model.validation.JsonSchemaContentValidator.JsonValidationFinding;
import dev.metaschema.core.model.validation.XmlSchemaContentValidator.XmlValidationFinding;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.IVersionInfo;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.core.util.UriUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.io.Format;
import dev.metaschema.databind.io.SerializationFeature;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Supports building a Static Analysis Results Interchange Format (SARIF)
 * document based on a set of validation findings.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class SarifValidationHandler implements ValidationEventListener {
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
  /**
   * The property key for specifying a URL that provides help information for a
   * constraint.
   */
  @NonNull
  public static final IAttributable.Key SARIF_HELP_URL_KEY
      = IAttributable.key("help-url", SARIF_NS);
  /**
   * The property key for specifying plain text help content for a constraint.
   */
  @NonNull
  public static final IAttributable.Key SARIF_HELP_TEXT_KEY
      = IAttributable.key("help-text", SARIF_NS);
  /**
   * The property key for specifying markdown-formatted help content for a
   * constraint.
   */
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
  @Nullable
  private TimingCollector timingCollector;
  @NonNull
  private final Instant constructionTimestamp = Instant.now();
  private final ThreadLocal<Long> currentEvaluationStartNanos = new ThreadLocal<>();
  private final ThreadLocal<List<ConstraintResult>> currentEvaluationResults = new ThreadLocal<>();
  private final ThreadLocal<Long> currentLetStartNanos = new ThreadLocal<>();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  private final ThreadLocal<Map<ILet, Long>> currentLetDurations = new ThreadLocal<>();
  @NonNull
  private final ConcurrentHashMap<IConstraint, EvaluationTimingSnapshot> evaluationTimings
      = new ConcurrentHashMap<>();

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
   * Set the timing collector to enrich SARIF output with performance data.
   * <p>
   * When set, the generated SARIF document will include:
   * <ul>
   * <li>An invocation element with start/end timestamps</li>
   * <li>Phase timing as tool execution notifications</li>
   * <li>Per-constraint timing in rule properties</li>
   * </ul>
   *
   * @param collector
   *          the timing collector containing measurement data, or {@code null} to
   *          disable timing output
   */
  public void setTimingCollector(@Nullable TimingCollector collector) {
    this.timingCollector = collector;
  }

  @Override
  public void beforeValidation(@NonNull URI document) {
    // No-op: always-on timing uses construction timestamp
  }

  @Override
  public void afterValidation(@NonNull URI document) {
    // No-op: always-on timing captures end time at SARIF generation
  }

  @Override
  public void beforePhase(@NonNull ValidationPhase phase) {
    // No-op: phase timing is handled by TimingCollector
  }

  @Override
  public void afterPhase(@NonNull ValidationPhase phase) {
    // No-op: phase timing is handled by TimingCollector
  }

  @Override
  public void beforeConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target) {
    currentEvaluationStartNanos.set(System.nanoTime());
    currentEvaluationResults.set(new ArrayList<>());
    currentLetDurations.set(new LinkedHashMap<>());
  }

  @SuppressWarnings("PMD.NullAssignment") // ThreadLocal cleanup
  @Override
  public void afterConstraintEvaluation(@NonNull IConstraint constraint, @NonNull INodeItem target) {
    Long startNanos = currentEvaluationStartNanos.get();
    List<ConstraintResult> evaluationResults = currentEvaluationResults.get();
    Map<ILet, Long> letDurations = currentLetDurations.get();

    if (startNanos != null) {
      long durationNs = System.nanoTime() - startNanos;
      Map<ILet, Long> snapshotLetDurations = letDurations != null && !letDurations.isEmpty()
          ? new LinkedHashMap<>(letDurations)
          : null;

      // Set timing on inline results (added during this evaluation)
      if (evaluationResults != null) {
        for (ConstraintResult result : evaluationResults) {
          result.setEvaluationDurationNs(durationNs);
          if (snapshotLetDurations != null) {
            result.setLetDurations(snapshotLetDurations);
          }
        }
      }

      // Store for deferred lookup (when findings are added after validation)
      evaluationTimings.put(constraint,
          new EvaluationTimingSnapshot(durationNs, snapshotLetDurations));
    }

    currentEvaluationStartNanos.remove();
    currentEvaluationResults.remove();
    currentLetStartNanos.remove();
    currentLetDurations.remove();
  }

  @Override
  public void beforeLetEvaluation(@NonNull ILet let) {
    currentLetStartNanos.set(System.nanoTime());
  }

  @Override
  public void afterLetEvaluation(@NonNull ILet let) {
    Long startNanos = currentLetStartNanos.get();
    Map<ILet, Long> letDurations = currentLetDurations.get();
    if (startNanos != null && letDurations != null) {
      long durationNs = System.nanoTime() - startNanos;
      letDurations.merge(let, durationNs, Long::sum);
    }
    currentLetStartNanos.remove();
  }

  @NonNull
  private static final BigDecimal NS_PER_MS = BigDecimal.valueOf(1_000_000L);

  /**
   * Convert nanoseconds to milliseconds as a BigDecimal with 3 decimal places.
   *
   * @param nanoseconds
   *          the duration in nanoseconds
   * @return the duration in milliseconds
   */
  @NonNull
  private static BigDecimal nsToMs(long nanoseconds) {
    return ObjectUtils.notNull(
        BigDecimal.valueOf(nanoseconds).divide(NS_PER_MS, 3, RoundingMode.HALF_UP));
  }

  /**
   * Convert a {@link TimingRecord} to a SARIF {@link TimingData} object.
   *
   * @param record
   *          the timing record to convert
   * @return the SARIF timing data
   */
  @NonNull
  private static TimingData toTimingData(@NonNull TimingRecord record) {
    TimingData data = new TimingData();
    data.setTotalMs(nsToMs(record.getTotalTimeNs()));
    data.setCount(BigInteger.valueOf(record.getCount()));
    if (record.getCount() > 0) {
      data.setMinMs(nsToMs(record.getMinTimeNs()));
      data.setMaxMs(nsToMs(record.getMaxTimeNs()));
    }
    return data;
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
    ConstraintResult constraintResult = new ConstraintResult(finding);
    results.add(constraintResult);

    // Track for per-evaluation timing if within a constraint evaluation (inline)
    List<ConstraintResult> evaluationResults = currentEvaluationResults.get();
    if (evaluationResults != null) {
      evaluationResults.add(constraintResult);
    } else {
      // Deferred pattern: look up timing from the most recent evaluation
      for (IConstraint constraint : finding.getConstraints()) {
        EvaluationTimingSnapshot snapshot = evaluationTimings.get(constraint);
        if (snapshot != null) {
          constraintResult.setEvaluationDurationNs(snapshot.durationNs);
          if (snapshot.letDurations != null) {
            constraintResult.setLetDurations(snapshot.letDurations);
          }
          break;
        }
      }
    }
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

    enrichWithTiming(run);

    return sarif;
  }

  /**
   * Enrich the SARIF run with timing data.
   * <p>
   * Always creates an invocation with start/end timestamps (always-on timing). If
   * a timing collector is set, overrides timestamps from the collector and adds
   * phase/let-statement timing as tool execution notifications.
   *
   * @param run
   *          the SARIF run to enrich
   */
  @SuppressWarnings("PMD.CognitiveComplexity")
  private void enrichWithTiming(@NonNull Run run) {
    // Always create invocation with timestamps (always-on timing)
    Invocation invocation = new Invocation();
    invocation.setExecutionSuccessful(Boolean.TRUE);
    invocation.setStartTimeUtc(ZonedDateTime.ofInstant(constructionTimestamp, ZoneOffset.UTC));
    invocation.setEndTimeUtc(ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));

    TimingCollector collector = this.timingCollector;
    if (collector != null) {
      // Override with collector timestamps if available
      TimingRecord validationTiming = collector.getValidationTiming();
      if (validationTiming != null) {
        Instant start = validationTiming.getStartTimestampUtc();
        if (start != null) {
          invocation.setStartTimeUtc(ZonedDateTime.ofInstant(start, ZoneOffset.UTC));
        }
        Instant end = validationTiming.getEndTimestampUtc();
        if (end != null) {
          invocation.setEndTimeUtc(ZonedDateTime.ofInstant(end, ZoneOffset.UTC));
        }
      }

      // Add phase timing as notifications
      for (Map.Entry<ValidationPhase, TimingRecord> entry : collector.getPhaseTimings().entrySet()) {
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        Notification notification = new Notification();
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        Message msg = new Message();
        msg.setText("Phase: " + entry.getKey().name());
        notification.setMessage(msg);

        TimingRecord phaseRecord = entry.getValue();
        Instant phaseEnd = phaseRecord.getEndTimestampUtc();
        if (phaseEnd != null) {
          notification.setTimeUtc(ZonedDateTime.ofInstant(phaseEnd, ZoneOffset.UTC));
        }

        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        PropertyBag phaseProps = new PropertyBag();
        phaseProps.setTiming(toTimingData(phaseRecord));
        notification.setProperties(phaseProps);

        invocation.addToolExecutionNotification(notification);
      }

      // Add let-statement timing as notifications
      for (Map.Entry<ILet, TimingRecord> entry : collector.getLetTimings().entrySet()) {
        ILet let = entry.getKey();

        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        Notification notification = new Notification();
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        Message msg = new Message();
        msg.setText("$" + let.getName().getLocalName() + " := " + let.getValueExpression().getPath());
        notification.setMessage(msg);

        TimingRecord letRecord = entry.getValue();
        Instant letEnd = letRecord.getEndTimestampUtc();
        if (letEnd != null) {
          notification.setTimeUtc(ZonedDateTime.ofInstant(letEnd, ZoneOffset.UTC));
        }

        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        PropertyBag letProps = new PropertyBag();
        letProps.setTiming(toTimingData(letRecord));
        notification.setProperties(letProps);

        invocation.addToolExecutionNotification(notification);
      }
    }

    run.addInvocation(invocation);
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
    registerSarifMetaschemaModule(bindingContext);
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

    registerSarifMetaschemaModule(bindingContext);
    bindingContext.newSerializer(Format.JSON, Sarif.class)
        .disableFeature(SerializationFeature.SERIALIZE_ROOT)
        .serialize(
            sarif,
            outputFile,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);
  }

  private static void registerSarifMetaschemaModule(@NonNull IBindingContext bindingContext) {
    try {
      bindingContext.registerModule(SarifModule.class);
    } catch (MetaschemaException ex) {
      throw new IllegalStateException("Unable to register the builtin SARIF module.", ex);
    }
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
        loc.addLogicalLocation(logical);
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
    @Nullable
    private Long evaluationDurationNs;
    @Nullable
    private Map<ILet, Long> letDurations;

    protected ConstraintResult(@NonNull ConstraintValidationFinding finding) {
      super(finding);
    }

    void setEvaluationDurationNs(long durationNs) {
      this.evaluationDurationNs = durationNs;
    }

    void setLetDurations(@NonNull Map<ILet, Long> durations) {
      this.letDurations = durations;
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
        addPerResultTiming(result);

        retval.add(result);
      }
      return retval;
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    private void addPerResultTiming(@NonNull Result result) {
      Long durationNs = this.evaluationDurationNs;
      if (durationNs == null) {
        return;
      }

      PropertyBag props = result.getProperties();
      if (props == null) {
        props = new PropertyBag();
        result.setProperties(props);
      }

      TimingData timing = new TimingData();
      timing.setTotalMs(nsToMs(durationNs));
      timing.setCount(BigInteger.ONE);
      props.setTiming(timing);

      Map<ILet, Long> letDurs = this.letDurations;
      if (letDurs != null) {
        for (Map.Entry<ILet, Long> entry : letDurs.entrySet()) {
          @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
          LetTimingEntry letEntry = new LetTimingEntry();
          letEntry.setName(entry.getKey().getName().getLocalName());

          @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
          TimingData letTiming = new TimingData();
          letTiming.setTotalMs(nsToMs(entry.getValue()));
          letTiming.setCount(BigInteger.ONE);
          letEntry.setTiming(letTiming);

          props.addLetTimingEntry(letEntry);
        }
      }
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

      // Add timing data if available
      TimingCollector collector = timingCollector;
      if (collector != null) {
        TimingRecord record = collector.getConstraintTiming(constraint.getInternalIdentifier());
        if (record != null) {
          PropertyBag props = retval.getProperties();
          if (props == null) {
            props = new PropertyBag();
            retval.setProperties(props);
          }
          props.setTiming(toTimingData(record));
        }
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

  /**
   * Snapshot of per-evaluation timing data, stored for deferred lookup when
   * findings are added after validation completes.
   */
  private static final class EvaluationTimingSnapshot {
    final long durationNs;
    @Nullable
    final Map<ILet, Long> letDurations;

    EvaluationTimingSnapshot(long durationNs, @Nullable Map<ILet, Long> letDurations) {
      this.durationNs = durationNs;
      this.letDurations = letDurations;
    }
  }
}
