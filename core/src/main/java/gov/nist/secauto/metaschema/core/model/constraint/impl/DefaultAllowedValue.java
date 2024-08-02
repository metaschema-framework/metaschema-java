/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DefaultAllowedValue implements IAllowedValue {
  @NonNull
  private final String value;
  @NonNull
  private final MarkupLine description;
  @Nullable
  private final String deprecatedVersion;

  /**
   * Construct a new allowed value entry for use in an
   * {@link IAllowedValuesConstraint}.
   *
   * @param value
   *          the allowed value
   * @param description
   *          a textual description of the value
   * @param deprecatedVersion
   *          the module version this value was deprecated in or {@code null} if
   *          the value is not deprecated
   */
  public DefaultAllowedValue(
      @NonNull String value,
      @NonNull MarkupLine description,
      @Nullable String deprecatedVersion) {
    this.value = value;
    this.description = description;
    this.deprecatedVersion = deprecatedVersion;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public MarkupLine getDescription() {
    return description;
  }

  @Override
  public String getDeprecatedVersion() {
    return deprecatedVersion;
  }
}
