/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.AnnotationSpec;
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
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Abstract base class for generating Java property code including fields,
 * getters, and setters with appropriate null-safety annotations.
 *
 * <h2>Null-Safety Annotation Contract</h2>
 *
 * <p>
 * Generated getters and setters receive null-safety annotations based on two
 * factors: whether the property is required and whether it is a collection.
 *
 * <h3>Getter Annotations</h3>
 * <ul>
 * <li>{@code @NonNull} - Collection properties (lazy initialized) or required
 * properties</li>
 * <li>{@code @Nullable} - Optional non-collection properties</li>
 * </ul>
 *
 * <h3>Setter Annotations</h3>
 * <ul>
 * <li>{@code @NonNull} - Collection properties or required properties</li>
 * <li>{@code @Nullable} - Optional non-collection properties</li>
 * </ul>
 *
 * <h2>Collection Handling</h2>
 *
 * <p>
 * Collection properties (where {@link #isCollectionType()} returns
 * {@code true}) use lazy initialization in their getters. The getter checks if
 * the field is null and initializes it with a new collection instance from
 * {@link #getCollectionImplementationClass()} before returning. This ensures
 * collection getters never return null, allowing them to be annotated
 * {@code @NonNull}.
 *
 * <p>
 * <strong>Contract:</strong> Subclasses must ensure that
 * {@link #isCollectionType()} and {@link #getCollectionImplementationClass()}
 * are consistent: {@code isCollectionType()} returns {@code true} if and only
 * if {@code getCollectionImplementationClass()} returns non-null.
 *
 * @param <PARENT>
 *          the type of the parent definition type info
 * @see IPropertyTypeInfo#isCollectionType()
 * @see IPropertyTypeInfo#getCollectionImplementationClass()
 */
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

  /**
   * Build getter and setter methods for this property.
   *
   * <p>
   * This method generates accessor methods with appropriate null-safety
   * annotations and Javadoc based on the property's characteristics. Collection
   * getters use lazy initialization to ensure they never return null.
   *
   * @param typeBuilder
   *          the class builder to add methods to
   * @param fieldBuilder
   *          the field spec for the backing field
   */
  protected void buildExtraMethods(
      @NonNull TypeSpec.Builder typeBuilder,
      @NonNull FieldSpec fieldBuilder) {

    TypeName javaFieldType = getJavaFieldType();
    String propertyName = getPropertyName();
    {
      Class<?> collectionImplClass = getCollectionImplementationClass();
      // Collections are always @NonNull (lazy initialized), otherwise based on
      // isRequired()
      Class<?> nullAnnotation = collectionImplClass != null || isRequired() ? NonNull.class : Nullable.class;
      MethodSpec.Builder method = MethodSpec.methodBuilder("get" + propertyName)
          .returns(javaFieldType)
          .addAnnotation(AnnotationSpec.builder(nullAnnotation).build())
          .addModifiers(Modifier.PUBLIC);
      assert method != null;
      buildGetterJavadoc(method);

      if (collectionImplClass != null) {
        // Use lazy initialization for collections
        method.beginControlFlow("if ($N == null)", fieldBuilder)
            .addStatement("$N = new $T<>()", fieldBuilder, collectionImplClass)
            .endControlFlow();
      }
      method.addStatement("return $N", fieldBuilder);
      typeBuilder.addMethod(method.build());
    }

    {
      // Add null-safety annotation to setter parameter
      // Collections get @NonNull (lazy initialized), required properties get @NonNull
      ParameterSpec.Builder paramBuilder = ParameterSpec.builder(javaFieldType, "value");
      Class<?> paramAnnotation = isCollectionType() || isRequired() ? NonNull.class : Nullable.class;
      paramBuilder.addAnnotation(AnnotationSpec.builder(paramAnnotation).build());
      ParameterSpec valueParam = paramBuilder.build();
      MethodSpec.Builder method = MethodSpec.methodBuilder("set" + propertyName)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(valueParam);
      assert method != null;
      buildSetterJavadoc(method, "value");
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
