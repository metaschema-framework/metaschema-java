/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Set;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.model.IAnyInstance;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;
import dev.metaschema.databind.model.annotations.BoundAny;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Type information for generating a {@link BoundAny}-annotated field in a Java
 * class corresponding to an {@link IAnyInstance} in a Metaschema assembly
 * model.
 * <p>
 * The generated field captures unmodeled content using the {@link IAnyContent}
 * interface, allowing round-trip preservation of arbitrary properties not
 * defined by the Metaschema model.
 */
public class AnyInstanceTypeInfoImpl
    extends AbstractInstanceTypeInfo<IAnyInstance, IAssemblyDefinitionTypeInfo> {

  /**
   * Constructs a new type information object for an any instance.
   *
   * @param instance
   *          the any instance
   * @param parentDefinition
   *          the type information for the parent assembly definition containing
   *          this instance
   */
  public AnyInstanceTypeInfoImpl(
      @NonNull IAnyInstance instance,
      @NonNull IAssemblyDefinitionTypeInfo parentDefinition) {
    super(instance, parentDefinition);
  }

  @Override
  public String getBaseName() {
    return "any";
  }

  @Override
  public boolean isRequired() {
    return false;
  }

  @Override
  public TypeName getJavaFieldType() {
    return ClassName.get(IAnyContent.class);
  }

  @Override
  public void buildFieldJavadoc(FieldSpec.Builder builder) {
    builder.addJavadoc("Captures unmodeled content not defined by the Metaschema model.\n");
  }

  @Override
  public void buildGetterJavadoc(MethodSpec.Builder builder) {
    builder.addJavadoc("Get the unmodeled content.\n");
    builder.addJavadoc("\n");
    builder.addJavadoc("@return the unmodeled content, or {@code null} if not set\n");
  }

  @Override
  public void buildSetterJavadoc(MethodSpec.Builder builder, String paramName) {
    builder.addJavadoc("Set the unmodeled content.\n");
    builder.addJavadoc("\n");
    builder.addJavadoc("@param $L\n", paramName);
    builder.addJavadoc("          the unmodeled content to set\n");
  }

  @Override
  public Set<IModelDefinition> buildField(
      TypeSpec.Builder typeBuilder,
      FieldSpec.Builder fieldBuilder) {
    super.buildField(typeBuilder, fieldBuilder);
    fieldBuilder.addAnnotation(BoundAny.class);
    return CollectionUtil.emptySet();
  }
}
