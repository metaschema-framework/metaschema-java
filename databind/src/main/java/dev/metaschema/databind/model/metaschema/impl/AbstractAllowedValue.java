/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.model.constraint.IAllowedValue;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Abstract base class for allowed value implementations.
 * <p>
 * This class provides common functionality for representing individual allowed
 * values within an allowed-values constraint.
 */
public abstract class AbstractAllowedValue implements IAllowedValue {

  /**
   * Gets the version at which this allowed value was deprecated.
   *
   * @return the deprecation version string, or {@code null} if not deprecated
   */
  @Nullable
  public abstract String getDeprecated();

  /**
   * Gets the remark describing this allowed value.
   *
   * @return the remark as markup, or {@code null} if not provided
   */
  @Nullable
  public abstract MarkupLine getRemark();

  @Override
  public abstract String getValue();

  @Override
  public String getDeprecatedVersion() {
    return getDeprecated();
  }

  @Override
  public MarkupLine getDescription() {
    MarkupLine remark = getRemark();
    return remark == null ? MarkupLine.fromMarkdown("") : remark;
  }
}
