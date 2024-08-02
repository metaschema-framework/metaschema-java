/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.binding.metaschema;

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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({
    "PMD.DataClass",
    "PMD.FieldNamingConventions"
})
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
          @AllowedValue(value = "WITH_WRAPPER", description = "Alias for WRAPPED.") })))
  private String _inXml;

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
      formalName = "Use Name",
      description = "Allows the name of the definition to be overridden.",
      useName = "use-name")
  private UseName _useName;

  @BoundAssembly(
      formalName = "Group As",
      useName = "group-as")
  private GroupingAs _groupAs;

  @BoundField(
      formalName = "Remarks",
      description = "Any explanatory or helpful information to be provided about the remarks parent.",
      useName = "remarks")
  private Remarks _remarks;

  public FieldReference() {
    this(null);
  }

  public FieldReference(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public String getRef() {
    return _ref;
  }

  public void setRef(String value) {
    _ref = value;
  }

  public BigInteger getIndex() {
    return _index;
  }

  public void setIndex(BigInteger value) {
    _index = value;
  }

  public String getDeprecated() {
    return _deprecated;
  }

  public void setDeprecated(String value) {
    _deprecated = value;
  }

  public String getDefault() {
    return _default;
  }

  public void setDefault(String value) {
    _default = value;
  }

  public BigInteger getMinOccurs() {
    return _minOccurs;
  }

  public void setMinOccurs(BigInteger value) {
    _minOccurs = value;
  }

  public String getMaxOccurs() {
    return _maxOccurs;
  }

  public void setMaxOccurs(String value) {
    _maxOccurs = value;
  }

  public String getInXml() {
    return _inXml;
  }

  public void setInXml(String value) {
    _inXml = value;
  }

  public String getFormalName() {
    return _formalName;
  }

  public void setFormalName(String value) {
    _formalName = value;
  }

  public MarkupLine getDescription() {
    return _description;
  }

  public void setDescription(MarkupLine value) {
    _description = value;
  }

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

  public UseName getUseName() {
    return _useName;
  }

  public void setUseName(UseName value) {
    _useName = value;
  }

  public GroupingAs getGroupAs() {
    return _groupAs;
  }

  public void setGroupAs(GroupingAs value) {
    _groupAs = value;
  }

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
