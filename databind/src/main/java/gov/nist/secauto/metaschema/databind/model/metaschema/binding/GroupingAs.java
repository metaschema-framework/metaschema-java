/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValue;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValues;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings({
    "PMD.DataClass",
    "PMD.FieldNamingConventions"
})
@MetaschemaAssembly(
    formalName = "Group As",
    name = "group-as",
    moduleClass = MetaschemaModelModule.class)
public class GroupingAs implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Grouping Name",
      name = "name",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _name;

  @BoundFlag(
      formalName = "In JSON Grouping Syntax",
      name = "in-json",
      defaultValue = "SINGLETON_OR_ARRAY",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
          @AllowedValue(value = "ARRAY", description = "Always use an array."),
          @AllowedValue(value = "SINGLETON_OR_ARRAY",
              description = "Produce a singleton for a single member or an array for multiple members."),
          @AllowedValue(value = "BY_KEY",
              description = "For any group of one or more members, produce an object with properties for each member, using a designated flag for their property name values, which must be distinct.") })))
  private String _inJson;

  @BoundFlag(
      formalName = "In XML Grouping Syntax",
      name = "in-xml",
      defaultValue = "UNGROUPED",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "GROUPED", description = "Use a wrapper element."),
              @AllowedValue(value = "UNGROUPED", description = "Do not use a wrapper element.") })))
  private String _inXml;

  public GroupingAs() {
    this(null);
  }

  public GroupingAs(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public String getName() {
    return _name;
  }

  public void setName(String value) {
    _name = value;
  }

  public String getInJson() {
    return _inJson;
  }

  public void setInJson(String value) {
    _inJson = value;
  }

  public String getInXml() {
    return _inXml;
  }

  public void setInXml(String value) {
    _inXml = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
