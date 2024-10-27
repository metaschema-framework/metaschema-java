/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.Property;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.Remarks;

import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface IConstraintBase {

  @Nullable
  String getId();

  @Nullable
  String getFormalName();

  @Nullable
  MarkupLine getDescription();

  @Nullable
  List<Property> getProps();

  @Nullable
  String getMessage();

  @Nullable
  Remarks getRemarks();

  @Nullable
  String getLevel();
}
