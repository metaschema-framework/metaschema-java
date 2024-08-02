/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.util;

import static org.fusesource.jansi.Ansi.ansi;

import gov.nist.secauto.metaschema.core.model.constraint.ConstraintValidationFinding;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.model.validation.IValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.IValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator.JsonValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator.XmlValidationFinding;

import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.xml.sax.SAXParseException;

import java.net.URI;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class LoggingValidationHandler {
  private static final Logger LOGGER = LogManager.getLogger(LoggingValidationHandler.class);

  private static final LoggingValidationHandler NO_LOG_EXCPTION_INSTANCE = new LoggingValidationHandler(false);
  private static final LoggingValidationHandler LOG_EXCPTION_INSTANCE = new LoggingValidationHandler(true);

  private final boolean logExceptions;

  public static LoggingValidationHandler instance() {
    return instance(false);
  }

  @SuppressFBWarnings(value = "SING_SINGLETON_GETTER_NOT_SYNCHRONIZED",
      justification = "both values are class initialized")
  public static LoggingValidationHandler instance(boolean logExceptions) {
    return logExceptions ? LOG_EXCPTION_INSTANCE : NO_LOG_EXCPTION_INSTANCE;
  }

  private LoggingValidationHandler(boolean logExceptions) {
    this.logExceptions = logExceptions;
  }

  public boolean isLogExceptions() {
    return logExceptions;
  }

  public boolean handleValidationResults(IValidationResult result) {
    handleValidationFindings(result.getFindings());
    return result.isPassing();
  }

  public void handleValidationFindings(@NonNull List<? extends IValidationFinding> findings) {
    for (IValidationFinding finding : findings) {
      if (finding instanceof JsonValidationFinding) {
        handleJsonValidationFinding((JsonValidationFinding) finding);
      } else if (finding instanceof XmlValidationFinding) {
        handleXmlValidationFinding((XmlValidationFinding) finding);
      } else if (finding instanceof ConstraintValidationFinding) {
        handleConstraintValidationFinding((ConstraintValidationFinding) finding);
      } else {
        throw new IllegalStateException();
      }
    }
  }

  private void handleJsonValidationFinding(@NonNull JsonValidationFinding finding) {
    Ansi ansi = generatePreamble(finding.getSeverity());

    ansi = ansi.a('[')
        .fgBright(Color.WHITE)
        .a(finding.getCause().getPointerToViolation())
        .reset()
        .a(']');

    URI documentUri = finding.getDocumentUri();
    ansi = documentUri == null
        ? ansi.format(" %s", finding.getMessage())
        : ansi.format(" %s [%s]", finding.getMessage(), documentUri.toString());

    getLogger(finding).log(ansi);
  }

  private void handleXmlValidationFinding(XmlValidationFinding finding) {
    Ansi ansi = generatePreamble(finding.getSeverity());
    SAXParseException ex = finding.getCause();

    URI documentUri = finding.getDocumentUri();
    ansi = documentUri == null
        ? ansi.format("%s [{%d,%d}]",
            finding.getMessage(),
            ex.getLineNumber(),
            ex.getColumnNumber())
        : ansi.format("%s [%s{%d,%d}]",
            finding.getMessage(),
            documentUri.toString(),
            ex.getLineNumber(),
            ex.getColumnNumber());

    getLogger(finding).log(ansi);
  }

  private void handleConstraintValidationFinding(@NonNull ConstraintValidationFinding finding) {
    Ansi ansi = generatePreamble(finding.getSeverity());

    getLogger(finding).log(
        ansi.format("[%s] %s", finding.getTarget().getMetapath(), finding.getMessage()));
  }

  @NonNull
  private LogBuilder getLogger(@NonNull IValidationFinding finding) {
    LogBuilder retval;
    switch (finding.getSeverity()) {
    case CRITICAL:
      retval = LOGGER.atFatal();
      break;
    case ERROR:
      retval = LOGGER.atError();
      break;
    case WARNING:
      retval = LOGGER.atWarn();
      break;
    case INFORMATIONAL:
      retval = LOGGER.atInfo();
      break;
    default:
      throw new IllegalArgumentException("Unknown level: " + finding.getSeverity().name());
    }

    assert retval != null;

    if (finding.getCause() != null && isLogExceptions()) {
      retval.withThrowable(finding.getCause());
    }

    return retval;
  }

  @SuppressWarnings("static-method")
  @NonNull
  private Ansi generatePreamble(@NonNull Level level) {
    Ansi ansi = ansi().fgBright(Color.WHITE).a('[').reset();

    switch (level) {
    case CRITICAL:
      ansi = ansi.fgRed().a("CRITICAL").reset();
      break;
    case ERROR:
      ansi = ansi.fgBrightRed().a("ERROR").reset();
      break;
    case WARNING:
      ansi = ansi.fgBrightYellow().a("WARNING").reset();
      break;
    case INFORMATIONAL:
      ansi = ansi.fgBrightBlue().a("INFO").reset();
      break;
    default:
      ansi = ansi().fgBright(Color.MAGENTA).a(level.name()).reset();
      break;
    }
    ansi = ansi.fgBright(Color.WHITE).a("] ").reset();

    assert ansi != null;
    return ansi;
  }
}
