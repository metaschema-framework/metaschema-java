/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.impl.AnnotationGenerator;
import dev.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.JsonFieldValueKeyFlag;
import dev.metaschema.databind.model.annotations.JsonKey;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FlagInstanceTypeInfoImpl
    extends AbstractInstanceTypeInfo<IFlagInstance, IDefinitionTypeInfo>
    implements IFlagInstanceTypeInfo {
  /**
   * Constructs a new type information object for a flag instance.
   *
   * @param instance
   *          the flag instance
   * @param parentDefinition
   *          the type information for the parent definition containing this
   *          instance
   */
  public FlagInstanceTypeInfoImpl(@NonNull IFlagInstance instance, @NonNull IDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  public String getBaseName() {
    return getInstance().getEffectiveName();
  }

  @Override
  public boolean isRequired() {
    return getInstance().isRequired();
  }

  @Override
  public TypeName getJavaFieldType() {
    return ObjectUtils.notNull(ClassName.get(getInstance().getDefinition().getJavaTypeAdapter().getJavaClass()));
  }

  @SuppressWarnings("PMD.CyclomaticComplexity") // acceptable
  @Override
  public Set<IModelDefinition> buildField(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder) {
    super.buildField(typeBuilder, fieldBuilder);

    IFlagInstance instance = getInstance();

    fieldBuilder.addAnnotation(buildBoundFlagAnnotation(instance).build());

    IModelDefinition parent = instance.getContainingDefinition();
    IFlagInstance jsonKey = parent.getJsonKey();
    if (instance.equals(jsonKey)) {
      fieldBuilder.addAnnotation(JsonKey.class);
    }

    if (parent instanceof IFieldDefinition) {
      IFieldDefinition parentField = (IFieldDefinition) parent;

      if (parentField.hasJsonValueKeyFlagInstance() && instance.equals(parentField.getJsonValueKeyFlagInstance())) {
        fieldBuilder.addAnnotation(JsonFieldValueKeyFlag.class);
      }
    }
    return CollectionUtil.emptySet();
  }

  private static AnnotationSpec.Builder buildBoundFlagAnnotation(@NonNull IFlagInstance instance) {
    AnnotationSpec.Builder annotation = AnnotationSpec.builder(BoundFlag.class);

    String formalName = instance.getEffectiveFormalName();
    if (formalName != null) {
      annotation.addMember("formalName", "$S", formalName);
    }

    MarkupLine description = instance.getEffectiveDescription();
    if (description != null) {
      annotation.addMember("description", "$S", description.toMarkdown());
    }

    annotation.addMember("name", "$S", instance.getEffectiveName());

    Integer index = instance.getEffectiveIndex();
    if (index != null) {
      annotation.addMember("useIndex", "$L", index);
    }

    // TODO: handle flag namespace as a prefix

    IFlagDefinition definition = instance.getDefinition();

    IDataTypeAdapter<?> valueDataType = definition.getJavaTypeAdapter();
    Object defaultValue = instance.getEffectiveDefaultValue();
    if (defaultValue != null) {
      annotation.addMember("defaultValue", "$S", valueDataType.asString(defaultValue));
    }

    if (instance.isRequired()) {
      annotation.addMember("required", "$L", true);
    }
    annotation.addMember("typeAdapter", "$T.class", valueDataType.getClass());

    MarkupMultiline remarks = instance.getRemarks();
    if (remarks != null) {
      annotation.addMember("remarks", "$S", remarks.toMarkdown());
    }

    AnnotationGenerator.buildValueConstraints(annotation, definition);

    return annotation;
  }
}
