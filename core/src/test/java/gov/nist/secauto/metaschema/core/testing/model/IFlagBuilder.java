/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema module-based model builder for producing flag
 * definitions and instances.
 */
public interface IFlagBuilder extends IMetaschemaBuilder<IFlagBuilder> {

  /**
   * Create a new builder using the provided mocking context.
   *
   * @return the new builder
   */
  @NonNull
  static IFlagBuilder builder() {
    return new FlagBuilder().reset();
  }

  /**
   * Apply the provided required setting to built flags.
   *
   * @param required
   *          {@code true} if the flag is required or {@code false} otherwise
   * @return this builder
   */
  IFlagBuilder required(boolean required);

  /**
   * Apply the provided data type adapter to built flags.
   *
   * @param dataTypeAdapter
   *          the data type adapter to use
   * @return this builder
   */
  IFlagBuilder dataTypeAdapter(@NonNull IDataTypeAdapter<?> dataTypeAdapter);

  /**
   * Apply the provided data type adapter to built flags.
   *
   * @param defaultValue
   *          the default value to use
   * @return this builder
   */
  IFlagBuilder defaultValue(@NonNull Object defaultValue);

  /**
   * Build a mocked flag instance, based on a mocked definition, as a child of the
   * provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @return the new mocked instance
   */
  @NonNull
  default IFlagInstance toInstance(@NonNull IModelDefinition parent) {
    IFlagDefinition def = toDefinition();
    return toInstance(parent, def);
  }

  /**
   * Build a mocked flag instance, using the provided definition, as a child of
   * the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @param definition
   *          the definition to base the instance on
   * @return the new mocked instance
   */
  @NonNull
  IFlagInstance toInstance(@NonNull IModelDefinition parent, @NonNull IFlagDefinition definition);

  /**
   * Build a mocked flag definition.
   *
   * @return the new mocked definition
   */
  @NonNull
  IFlagDefinition toDefinition();
}
