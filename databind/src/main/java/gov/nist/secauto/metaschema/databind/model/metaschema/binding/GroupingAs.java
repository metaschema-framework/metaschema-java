/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
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

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.GroupingAs}
   * instance with no metadata.
   */
  public GroupingAs() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.GroupingAs}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public GroupingAs(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the grouping Name.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the grouping Name.
   *
   * @param value
   *          the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
  }

  /**
   * Get the in JSON Grouping Syntax.
   *
   * @return the in-json value, or {@code null} if not set
   */
  @Nullable
  public String getInJson() {
    return _inJson;
  }

  /**
   * Set the in JSON Grouping Syntax.
   *
   * @param value
   *          the in-json value to set
   */
  public void setInJson(@Nullable String value) {
    _inJson = value;
  }

  /**
   * Get the in XML Grouping Syntax.
   *
   * @return the in-xml value, or {@code null} if not set
   */
  @Nullable
  public String getInXml() {
    return _inXml;
  }

  /**
   * Set the in XML Grouping Syntax.
   *
   * @param value
   *          the in-xml value to set
   */
  public void setInXml(@Nullable String value) {
    _inXml = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
