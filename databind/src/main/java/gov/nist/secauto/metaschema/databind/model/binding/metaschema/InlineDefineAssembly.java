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
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
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
    formalName = "Inline Assembly Definition",
    name = "inline-define-assembly",
    moduleClass = MetaschemaModelModule.class)
public class InlineDefineAssembly implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundFlag(
      formalName = "Inline Assembly Name",
      name = "name",
      required = true,
      typeAdapter = TokenAdapter.class)
  private String _name;

  @BoundFlag(
      formalName = "Inline Assembly Binary Name",
      name = "index",
      typeAdapter = PositiveIntegerAdapter.class)
  private BigInteger _index;

  @BoundFlag(
      formalName = "Deprecated Version",
      name = "deprecated",
      typeAdapter = StringAdapter.class)
  private String _deprecated;

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

  @BoundAssembly(
      formalName = "JSON Key",
      description = "Used in JSON (and similar formats) to identify a flag that will be used as the property name in an object hold a collection of sibling objects. Requires that siblings must never share `json-key` values.",
      useName = "json-key")
  private JsonKey _jsonKey;

  @BoundAssembly(
      formalName = "Group As",
      useName = "group-as")
  private GroupingAs _groupAs;

  @BoundChoiceGroup(
      maxOccurs = -1,
      assemblies = {
          @BoundGroupedAssembly(formalName = "Inline Flag Definition", useName = "define-flag",
              binding = InlineDefineFlag.class),
          @BoundGroupedAssembly(formalName = "Flag Reference", useName = "flag", binding = FlagReference.class)
      },
      groupAs = @GroupAs(name = "flags", inJson = JsonGroupAsBehavior.LIST))
  private List<Object> _flags;

  @BoundAssembly(
      useName = "model")
  private AssemblyModel _model;

  @BoundAssembly(
      useName = "constraint")
  private AssemblyConstraints _constraint;

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

  public InlineDefineAssembly() {
    this(null);
  }

  public InlineDefineAssembly(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public String getName() {
    return _name;
  }

  public void setName(String value) {
    _name = value;
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

  public JsonKey getJsonKey() {
    return _jsonKey;
  }

  public void setJsonKey(JsonKey value) {
    _jsonKey = value;
  }

  public GroupingAs getGroupAs() {
    return _groupAs;
  }

  public void setGroupAs(GroupingAs value) {
    _groupAs = value;
  }

  public List<Object> getFlags() {
    return _flags;
  }

  public void setFlags(List<Object> value) {
    _flags = value;
  }

  public AssemblyModel getModel() {
    return _model;
  }

  public void setModel(AssemblyModel value) {
    _model = value;
  }

  public AssemblyConstraints getConstraint() {
    return _constraint;
  }

  public void setConstraint(AssemblyConstraints value) {
    _constraint = value;
  }

  public Remarks getRemarks() {
    return _remarks;
  }

  public void setRemarks(Remarks value) {
    _remarks = value;
  }

  public List<Example> getExamples() {
    return _examples;
  }

  public void setExamples(List<Example> value) {
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
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
  }
}
