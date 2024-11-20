/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.MetaschemaConstants;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides commonly used XML constants related to an XML-based Metaschema
 * module.
 */
// FIXME: integrate these into the model interfaces or generalize these outside
// of XML
@SuppressWarnings("PMD.DataClass")
public final class XmlModuleConstants {
  @NonNull
  public static final IEnhancedQName ASSEMBLY_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "assembly");
  @NonNull
  public static final IEnhancedQName DEFINE_ASSEMBLY_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-assembly");
  @NonNull
  public static final IEnhancedQName FIELD_QNAME = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "field");
  @NonNull
  public static final IEnhancedQName DEFINE_FIELD_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-field");
  @NonNull
  public static final IEnhancedQName FLAG_QNAME = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "flag");
  @NonNull
  public static final IEnhancedQName DEFINE_FLAG_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-flag");
  @NonNull
  public static final IEnhancedQName CHOICE_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "choice");
  @NonNull
  public static final IEnhancedQName CHOICE_GROUP_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "choice-group");
  @NonNull
  public static final IEnhancedQName MODEL_QNAME = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "model");

  @NonNull
  public static final IEnhancedQName ALLOWED_VALUES_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "allowed-values");

  @NonNull
  public static final IEnhancedQName INDEX_HAS_KEY_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "index-has-key");

  @NonNull
  public static final IEnhancedQName MATCHES_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "matches");

  @NonNull
  public static final IEnhancedQName EXPECT_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "expect");

  @NonNull
  public static final IEnhancedQName INDEX_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "index");

  @NonNull
  public static final IEnhancedQName IS_UNIQUE_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "is-unique");

  @NonNull
  public static final IEnhancedQName HAS_CARDINALITY_CONSTRAINT_QNAME
      = IEnhancedQName.of(MetaschemaConstants.METASCHEMA_NAMESPACE, "has-cardinality");

  private XmlModuleConstants() {
    // disable construction
  }
}
