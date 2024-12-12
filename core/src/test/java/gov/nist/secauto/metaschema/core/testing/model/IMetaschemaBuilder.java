/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema module-based model builder.
 *
 * @param <T>
 *          the Java type of the implementation of this builder
 */
public interface IMetaschemaBuilder<T extends IMetaschemaBuilder<T>> {
  /**
   * Reset the builder back to a default state.
   *
   * @return this builder
   */
  @NonNull
  T reset();

  /**
   * Apply the provided namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @NonNull
  T namespace(@NonNull String name);

  /**
   * Apply the provided namespace for use by this builder.
   *
   * @param name
   *          the namespace to use
   * @return this builder
   */
  @NonNull
  default T namespace(@NonNull URI name) {
    return namespace(ObjectUtils.notNull(name.toASCIIString()));
  }

  /**
   * Apply the provided name for use by this builder.
   *
   * @param name
   *          the name to use
   * @return this builder
   */
  @NonNull
  T name(@NonNull String name);

  /**
   * Apply the provided qualified name for use by this builder.
   *
   * @param qname
   *          the qualified name to use
   * @return this builder
   */
  @NonNull
  T qname(@NonNull IEnhancedQName qname);

  /**
   * Apply the provided source information for use by this builder.
   *
   * @param source
   *          the source information
   * @return this builder
   */
  @NonNull
  T source(@NonNull ISource source);

}
