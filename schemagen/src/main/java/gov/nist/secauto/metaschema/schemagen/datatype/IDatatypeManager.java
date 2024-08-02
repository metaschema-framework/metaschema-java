/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.datatype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDatatypeManager {
  String getTypeNameForDatatype(@NonNull IDataTypeAdapter<?> datatype);

  Set<String> getUsedTypes();
}
