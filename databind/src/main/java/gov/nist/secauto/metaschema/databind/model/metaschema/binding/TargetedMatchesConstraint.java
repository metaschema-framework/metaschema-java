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
import gov.nist.secauto.metaschema.databind.model.metaschema.IConfigurableMessageConstraintBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.ITargetedConstraintBase;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Value Matches Constraint",
    name = "targeted-matches-constraint",
    moduleClass = MetaschemaModelModule.class)
public class TargetedMatchesConstraint
    implements IBoundObject, ITargetedConstraintBase, IConfigurableMessageConstraintBase {
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
              description = "The [base64](https://framework.metaschema.dev/specification/datatypes/#base64) data type."),
              @AllowedValue(value = "boolean",
                  description = "The [boolean](https://framework.metaschema.dev/specification/datatypes/#boolean) data type."),
              @AllowedValue(value = "date",
                  description = "The [date](https://framework.metaschema.dev/specification/datatypes/#date) data type."),
              @AllowedValue(value = "date-time",
                  description = "The [date-time](https://framework.metaschema.dev/specification/datatypes/#date-time) data type."),
              @AllowedValue(value = "date-time-with-timezone",
                  description = "The [date-time-with-timezone](https://framework.metaschema.dev/specification/datatypes/#date-time-with-timezone) data type."),
              @AllowedValue(value = "date-with-timezone",
                  description = "The [date-with-timezone](https://framework.metaschema.dev/specification/datatypes/#date-with-timezone) data type."),
              @AllowedValue(value = "day-time-duration",
                  description = "The [day-time-duration](https://framework.metaschema.dev/specification/datatypes/#day-time-duration) data type."),
              @AllowedValue(value = "decimal",
                  description = "The [decimal](https://framework.metaschema.dev/specification/datatypes/#decimal) data type."),
              @AllowedValue(value = "email-address",
                  description = "The [email-address](https://framework.metaschema.dev/specification/datatypes/#email-address) data type."),
              @AllowedValue(value = "hostname",
                  description = "The [hostname](https://framework.metaschema.dev/specification/datatypes/#hostname) data type."),
              @AllowedValue(value = "integer",
                  description = "The [integer](https://framework.metaschema.dev/specification/datatypes/#integer) data type."),
              @AllowedValue(value = "ip-v4-address",
                  description = "The [ip-v4-address](https://framework.metaschema.dev/specification/datatypes/#ip-v4-address) data type."),
              @AllowedValue(value = "ip-v6-address",
                  description = "The [ip-v6-address](https://framework.metaschema.dev/specification/datatypes/#ip-v6-address) data type."),
              @AllowedValue(value = "non-negative-integer",
                  description = "The [non-negative-integer](https://framework.metaschema.dev/specification/datatypes/#non-negative-integer) data type."),
              @AllowedValue(value = "positive-integer",
                  description = "The [positive-integer](https://framework.metaschema.dev/specification/datatypes/#positive-integer) data type."),
              @AllowedValue(value = "string",
                  description = "The [string](https://framework.metaschema.dev/specification/datatypes/#string) data type."),
              @AllowedValue(value = "token",
                  description = "The [token](https://framework.metaschema.dev/specification/datatypes/#token) data type."),
              @AllowedValue(value = "uri",
                  description = "The [uri](https://framework.metaschema.dev/specification/datatypes/#uri) data type."),
              @AllowedValue(value = "uri-reference",
                  description = "The [uri-reference](https://framework.metaschema.dev/specification/datatypes/#uri-reference) data type."),
              @AllowedValue(value = "uuid",
                  description = "The [uuid](https://framework.metaschema.dev/specification/datatypes/#uuid) data type."),
              @AllowedValue(value = "base64Binary",
                  description = "An old name which is deprecated for use in favor of the 'base64' data type.",
                  deprecatedVersion = "1.0.0"),
              @AllowedValue(value = "dateTime",
                  description = "An old name which is deprecated for use in favor of the 'date-time' data type.",
                  deprecatedVersion = "1.0.0"),
              @AllowedValue(value = "dateTime-with-timezone",
                  description = "An old name which is deprecated for use in favor of the 'date-time-with-timezone' data type.",
                  deprecatedVersion = "1.0.0"),
              @AllowedValue(value = "email",
                  description = "An old name which is deprecated for use in favor of the 'email-address' data type.",
                  deprecatedVersion = "1.0.0"),
              @AllowedValue(value = "nonNegativeInteger",
                  description = "An old name which is deprecated for use in favor of the 'non-negative-integer' data type.",
                  deprecatedVersion = "1.0.0"),
              @AllowedValue(value = "positiveInteger",
                  description = "An old name which is deprecated for use in favor of the 'positive-integer' data type.",
                  deprecatedVersion = "1.0.0") })))
  private String _datatype;

  @BoundFlag(
      formalName = "Constraint Target Metapath Expression",
      name = "target",
      required = true,
      typeAdapter = StringAdapter.class)
  private String _target;

  /**
   * A formal name for the data construct, to be presented in documentation.
   */
  @BoundField(
      formalName = "Formal Name",
      description = "A formal name for the data construct, to be presented in documentation.",
      useName = "formal-name",
      typeAdapter = StringAdapter.class)
  private String _formalName;

  /**
   * A short description of the data construct's purpose, describing the
   * constructs semantics.
   */
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
      useName = "message",
      typeAdapter = StringAdapter.class)
  private String _message;

  /**
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedMatchesConstraint}
   * instance with no metadata.
   */
  public TargetedMatchesConstraint() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedMatchesConstraint}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public TargetedMatchesConstraint(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the constraint Identifier.
   *
   * @return the id value, or {@code null} if not set
   */
  @Nullable
  public String getId() {
    return _id;
  }

  /**
   * Set the constraint Identifier.
   *
   * @param value
   *          the id value to set
   */
  public void setId(@Nullable String value) {
    _id = value;
  }

  /**
   * Get the constraint Severity Level.
   *
   * @return the level value, or {@code null} if not set
   */
  @Nullable
  public String getLevel() {
    return _level;
  }

  /**
   * Set the constraint Severity Level.
   *
   * @param value
   *          the level value to set
   */
  public void setLevel(@Nullable String value) {
    _level = value;
  }

  /**
   * Get the matches Regular Expression.
   *
   * @return the regex value, or {@code null} if not set
   */
  @Nullable
  public String getRegex() {
    return _regex;
  }

  /**
   * Set the matches Regular Expression.
   *
   * @param value
   *          the regex value to set
   */
  public void setRegex(@Nullable String value) {
    _regex = value;
  }

  /**
   * Get the matches Data Type.
   *
   * @return the datatype value, or {@code null} if not set
   */
  @Nullable
  public String getDatatype() {
    return _datatype;
  }

  /**
   * Set the matches Data Type.
   *
   * @param value
   *          the datatype value to set
   */
  public void setDatatype(@Nullable String value) {
    _datatype = value;
  }

  /**
   * Get the constraint Target Metapath Expression.
   *
   * @return the target value
   */
  @NonNull
  public String getTarget() {
    return _target;
  }

  /**
   * Set the constraint Target Metapath Expression.
   *
   * @param value
   *          the target value to set
   */
  public void setTarget(@NonNull String value) {
    _target = value;
  }

  /**
   * Get the formal Name.
   *
   * <p>
   * A formal name for the data construct, to be presented in documentation.
   *
   * @return the formal-name value, or {@code null} if not set
   */
  @Nullable
  public String getFormalName() {
    return _formalName;
  }

  /**
   * Set the formal Name.
   *
   * <p>
   * A formal name for the data construct, to be presented in documentation.
   *
   * @param value
   *          the formal-name value to set
   */
  public void setFormalName(@Nullable String value) {
    _formalName = value;
  }

  /**
   * Get the description.
   *
   * <p>
   * A short description of the data construct's purpose, describing the
   * constructs semantics.
   *
   * @return the description value, or {@code null} if not set
   */
  @Nullable
  public MarkupLine getDescription() {
    return _description;
  }

  /**
   * Set the description.
   *
   * <p>
   * A short description of the data construct's purpose, describing the
   * constructs semantics.
   *
   * @param value
   *          the description value to set
   */
  public void setDescription(@Nullable MarkupLine value) {
    _description = value;
  }

  /**
   * Get the property.
   *
   * @return the prop value
   */
  @NonNull
  public List<Property> getProps() {
    if (_props == null) {
      _props = new LinkedList<>();
    }
    return _props;
  }

  /**
   * Set the property.
   *
   * @param value
   *          the prop value to set
   */
  public void setProps(@NonNull List<Property> value) {
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

  /**
   * Get the constraint Condition Violation Message.
   *
   * @return the message value, or {@code null} if not set
   */
  @Nullable
  public String getMessage() {
    return _message;
  }

  /**
   * Set the constraint Condition Violation Message.
   *
   * @param value
   *          the message value to set
   */
  public void setMessage(@Nullable String value) {
    _message = value;
  }

  /**
   * Get the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   *
   * @return the remarks value, or {@code null} if not set
   */
  @Nullable
  public Remarks getRemarks() {
    return _remarks;
  }

  /**
   * Set the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   *
   * @param value
   *          the remarks value to set
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
