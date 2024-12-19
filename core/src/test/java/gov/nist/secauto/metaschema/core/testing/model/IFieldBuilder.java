/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema module-based model builder for producing field
 * definitions and instances.
 */
public interface IFieldBuilder extends IModelBuilder<IFieldBuilder> {

  /**
   * Create a new builder using the provided mocking context.
   *
   * @return the new builder
   */
  @NonNull
  static IFieldBuilder builder() {
    return new FieldBuilder().reset();
  }

  /**
   * Apply the provided data type adapter to built fields.
   *
   * @param dataTypeAdapter
   *          the data type adapter to use
   * @return this builder
   */
  IFieldBuilder dataTypeAdapter(@NonNull IDataTypeAdapter<?> dataTypeAdapter);

  /**
   * Apply the provided data type adapter to built fields.
   *
   * @param defaultValue
   *          the default value to use
   * @return this builder
   */
  IFieldBuilder defaultValue(@NonNull Object defaultValue);

  /**
   * Build a mocked field instance, based on a mocked definition, as a child of
   * the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @return the new mocked instance
   */
  @Override
  @NonNull
  IFieldInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent);

  /**
   * Build a mocked field instance, using the provided definition, as a child of
   * the provided parent.
   *
   * @param parent
   *          the parent containing the new instance
   * @param definition
   *          the definition to base the instance on
   * @return the new mocked instance
   */
  @NonNull
  IFieldInstanceAbsolute toInstance(@NonNull IAssemblyDefinition parent, @NonNull IFieldDefinition definition);

  /**
   * Build a mocked field definition.
   *
   * @return the new mocked definition
   */
  @NonNull
  IFieldDefinition toDefinition();
}
