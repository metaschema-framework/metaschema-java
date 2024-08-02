/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;

import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractAllowedValue implements IAllowedValue {

  @Nullable
  public abstract String getDeprecated();

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
