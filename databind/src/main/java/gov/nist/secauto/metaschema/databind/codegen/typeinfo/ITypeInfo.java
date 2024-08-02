/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.TypeName;

import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ITypeInfo {
  @NonNull
  IDefinitionTypeInfo getParentTypeInfo();

  /**
   * Get the name to use for the property. If the property is a collection type,
   * then this will be the group-as name, else this will be the use name or the
   * name if the use name is not set.
   *
   * @return the name
   */
  @NonNull
  String getBaseName();

  /**
   * The name to use for Java constructs that refer to the item. This is used for
   * when a field is collection-based and there is a need to refer to a single
   * item, such as in an add/remove method name.
   *
   * @return the item base name
   */
  @NonNull
  default String getItemBaseName() {
    return getBaseName();
  }

  /**
   * Get the Java property name for the property.
   *
   * @return the Java property name
   */
  @NonNull
  String getPropertyName();

  /**
   * Gets the name of the Java field for this property.
   *
   * @return the Java field name
   */
  @NonNull
  String getJavaFieldName();

  /**
   * Gets the type of the associated Java field for the property.
   *
   * @return the Java type for the field
   */
  @NonNull
  TypeName getJavaFieldType();

  /**
   * Gets the type of the property's item.
   *
   * @return the Java type for the item
   */
  @NonNull
  default TypeName getJavaItemType() {
    return getJavaFieldType();
  }
}
