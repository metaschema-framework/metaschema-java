/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

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

@SuppressWarnings({
    "PMD.DataClass",
    "PMD.FieldNamingConventions",
    "null"
})
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

  public ConstraintValueEnum() {
    this(null);
  }

  public ConstraintValueEnum(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  @Override
  public String getValue() {
    return _value;
  }

  public void setValue(String value) {
    _value = value;
  }

  @Override
  public String getDeprecated() {
    return _deprecated;
  }

  public void setDeprecated(String value) {
    _deprecated = value;
  }

  @Override
  public MarkupLine getRemark() {
    return _remark;
  }

  public void setRemark(MarkupLine value) {
    _remark = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
