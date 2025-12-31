/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.PositiveIntegerAdapter;
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
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Inline Flag Definition",
    name = "inline-define-flag",
    moduleClass = MetaschemaModelModule.class)
public class InlineDefineFlag implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Inline Flag Name",
      name = "name",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _name;

  @BoundFlag(
      formalName = "Inline Flag Binary Name",
      name = "index",
      typeAdapter = PositiveIntegerAdapter.class)
  private BigInteger _index;

  @BoundFlag(
      formalName = "Deprecated Version",
      name = "deprecated",
      typeAdapter = StringAdapter.class)
  private String _deprecated;

  @BoundFlag(
      formalName = "Flag Value Data Type",
      name = "as-type",
      defaultValue = "string",
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
  private String _asType;

  @BoundFlag(
      formalName = "Default Flag Value",
      name = "default",
      typeAdapter = StringAdapter.class)
  private String _default;

  @BoundFlag(
      formalName = "Is Flag Required?",
      name = "required",
      defaultValue = "no",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "yes", description = "The flag is required."),
              @AllowedValue(value = "no", description = "The flag is optional.") })))
  private String _required;

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

  @BoundAssembly(
      useName = "constraint")
  private FlagConstraints _constraint;

  /**
   * Any explanatory or helpful information to be provided about the remarks
   * parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  @BoundAssembly(
      formalName = "Example",
      useName = "example",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "examples", inJson = JsonGroupAsBehavior.LIST))
  private List<Example> _examples;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.InlineDefineFlag}
   * instance with no metadata.
   */
  public InlineDefineFlag() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.InlineDefineFlag}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public InlineDefineFlag(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the inline Flag Name.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the inline Flag Name.
   *
   * @param value
   *          the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
  }

  /**
   * Get the inline Flag Binary Name.
   *
   * @return the index value, or {@code null} if not set
   */
  @Nullable
  public BigInteger getIndex() {
    return _index;
  }

  /**
   * Set the inline Flag Binary Name.
   *
   * @param value
   *          the index value to set
   */
  public void setIndex(@Nullable BigInteger value) {
    _index = value;
  }

  /**
   * Get the deprecated Version.
   *
   * @return the deprecated value, or {@code null} if not set
   */
  @Nullable
  public String getDeprecated() {
    return _deprecated;
  }

  /**
   * Set the deprecated Version.
   *
   * @param value
   *          the deprecated value to set
   */
  public void setDeprecated(@Nullable String value) {
    _deprecated = value;
  }

  /**
   * Get the flag Value Data Type.
   *
   * @return the as-type value, or {@code null} if not set
   */
  @Nullable
  public String getAsType() {
    return _asType;
  }

  /**
   * Set the flag Value Data Type.
   *
   * @param value
   *          the as-type value to set
   */
  public void setAsType(@Nullable String value) {
    _asType = value;
  }

  /**
   * Get the default Flag Value.
   *
   * @return the default value, or {@code null} if not set
   */
  @Nullable
  public String getDefault() {
    return _default;
  }

  /**
   * Set the default Flag Value.
   *
   * @param value
   *          the default value to set
   */
  public void setDefault(@Nullable String value) {
    _default = value;
  }

  /**
   * Get the is Flag Required?.
   *
   * @return the required value, or {@code null} if not set
   */
  @Nullable
  public String getRequired() {
    return _required;
  }

  /**
   * Set the is Flag Required?.
   *
   * @param value
   *          the required value to set
   */
  public void setRequired(@Nullable String value) {
    _required = value;
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
    return ObjectUtils.notNull(_props);
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
   * Get the {@code constraint} property.
   *
   * @return the constraint value, or {@code null} if not set
   */
  @Nullable
  public FlagConstraints getConstraint() {
    return _constraint;
  }

  /**
   * Set the {@code constraint} property.
   *
   * @param value
   *          the constraint value to set
   */
  public void setConstraint(@Nullable FlagConstraints value) {
    _constraint = value;
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

  /**
   * Get the example.
   *
   * @return the example value
   */
  @NonNull
  public List<Example> getExamples() {
    if (_examples == null) {
      _examples = new LinkedList<>();
    }
    return ObjectUtils.notNull(_examples);
  }

  /**
   * Set the example.
   *
   * @param value
   *          the example value to set
   */
  public void setExamples(@NonNull List<Example> value) {
    _examples = value;
  }

  /**
   * Add a new {@link Example} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addExample(Example item) {
    Example value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_examples == null) {
      _examples = new LinkedList<>();
    }
    return _examples.add(value);
  }

  /**
   * Remove the first matching {@link Example} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeExample(Example item) {
    Example value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _examples != null && _examples.remove(value);
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
