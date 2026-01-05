/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Identifies that the annotation target is a bound property that participates
 * in a Metaschema choice.
 * <p>
 * A choice represents mutually exclusive alternatives in a Metaschema model.
 * Fields with the same {@link #choiceId()} are part of the same choice and
 * exactly one of them must be provided when the choice is required.
 * <p>
 * This is distinct from {@link BoundChoiceGroup}, which represents a
 * polymorphic collection with a type discriminator.
 * <p>
 * <strong>Adjacency requirement:</strong> All fields with the same
 * {@code choiceId} must be declared consecutively in the class, reflecting the
 * Metaschema model where choice alternatives occupy the same position in the
 * serialization order.
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface BoundChoice {
  /**
   * Identifies which choice this field belongs to.
   * <p>
   * Fields with the same choiceId are mutually exclusive alternatives. The
   * choiceId must be unique within the containing assembly and consistent across
   * all alternatives in the choice.
   *
   * @return the choice identifier
   */
  @NonNull
  String choiceId();
}
