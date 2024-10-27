/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.constraint.impl.DefaultAllowedValue;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an individual enumerated value associated with an
 * {@link IAllowedValuesConstraint}.
 */
public interface IAllowedValue {
  /**
   * Construct a new allowed value entry for use in an
   * {@link IAllowedValuesConstraint}.
   *
   * @param value
   *          the allowed value
   * @param description
   *          a textual description of the value
   * @param deprecatedVersion
   *          the version this value was deprecated in
   * @return the new allowed value
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IAllowedValue of(
      @NonNull String value,
      @NonNull MarkupLine description,
      @Nullable String deprecatedVersion) {
    return new DefaultAllowedValue(value, description, deprecatedVersion);
  }

  /**
   * Retrieves the enumerated value associated with this allowed value constraint
   * entry.
   *
   * @return the value
   */
  @NonNull
  String getValue();

  /**
   * If the value is deprecated, get the deprecated version.
   *
   * @return the deprecated version or {@code null} if the value is not deprecated
   */
  String getDeprecatedVersion();

  /**
   * Retrieves the enumerated value's description associated with this allowed
   * value constraint entry.
   *
   * @return the description
   */
  @NonNull
  MarkupLine getDescription();
}
