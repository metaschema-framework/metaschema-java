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
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoice;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
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
    name = "assembly-model",
    moduleClass = MetaschemaModelModule.class)
public class AssemblyModel implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundChoiceGroup(
      maxOccurs = -1,
      groupAs = @GroupAs(name = "instances", inJson = JsonGroupAsBehavior.LIST),
      assemblies = {
          @BoundGroupedAssembly(formalName = "Assembly Reference", useName = "assembly",
              discriminatorValue = "assembly-ref", binding = AssemblyReference.class),
          @BoundGroupedAssembly(formalName = "Inline Assembly Definition", useName = "define-assembly",
              discriminatorValue = "assembly", binding = InlineDefineAssembly.class),
          @BoundGroupedAssembly(formalName = "Field Reference", useName = "field", discriminatorValue = "field-ref",
              binding = FieldReference.class),
          @BoundGroupedAssembly(formalName = "Inline Field Definition", useName = "define-field",
              discriminatorValue = "field", binding = InlineDefineField.class),
          @BoundGroupedAssembly(formalName = "Choice", useName = "choice", binding = Choice.class),
          @BoundGroupedAssembly(formalName = "Choice Grouping", useName = "choice-group", binding = ChoiceGroup.class)
      })
  private List<Object> _instances;

  @BoundAssembly(
      formalName = "Any Additional Content",
      useName = "any")
  private Any _any;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel}
   * instance with no metadata.
   */
  public AssemblyModel() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public AssemblyModel(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the {@code instances} choice group items.
   *
   * @return the instances items
   */
  @NonNull
  public List<Object> getInstances() {
    if (_instances == null) {
      _instances = new LinkedList<>();
    }
    return ObjectUtils.notNull(_instances);
  }

  /**
   * Set the {@code instances} choice group items.
   *
   * @param value
   *          the instances items to set
   */
  public void setInstances(@NonNull List<Object> value) {
    _instances = value;
  }

  /**
   * Get the any Additional Content.
   *
   * @return the any value, or {@code null} if not set
   */
  @Nullable
  public Any getAny() {
    return _any;
  }

  /**
   * Set the any Additional Content.
   *
   * @param value
   *          the any value to set, or {@code null} to clear
   */
  public void setAny(@Nullable Any value) {
    _any = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }

  @MetaschemaAssembly(
      formalName = "Choice",
      name = "choice",
      moduleClass = MetaschemaModelModule.class)
  public static class Choice implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundChoiceGroup(
        minOccurs = 1,
        maxOccurs = -1,
        groupAs = @GroupAs(name = "choices", inJson = JsonGroupAsBehavior.LIST),
        assemblies = {
            @BoundGroupedAssembly(formalName = "Assembly Reference", useName = "assembly",
                discriminatorValue = "assembly-ref", binding = AssemblyReference.class),
            @BoundGroupedAssembly(formalName = "Inline Assembly Definition", useName = "define-assembly",
                discriminatorValue = "assembly", binding = InlineDefineAssembly.class),
            @BoundGroupedAssembly(formalName = "Field Reference", useName = "field", discriminatorValue = "field-ref",
                binding = FieldReference.class),
            @BoundGroupedAssembly(formalName = "Inline Field Definition", useName = "define-field",
                discriminatorValue = "field", binding = InlineDefineField.class)
        })
    private List<Object> _choices;

    @BoundAssembly(
        formalName = "Any Additional Content",
        useName = "any")
    private Any _any;

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.Choice}
     * instance with no metadata.
     */
    public Choice() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.Choice}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public Choice(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the {@code choices} choice group items.
     *
     * @return the choices items
     */
    @NonNull
    public List<Object> getChoices() {
      if (_choices == null) {
        _choices = new LinkedList<>();
      }
      return ObjectUtils.notNull(_choices);
    }

    /**
     * Set the {@code choices} choice group items.
     *
     * @param value
     *          the choices items to set
     */
    public void setChoices(@NonNull List<Object> value) {
      _choices = value;
    }

    /**
     * Get the any Additional Content.
     *
     * @return the any value, or {@code null} if not set
     */
    @Nullable
    public Any getAny() {
      return _any;
    }

    /**
     * Set the any Additional Content.
     *
     * @param value
     *          the any value to set, or {@code null} to clear
     */
    public void setAny(@Nullable Any value) {
      _any = value;
    }

    @Override
    public String toString() {
      return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
    }
  }

  @MetaschemaAssembly(
      formalName = "Choice Grouping",
      name = "choice-group",
      moduleClass = MetaschemaModelModule.class)
  public static class ChoiceGroup implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundFlag(
        formalName = "Minimum Occurrence",
        name = "min-occurs",
        defaultValue = "0",
        typeAdapter = NonNegativeIntegerAdapter.class)
    private BigInteger _minOccurs;

    @BoundFlag(
        formalName = "Maximum Occurrence",
        name = "max-occurs",
        defaultValue = "unbounded",
        typeAdapter = StringAdapter.class,
        valueConstraints = @ValueConstraints(
            matches = @Matches(level = IConstraint.Level.ERROR, pattern = "^[1-9][0-9]*|unbounded$")))
    private String _maxOccurs;

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
        useName = "group-as",
        minOccurs = 1)
    private GroupingAs _groupAs;

    @BoundField(
        formalName = "Discriminator JSON Property",
        useName = "discriminator",
        defaultValue = "object-type",
        typeAdapter = TokenAdapter.class)
    private String _discriminator;

    @BoundChoiceGroup(
        minOccurs = 1,
        maxOccurs = -1,
        groupAs = @GroupAs(name = "choices", inJson = JsonGroupAsBehavior.LIST),
        assemblies = {
            @BoundGroupedAssembly(formalName = "Grouping Assembly Reference", useName = "assembly",
                discriminatorValue = "assembly-ref", binding = Assembly.class),
            @BoundGroupedAssembly(formalName = "Inline Assembly Definition", useName = "define-assembly",
                discriminatorValue = "assembly", binding = DefineAssembly.class),
            @BoundGroupedAssembly(formalName = "Grouping Field Reference", useName = "field",
                discriminatorValue = "field-ref", binding = Field.class),
            @BoundGroupedAssembly(formalName = "Inline Field Definition", useName = "define-field",
                discriminatorValue = "field", binding = DefineField.class)
        })
    private List<Object> _choices;

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
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup}
     * instance with no metadata.
     */
    public ChoiceGroup() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public ChoiceGroup(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
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
     * @return the group-as value
     */
    @NonNull
    public GroupingAs getGroupAs() {
      return _groupAs;
    }

    /**
     * Set the group As.
     *
     * @param value
     *          the group-as value to set
     */
    public void setGroupAs(@NonNull GroupingAs value) {
      _groupAs = value;
    }

    /**
     * Get the discriminator JSON Property.
     *
     * @return the discriminator value, or {@code null} if not set
     */
    @Nullable
    public String getDiscriminator() {
      return _discriminator;
    }

    /**
     * Set the discriminator JSON Property.
     *
     * @param value
     *          the discriminator value to set, or {@code null} to clear
     */
    public void setDiscriminator(@Nullable String value) {
      _discriminator = value;
    }

    /**
     * Get the {@code choices} choice group items.
     *
     * @return the choices items
     */
    @NonNull
    public List<Object> getChoices() {
      if (_choices == null) {
        _choices = new LinkedList<>();
      }
      return ObjectUtils.notNull(_choices);
    }

    /**
     * Set the {@code choices} choice group items.
     *
     * @param value
     *          the choices items to set
     */
    public void setChoices(@NonNull List<Object> value) {
      _choices = value;
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

    @Override
    public String toString() {
      return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
    }

    @MetaschemaAssembly(
        formalName = "Grouping Assembly Reference",
        name = "assembly",
        moduleClass = MetaschemaModelModule.class)
    public static class Assembly implements IBoundObject {
      private final IMetaschemaData __metaschemaData;

      @BoundFlag(
          formalName = "Global Assembly Reference",
          name = "ref",
          required = true,
          typeAdapter = TokenAdapter.class)
      private String _ref;

      @BoundFlag(
          formalName = "Assembly Reference Binary Name",
          name = "index",
          typeAdapter = PositiveIntegerAdapter.class)
      private BigInteger _index;

      @BoundFlag(
          formalName = "Deprecated Version",
          name = "deprecated",
          typeAdapter = StringAdapter.class)
      private String _deprecated;

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

      @BoundField(
          formalName = "Grouping Discriminator Value",
          useName = "discriminator-value",
          typeAdapter = TokenAdapter.class)
      private String _discriminatorValue;

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
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.Assembly}
       * instance with no metadata.
       */
      public Assembly() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.Assembly}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public Assembly(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the global Assembly Reference.
       *
       * @return the ref value
       */
      @NonNull
      public String getRef() {
        return _ref;
      }

      /**
       * Set the global Assembly Reference.
       *
       * @param value
       *          the ref value to set
       */
      public void setRef(@NonNull String value) {
        _ref = value;
      }

      /**
       * Get the assembly Reference Binary Name.
       *
       * @return the index value, or {@code null} if not set
       */
      @Nullable
      public BigInteger getIndex() {
        return _index;
      }

      /**
       * Set the assembly Reference Binary Name.
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
       *          the use-name value to set, or {@code null} to clear
       */
      public void setUseName(@Nullable UseName value) {
        _useName = value;
      }

      /**
       * Get the grouping Discriminator Value.
       *
       * @return the discriminator-value value, or {@code null} if not set
       */
      @Nullable
      public String getDiscriminatorValue() {
        return _discriminatorValue;
      }

      /**
       * Set the grouping Discriminator Value.
       *
       * @param value
       *          the discriminator-value value to set, or {@code null} to clear
       */
      public void setDiscriminatorValue(@Nullable String value) {
        _discriminatorValue = value;
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

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }

    @MetaschemaAssembly(
        formalName = "Inline Assembly Definition",
        name = "define-assembly",
        moduleClass = MetaschemaModelModule.class)
    public static class DefineAssembly implements IBoundObject {
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
          formalName = "Grouping Discriminator Value",
          useName = "discriminator-value",
          typeAdapter = TokenAdapter.class)
      private String _discriminatorValue;

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
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.DefineAssembly}
       * instance with no metadata.
       */
      public DefineAssembly() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.DefineAssembly}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public DefineAssembly(IMetaschemaData data) {
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
       * Get the grouping Discriminator Value.
       *
       * @return the discriminator-value value, or {@code null} if not set
       */
      @Nullable
      public String getDiscriminatorValue() {
        return _discriminatorValue;
      }

      /**
       * Set the grouping Discriminator Value.
       *
       * @param value
       *          the discriminator-value value to set, or {@code null} to clear
       */
      public void setDiscriminatorValue(@Nullable String value) {
        _discriminatorValue = value;
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

    @MetaschemaAssembly(
        formalName = "Grouping Field Reference",
        name = "field",
        moduleClass = MetaschemaModelModule.class)
    public static class Field implements IBoundObject {
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
          formalName = "Field In XML",
          name = "in-xml",
          defaultValue = "WRAPPED",
          typeAdapter = TokenAdapter.class,
          valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
              values = { @AllowedValue(value = "WRAPPED",
                  description = "Block contents of a markup-multiline field will be represented with a containing (wrapper) element in the XML."),
                  @AllowedValue(value = "UNWRAPPED",
                      description = "Block contents of a markup-multiline will be represented in the XML with no wrapper, making the field implicit. Among sibling fields in a given model, only one of them may be designated as UNWRAPPED."),
                  @AllowedValue(value = "WITH_WRAPPER", description = "Alias for WRAPPED.",
                      deprecatedVersion = "0.9.0") })))
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

      @BoundField(
          formalName = "Grouping Discriminator Value",
          useName = "discriminator-value",
          typeAdapter = TokenAdapter.class)
      private String _discriminatorValue;

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
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.Field}
       * instance with no metadata.
       */
      public Field() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.Field}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public Field(IMetaschemaData data) {
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
       *          the default value to set, or {@code null} to clear
       */
      public void setDefault(@Nullable String value) {
        _default = value;
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
       *          the in-xml value to set, or {@code null} to clear
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
       *          the use-name value to set, or {@code null} to clear
       */
      public void setUseName(@Nullable UseName value) {
        _useName = value;
      }

      /**
       * Get the grouping Discriminator Value.
       *
       * @return the discriminator-value value, or {@code null} if not set
       */
      @Nullable
      public String getDiscriminatorValue() {
        return _discriminatorValue;
      }

      /**
       * Set the grouping Discriminator Value.
       *
       * @param value
       *          the discriminator-value value to set, or {@code null} to clear
       */
      public void setDiscriminatorValue(@Nullable String value) {
        _discriminatorValue = value;
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

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }

    @MetaschemaAssembly(
        formalName = "Inline Field Definition",
        name = "define-field",
        moduleClass = MetaschemaModelModule.class)
    public static class DefineField implements IBoundObject {
      private final IMetaschemaData __metaschemaData;

      @BoundFlag(
          formalName = "Inline Field Name",
          name = "name",
          required = true,
          typeAdapter = TokenAdapter.class)
      private String _name;

      @BoundFlag(
          formalName = "Inline Field Binary Name",
          name = "index",
          typeAdapter = PositiveIntegerAdapter.class)
      private BigInteger _index;

      @BoundFlag(
          formalName = "Deprecated Version",
          name = "deprecated",
          typeAdapter = StringAdapter.class)
      private String _deprecated;

      @BoundFlag(
          formalName = "Field Value Data Type",
          name = "as-type",
          defaultValue = "string",
          typeAdapter = TokenAdapter.class,
          valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
              allowOthers = true,
              values = { @AllowedValue(value = "markup-line",
                  description = "The [markup-line](https://framework.metaschema.dev/specification/datatypes/#markup-line) data type."),
                  @AllowedValue(value = "markup-multiline",
                      description = "The [markup-multiline](https://framework.metaschema.dev/specification/datatypes/#markup-multiline) data type."),
                  @AllowedValue(value = "base64",
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
          formalName = "Default Field Value",
          name = "default",
          typeAdapter = StringAdapter.class)
      private String _default;

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
          formalName = "Grouping Discriminator Value",
          useName = "discriminator-value",
          typeAdapter = TokenAdapter.class)
      private String _discriminatorValue;

      @BoundField(
          formalName = "Field Value JSON Property Name",
          useName = "json-value-key",
          typeAdapter = TokenAdapter.class)
      @BoundChoice(
          choiceId = "choice-1")
      private String _jsonValueKey;

      @BoundAssembly(
          formalName = "Flag Used as the Field Value's JSON Property Name",
          useName = "json-value-key-flag")
      @BoundChoice(
          choiceId = "choice-1")
      private JsonValueKeyFlag _jsonValueKeyFlag;

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
          useName = "constraint")
      private FieldConstraints _constraint;

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
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.DefineField}
       * instance with no metadata.
       */
      public DefineField() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup.DefineField}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public DefineField(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the inline Field Name.
       *
       * @return the name value
       */
      @NonNull
      public String getName() {
        return _name;
      }

      /**
       * Set the inline Field Name.
       *
       * @param value
       *          the name value to set
       */
      public void setName(@NonNull String value) {
        _name = value;
      }

      /**
       * Get the inline Field Binary Name.
       *
       * @return the index value, or {@code null} if not set
       */
      @Nullable
      public BigInteger getIndex() {
        return _index;
      }

      /**
       * Set the inline Field Binary Name.
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
       * Get the field Value Data Type.
       *
       * @return the as-type value, or {@code null} if not set
       */
      @Nullable
      public String getAsType() {
        return _asType;
      }

      /**
       * Set the field Value Data Type.
       *
       * @param value
       *          the as-type value to set, or {@code null} to clear
       */
      public void setAsType(@Nullable String value) {
        _asType = value;
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
       *          the default value to set, or {@code null} to clear
       */
      public void setDefault(@Nullable String value) {
        _default = value;
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
       * Get the grouping Discriminator Value.
       *
       * @return the discriminator-value value, or {@code null} if not set
       */
      @Nullable
      public String getDiscriminatorValue() {
        return _discriminatorValue;
      }

      /**
       * Set the grouping Discriminator Value.
       *
       * @param value
       *          the discriminator-value value to set, or {@code null} to clear
       */
      public void setDiscriminatorValue(@Nullable String value) {
        _discriminatorValue = value;
      }

      /**
       * Get the field Value JSON Property Name.
       *
       * @return the json-value-key value, or {@code null} if not set
       */
      @Nullable
      public String getJsonValueKey() {
        return _jsonValueKey;
      }

      /**
       * Set the field Value JSON Property Name.
       *
       * @param value
       *          the json-value-key value to set, or {@code null} to clear
       */
      public void setJsonValueKey(@Nullable String value) {
        _jsonValueKey = value;
      }

      /**
       * Get the flag Used as the Field Value's JSON Property Name.
       *
       * @return the json-value-key-flag value, or {@code null} if not set
       */
      @Nullable
      public JsonValueKeyFlag getJsonValueKeyFlag() {
        return _jsonValueKeyFlag;
      }

      /**
       * Set the flag Used as the Field Value's JSON Property Name.
       *
       * @param value
       *          the json-value-key-flag value to set, or {@code null} to clear
       */
      public void setJsonValueKeyFlag(@Nullable JsonValueKeyFlag value) {
        _jsonValueKeyFlag = value;
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
       * Get the {@code constraint} property.
       *
       * @return the constraint value, or {@code null} if not set
       */
      @Nullable
      public FieldConstraints getConstraint() {
        return _constraint;
      }

      /**
       * Set the {@code constraint} property.
       *
       * @param value
       *          the constraint value to set, or {@code null} to clear
       */
      public void setConstraint(@Nullable FieldConstraints value) {
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
  }
}
