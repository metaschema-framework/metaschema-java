/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype;

import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A service interface used to provide implementations of data types represented
 * as {@link IDataTypeAdapter} instances.
 * <p>
 * Multiple providers can be used to support dynamic data type discovery in an
 * Metaschema-based application, allowing data type extensions to be loaded at
 * runtime using the {@link DataTypeService}.
 */
public interface IDataTypeProvider {
  /**
   * Get the type information for abstract item types that do not have an
   * associated data type adpater.
   *
   * @return the abstract item types provided
   */
  List<? extends IAtomicOrUnionType<?>> getAbstractTypes();

  /**
   * Get the {@link IDataTypeAdapter} instances associated with this provider.
   *
   * @return the sequence of adapters in match priority order
   */
  @NonNull
  List<? extends IDataTypeAdapter<?>> getJavaTypeAdapters();
}
