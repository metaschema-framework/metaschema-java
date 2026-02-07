/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a field of type {@link dev.metaschema.core.model.IAnyContent} on a
 * bound class to receive unmodeled content from assemblies that declare
 * {@code <any/>} in their model.
 *
 * <p>
 * During deserialization, content not matching any declared model instance is
 * captured into this field. During serialization, captured content is written
 * back after all declared model instances.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface BoundAny {
}
