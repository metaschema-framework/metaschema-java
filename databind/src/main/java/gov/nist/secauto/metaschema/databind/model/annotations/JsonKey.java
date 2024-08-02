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
 * Indicates that the target of this annotation is a Java property that is to be
 * used as the JSON or YAML property/item name in for a collection of similar
 * objects/items.
 */
// TODO: remove this and move this to MetaschemaField/MetaschemaAssembly
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface JsonKey {
  // no fields
}
