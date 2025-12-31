/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../metaschema/metaschema-bindings.yaml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.config.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.BooleanAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The root element for a set of metaschema binding customizations.
 */
@MetaschemaAssembly(
    formalName = "Metaschema Bindings",
    description = "The root element for a set of metaschema binding customizations.",
    name = "metaschema-bindings",
    moduleClass = MetaschemaBindingsModule.class,
    rootName = "metaschema-bindings")
public class MetaschemaBindings implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * Defines binding configurations that apply to a whole model described by a
   * namespace.
   */
  @BoundAssembly(
      formalName = "Model Binding",
      description = "Defines binding configurations that apply to a whole model described by a namespace.",
      useName = "model-binding",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "model-bindings", inJson = JsonGroupAsBehavior.LIST))
  private List<ModelBinding> _modelBindings;

  /**
   * Defines a binding for a given metaschema identified by a relative URL.
   */
  @BoundAssembly(
      formalName = "Metaschema Binding",
      description = "Defines a binding for a given metaschema identified by a relative URL.",
      useName = "metaschema-binding",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "metaschema-bindings", inJson = JsonGroupAsBehavior.LIST))
  private List<MetaschemaBinding> _metaschemaBindings;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings}
   * instance with no metadata.
   */
  public MetaschemaBindings() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public MetaschemaBindings(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the model Binding.
   *
   * <p>
   * Defines binding configurations that apply to a whole model described by a
   * namespace.
   *
   * @return the model-binding value
   */
  @NonNull
  public List<ModelBinding> getModelBindings() {
    if (_modelBindings == null) {
      _modelBindings = new LinkedList<>();
    }
    return ObjectUtils.notNull(_modelBindings);
  }

  /**
   * Set the model Binding.
   *
   * <p>
   * Defines binding configurations that apply to a whole model described by a
   * namespace.
   *
   * @param value
   *          the model-binding value to set
   */
  public void setModelBindings(@NonNull List<ModelBinding> value) {
    _modelBindings = value;
  }

  /**
   * Add a new {@link ModelBinding} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addModelBinding(ModelBinding item) {
    ModelBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_modelBindings == null) {
      _modelBindings = new LinkedList<>();
    }
    return _modelBindings.add(value);
  }

  /**
   * Remove the first matching {@link ModelBinding} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeModelBinding(ModelBinding item) {
    ModelBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _modelBindings != null && _modelBindings.remove(value);
  }

  /**
   * Get the metaschema Binding.
   *
   * <p>
   * Defines a binding for a given metaschema identified by a relative URL.
   *
   * @return the metaschema-binding value
   */
  @NonNull
  public List<MetaschemaBinding> getMetaschemaBindings() {
    if (_metaschemaBindings == null) {
      _metaschemaBindings = new LinkedList<>();
    }
    return ObjectUtils.notNull(_metaschemaBindings);
  }

  /**
   * Set the metaschema Binding.
   *
   * <p>
   * Defines a binding for a given metaschema identified by a relative URL.
   *
   * @param value
   *          the metaschema-binding value to set
   */
  public void setMetaschemaBindings(@NonNull List<MetaschemaBinding> value) {
    _metaschemaBindings = value;
  }

  /**
   * Add a new {@link MetaschemaBinding} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addMetaschemaBinding(MetaschemaBinding item) {
    MetaschemaBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_metaschemaBindings == null) {
      _metaschemaBindings = new LinkedList<>();
    }
    return _metaschemaBindings.add(value);
  }

  /**
   * Remove the first matching {@link MetaschemaBinding} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeMetaschemaBinding(MetaschemaBinding item) {
    MetaschemaBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _metaschemaBindings != null && _metaschemaBindings.remove(value);
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }

  /**
   * Defines binding configurations that apply to a whole model described by a
   * namespace.
   */
  @MetaschemaAssembly(
      formalName = "Model Binding",
      description = "Defines binding configurations that apply to a whole model described by a namespace.",
      name = "model-binding",
      moduleClass = MetaschemaBindingsModule.class)
  public static class ModelBinding implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    /**
     * A URI referencing the namespace of one or more related metaschema
     * definitions.
     */
    @BoundFlag(
        formalName = "Namespace",
        description = "A URI referencing the namespace of one or more related metaschema definitions.",
        name = "namespace",
        required = true,
        typeAdapter = UriAdapter.class)
    private URI _namespace;

    /**
     * Java-specific binding configuration for a model namespace.
     */
    @BoundAssembly(
        formalName = "Java Model Binding",
        description = "Java-specific binding configuration for a model namespace.",
        useName = "java")
    private Java _java;

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.ModelBinding}
     * instance with no metadata.
     */
    public ModelBinding() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.ModelBinding}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public ModelBinding(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the namespace.
     *
     * <p>
     * A URI referencing the namespace of one or more related metaschema
     * definitions.
     *
     * @return the namespace value
     */
    @NonNull
    public URI getNamespace() {
      return _namespace;
    }

    /**
     * Set the namespace.
     *
     * <p>
     * A URI referencing the namespace of one or more related metaschema
     * definitions.
     *
     * @param value
     *          the namespace value to set
     */
    public void setNamespace(@NonNull URI value) {
      _namespace = value;
    }

    /**
     * Get the java Model Binding.
     *
     * <p>
     * Java-specific binding configuration for a model namespace.
     *
     * @return the java value, or {@code null} if not set
     */
    @Nullable
    public Java getJava() {
      return _java;
    }

    /**
     * Set the java Model Binding.
     *
     * <p>
     * Java-specific binding configuration for a model namespace.
     *
     * @param value
     *          the java value to set, or {@code null} to clear
     */
    public void setJava(@Nullable Java value) {
      _java = value;
    }

    @Override
    public String toString() {
      return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
    }

    /**
     * Java-specific binding configuration for a model namespace.
     */
    @MetaschemaAssembly(
        formalName = "Java Model Binding",
        description = "Java-specific binding configuration for a model namespace.",
        name = "java",
        moduleClass = MetaschemaBindingsModule.class)
    public static class Java implements IBoundObject {
      private final IMetaschemaData __metaschemaData;

      /**
       * The Java package name to use for classes generated from this namespace.
       */
      @BoundField(
          formalName = "Use Package Name",
          description = "The Java package name to use for classes generated from this namespace.",
          useName = "use-package-name",
          typeAdapter = TokenAdapter.class)
      private String _usePackageName;

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.ModelBinding.Java}
       * instance with no metadata.
       */
      public Java() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.ModelBinding.Java}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public Java(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the use Package Name.
       *
       * <p>
       * The Java package name to use for classes generated from this namespace.
       *
       * @return the use-package-name value, or {@code null} if not set
       */
      @Nullable
      public String getUsePackageName() {
        return _usePackageName;
      }

      /**
       * Set the use Package Name.
       *
       * <p>
       * The Java package name to use for classes generated from this namespace.
       *
       * @param value
       *          the use-package-name value to set, or {@code null} to clear
       */
      public void setUsePackageName(@Nullable String value) {
        _usePackageName = value;
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }
  }

  /**
   * Defines a binding for a given metaschema identified by a relative URL.
   */
  @MetaschemaAssembly(
      formalName = "Metaschema Binding",
      description = "Defines a binding for a given metaschema identified by a relative URL.",
      name = "metaschema-binding",
      moduleClass = MetaschemaBindingsModule.class)
  public static class MetaschemaBinding implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    /**
     * A URL relative to this binding configuration file, pointing to a metaschema
     * definition.
     */
    @BoundFlag(
        formalName = "Href",
        description = "A URL relative to this binding configuration file, pointing to a metaschema definition.",
        name = "href",
        required = true,
        typeAdapter = UriReferenceAdapter.class)
    private URI _href;

    /**
     * Provides binding configurations for a given defined assembly within the
     * parent metaschema.
     */
    @BoundAssembly(
        formalName = "Define Assembly Binding",
        description = "Provides binding configurations for a given defined assembly within the parent metaschema.",
        useName = "define-assembly-binding",
        maxOccurs = -1,
        groupAs = @GroupAs(name = "define-assembly-bindings", inJson = JsonGroupAsBehavior.LIST))
    private List<DefineAssemblyBinding> _defineAssemblyBindings;

    /**
     * Provides binding configurations for a given defined field within the parent
     * metaschema.
     */
    @BoundAssembly(
        formalName = "Define Field Binding",
        description = "Provides binding configurations for a given defined field within the parent metaschema.",
        useName = "define-field-binding",
        maxOccurs = -1,
        groupAs = @GroupAs(name = "define-field-bindings", inJson = JsonGroupAsBehavior.LIST))
    private List<DefineFieldBinding> _defineFieldBindings;

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding}
     * instance with no metadata.
     */
    public MetaschemaBinding() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public MetaschemaBinding(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the href.
     *
     * <p>
     * A URL relative to this binding configuration file, pointing to a metaschema
     * definition.
     *
     * @return the href value
     */
    @NonNull
    public URI getHref() {
      return _href;
    }

    /**
     * Set the href.
     *
     * <p>
     * A URL relative to this binding configuration file, pointing to a metaschema
     * definition.
     *
     * @param value
     *          the href value to set
     */
    public void setHref(@NonNull URI value) {
      _href = value;
    }

    /**
     * Get the define Assembly Binding.
     *
     * <p>
     * Provides binding configurations for a given defined assembly within the
     * parent metaschema.
     *
     * @return the define-assembly-binding value
     */
    @NonNull
    public List<DefineAssemblyBinding> getDefineAssemblyBindings() {
      if (_defineAssemblyBindings == null) {
        _defineAssemblyBindings = new LinkedList<>();
      }
      return ObjectUtils.notNull(_defineAssemblyBindings);
    }

    /**
     * Set the define Assembly Binding.
     *
     * <p>
     * Provides binding configurations for a given defined assembly within the
     * parent metaschema.
     *
     * @param value
     *          the define-assembly-binding value to set
     */
    public void setDefineAssemblyBindings(@NonNull List<DefineAssemblyBinding> value) {
      _defineAssemblyBindings = value;
    }

    /**
     * Add a new {@link DefineAssemblyBinding} item to the underlying collection.
     *
     * @param item
     *          the item to add
     * @return {@code true}
     */
    public boolean addDefineAssemblyBinding(DefineAssemblyBinding item) {
      DefineAssemblyBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
      if (_defineAssemblyBindings == null) {
        _defineAssemblyBindings = new LinkedList<>();
      }
      return _defineAssemblyBindings.add(value);
    }

    /**
     * Remove the first matching {@link DefineAssemblyBinding} item from the
     * underlying collection.
     *
     * @param item
     *          the item to remove
     * @return {@code true} if the item was removed or {@code false} otherwise
     */
    public boolean removeDefineAssemblyBinding(DefineAssemblyBinding item) {
      DefineAssemblyBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
      return _defineAssemblyBindings != null && _defineAssemblyBindings.remove(value);
    }

    /**
     * Get the define Field Binding.
     *
     * <p>
     * Provides binding configurations for a given defined field within the parent
     * metaschema.
     *
     * @return the define-field-binding value
     */
    @NonNull
    public List<DefineFieldBinding> getDefineFieldBindings() {
      if (_defineFieldBindings == null) {
        _defineFieldBindings = new LinkedList<>();
      }
      return ObjectUtils.notNull(_defineFieldBindings);
    }

    /**
     * Set the define Field Binding.
     *
     * <p>
     * Provides binding configurations for a given defined field within the parent
     * metaschema.
     *
     * @param value
     *          the define-field-binding value to set
     */
    public void setDefineFieldBindings(@NonNull List<DefineFieldBinding> value) {
      _defineFieldBindings = value;
    }

    /**
     * Add a new {@link DefineFieldBinding} item to the underlying collection.
     *
     * @param item
     *          the item to add
     * @return {@code true}
     */
    public boolean addDefineFieldBinding(DefineFieldBinding item) {
      DefineFieldBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
      if (_defineFieldBindings == null) {
        _defineFieldBindings = new LinkedList<>();
      }
      return _defineFieldBindings.add(value);
    }

    /**
     * Remove the first matching {@link DefineFieldBinding} item from the underlying
     * collection.
     *
     * @param item
     *          the item to remove
     * @return {@code true} if the item was removed or {@code false} otherwise
     */
    public boolean removeDefineFieldBinding(DefineFieldBinding item) {
      DefineFieldBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
      return _defineFieldBindings != null && _defineFieldBindings.remove(value);
    }

    @Override
    public String toString() {
      return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
    }

    /**
     * Provides binding configurations for a given defined assembly within the
     * parent metaschema.
     */
    @MetaschemaAssembly(
        formalName = "Define Assembly Binding",
        description = "Provides binding configurations for a given defined assembly within the parent metaschema.",
        name = "define-assembly-binding",
        moduleClass = MetaschemaBindingsModule.class)
    public static class DefineAssemblyBinding implements IBoundObject {
      private final IMetaschemaData __metaschemaData;

      /**
       * The name of the metaschema assembly. Used for top-level definitions.
       */
      @BoundFlag(
          formalName = "Name",
          description = "The name of the metaschema assembly. Used for top-level definitions.",
          name = "name",
          typeAdapter = TokenAdapter.class)
      private String _name;

      /**
       * A Metapath expression targeting the assembly definition(s) within the
       * metaschema. Used for inline definitions.
       */
      @BoundFlag(
          formalName = "Target",
          description = "A Metapath expression targeting the assembly definition(s) within the metaschema. Used for inline definitions.",
          name = "target",
          typeAdapter = StringAdapter.class)
      private String _target;

      /**
       * Field and assembly binding configurations for Java bound classes.
       */
      @BoundAssembly(
          formalName = "Java Object Definition Binding",
          description = "Field and assembly binding configurations for Java bound classes.",
          useName = "java")
      private Java _java;

      /**
       * Provides binding configurations for a property within the parent definition.
       */
      @BoundAssembly(
          formalName = "Property Binding",
          description = "Provides binding configurations for a property within the parent definition.",
          useName = "property-binding",
          maxOccurs = -1,
          groupAs = @GroupAs(name = "property-bindings", inJson = JsonGroupAsBehavior.LIST))
      private List<PropertyBinding> _propertyBindings;

      /**
       * Provides binding configuration for a choice group within the parent assembly.
       */
      @BoundAssembly(
          formalName = "Choice Group Binding",
          description = "Provides binding configuration for a choice group within the parent assembly.",
          useName = "choice-group-binding",
          maxOccurs = -1,
          groupAs = @GroupAs(name = "choice-group-bindings", inJson = JsonGroupAsBehavior.LIST))
      private List<ChoiceGroupBinding> _choiceGroupBindings;

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding}
       * instance with no metadata.
       */
      public DefineAssemblyBinding() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public DefineAssemblyBinding(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the name.
       *
       * <p>
       * The name of the metaschema assembly. Used for top-level definitions.
       *
       * @return the name value, or {@code null} if not set
       */
      @Nullable
      public String getName() {
        return _name;
      }

      /**
       * Set the name.
       *
       * <p>
       * The name of the metaschema assembly. Used for top-level definitions.
       *
       * @param value
       *          the name value to set, or {@code null} to clear
       */
      public void setName(@Nullable String value) {
        _name = value;
      }

      /**
       * Get the target.
       *
       * <p>
       * A Metapath expression targeting the assembly definition(s) within the
       * metaschema. Used for inline definitions.
       *
       * @return the target value, or {@code null} if not set
       */
      @Nullable
      public String getTarget() {
        return _target;
      }

      /**
       * Set the target.
       *
       * <p>
       * A Metapath expression targeting the assembly definition(s) within the
       * metaschema. Used for inline definitions.
       *
       * @param value
       *          the target value to set, or {@code null} to clear
       */
      public void setTarget(@Nullable String value) {
        _target = value;
      }

      /**
       * Get the java Object Definition Binding.
       *
       * <p>
       * Field and assembly binding configurations for Java bound classes.
       *
       * @return the java value, or {@code null} if not set
       */
      @Nullable
      public Java getJava() {
        return _java;
      }

      /**
       * Set the java Object Definition Binding.
       *
       * <p>
       * Field and assembly binding configurations for Java bound classes.
       *
       * @param value
       *          the java value to set, or {@code null} to clear
       */
      public void setJava(@Nullable Java value) {
        _java = value;
      }

      /**
       * Get the property Binding.
       *
       * <p>
       * Provides binding configurations for a property within the parent definition.
       *
       * @return the property-binding value
       */
      @NonNull
      public List<PropertyBinding> getPropertyBindings() {
        if (_propertyBindings == null) {
          _propertyBindings = new LinkedList<>();
        }
        return ObjectUtils.notNull(_propertyBindings);
      }

      /**
       * Set the property Binding.
       *
       * <p>
       * Provides binding configurations for a property within the parent definition.
       *
       * @param value
       *          the property-binding value to set
       */
      public void setPropertyBindings(@NonNull List<PropertyBinding> value) {
        _propertyBindings = value;
      }

      /**
       * Add a new {@link PropertyBinding} item to the underlying collection.
       *
       * @param item
       *          the item to add
       * @return {@code true}
       */
      public boolean addPropertyBinding(PropertyBinding item) {
        PropertyBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
        if (_propertyBindings == null) {
          _propertyBindings = new LinkedList<>();
        }
        return _propertyBindings.add(value);
      }

      /**
       * Remove the first matching {@link PropertyBinding} item from the underlying
       * collection.
       *
       * @param item
       *          the item to remove
       * @return {@code true} if the item was removed or {@code false} otherwise
       */
      public boolean removePropertyBinding(PropertyBinding item) {
        PropertyBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
        return _propertyBindings != null && _propertyBindings.remove(value);
      }

      /**
       * Get the choice Group Binding.
       *
       * <p>
       * Provides binding configuration for a choice group within the parent assembly.
       *
       * @return the choice-group-binding value
       */
      @NonNull
      public List<ChoiceGroupBinding> getChoiceGroupBindings() {
        if (_choiceGroupBindings == null) {
          _choiceGroupBindings = new LinkedList<>();
        }
        return ObjectUtils.notNull(_choiceGroupBindings);
      }

      /**
       * Set the choice Group Binding.
       *
       * <p>
       * Provides binding configuration for a choice group within the parent assembly.
       *
       * @param value
       *          the choice-group-binding value to set
       */
      public void setChoiceGroupBindings(@NonNull List<ChoiceGroupBinding> value) {
        _choiceGroupBindings = value;
      }

      /**
       * Add a new {@link ChoiceGroupBinding} item to the underlying collection.
       *
       * @param item
       *          the item to add
       * @return {@code true}
       */
      public boolean addChoiceGroupBinding(ChoiceGroupBinding item) {
        ChoiceGroupBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
        if (_choiceGroupBindings == null) {
          _choiceGroupBindings = new LinkedList<>();
        }
        return _choiceGroupBindings.add(value);
      }

      /**
       * Remove the first matching {@link ChoiceGroupBinding} item from the underlying
       * collection.
       *
       * @param item
       *          the item to remove
       * @return {@code true} if the item was removed or {@code false} otherwise
       */
      public boolean removeChoiceGroupBinding(ChoiceGroupBinding item) {
        ChoiceGroupBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
        return _choiceGroupBindings != null && _choiceGroupBindings.remove(value);
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }

      /**
       * Field and assembly binding configurations for Java bound classes.
       */
      @MetaschemaAssembly(
          formalName = "Java Object Definition Binding",
          description = "Field and assembly binding configurations for Java bound classes.",
          name = "java",
          moduleClass = MetaschemaBindingsModule.class)
      public static class Java implements IBoundObject {
        private final IMetaschemaData __metaschemaData;

        /**
         * The Java class name to use for the generated class.
         */
        @BoundField(
            formalName = "Use Class Name",
            description = "The Java class name to use for the generated class.",
            useName = "use-class-name",
            typeAdapter = TokenAdapter.class)
        private String _useClassName;

        /**
         * A fully qualified Java interface name that the generated class should
         * implement.
         */
        @BoundField(
            formalName = "Implement Interface",
            description = "A fully qualified Java interface name that the generated class should implement.",
            useName = "implement-interface",
            maxOccurs = -1,
            groupAs = @GroupAs(name = "implement-interfaces", inJson = JsonGroupAsBehavior.LIST),
            typeAdapter = TokenAdapter.class)
        private List<String> _implementInterfaces;

        /**
         * A fully qualified Java class name that the generated class should extend.
         */
        @BoundField(
            formalName = "Extend Base Class",
            description = "A fully qualified Java class name that the generated class should extend.",
            useName = "extend-base-class",
            typeAdapter = TokenAdapter.class)
        private String _extendBaseClass;

        /**
         * A fully qualified Java collection class name to use instead of the default.
         */
        @BoundField(
            formalName = "Collection Class",
            description = "A fully qualified Java collection class name to use instead of the default.",
            useName = "collection-class",
            typeAdapter = TokenAdapter.class)
        private String _collectionClass;

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.Java}
         * instance with no metadata.
         */
        public Java() {
          this(null);
        }

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.Java}
         * instance with the specified metadata.
         *
         * @param data
         *          the metaschema data, or {@code null} if none
         */
        public Java(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        /**
         * Get the use Class Name.
         *
         * <p>
         * The Java class name to use for the generated class.
         *
         * @return the use-class-name value, or {@code null} if not set
         */
        @Nullable
        public String getUseClassName() {
          return _useClassName;
        }

        /**
         * Set the use Class Name.
         *
         * <p>
         * The Java class name to use for the generated class.
         *
         * @param value
         *          the use-class-name value to set, or {@code null} to clear
         */
        public void setUseClassName(@Nullable String value) {
          _useClassName = value;
        }

        /**
         * Get the implement Interface.
         *
         * <p>
         * A fully qualified Java interface name that the generated class should
         * implement.
         *
         * @return the implement-interface value
         */
        @NonNull
        public List<String> getImplementInterfaces() {
          if (_implementInterfaces == null) {
            _implementInterfaces = new LinkedList<>();
          }
          return ObjectUtils.notNull(_implementInterfaces);
        }

        /**
         * Set the implement Interface.
         *
         * <p>
         * A fully qualified Java interface name that the generated class should
         * implement.
         *
         * @param value
         *          the implement-interface value to set
         */
        public void setImplementInterfaces(@NonNull List<String> value) {
          _implementInterfaces = value;
        }

        /**
         * Add a new {@link String} item to the underlying collection.
         *
         * @param item
         *          the item to add
         * @return {@code true}
         */
        public boolean addImplementInterface(String item) {
          String value = ObjectUtils.requireNonNull(item, "item cannot be null");
          if (_implementInterfaces == null) {
            _implementInterfaces = new LinkedList<>();
          }
          return _implementInterfaces.add(value);
        }

        /**
         * Remove the first matching {@link String} item from the underlying collection.
         *
         * @param item
         *          the item to remove
         * @return {@code true} if the item was removed or {@code false} otherwise
         */
        public boolean removeImplementInterface(String item) {
          String value = ObjectUtils.requireNonNull(item, "item cannot be null");
          return _implementInterfaces != null && _implementInterfaces.remove(value);
        }

        /**
         * Get the extend Base Class.
         *
         * <p>
         * A fully qualified Java class name that the generated class should extend.
         *
         * @return the extend-base-class value, or {@code null} if not set
         */
        @Nullable
        public String getExtendBaseClass() {
          return _extendBaseClass;
        }

        /**
         * Set the extend Base Class.
         *
         * <p>
         * A fully qualified Java class name that the generated class should extend.
         *
         * @param value
         *          the extend-base-class value to set, or {@code null} to clear
         */
        public void setExtendBaseClass(@Nullable String value) {
          _extendBaseClass = value;
        }

        /**
         * Get the collection Class.
         *
         * <p>
         * A fully qualified Java collection class name to use instead of the default.
         *
         * @return the collection-class value, or {@code null} if not set
         */
        @Nullable
        public String getCollectionClass() {
          return _collectionClass;
        }

        /**
         * Set the collection Class.
         *
         * <p>
         * A fully qualified Java collection class name to use instead of the default.
         *
         * @param value
         *          the collection-class value to set, or {@code null} to clear
         */
        public void setCollectionClass(@Nullable String value) {
          _collectionClass = value;
        }

        @Override
        public String toString() {
          return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
        }
      }

      /**
       * Provides binding configurations for a property within the parent definition.
       */
      @MetaschemaAssembly(
          formalName = "Property Binding",
          description = "Provides binding configurations for a property within the parent definition.",
          name = "property-binding",
          moduleClass = MetaschemaBindingsModule.class)
      public static class PropertyBinding implements IBoundObject {
        private final IMetaschemaData __metaschemaData;

        /**
         * The name of the property within the parent definition.
         */
        @BoundFlag(
            formalName = "Name",
            description = "The name of the property within the parent definition.",
            name = "name",
            required = true,
            typeAdapter = TokenAdapter.class)
        private String _name;

        /**
         * Java-specific binding configuration for a property.
         */
        @BoundAssembly(
            formalName = "Java Property Binding",
            description = "Java-specific binding configuration for a property.",
            useName = "java")
        private Java _java;

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding}
         * instance with no metadata.
         */
        public PropertyBinding() {
          this(null);
        }

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding}
         * instance with the specified metadata.
         *
         * @param data
         *          the metaschema data, or {@code null} if none
         */
        public PropertyBinding(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        /**
         * Get the name.
         *
         * <p>
         * The name of the property within the parent definition.
         *
         * @return the name value
         */
        @NonNull
        public String getName() {
          return _name;
        }

        /**
         * Set the name.
         *
         * <p>
         * The name of the property within the parent definition.
         *
         * @param value
         *          the name value to set
         */
        public void setName(@NonNull String value) {
          _name = value;
        }

        /**
         * Get the java Property Binding.
         *
         * <p>
         * Java-specific binding configuration for a property.
         *
         * @return the java value, or {@code null} if not set
         */
        @Nullable
        public Java getJava() {
          return _java;
        }

        /**
         * Set the java Property Binding.
         *
         * <p>
         * Java-specific binding configuration for a property.
         *
         * @param value
         *          the java value to set, or {@code null} to clear
         */
        public void setJava(@Nullable Java value) {
          _java = value;
        }

        @Override
        public String toString() {
          return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
        }

        /**
         * Java-specific binding configuration for a property.
         */
        @MetaschemaAssembly(
            formalName = "Java Property Binding",
            description = "Java-specific binding configuration for a property.",
            name = "java",
            moduleClass = MetaschemaBindingsModule.class)
        public static class Java implements IBoundObject {
          private final IMetaschemaData __metaschemaData;

          /**
           * A fully qualified Java collection class name to use instead of the default.
           */
          @BoundField(
              formalName = "Collection Class",
              description = "A fully qualified Java collection class name to use instead of the default.",
              useName = "collection-class",
              typeAdapter = TokenAdapter.class)
          private String _collectionClass;

          /**
           * Constructs a new
           * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding.Java}
           * instance with no metadata.
           */
          public Java() {
            this(null);
          }

          /**
           * Constructs a new
           * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding.Java}
           * instance with the specified metadata.
           *
           * @param data
           *          the metaschema data, or {@code null} if none
           */
          public Java(IMetaschemaData data) {
            this.__metaschemaData = data;
          }

          @Override
          public IMetaschemaData getMetaschemaData() {
            return __metaschemaData;
          }

          /**
           * Get the collection Class.
           *
           * <p>
           * A fully qualified Java collection class name to use instead of the default.
           *
           * @return the collection-class value, or {@code null} if not set
           */
          @Nullable
          public String getCollectionClass() {
            return _collectionClass;
          }

          /**
           * Set the collection Class.
           *
           * <p>
           * A fully qualified Java collection class name to use instead of the default.
           *
           * @param value
           *          the collection-class value to set, or {@code null} to clear
           */
          public void setCollectionClass(@Nullable String value) {
            _collectionClass = value;
          }

          @Override
          public String toString() {
            return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
          }
        }
      }

      /**
       * Provides binding configuration for a choice group within the parent assembly.
       */
      @MetaschemaAssembly(
          formalName = "Choice Group Binding",
          description = "Provides binding configuration for a choice group within the parent assembly.",
          name = "choice-group-binding",
          moduleClass = MetaschemaBindingsModule.class)
      public static class ChoiceGroupBinding implements IBoundObject {
        private final IMetaschemaData __metaschemaData;

        /**
         * The name of the choice group (matches the group-as name in the metaschema).
         */
        @BoundFlag(
            formalName = "Name",
            description = "The name of the choice group (matches the group-as name in the metaschema).",
            name = "name",
            required = true,
            typeAdapter = TokenAdapter.class)
        private String _name;

        /**
         * A fully qualified Java type for collection items. When specified, the
         * generated field and getter will use this type instead of Object.
         */
        @BoundField(
            formalName = "Item Type",
            description = "A fully qualified Java type for collection items. When specified, the generated field and getter will use this type instead of Object.",
            useName = "item-type")
        private ItemType _itemType;

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding}
         * instance with no metadata.
         */
        public ChoiceGroupBinding() {
          this(null);
        }

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding}
         * instance with the specified metadata.
         *
         * @param data
         *          the metaschema data, or {@code null} if none
         */
        public ChoiceGroupBinding(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        /**
         * Get the name.
         *
         * <p>
         * The name of the choice group (matches the group-as name in the metaschema).
         *
         * @return the name value
         */
        @NonNull
        public String getName() {
          return _name;
        }

        /**
         * Set the name.
         *
         * <p>
         * The name of the choice group (matches the group-as name in the metaschema).
         *
         * @param value
         *          the name value to set
         */
        public void setName(@NonNull String value) {
          _name = value;
        }

        /**
         * Get the item Type.
         *
         * <p>
         * A fully qualified Java type for collection items. When specified, the
         * generated field and getter will use this type instead of Object.
         *
         * @return the item-type value, or {@code null} if not set
         */
        @Nullable
        public ItemType getItemType() {
          return _itemType;
        }

        /**
         * Set the item Type.
         *
         * <p>
         * A fully qualified Java type for collection items. When specified, the
         * generated field and getter will use this type instead of Object.
         *
         * @param value
         *          the item-type value to set, or {@code null} to clear
         */
        public void setItemType(@Nullable ItemType value) {
          _itemType = value;
        }

        @Override
        public String toString() {
          return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
        }

        /**
         * A fully qualified Java type for collection items. When specified, the
         * generated field and getter will use this type instead of Object.
         */
        @MetaschemaField(
            formalName = "Item Type",
            description = "A fully qualified Java type for collection items. When specified, the generated field and getter will use this type instead of Object.",
            name = "item-type",
            moduleClass = MetaschemaBindingsModule.class)
        public static class ItemType implements IBoundObject {
          private final IMetaschemaData __metaschemaData;

          /**
           * Whether to use a wildcard bounded type (List&lt;? extends Type&gt;). Defaults
           * to true.
           */
          @BoundFlag(
              formalName = "Use Wildcard",
              description = "Whether to use a wildcard bounded type (List<? extends Type>). Defaults to true.",
              name = "use-wildcard",
              defaultValue = "true",
              typeAdapter = BooleanAdapter.class)
          private Boolean _useWildcard;

          @BoundFieldValue(
              valueKeyName = "STRVALUE",
              typeAdapter = TokenAdapter.class)
          private String _value;

          /**
           * Constructs a new
           * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding.ItemType}
           * instance with no metadata.
           */
          public ItemType() {
            this(null);
          }

          /**
           * Constructs a new
           * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding.ItemType}
           * instance with the specified metadata.
           *
           * @param data
           *          the metaschema data, or {@code null} if none
           */
          public ItemType(IMetaschemaData data) {
            this.__metaschemaData = data;
          }

          @Override
          public IMetaschemaData getMetaschemaData() {
            return __metaschemaData;
          }

          /**
           * Get the use Wildcard.
           *
           * <p>
           * Whether to use a wildcard bounded type (List&lt;? extends Type&gt;). Defaults
           * to true.
           *
           * @return the use-wildcard value, or {@code null} if not set
           */
          @Nullable
          public Boolean getUseWildcard() {
            return _useWildcard;
          }

          /**
           * Set the use Wildcard.
           *
           * <p>
           * Whether to use a wildcard bounded type (List&lt;? extends Type&gt;). Defaults
           * to true.
           *
           * @param value
           *          the use-wildcard value to set, or {@code null} to clear
           */
          public void setUseWildcard(@Nullable Boolean value) {
            _useWildcard = value;
          }

          @Nullable
          public String getValue() {
            return _value;
          }

          public void setValue(@Nullable String value) {
            _value = value;
          }

          @Override
          public String toString() {
            return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
          }
        }
      }
    }

    /**
     * Provides binding configurations for a given defined field within the parent
     * metaschema.
     */
    @MetaschemaAssembly(
        formalName = "Define Field Binding",
        description = "Provides binding configurations for a given defined field within the parent metaschema.",
        name = "define-field-binding",
        moduleClass = MetaschemaBindingsModule.class)
    public static class DefineFieldBinding implements IBoundObject {
      private final IMetaschemaData __metaschemaData;

      /**
       * The name of the metaschema field. Used for top-level definitions.
       */
      @BoundFlag(
          formalName = "Name",
          description = "The name of the metaschema field. Used for top-level definitions.",
          name = "name",
          typeAdapter = TokenAdapter.class)
      private String _name;

      /**
       * A Metapath expression targeting the field definition(s) within the
       * metaschema. Used for inline definitions.
       */
      @BoundFlag(
          formalName = "Target",
          description = "A Metapath expression targeting the field definition(s) within the metaschema. Used for inline definitions.",
          name = "target",
          typeAdapter = StringAdapter.class)
      private String _target;

      /**
       * Field and assembly binding configurations for Java bound classes.
       */
      @BoundAssembly(
          formalName = "Java Object Definition Binding",
          description = "Field and assembly binding configurations for Java bound classes.",
          useName = "java")
      private Java _java;

      /**
       * Provides binding configurations for a property within the parent definition.
       */
      @BoundAssembly(
          formalName = "Property Binding",
          description = "Provides binding configurations for a property within the parent definition.",
          useName = "property-binding",
          maxOccurs = -1,
          groupAs = @GroupAs(name = "property-bindings", inJson = JsonGroupAsBehavior.LIST))
      private List<PropertyBinding> _propertyBindings;

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding}
       * instance with no metadata.
       */
      public DefineFieldBinding() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public DefineFieldBinding(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the name.
       *
       * <p>
       * The name of the metaschema field. Used for top-level definitions.
       *
       * @return the name value, or {@code null} if not set
       */
      @Nullable
      public String getName() {
        return _name;
      }

      /**
       * Set the name.
       *
       * <p>
       * The name of the metaschema field. Used for top-level definitions.
       *
       * @param value
       *          the name value to set, or {@code null} to clear
       */
      public void setName(@Nullable String value) {
        _name = value;
      }

      /**
       * Get the target.
       *
       * <p>
       * A Metapath expression targeting the field definition(s) within the
       * metaschema. Used for inline definitions.
       *
       * @return the target value, or {@code null} if not set
       */
      @Nullable
      public String getTarget() {
        return _target;
      }

      /**
       * Set the target.
       *
       * <p>
       * A Metapath expression targeting the field definition(s) within the
       * metaschema. Used for inline definitions.
       *
       * @param value
       *          the target value to set, or {@code null} to clear
       */
      public void setTarget(@Nullable String value) {
        _target = value;
      }

      /**
       * Get the java Object Definition Binding.
       *
       * <p>
       * Field and assembly binding configurations for Java bound classes.
       *
       * @return the java value, or {@code null} if not set
       */
      @Nullable
      public Java getJava() {
        return _java;
      }

      /**
       * Set the java Object Definition Binding.
       *
       * <p>
       * Field and assembly binding configurations for Java bound classes.
       *
       * @param value
       *          the java value to set, or {@code null} to clear
       */
      public void setJava(@Nullable Java value) {
        _java = value;
      }

      /**
       * Get the property Binding.
       *
       * <p>
       * Provides binding configurations for a property within the parent definition.
       *
       * @return the property-binding value
       */
      @NonNull
      public List<PropertyBinding> getPropertyBindings() {
        if (_propertyBindings == null) {
          _propertyBindings = new LinkedList<>();
        }
        return ObjectUtils.notNull(_propertyBindings);
      }

      /**
       * Set the property Binding.
       *
       * <p>
       * Provides binding configurations for a property within the parent definition.
       *
       * @param value
       *          the property-binding value to set
       */
      public void setPropertyBindings(@NonNull List<PropertyBinding> value) {
        _propertyBindings = value;
      }

      /**
       * Add a new {@link PropertyBinding} item to the underlying collection.
       *
       * @param item
       *          the item to add
       * @return {@code true}
       */
      public boolean addPropertyBinding(PropertyBinding item) {
        PropertyBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
        if (_propertyBindings == null) {
          _propertyBindings = new LinkedList<>();
        }
        return _propertyBindings.add(value);
      }

      /**
       * Remove the first matching {@link PropertyBinding} item from the underlying
       * collection.
       *
       * @param item
       *          the item to remove
       * @return {@code true} if the item was removed or {@code false} otherwise
       */
      public boolean removePropertyBinding(PropertyBinding item) {
        PropertyBinding value = ObjectUtils.requireNonNull(item, "item cannot be null");
        return _propertyBindings != null && _propertyBindings.remove(value);
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }

      /**
       * Field and assembly binding configurations for Java bound classes.
       */
      @MetaschemaAssembly(
          formalName = "Java Object Definition Binding",
          description = "Field and assembly binding configurations for Java bound classes.",
          name = "java",
          moduleClass = MetaschemaBindingsModule.class)
      public static class Java implements IBoundObject {
        private final IMetaschemaData __metaschemaData;

        /**
         * The Java class name to use for the generated class.
         */
        @BoundField(
            formalName = "Use Class Name",
            description = "The Java class name to use for the generated class.",
            useName = "use-class-name",
            typeAdapter = TokenAdapter.class)
        private String _useClassName;

        /**
         * A fully qualified Java interface name that the generated class should
         * implement.
         */
        @BoundField(
            formalName = "Implement Interface",
            description = "A fully qualified Java interface name that the generated class should implement.",
            useName = "implement-interface",
            maxOccurs = -1,
            groupAs = @GroupAs(name = "implement-interfaces", inJson = JsonGroupAsBehavior.LIST),
            typeAdapter = TokenAdapter.class)
        private List<String> _implementInterfaces;

        /**
         * A fully qualified Java class name that the generated class should extend.
         */
        @BoundField(
            formalName = "Extend Base Class",
            description = "A fully qualified Java class name that the generated class should extend.",
            useName = "extend-base-class",
            typeAdapter = TokenAdapter.class)
        private String _extendBaseClass;

        /**
         * A fully qualified Java collection class name to use instead of the default.
         */
        @BoundField(
            formalName = "Collection Class",
            description = "A fully qualified Java collection class name to use instead of the default.",
            useName = "collection-class",
            typeAdapter = TokenAdapter.class)
        private String _collectionClass;

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.Java}
         * instance with no metadata.
         */
        public Java() {
          this(null);
        }

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.Java}
         * instance with the specified metadata.
         *
         * @param data
         *          the metaschema data, or {@code null} if none
         */
        public Java(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        /**
         * Get the use Class Name.
         *
         * <p>
         * The Java class name to use for the generated class.
         *
         * @return the use-class-name value, or {@code null} if not set
         */
        @Nullable
        public String getUseClassName() {
          return _useClassName;
        }

        /**
         * Set the use Class Name.
         *
         * <p>
         * The Java class name to use for the generated class.
         *
         * @param value
         *          the use-class-name value to set, or {@code null} to clear
         */
        public void setUseClassName(@Nullable String value) {
          _useClassName = value;
        }

        /**
         * Get the implement Interface.
         *
         * <p>
         * A fully qualified Java interface name that the generated class should
         * implement.
         *
         * @return the implement-interface value
         */
        @NonNull
        public List<String> getImplementInterfaces() {
          if (_implementInterfaces == null) {
            _implementInterfaces = new LinkedList<>();
          }
          return ObjectUtils.notNull(_implementInterfaces);
        }

        /**
         * Set the implement Interface.
         *
         * <p>
         * A fully qualified Java interface name that the generated class should
         * implement.
         *
         * @param value
         *          the implement-interface value to set
         */
        public void setImplementInterfaces(@NonNull List<String> value) {
          _implementInterfaces = value;
        }

        /**
         * Add a new {@link String} item to the underlying collection.
         *
         * @param item
         *          the item to add
         * @return {@code true}
         */
        public boolean addImplementInterface(String item) {
          String value = ObjectUtils.requireNonNull(item, "item cannot be null");
          if (_implementInterfaces == null) {
            _implementInterfaces = new LinkedList<>();
          }
          return _implementInterfaces.add(value);
        }

        /**
         * Remove the first matching {@link String} item from the underlying collection.
         *
         * @param item
         *          the item to remove
         * @return {@code true} if the item was removed or {@code false} otherwise
         */
        public boolean removeImplementInterface(String item) {
          String value = ObjectUtils.requireNonNull(item, "item cannot be null");
          return _implementInterfaces != null && _implementInterfaces.remove(value);
        }

        /**
         * Get the extend Base Class.
         *
         * <p>
         * A fully qualified Java class name that the generated class should extend.
         *
         * @return the extend-base-class value, or {@code null} if not set
         */
        @Nullable
        public String getExtendBaseClass() {
          return _extendBaseClass;
        }

        /**
         * Set the extend Base Class.
         *
         * <p>
         * A fully qualified Java class name that the generated class should extend.
         *
         * @param value
         *          the extend-base-class value to set, or {@code null} to clear
         */
        public void setExtendBaseClass(@Nullable String value) {
          _extendBaseClass = value;
        }

        /**
         * Get the collection Class.
         *
         * <p>
         * A fully qualified Java collection class name to use instead of the default.
         *
         * @return the collection-class value, or {@code null} if not set
         */
        @Nullable
        public String getCollectionClass() {
          return _collectionClass;
        }

        /**
         * Set the collection Class.
         *
         * <p>
         * A fully qualified Java collection class name to use instead of the default.
         *
         * @param value
         *          the collection-class value to set, or {@code null} to clear
         */
        public void setCollectionClass(@Nullable String value) {
          _collectionClass = value;
        }

        @Override
        public String toString() {
          return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
        }
      }

      /**
       * Provides binding configurations for a property within the parent definition.
       */
      @MetaschemaAssembly(
          formalName = "Property Binding",
          description = "Provides binding configurations for a property within the parent definition.",
          name = "property-binding",
          moduleClass = MetaschemaBindingsModule.class)
      public static class PropertyBinding implements IBoundObject {
        private final IMetaschemaData __metaschemaData;

        /**
         * The name of the property within the parent definition.
         */
        @BoundFlag(
            formalName = "Name",
            description = "The name of the property within the parent definition.",
            name = "name",
            required = true,
            typeAdapter = TokenAdapter.class)
        private String _name;

        /**
         * Java-specific binding configuration for a property.
         */
        @BoundAssembly(
            formalName = "Java Property Binding",
            description = "Java-specific binding configuration for a property.",
            useName = "java")
        private Java _java;

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding}
         * instance with no metadata.
         */
        public PropertyBinding() {
          this(null);
        }

        /**
         * Constructs a new
         * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding}
         * instance with the specified metadata.
         *
         * @param data
         *          the metaschema data, or {@code null} if none
         */
        public PropertyBinding(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        /**
         * Get the name.
         *
         * <p>
         * The name of the property within the parent definition.
         *
         * @return the name value
         */
        @NonNull
        public String getName() {
          return _name;
        }

        /**
         * Set the name.
         *
         * <p>
         * The name of the property within the parent definition.
         *
         * @param value
         *          the name value to set
         */
        public void setName(@NonNull String value) {
          _name = value;
        }

        /**
         * Get the java Property Binding.
         *
         * <p>
         * Java-specific binding configuration for a property.
         *
         * @return the java value, or {@code null} if not set
         */
        @Nullable
        public Java getJava() {
          return _java;
        }

        /**
         * Set the java Property Binding.
         *
         * <p>
         * Java-specific binding configuration for a property.
         *
         * @param value
         *          the java value to set, or {@code null} to clear
         */
        public void setJava(@Nullable Java value) {
          _java = value;
        }

        @Override
        public String toString() {
          return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
        }

        /**
         * Java-specific binding configuration for a property.
         */
        @MetaschemaAssembly(
            formalName = "Java Property Binding",
            description = "Java-specific binding configuration for a property.",
            name = "java",
            moduleClass = MetaschemaBindingsModule.class)
        public static class Java implements IBoundObject {
          private final IMetaschemaData __metaschemaData;

          /**
           * A fully qualified Java collection class name to use instead of the default.
           */
          @BoundField(
              formalName = "Collection Class",
              description = "A fully qualified Java collection class name to use instead of the default.",
              useName = "collection-class",
              typeAdapter = TokenAdapter.class)
          private String _collectionClass;

          /**
           * Constructs a new
           * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding.Java}
           * instance with no metadata.
           */
          public Java() {
            this(null);
          }

          /**
           * Constructs a new
           * {@code gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding.Java}
           * instance with the specified metadata.
           *
           * @param data
           *          the metaschema data, or {@code null} if none
           */
          public Java(IMetaschemaData data) {
            this.__metaschemaData = data;
          }

          @Override
          public IMetaschemaData getMetaschemaData() {
            return __metaschemaData;
          }

          /**
           * Get the collection Class.
           *
           * <p>
           * A fully qualified Java collection class name to use instead of the default.
           *
           * @return the collection-class value, or {@code null} if not set
           */
          @Nullable
          public String getCollectionClass() {
            return _collectionClass;
          }

          /**
           * Set the collection Class.
           *
           * <p>
           * A fully qualified Java collection class name to use instead of the default.
           *
           * @param value
           *          the collection-class value to set, or {@code null} to clear
           */
          public void setCollectionClass(@Nullable String value) {
            _collectionClass = value;
          }

          @Override
          public String toString() {
            return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
          }
        }
      }
    }
  }
}
