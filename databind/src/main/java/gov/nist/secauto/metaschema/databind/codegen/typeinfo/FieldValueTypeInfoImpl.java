/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IFieldDefinitionTypeInfo;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

class FieldValueTypeInfoImpl
    extends AbstractPropertyTypeInfo<IFieldDefinitionTypeInfo>
    implements IFieldValueTypeInfo {

  public FieldValueTypeInfoImpl(@NonNull IFieldDefinitionTypeInfo parentDefinition) {
    super(parentDefinition);
  }

  @Override
  public String getBaseName() {
    String valueKeyName = getParentTypeInfo().getDefinition().getJsonValueKeyName();
    return valueKeyName == null ? "value" : valueKeyName;
  }

  @SuppressWarnings("null")
  @Override
  public TypeName getJavaFieldType() {
    return ClassName.get(
        getParentTypeInfo().getDefinition().getJavaTypeAdapter().getJavaClass());
  }

  @Override
  protected Set<IModelDefinition> buildField(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder) {

    IFieldDefinition definition = getParentTypeInfo().getDefinition();
    AnnotationSpec.Builder fieldValue = AnnotationSpec.builder(BoundFieldValue.class);

    IDataTypeAdapter<?> valueDataType = definition.getJavaTypeAdapter();

    // a field object always has a single value
    if (!definition.hasJsonValueKeyFlagInstance()) {
      fieldValue.addMember("valueKeyName", "$S", definition.getEffectiveJsonValueKeyName());
    } // else do nothing, the annotation will be on the flag

    if (!MetaschemaDataTypeProvider.DEFAULT_DATA_TYPE.equals(valueDataType)) {
      fieldValue.addMember("typeAdapter", "$T.class", valueDataType.getClass());
    }

    Object defaultValue = definition.getDefaultValue();
    if (defaultValue != null) {
      fieldValue.addMember("defaultValue", "$S", valueDataType.asString(defaultValue));
    }

    Set<IModelDefinition> retval = super.buildField(typeBuilder, fieldBuilder);

    fieldBuilder.addAnnotation(fieldValue.build());
    return retval;
  }
}
