/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface NsBinding {
  /**
   * The Metapath prefix to bind to.
   *
   * @return the prefix
   */
  @NonNull
  String prefix();

  /**
   * The Metapath namespace URI that is bound to the prefix.
   *
   * @return the prefix
   */
  @NonNull
  String uri();
}
