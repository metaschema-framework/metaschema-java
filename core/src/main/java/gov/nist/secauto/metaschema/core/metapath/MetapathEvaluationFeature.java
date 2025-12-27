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

  /**
   * The maximum number of documents to cache in the document loader cache.
   * <p>
   * Documents are cached to avoid redundant loading when the same URI is
   * referenced multiple times during evaluation.
   */
  @NonNull
  public static final MetapathEvaluationFeature<Integer> DOCUMENT_CACHE_MAXIMUM_SIZE
      = new MetapathEvaluationFeature<>("document-cache-maximum-size", Integer.class, 1000);

  /**
   * The number of minutes after last access before a cached document expires.
   * <p>
   * This helps prevent memory exhaustion in long-running contexts by evicting
   * documents that haven't been accessed recently.
   */
  @NonNull
  public static final MetapathEvaluationFeature<Integer> DOCUMENT_CACHE_EXPIRE_AFTER_ACCESS_MINUTES
      = new MetapathEvaluationFeature<>("document-cache-expire-after-access-minutes", Integer.class, 10);

  private MetapathEvaluationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
