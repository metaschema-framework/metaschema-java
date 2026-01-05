/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import dev.metaschema.core.model.IModelDefinition;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IPropertyTypeInfo extends ITypeInfo {
  /**
   * Determines if this property is unconditionally required to have a value.
   *
   * <p>
   * For flags, this checks
   * {@link dev.metaschema.core.model.IFlagInstance#isRequired()}. For model
   * instances, this is based on minimum occurrence constraints.
   *
   * <p>
   * <strong>Choice blocks:</strong> Properties inside Metaschema choice blocks
   * always return {@code false}, regardless of their {@code min-occurs} value.
   * The requirement is conditional on the choice branch being taken, so for
   * null-safety purposes they are treated as optional.
   *
   * @return {@code true} if a value is unconditionally required, or {@code false}
   *         otherwise
   */
  default boolean isRequired() {
    return false;
  }

  /**
   * Determines if this property represents a collection (list or map).
   *
   * <p>
   * Collections are model instances with maxOccurs greater than 1 or unbounded.
   * Collection getters use lazy initialization and are always {@code @NonNull}.
   *
   * @return {@code true} if this property is a collection, or {@code false}
   *         otherwise
   */
  default boolean isCollectionType() {
    return false;
  }

  /**
   * Get the implementation class to use for lazy initialization of collections.
   *
   * <p>
   * For list-based collections, this returns {@link java.util.LinkedList}. For
   * map-based (keyed) collections, this returns {@link java.util.LinkedHashMap}.
   * For non-collection properties, this returns {@code null}.
   *
   * <p>
   * <strong>Contract:</strong> This method must return non-null if and only if
   * {@link #isCollectionType()} returns {@code true}. Implementations must
   * maintain this invariant.
   *
   * @return the collection implementation class, or {@code null} if not a
   *         collection
   * @see #isCollectionType()
   */
  @Nullable
  default Class<?> getCollectionImplementationClass() {
    return null;
  }

  /**
   * Generate the Java field associated with this property.
   *
   * @param builder
   *          the containing class builder
   * @return the set of definitions used by this field
   */
  Set<? extends IModelDefinition> build(@NonNull TypeSpec.Builder builder);

  /**
   * Add the Javadoc for the current property's field.
   *
   * @param builder
   *          the field builder to annotate with the Javadoc
   */
  default void buildFieldJavadoc(@NonNull FieldSpec.Builder builder) {
    // do nothing by default
  }

  /**
   * Add the Javadoc for the current property's getter method.
   *
   * @param builder
   *          the method builder to annotate with the Javadoc
   */
  default void buildGetterJavadoc(@NonNull MethodSpec.Builder builder) {
    // do nothing by default
  }

  /**
   * Add the Javadoc for the current property's setter method.
   *
   * @param builder
   *          the method builder to annotate with the Javadoc
   * @param paramName
   *          the name of the parameter
   */
  default void buildSetterJavadoc(@NonNull MethodSpec.Builder builder, @NonNull String paramName) {
    // do nothing by default
  }
}
