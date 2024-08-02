/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IFieldDefinitionTypeInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides type information related to the value of a {@link IFieldDefinition}.
 */
public interface IFieldValueTypeInfo extends IPropertyTypeInfo {
  /**
   * Construct a new type info based on the provided parent field definition which
   * contains the field value.
   *
   * @param parentDefinition
   *          the definition associated with the field value type info
   * @return the type info for the definition
   */
  @NonNull
  static IFieldValueTypeInfo newTypeInfo(@NonNull IFieldDefinitionTypeInfo parentDefinition) {
    return new FieldValueTypeInfoImpl(parentDefinition);
  }

  /**
   * Get the type information for this field value's containing field definition.
   *
   * @return the containing field's type information
   */
  @Override
  IFieldDefinitionTypeInfo getParentTypeInfo();
}
