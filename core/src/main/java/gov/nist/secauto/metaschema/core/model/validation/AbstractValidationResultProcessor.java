/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import gov.nist.secauto.metaschema.core.model.constraint.ConstraintValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator.JsonValidationFinding;
import gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator.XmlValidationFinding;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides processing hooks for a set of validation results.
 *
 * @since 2.0.0
 */
public abstract class AbstractValidationResultProcessor {

  /**
   * Handle the provided collection of validation results.
   *
   * @param result
   *          the validation results
   * @return {@code true} if the result is passing or {@code false} otherwise
   */
  public boolean handleResults(IValidationResult result) {
    handleValidationFindings(result.getFindings());
    return result.isPassing();
  }

  /**
   * Handle the provided collection of validation findings.
   *
   * @param findings
   *          the findings to process
   */
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

  /**
   * Process the JSON validation finding.
   *
   * @param finding
   *          the validation finding to process
   */
  protected abstract void handleJsonValidationFinding(@NonNull JsonValidationFinding finding);

  /**
   * Process the XML validation finding.
   *
   * @param finding
   *          the validation finding to process
   */
  protected abstract void handleXmlValidationFinding(@NonNull XmlValidationFinding finding);

  /**
   * Process the Metaschema module constraint validation finding.
   *
   * @param finding
   *          the validation finding to process
   */
  protected abstract void handleConstraintValidationFinding(@NonNull ConstraintValidationFinding finding);
}
