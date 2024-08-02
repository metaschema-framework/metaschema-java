/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.configuration.AbstractConfigurationFeature;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.DataClass") // not a data class
public final class ValidationFeature<V>
    extends AbstractConfigurationFeature<V> {
  /**
   * If enabled, generate findings for passing constraints.
   */
  @NonNull
  public static final ValidationFeature<Boolean> VALIDATE_GENERATE_PASS_FINDINGS
      = new ValidationFeature<>("include-pass-findings", Boolean.class, false);

  private ValidationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
