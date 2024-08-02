/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the target of this annotation is a flag whose value should be
 * the property/item name of the field's value in JSON or YAML.
 * <p>
 * Use of this annotation is mutually exclusive with the {@link BoundFieldValue}
 * annotation.
 */
// TODO: remove this and move this to MetaschemaField
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface JsonFieldValueKeyFlag {
  // no fields
}
