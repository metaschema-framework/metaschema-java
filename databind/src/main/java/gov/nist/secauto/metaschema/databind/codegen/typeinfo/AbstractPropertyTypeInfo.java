/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;

import java.util.Set;

import javax.lang.model.element.Modifier;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractPropertyTypeInfo<PARENT extends IDefinitionTypeInfo>
    extends AbstractTypeInfo<PARENT>
    implements IPropertyTypeInfo {

  /**
   * Construct a new type information for a Java property.
   *
   * @param parentDefinition
   *          the definition containing the data this property is based on
   */
  protected AbstractPropertyTypeInfo(@NonNull PARENT parentDefinition) {
    super(parentDefinition);
  }

  @Override
  public Set<IModelDefinition> build(@NonNull TypeSpec.Builder builder) {

    TypeName javaFieldType = getJavaFieldType();
    FieldSpec.Builder field = FieldSpec.builder(javaFieldType, getJavaFieldName())
        .addModifiers(Modifier.PRIVATE);
    assert field != null;

    final Set<IModelDefinition> retval = buildField(builder, field);

    FieldSpec valueField = ObjectUtils.notNull(field.build());
    builder.addField(valueField);

    buildExtraMethods(builder, valueField);
    return retval;
  }

  protected void buildExtraMethods(
      @NonNull TypeSpec.Builder typeBuilder,
      @NonNull FieldSpec fieldBuilder) {

    TypeName javaFieldType = getJavaFieldType();
    String propertyName = getPropertyName();
    {
      MethodSpec.Builder method = MethodSpec.methodBuilder("get" + propertyName)
          .returns(javaFieldType)
          .addModifiers(Modifier.PUBLIC);
      assert method != null;
      method.addStatement("return $N", fieldBuilder);
      typeBuilder.addMethod(method.build());
    }

    {
      ParameterSpec valueParam = ParameterSpec.builder(javaFieldType, "value").build();
      MethodSpec.Builder method = MethodSpec.methodBuilder("set" + propertyName)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(valueParam);
      assert method != null;
      method.addStatement("$N = $N", fieldBuilder, valueParam);
      typeBuilder.addMethod(method.build());
    }
  }

  /**
   * Generate the Java field associated with this property.
   *
   * @param typeBuilder
   *          the class builder the field is on
   * @param fieldBuilder
   *          the field builder
   * @return the set of definitions used by this field
   */
  protected Set<IModelDefinition> buildField(
      @NonNull TypeSpec.Builder typeBuilder,
      @NonNull FieldSpec.Builder fieldBuilder) {
    buildFieldJavadoc(fieldBuilder);
    return CollectionUtil.emptySet();
  }
}
