/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.configuration.AbstractConfigurationFeature;

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

  private MetapathEvaluationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
