/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.MetaschemaConstants;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides commonly used XML constants related to an XML-based Metaschema
 * module.
 */
@SuppressWarnings("PMD.DataClass")
public final class XmlModuleConstants {
  @NonNull
  public static final QName ASSEMBLY_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "assembly");
  @NonNull
  public static final QName DEFINE_ASSEMBLY_QNAME
      = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-assembly");
  @NonNull
  public static final QName FIELD_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "field");
  @NonNull
  public static final QName DEFINE_FIELD_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-field");
  @NonNull
  public static final QName FLAG_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "flag");
  @NonNull
  public static final QName DEFINE_FLAG_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "define-flag");
  @NonNull
  public static final QName CHOICE_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "choice");
  @NonNull
  public static final QName CHOICE_GROUP_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "choice-group");
  @NonNull
  public static final QName MODEL_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "model");

  @NonNull
  public static final QName ALLOWED_VALUES_CONSTRAINT_QNAME
      = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "allowed-values");

  @NonNull
  public static final QName INDEX_HAS_KEY_CONSTRAINT_QNAME
      = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "index-has-key");

  @NonNull
  public static final QName MATCHES_CONSTRAINT_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "matches");

  @NonNull
  public static final QName EXPECT_CONSTRAINT_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "expect");

  @NonNull
  public static final QName INDEX_CONSTRAINT_QNAME = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "index");

  @NonNull
  public static final QName IS_UNIQUE_CONSTRAINT_QNAME
      = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "is-unique");

  @NonNull
  public static final QName HAS_CARDINALITY_CONSTRAINT_QNAME
      = new QName(MetaschemaConstants.METASCHEMA_NAMESPACE, "has-cardinality");

  private XmlModuleConstants() {
    // disable construction
  }
}
