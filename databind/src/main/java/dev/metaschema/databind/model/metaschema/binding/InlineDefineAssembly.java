/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import dev.metaschema.core.datatype.adapter.NonNegativeIntegerAdapter;
import dev.metaschema.core.datatype.adapter.PositiveIntegerAdapter;
import dev.metaschema.core.datatype.adapter.StringAdapter;
import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupLineAdapter;
import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.annotations.BoundAssembly;
import dev.metaschema.databind.model.annotations.BoundChoiceGroup;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.BoundFlag;
import dev.metaschema.databind.model.annotations.BoundGroupedAssembly;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.Matches;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import dev.metaschema.databind.model.annotations.ValueConstraints;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Inline Assembly Definition.
 */
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
   * Used in JSON (and similar formats) to identify a flag that will be used as
   * the property name in an object hold a collection of sibling objects. Requires
   * that siblings must never share <code>json-key</code> values.
   */
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
      groupAs = @GroupAs(name = "flags", inJson = JsonGroupAsBehavior.LIST),
      assemblies = {
          @BoundGroupedAssembly(formalName = "Inline Flag Definition", useName = "define-flag",
              discriminatorValue = "flag", binding = InlineDefineFlag.class),
          @BoundGroupedAssembly(formalName = "Flag Reference", useName = "flag", discriminatorValue = "flag-ref",
              binding = FlagReference.class)
      })
  private List<Object> _flags;

  @BoundAssembly(
      useName = "model")
  private AssemblyModel _model;

  @BoundAssembly(
      useName = "constraint")
  private AssemblyConstraints _constraint;

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
   * {@code dev.metaschema.databind.model.metaschema.binding.InlineDefineAssembly}
   * instance with no metadata.
   */
  public InlineDefineAssembly() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.InlineDefineAssembly}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public InlineDefineAssembly(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the inline Assembly Name.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the inline Assembly Name.
   *
   * @param value
   *          the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
  }

  /**
   * Get the inline Assembly Binary Name.
   *
   * @return the index value, or {@code null} if not set
   */
  @Nullable
  public BigInteger getIndex() {
    return _index;
  }

  /**
   * Set the inline Assembly Binary Name.
   *
   * @param value
   *          the index value to set, or {@code null} to clear
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
   *          the deprecated value to set, or {@code null} to clear
   */
  public void setDeprecated(@Nullable String value) {
    _deprecated = value;
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
   *          the min-occurs value to set, or {@code null} to clear
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
   *          the max-occurs value to set, or {@code null} to clear
   */
  public void setMaxOccurs(@Nullable String value) {
    _maxOccurs = value;
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
   * Get the jSON Key.
   *
   * <p>
   * Used in JSON (and similar formats) to identify a flag that will be used as
   * the property name in an object hold a collection of sibling objects. Requires
   * that siblings must never share <code>json-key</code> values.
   *
   * @return the json-key value, or {@code null} if not set
   */
  @Nullable
  public JsonKey getJsonKey() {
    return _jsonKey;
  }

  /**
   * Set the jSON Key.
   *
   * <p>
   * Used in JSON (and similar formats) to identify a flag that will be used as
   * the property name in an object hold a collection of sibling objects. Requires
   * that siblings must never share <code>json-key</code> values.
   *
   * @param value
   *          the json-key value to set, or {@code null} to clear
   */
  public void setJsonKey(@Nullable JsonKey value) {
    _jsonKey = value;
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
   *          the group-as value to set, or {@code null} to clear
   */
  public void setGroupAs(@Nullable GroupingAs value) {
    _groupAs = value;
  }

  /**
   * Get the {@code flags} choice group items.
   *
   * @return the flags items
   */
  @NonNull
  public List<Object> getFlags() {
    if (_flags == null) {
      _flags = new LinkedList<>();
    }
    return ObjectUtils.notNull(_flags);
  }

  /**
   * Set the {@code flags} choice group items.
   *
   * @param value
   *          the flags items to set
   */
  public void setFlags(@NonNull List<Object> value) {
    _flags = value;
  }

  /**
   * Get the {@code model} property.
   *
   * @return the model value, or {@code null} if not set
   */
  @Nullable
  public AssemblyModel getModel() {
    return _model;
  }

  /**
   * Set the {@code model} property.
   *
   * @param value
   *          the model value to set, or {@code null} to clear
   */
  public void setModel(@Nullable AssemblyModel value) {
    _model = value;
  }

  /**
   * Get the {@code constraint} property.
   *
   * @return the constraint value, or {@code null} if not set
   */
  @Nullable
  public AssemblyConstraints getConstraint() {
    return _constraint;
  }

  /**
   * Set the {@code constraint} property.
   *
   * @param value
   *          the constraint value to set, or {@code null} to clear
   */
  public void setConstraint(@Nullable AssemblyConstraints value) {
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
   *          the remarks value to set, or {@code null} to clear
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
