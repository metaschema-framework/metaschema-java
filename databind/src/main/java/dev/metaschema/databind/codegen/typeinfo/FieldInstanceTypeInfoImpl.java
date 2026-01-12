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

import java.util.Set;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.impl.AnnotationGenerator;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import dev.metaschema.databind.model.annotations.BoundField;
import edu.umd.cs.findbugs.annotations.NonNull;

public class FieldInstanceTypeInfoImpl
    extends AbstractNamedModelInstanceTypeInfo<IFieldInstanceAbsolute>
    implements IFieldInstanceTypeInfo {

  /**
   * Constructs a new type information object for a field instance.
   *
   * @param instance
   *          the field instance
   * @param parentDefinition
   *          the type information for the parent assembly definition containing
   *          this instance
   */
  public FieldInstanceTypeInfoImpl(
      @NonNull IFieldInstanceAbsolute instance,
      @NonNull IAssemblyDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  public TypeName getJavaItemType() {
    TypeName retval;
    IFieldInstance fieldInstance = getInstance();
    if (fieldInstance.getDefinition().hasChildren()) {
      retval = super.getJavaItemType();
    } else {
      IDataTypeAdapter<?> dataType = fieldInstance.getDefinition().getJavaTypeAdapter();
      // this is a simple value
      retval = ObjectUtils.notNull(ClassName.get(dataType.getJavaClass()));
    }
    return retval;
  }

  @Override
  protected AnnotationSpec.Builder newBindingAnnotation() {
    return ObjectUtils.notNull(AnnotationSpec.builder(BoundField.class));
  }

  @SuppressWarnings("checkstyle:methodlength")
  @Override
  public Set<IModelDefinition> buildBindingAnnotation(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder,
      AnnotationSpec.Builder annotation) {
    // first build the core attributes
    final Set<IModelDefinition> retval = super.buildBindingAnnotation(typeBuilder, fieldBuilder, annotation);

    IFieldInstance instance = getInstance();

    // next build the field-specific attributes
    if (IFieldInstance.DEFAULT_FIELD_IN_XML_WRAPPED != instance.isInXmlWrapped()) {
      annotation.addMember("inXmlWrapped", "$L", instance.isInXmlWrapped());
    }

    IFieldDefinition definition = instance.getDefinition();
    IDataTypeAdapter<?> adapter = instance.getDefinition().getJavaTypeAdapter();

    Object defaultValue = instance.getDefaultValue();
    if (defaultValue != null) {
      annotation.addMember("defaultValue", "$S", adapter.asString(defaultValue));
    }

    // handle the field value related info
    if (!definition.hasChildren()) {
      // this is a simple field, without flags
      // Always add typeAdapter so the reader knows to treat this as a simple value
      annotation.addMember("typeAdapter", "$T.class", adapter.getClass());
      AnnotationGenerator.buildValueConstraints(annotation, definition);
    }
    return retval;
  }
}
