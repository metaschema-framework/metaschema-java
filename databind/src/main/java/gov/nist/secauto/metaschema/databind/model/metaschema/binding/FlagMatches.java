/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

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
import gov.nist.secauto.metaschema.databind.model.metaschema.IConstraintBase;

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
    name = "flag-matches",
    moduleClass = MetaschemaModelModule.class)
public class FlagMatches implements IBoundObject, IConstraintBase {
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
          values = { @AllowedValue(value = "base64",
              description = "The [base64](https://pages.nist.gov/metaschema/specification/datatypes/#base64) data type."),
              @AllowedValue(value = "boolean",
                  description = "The [boolean](https://pages.nist.gov/metaschema/specification/datatypes/#boolean) data type."),
              @AllowedValue(value = "date",
                  description = "The [date](https://pages.nist.gov/metaschema/specification/datatypes/#date) data type."),
              @AllowedValue(value = "date-time",
                  description = "The [date-time](https://pages.nist.gov/metaschema/specification/datatypes/#date-time) data type."),
              @AllowedValue(value = "date-time-with-timezone",
                  description = "The [date-time-with-timezone](https://pages.nist.gov/metaschema/specification/datatypes/#date-time-with-timezone) data type."),
              @AllowedValue(value = "date-with-timezone",
                  description = "The [date-with-timezone](https://pages.nist.gov/metaschema/specification/datatypes/#date-with-timezone) data type."),
              @AllowedValue(value = "day-time-duration",
                  description = "The [day-time-duration](https://pages.nist.gov/metaschema/specification/datatypes/#day-time-duration) data type."),
              @AllowedValue(value = "decimal",
                  description = "The [decimal](https://pages.nist.gov/metaschema/specification/datatypes/#decimal) data type."),
              @AllowedValue(value = "email-address",
                  description = "The [email-address](https://pages.nist.gov/metaschema/specification/datatypes/#email-address) data type."),
              @AllowedValue(value = "hostname",
                  description = "The [hostname](https://pages.nist.gov/metaschema/specification/datatypes/#hostname) data type."),
              @AllowedValue(value = "integer",
                  description = "The [integer](https://pages.nist.gov/metaschema/specification/datatypes/#integer) data type."),
              @AllowedValue(value = "ip-v4-address",
                  description = "The [ip-v4-address](https://pages.nist.gov/metaschema/specification/datatypes/#ip-v4-address) data type."),
              @AllowedValue(value = "ip-v6-address",
                  description = "The [ip-v6-address](https://pages.nist.gov/metaschema/specification/datatypes/#ip-v6-address) data type."),
              @AllowedValue(value = "non-negative-integer",
                  description = "The [non-negative-integer](https://pages.nist.gov/metaschema/specification/datatypes/#non-negative-integer) data type."),
              @AllowedValue(value = "positive-integer",
                  description = "The [positive-integer](https://pages.nist.gov/metaschema/specification/datatypes/#positive-integer) data type."),
              @AllowedValue(value = "string",
                  description = "The [string](https://pages.nist.gov/metaschema/specification/datatypes/#string) data type."),
              @AllowedValue(value = "token",
                  description = "The [token](https://pages.nist.gov/metaschema/specification/datatypes/#token) data type."),
              @AllowedValue(value = "uri",
                  description = "The [uri](https://pages.nist.gov/metaschema/specification/datatypes/#uri) data type."),
              @AllowedValue(value = "uri-reference",
                  description = "The [uri-reference](https://pages.nist.gov/metaschema/specification/datatypes/#uri-reference) data type."),
              @AllowedValue(value = "uuid",
                  description = "The [uuid](https://pages.nist.gov/metaschema/specification/datatypes/#uuid) data type."),
              @AllowedValue(value = "base64Binary",
                  description = "An old name which is deprecated for use in favor of the 'base64' data type."),
              @AllowedValue(value = "dateTime",
                  description = "An old name which is deprecated for use in favor of the 'date-time' data type."),
              @AllowedValue(value = "dateTime-with-timezone",
                  description = "An old name which is deprecated for use in favor of the 'date-time-with-timezone' data type."),
              @AllowedValue(value = "email",
                  description = "An old name which is deprecated for use in favor of the 'email-address' data type."),
              @AllowedValue(value = "nonNegativeInteger",
                  description = "An old name which is deprecated for use in favor of the 'non-negative-integer' data type."),
              @AllowedValue(value = "positiveInteger",
                  description = "An old name which is deprecated for use in favor of the 'positive-integer' data type.") })))
  private String _datatype;

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
      formalName = "Constraint Condition Violation Message",
      useName = "message")
  private String _message;

  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  public FlagMatches() {
    this(null);
  }

  public FlagMatches(IMetaschemaData data) {
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
  public String getMessage() {
    return _message;
  }

  public void setMessage(String value) {
    _message = value;
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
