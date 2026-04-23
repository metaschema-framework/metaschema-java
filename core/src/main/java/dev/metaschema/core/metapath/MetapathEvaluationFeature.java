/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import dev.metaschema.core.configuration.AbstractConfigurationFeature;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a mechanism to configure Metapath evaluation settings.
 *
 * @param <V>
 *          the feature value Java type
 */
public final class MetapathEvaluationFeature<V>
    extends AbstractConfigurationFeature<V> {
  /**
   * If enabled, evaluate <a href=
   * "https://www.w3.org/TR/xpath-31/#id-filter-expression">predicates</a>,
   * otherwise skip evaluating them.
   */
  @NonNull
  public static final MetapathEvaluationFeature<Boolean> METAPATH_EVALUATE_PREDICATES
      = new MetapathEvaluationFeature<>("evaluate-predicates", Boolean.class, true);

  /**
   * If enabled, atomization of a node item that has no associated typed value
   * (for example, a flag or field node reached while walking a module definition
   * rather than an instance document) yields a {@code null} atomic value instead
   * of raising
   * {@link dev.metaschema.core.metapath.function.InvalidTypeFunctionException}
   * with code
   * {@link dev.metaschema.core.metapath.function.InvalidTypeFunctionException#NODE_HAS_NO_TYPED_VALUE}.
   * <p>
   * This is intended for visitors and tools that traverse an
   * {@link dev.metaschema.core.metapath.item.node.IModuleNodeItem} graph and need
   * downstream function calls (for example {@code fn:resolve-uri} or
   * {@code fn:doc}) to degrade gracefully when they receive a no-data flag rather
   * than an instance value.
   */
  @NonNull
  public static final MetapathEvaluationFeature<Boolean> METAPATH_ATOMIZE_NO_DATA_AS_EMPTY
      = new MetapathEvaluationFeature<>("atomize-no-data-as-empty", Boolean.class, false);

  private MetapathEvaluationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
