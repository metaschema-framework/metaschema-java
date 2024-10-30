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
   * "Determines if the given enumerated values may be extended by other allowed
   * value constraints."
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
      formalName = "Allowed Value Enumeration",
      useName = "enum",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "enums", inJson = JsonGroupAsBehavior.LIST))
  private List<ConstraintValueEnum> _enums;

  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  public TargetedAllowedValuesConstraint() {
    this(null);
  }

  public TargetedAllowedValuesConstraint(IMetaschemaData data) {
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

  public String getAllowOther() {
    return _allowOther;
  }

  public void setAllowOther(String value) {
    _allowOther = value;
  }

  public String getExtensible() {
    return _extensible;
  }

  public void setExtensible(String value) {
    _extensible = value;
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

  public List<ConstraintValueEnum> getEnums() {
    return _enums;
  }

  public void setEnums(List<ConstraintValueEnum> value) {
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
