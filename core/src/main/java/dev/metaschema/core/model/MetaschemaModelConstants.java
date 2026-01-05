/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import dev.metaschema.core.MetaschemaConstants;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.ICardinalityConstraint;
import dev.metaschema.core.model.constraint.IExpectConstraint;
import dev.metaschema.core.model.constraint.IIndexConstraint;
import dev.metaschema.core.model.constraint.IIndexHasKeyConstraint;
import dev.metaschema.core.model.constraint.IMatchesConstraint;
import dev.metaschema.core.model.constraint.IReportConstraint;
import dev.metaschema.core.model.constraint.IUniqueConstraint;
import dev.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides commonly used QName constants for Metaschema model elements.
 */
@SuppressWarnings("PMD.DataClass")
public final class MetaschemaModelConstants {
  /**
   * The name of an {@link IAssemblyInstance} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName ASSEMBLY_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "assembly");

  /**
   * The name of an inline {@link IAssemblyDefinition} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName DEFINE_ASSEMBLY_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-assembly");

  /**
   * The name of an {@link IFieldInstance} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName FIELD_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "field");

  /**
   * The name of an inline {@link IFieldDefinition} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName DEFINE_FIELD_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-field");

  /**
   * The name of an {@link IFlagInstance} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName FLAG_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "flag");

  /**
   * The name of an inline {@link IFlagDefinition} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName DEFINE_FLAG_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-flag");

  /**
   * The name of an {@link IChoiceInstance} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName CHOICE_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "choice");

  /**
   * The name of an {@link IChoiceGroupInstance} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName CHOICE_GROUP_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "choice-group");

  /**
   * The name of the element that identifies the model of an
   * {@link IAssemblyDefinition} in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName MODEL_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "model");

  /**
   * The name of an {@link IAllowedValuesConstraint} constraint in the Metaschema
   * model.
   */
  @NonNull
  public static final IEnhancedQName ALLOWED_VALUES_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "allowed-values");

  /**
   * The name of an {@link IIndexHasKeyConstraint} constraint in the Metaschema
   * model.
   */
  @NonNull
  public static final IEnhancedQName INDEX_HAS_KEY_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "index-has-key");

  /**
   * The name of an {@link IMatchesConstraint} constraint in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName MATCHES_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "matches");

  /**
   * The name of an {@link IExpectConstraint} constraint in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName EXPECT_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "expect");

  /**
   * The name of an {@link IReportConstraint} constraint in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName REPORT_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "report");

  /**
   * The name of an {@link IIndexConstraint} constraint in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName INDEX_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "index");

  /**
   * The name of an {@link IUniqueConstraint} constraint in the Metaschema model.
   */
  @NonNull
  public static final IEnhancedQName IS_UNIQUE_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "is-unique");

  /**
   * The name of an {@link ICardinalityConstraint} constraint in the Metaschema
   * model.
   */
  @NonNull
  public static final IEnhancedQName HAS_CARDINALITY_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "has-cardinality");

  private MetaschemaModelConstants() {
    // disable construction
  }
}
