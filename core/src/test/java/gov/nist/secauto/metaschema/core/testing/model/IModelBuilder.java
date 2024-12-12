/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a Metaschema module-based model builder, that supports flag
 * children.
 *
 * @param <T>
 *          the Java type of the implementation of this builder
 */
public interface IModelBuilder<T extends IModelBuilder<T>> extends IMetaschemaBuilder<T> {
  /**
   * Use the provided flag instances for built fields.
   *
   * @param flags
   *          the flag builders to add
   * @return this builder
   */
  T flags(@Nullable List<IFlagBuilder> flags);

  /**
   * Generate an instance of the implementing type using the provided parent.
   *
   * @param parent
   *          the parent assembly definition containing the instance
   * @return the new instance
   */
  @NonNull
  INamedModelInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent);
}
