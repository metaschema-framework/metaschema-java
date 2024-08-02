/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Identifies a field on a class annotated with the {@link MetaschemaField}
 * annotation as the Module field's value.
 */
// TODO: how are index names handled here?
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface BoundFieldValue {
  /**
   * The Module data type adapter for the field's value.
   *
   * @return the data type adapter
   */
  @NonNull
  Class<? extends IDataTypeAdapter<?>> typeAdapter() default NullJavaTypeAdapter.class;

  /**
   * The default value of the field represented as a string.
   * <p>
   * The value {@link ModelUtil#NULL_VALUE} is used to indicate if no default
   * value is provided.
   *
   * @return the default value
   */
  @NonNull
  String defaultValue() default ModelUtil.NULL_VALUE;

  /**
   * The name of the JSON property that contains the field's value. If this value
   * is provided, the the name will be used as the property name. Otherwise, the
   * property name will default to a value defined by the data type.
   * <p>
   * Use of this annotation is mutually exclusive with the
   * {@link JsonFieldValueKeyFlag} annotation.
   *
   * @return the name
   */
  @NonNull
  String valueKeyName() default ModelUtil.NO_STRING_VALUE;
}
