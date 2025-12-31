/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.NonNegativeIntegerAdapter;
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
import gov.nist.secauto.metaschema.databind.model.annotations.Matches;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MetaschemaAssembly(
    formalName = "Field Reference",
    name = "field-reference",
    moduleClass = MetaschemaModelModule.class)
public class FieldReference implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Global Field Reference",
      name = "ref",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _ref;

  @BoundFlag(
      formalName = "Field Reference Binary Name",
      name = "index",
      typeAdapter = PositiveIntegerAdapter.class)
  private BigInteger _index;

  @BoundFlag(
      formalName = "Deprecated Version",
      name = "deprecated",
      typeAdapter = StringAdapter.class)
  private String _deprecated;

  @BoundFlag(
      formalName = "Default Field Value",
      name = "default",
      typeAdapter = StringAdapter.class)
  private String _default;

  @BoundFlag(
      formalName = "Minimum Occurrence",
      name = "min-occurs",
      defaultValue = "0",
      typeAdapter = NonNegativeIntegerAdapter.class)
  private BigInteger _minOccurs;

  @BoundFlag(
      formalName = "Maximum Occurrence",
      name = "max-occurs",
      defaultValue = "1",
      typeAdapter = StringAdapter.class,
      valueConstraints = @ValueConstraints(
          matches = @Matches(level = IConstraint.Level.ERROR, pattern = "^[1-9][0-9]*|unbounded$")))
  private String _maxOccurs;

  @BoundFlag(
      formalName = "Field In XML",
      name = "in-xml",
      defaultValue = "WRAPPED",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
          @AllowedValue(value = "WRAPPED",
              description = "Block contents of a markup-multiline field will be represented with a containing (wrapper) element in the XML."),
          @AllowedValue(value = "UNWRAPPED",
              description = "Block contents of a markup-multiline will be represented in the XML with no wrapper, making the field implicit. Among sibling fields in a given model, only one of them may be designated as UNWRAPPED."),
          @AllowedValue(value = "WITH_WRAPPER", description = "Alias for WRAPPED.", deprecatedVersion = "0.9.0") })))
  private String _inXml;

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

  /**
   * Allows the name of the definition to be overridden.
   */
  @BoundField(
      formalName = "Use Name",
      description = "Allows the name of the definition to be overridden.",
      useName = "use-name")
  private UseName _useName;

  @BoundAssembly(
      formalName = "Group As",
      useName = "group-as")
  private GroupingAs _groupAs;

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
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.FieldReference}
   * instance with no metadata.
   */
  public FieldReference() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.FieldReference}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public FieldReference(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the global Field Reference.
   *
   * @return the ref value
   */
  @NonNull
  public String getRef() {
    return _ref;
  }

  /**
   * Set the global Field Reference.
   *
   * @param value
   *          the ref value to set
   */
  public void setRef(@NonNull String value) {
    _ref = value;
  }

  /**
   * Get the field Reference Binary Name.
   *
   * @return the index value, or {@code null} if not set
   */
  @Nullable
  public BigInteger getIndex() {
    return _index;
  }

  /**
   * Set the field Reference Binary Name.
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
   * Get the default Field Value.
   *
   * @return the default value, or {@code null} if not set
   */
  @Nullable
  public String getDefault() {
    return _default;
  }

  /**
   * Set the default Field Value.
   *
   * @param value
   *          the default value to set
   */
  public void setDefault(@Nullable String value) {
    _default = value;
  }

  /**
   * Get the minimum Occurrence.
   *
   * @return the min-occurs value, or {@code null} if not set
   */
  @Nullable
  public BigInteger getMinOccurs() {
    return _minOccurs;
  }

  /**
   * Set the minimum Occurrence.
   *
   * @param value
   *          the min-occurs value to set
   */
  public void setMinOccurs(@Nullable BigInteger value) {
    _minOccurs = value;
  }

  /**
   * Get the maximum Occurrence.
   *
   * @return the max-occurs value, or {@code null} if not set
   */
  @Nullable
  public String getMaxOccurs() {
    return _maxOccurs;
  }

  /**
   * Set the maximum Occurrence.
   *
   * @param value
   *          the max-occurs value to set
   */
  public void setMaxOccurs(@Nullable String value) {
    _maxOccurs = value;
  }

  /**
   * Get the field In XML.
   *
   * @return the in-xml value, or {@code null} if not set
   */
  @Nullable
  public String getInXml() {
    return _inXml;
  }

  /**
   * Set the field In XML.
   *
   * @param value
   *          the in-xml value to set
   */
  public void setInXml(@Nullable String value) {
    _inXml = value;
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
   * Get the use Name.
   *
   * <p>
   * Allows the name of the definition to be overridden.
   *
   * @return the use-name value, or {@code null} if not set
   */
  @Nullable
  public UseName getUseName() {
    return _useName;
  }

  /**
   * Set the use Name.
   *
   * <p>
   * Allows the name of the definition to be overridden.
   *
   * @param value
   *          the use-name value to set
   */
  public void setUseName(@Nullable UseName value) {
    _useName = value;
  }

  /**
   * Get the group As.
   *
   * @return the group-as value, or {@code null} if not set
   */
  @Nullable
  public GroupingAs getGroupAs() {
    return _groupAs;
  }

  /**
   * Set the group As.
   *
   * @param value
   *          the group-as value to set
   */
  public void setGroupAs(@Nullable GroupingAs value) {
    _groupAs = value;
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
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }
}
