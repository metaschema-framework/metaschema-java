/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLineAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import gov.nist.secauto.metaschema.databind.model.metaschema.impl.AbstractAllowedValue;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaField(
    formalName = "Allowed Value Enumeration",
    name = "constraint-value-enum",
    moduleClass = MetaschemaModelModule.class)
public class ConstraintValueEnum
    extends AbstractAllowedValue
    implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Allowed Value Enumeration Value",
      name = "value",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _value;

  @BoundFlag(
      formalName = "Allowed Value Deprecation Version",
      name = "deprecated",
      typeAdapter = StringAdapter.class)
  private String _deprecated;

  @BoundFieldValue(
      valueKeyName = "remark",
      typeAdapter = MarkupLineAdapter.class)
  private MarkupLine _remark;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.ConstraintValueEnum}
   * instance with no metadata.
   */
  public ConstraintValueEnum() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.ConstraintValueEnum}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public ConstraintValueEnum(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the allowed Value Enumeration Value.
   *
   * @return the value value
   */
  @NonNull
  public String getValue() {
    return _value;
  }

  /**
   * Set the allowed Value Enumeration Value.
   *
   * @param value
   *          the value value to set
   */
  public void setValue(@NonNull String value) {
    _value = value;
  }

  /**
   * Get the allowed Value Deprecation Version.
   *
   * @return the deprecated value, or {@code null} if not set
   */
  @Nullable
  public String getDeprecated() {
    return _deprecated;
  }

  /**
   * Set the allowed Value Deprecation Version.
   *
   * @param value
   *          the deprecated value to set
   */
  public void setDeprecated(@Nullable String value) {
    _deprecated = value;
  }

  @Nullable
  public MarkupLine getRemark() {
    return _remark;
  }

  public void setRemark(@Nullable MarkupLine value) {
    _remark = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
