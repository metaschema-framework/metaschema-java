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
import gov.nist.secauto.metaschema.databind.model.metaschema.ITargetedConstraintBase;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Allowed Values Constraint",
    name = "targeted-allowed-values-constraint",
    moduleClass = MetaschemaModelModule.class)
public class TargetedAllowedValuesConstraint implements IBoundObject, ITargetedConstraintBase {
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
      formalName = "Allow Non-Enumerated Values?",
      name = "allow-other",
      defaultValue = "no",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "no", description = "Other value are not allowed."),
              @AllowedValue(value = "yes", description = "Other values are allowed.") })))
  private String _allowOther;

  /**
   * Determines if the given enumerated values may be extended by other allowed
   * value constraints.
   */
  @BoundFlag(
      formalName = "Allow Extension?",
      description = "Determines if the given enumerated values may be extended by other allowed value constraints.",
      name = "extensible",
      defaultValue = "external",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = {
              @AllowedValue(value = "model", description = "Can be extended by constraints within the same module."),
              @AllowedValue(value = "external", description = "Can be extended by external constraints."),
              @AllowedValue(value = "none", description = "Cannot be extended.") })))
  private String _extensible;

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
      formalName = "Allowed Value Enumeration",
      useName = "enum",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "enums", inJson = JsonGroupAsBehavior.LIST))
  private List<ConstraintValueEnum> _enums;

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
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedAllowedValuesConstraint}
   * instance with no metadata.
   */
  public TargetedAllowedValuesConstraint() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.TargetedAllowedValuesConstraint}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public TargetedAllowedValuesConstraint(IMetaschemaData data) {
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
  @Override
  public String getId() {
    return _id;
  }

  /**
   * Set the constraint Identifier.
   *
   * @param value
   *          the id value to set, or {@code null} to clear
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
  @Override
  public String getLevel() {
    return _level;
  }

  /**
   * Set the constraint Severity Level.
   *
   * @param value
   *          the level value to set, or {@code null} to clear
   */
  public void setLevel(@Nullable String value) {
    _level = value;
  }

  /**
   * Get the allow Non-Enumerated Values?.
   *
   * @return the allow-other value, or {@code null} if not set
   */
  @Nullable
  public String getAllowOther() {
    return _allowOther;
  }

  /**
   * Set the allow Non-Enumerated Values?.
   *
   * @param value
   *          the allow-other value to set, or {@code null} to clear
   */
  public void setAllowOther(@Nullable String value) {
    _allowOther = value;
  }

  /**
   * Get the allow Extension?.
   *
   * <p>
   * Determines if the given enumerated values may be extended by other allowed
   * value constraints.
   *
   * @return the extensible value, or {@code null} if not set
   */
  @Nullable
  public String getExtensible() {
    return _extensible;
  }

  /**
   * Set the allow Extension?.
   *
   * <p>
   * Determines if the given enumerated values may be extended by other allowed
   * value constraints.
   *
   * @param value
   *          the extensible value to set, or {@code null} to clear
   */
  public void setExtensible(@Nullable String value) {
    _extensible = value;
  }

  /**
   * Get the constraint Target Metapath Expression.
   *
   * @return the target value
   */
  @NonNull
  @Override
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
  @Override
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
   *          the formal-name value to set, or {@code null} to clear
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
  @Override
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
   *          the description value to set, or {@code null} to clear
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
  @Override
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
   * Get the allowed Value Enumeration.
   *
   * @return the enum value
   */
  @NonNull
  public List<ConstraintValueEnum> getEnums() {
    if (_enums == null) {
      _enums = new LinkedList<>();
    }
    return ObjectUtils.notNull(_enums);
  }

  /**
   * Set the allowed Value Enumeration.
   *
   * @param value
   *          the enum value to set
   */
  public void setEnums(@NonNull List<ConstraintValueEnum> value) {
    _enums = value;
  }

  /**
   * Add a new {@link ConstraintValueEnum} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addEnum(ConstraintValueEnum item) {
    ConstraintValueEnum value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_enums == null) {
      _enums = new LinkedList<>();
    }
    return _enums.add(value);
  }

  /**
   * Remove the first matching {@link ConstraintValueEnum} item from the
   * underlying collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeEnum(ConstraintValueEnum item) {
    ConstraintValueEnum value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _enums != null && _enums.remove(value);
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
  @Override
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
   *          the remarks value to set, or {@code null} to clear
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
