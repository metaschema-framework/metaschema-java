/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.binding.metaschema;

import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLineAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValue;
import gov.nist.secauto.metaschema.databind.model.annotations.AllowedValues;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import gov.nist.secauto.metaschema.databind.model.metaschema.ITargetedConstraintBase;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({
    "PMD.DataClass",
    "PMD.FieldNamingConventions"
})
@MetaschemaAssembly(
    formalName = "Value Matches Constraint",
    name = "targeted-matches-constraint",
    moduleClass = MetaschemaModelModule.class)
public class TargetedMatchesConstraint implements IBoundObject, ITargetedConstraintBase {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Constraint Identifier",
      name = "id",
      typeAdapter = TokenAdapter.class)
  private String _id;

  @BoundFlag(
      formalName = "Constraint Severity Level",
      name = "level",
      defaultValue = "ERROR",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
          @AllowedValue(value = "CRITICAL",
              description = "A violation of the constraint represents a serious fault in the content that will prevent typical use of the content."),
          @AllowedValue(value = "ERROR",
              description = "A violation of the constraint represents a fault in the content. This may include issues around compatibility, integrity, consistency, etc."),
          @AllowedValue(value = "WARNING",
              description = "A violation of the constraint represents a potential issue with the content."),
          @AllowedValue(value = "INFORMATIONAL",
              description = "A violation of the constraint represents a point of interest."),
          @AllowedValue(value = "DEBUG",
              description = "A violation of the constraint represents a fault in the content that may warrant review by a developer when performing model or tool development.") })))
  private String _level;

  @BoundFlag(
      formalName = "Matches Regular Expression",
      name = "regex",
      typeAdapter = StringAdapter.class)
  private String _regex;

  @BoundFlag(
      formalName = "Matches Data Type",
      name = "datatype",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          allowOthers = true,
          values = { @AllowedValue(value = "base64", description = ""),
              @AllowedValue(value = "boolean", description = ""), @AllowedValue(value = "date", description = ""),
              @AllowedValue(value = "date-time", description = ""),
              @AllowedValue(value = "date-time-with-timezone", description = ""),
              @AllowedValue(value = "date-with-timezone", description = ""),
              @AllowedValue(value = "day-time-duration", description = ""),
              @AllowedValue(value = "decimal", description = ""),
              @AllowedValue(value = "email-address", description = ""),
              @AllowedValue(value = "hostname", description = ""), @AllowedValue(value = "integer", description = ""),
              @AllowedValue(value = "ip-v4-address", description = ""),
              @AllowedValue(value = "ip-v6-address", description = ""),
              @AllowedValue(value = "non-negative-integer", description = ""),
              @AllowedValue(value = "positive-integer", description = ""),
              @AllowedValue(value = "string", description = ""), @AllowedValue(value = "token", description = ""),
              @AllowedValue(value = "uri", description = ""), @AllowedValue(value = "uri-reference", description = ""),
              @AllowedValue(value = "uuid", description = ""), @AllowedValue(value = "base64Binary", description = ""),
              @AllowedValue(value = "dateTime", description = ""),
              @AllowedValue(value = "dateTime-with-timezone", description = ""),
              @AllowedValue(value = "email", description = ""),
              @AllowedValue(value = "nonNegativeInteger", description = ""),
              @AllowedValue(value = "positiveInteger", description = "") })))
  private String _datatype;

  @BoundFlag(
      formalName = "Constraint Target Metapath Expression",
      name = "target",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _target;

  @BoundField(
      formalName = "Formal Name",
      description = "A formal name for the data construct, to be presented in documentation.",
      useName = "formal-name")
  private String _formalName;

  @BoundField(
      formalName = "Description",
      description = "A short description of the data construct's purpose, describing the constructs semantics.",
      useName = "description",
      typeAdapter = MarkupLineAdapter.class)
  private MarkupLine _description;

  @BoundAssembly(
      formalName = "Property",
      useName = "prop",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "props", inJson = JsonGroupAsBehavior.LIST))
  private List<Property> _props;

  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  public TargetedMatchesConstraint() {
    this(null);
  }

  public TargetedMatchesConstraint(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  @Override
  public String getId() {
    return _id;
  }

  public void setId(String value) {
    _id = value;
  }

  @Override
  public String getLevel() {
    return _level;
  }

  public void setLevel(String value) {
    _level = value;
  }

  public String getRegex() {
    return _regex;
  }

  public void setRegex(String value) {
    _regex = value;
  }

  public String getDatatype() {
    return _datatype;
  }

  public void setDatatype(String value) {
    _datatype = value;
  }

  @Override
  public String getTarget() {
    return _target;
  }

  public void setTarget(String value) {
    _target = value;
  }

  @Override
  public String getFormalName() {
    return _formalName;
  }

  public void setFormalName(String value) {
    _formalName = value;
  }

  @Override
  public MarkupLine getDescription() {
    return _description;
  }

  public void setDescription(MarkupLine value) {
    _description = value;
  }

  @Override
  public List<Property> getProps() {
    return _props;
  }

  public void setProps(List<Property> value) {
    _props = value;
  }

  /**
   * Add a new {@link Property} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addProp(Property item) {
    Property value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_props == null) {
      _props = new LinkedList<>();
    }
    return _props.add(value);
  }

  /**
   * Remove the first matching {@link Property} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeProp(Property item) {
    Property value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _props != null && _props.remove(value);
  }

  @Override
  public Remarks getRemarks() {
    return _remarks;
  }

  public void setRemarks(Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
