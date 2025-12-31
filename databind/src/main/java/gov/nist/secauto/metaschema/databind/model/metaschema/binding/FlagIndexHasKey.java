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
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Index Has Key Constraint",
    name = "flag-index-has-key",
    moduleClass = MetaschemaModelModule.class
)
public class FlagIndexHasKey implements IBoundObject, IConfigurableMessageConstraintBase {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Constraint Identifier",
      name = "id",
      typeAdapter = TokenAdapter.class
  )
  private String _id;

  @BoundFlag(
      formalName = "Constraint Severity Level",
      name = "level",
      defaultValue = "ERROR",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {@AllowedValue(value = "CRITICAL", description = "A violation of the constraint represents a serious fault in the content that will prevent typical use of the content."), @AllowedValue(value = "ERROR", description = "A violation of the constraint represents a fault in the content. This may include issues around compatibility, integrity, consistency, etc."), @AllowedValue(value = "WARNING", description = "A violation of the constraint represents a potential issue with the content."), @AllowedValue(value = "INFORMATIONAL", description = "A violation of the constraint represents a point of interest."), @AllowedValue(value = "DEBUG", description = "A violation of the constraint represents a fault in the content that may warrant review by a developer when performing model or tool development.")}))
  )
  private String _level;

  @BoundFlag(
      formalName = "Index Name",
      name = "name",
      required = true,
      typeAdapter = TokenAdapter.class
  )
  private String _name;

  /**
   * A formal name for the data construct, to be presented in documentation.
   */
  @BoundField(
      formalName = "Formal Name",
      description = "A formal name for the data construct, to be presented in documentation.",
      useName = "formal-name",
      typeAdapter = StringAdapter.class
  )
  private String _formalName;

  /**
   * A short description of the data construct's purpose, describing the constructs semantics.
   */
  @BoundField(
      formalName = "Description",
      description = "A short description of the data construct's purpose, describing the constructs semantics.",
      useName = "description",
      typeAdapter = MarkupLineAdapter.class
  )
  private MarkupLine _description;

  @BoundAssembly(
      formalName = "Property",
      useName = "prop",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "props", inJson = JsonGroupAsBehavior.LIST)
  )
  private List<Property> _props;

  @BoundAssembly(
      formalName = "Key Constraint Field",
      useName = "key-field",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "key-fields", inJson = JsonGroupAsBehavior.LIST)
  )
  private List<KeyConstraintField> _keyFields;

  @BoundField(
      formalName = "Constraint Condition Violation Message",
      useName = "message",
      typeAdapter = StringAdapter.class
  )
  private String _message;

  /**
   * Any explanatory or helpful information to be provided about the remarks parent.
   */
  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks"
  )
  private Remarks _remarks;

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagIndexHasKey} instance with no metadata.
   */
  public FlagIndexHasKey() {
    this(null);
  }

  /**
   * Constructs a new {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.FlagIndexHasKey} instance with the specified metadata.
   *
   * @param data
   *           the metaschema data, or {@code null} if none
   */
  public FlagIndexHasKey(IMetaschemaData data) {
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
   *           the id value to set
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
   *           the level value to set
   */
  public void setLevel(@Nullable String value) {
    _level = value;
  }

  /**
   * Get the index Name.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the index Name.
   *
   * @param value
   *           the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
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
   *           the formal-name value to set
   */
  public void setFormalName(@Nullable String value) {
    _formalName = value;
  }

  /**
   * Get the description.
   *
   * <p>
   * A short description of the data construct's purpose, describing the constructs semantics.
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
   * A short description of the data construct's purpose, describing the constructs semantics.
   *
   * @param value
   *           the description value to set
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
   *           the prop value to set
   */
  public void setProps(@NonNull List<Property> value) {
    _props = value;
  }

  /**
   * Add a new {@link Property} item to the underlying collection.
   * @param item the item to add
   * @return {@code true}
   */
  public boolean addProp(Property item) {
    Property value = ObjectUtils.requireNonNull(item,"item cannot be null");
    if (_props == null) {
      _props = new LinkedList<>();
    }
    return _props.add(value);
  }

  /**
   * Remove the first matching {@link Property} item from the underlying collection.
   * @param item the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeProp(Property item) {
    Property value = ObjectUtils.requireNonNull(item,"item cannot be null");
    return _props != null && _props.remove(value);
  }

  /**
   * Get the key Constraint Field.
   *
   * @return the key-field value
   */
  @NonNull
  public List<KeyConstraintField> getKeyFields() {
    if (_keyFields == null) {
      _keyFields = new LinkedList<>();
    }
    return ObjectUtils.notNull(_keyFields);
  }

  /**
   * Set the key Constraint Field.
   *
   * @param value
   *           the key-field value to set
   */
  public void setKeyFields(@NonNull List<KeyConstraintField> value) {
    _keyFields = value;
  }

  /**
   * Add a new {@link KeyConstraintField} item to the underlying collection.
   * @param item the item to add
   * @return {@code true}
   */
  public boolean addKeyField(KeyConstraintField item) {
    KeyConstraintField value = ObjectUtils.requireNonNull(item,"item cannot be null");
    if (_keyFields == null) {
      _keyFields = new LinkedList<>();
    }
    return _keyFields.add(value);
  }

  /**
   * Remove the first matching {@link KeyConstraintField} item from the underlying collection.
   * @param item the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeKeyField(KeyConstraintField item) {
    KeyConstraintField value = ObjectUtils.requireNonNull(item,"item cannot be null");
    return _keyFields != null && _keyFields.remove(value);
  }

  /**
   * Get the constraint Condition Violation Message.
   *
   * @return the message value, or {@code null} if not set
   */
  @Nullable
  @Override
  public String getMessage() {
    return _message;
  }

  /**
   * Set the constraint Condition Violation Message.
   *
   * @param value
   *           the message value to set
   */
  public void setMessage(@Nullable String value) {
    _message = value;
  }

  /**
   * Get the remarks.
   *
   * <p>
   * Any explanatory or helpful information to be provided about the remarks parent.
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
   * Any explanatory or helpful information to be provided about the remarks parent.
   *
   * @param value
   *           the remarks value to set
   */
  public void setRemarks(@Nullable Remarks value) {
    _remarks = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
