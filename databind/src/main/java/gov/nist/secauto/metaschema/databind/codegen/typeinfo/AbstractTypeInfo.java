/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.ClassUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractTypeInfo<PARENT extends IDefinitionTypeInfo> implements ITypeInfo {
  @NonNull
  private final PARENT parentDefinition;
  private String propertyName;
  private String fieldName;

  protected AbstractTypeInfo(@NonNull PARENT parentDefinition) {
    this.parentDefinition = parentDefinition;
  }

  @Override
  @NonNull
  public PARENT getParentTypeInfo() {
    return parentDefinition;
  }

  /**
   * The property name of the instance, which must be unique within the class.
   *
   * @return the name
   */
  @Override
  @NonNull
  public String getPropertyName() {
    synchronized (this) {
      if (this.propertyName == null) {
        String name = ClassUtils.toPropertyName(getBaseName());
        IDefinitionTypeInfo parent = getParentTypeInfo();

        // first check if a property already exists with the same name
        this.propertyName = parent.getTypeResolver().getPropertyName(parent, name);
      }
      return ObjectUtils.notNull(this.propertyName);
    }
  }

  /**
   * Gets the name of the Java field for this property item.
   *
   * @return the Java field name
   */
  @Override
  @NonNull
  public final String getJavaFieldName() {
    synchronized (this) {
      if (this.fieldName == null) {
        this.fieldName = "_" + ClassUtils.toVariableName(getPropertyName());
      }
      return ObjectUtils.notNull(this.fieldName);
    }
  }
}
