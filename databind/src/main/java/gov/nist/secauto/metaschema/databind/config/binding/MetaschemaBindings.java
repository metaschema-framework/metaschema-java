/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.config.binding;

import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
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

  @BoundAssembly(
      formalName = "Model Binding",
      description = "Defines binding configurations that apply to a whole model described by a namespace.",
      useName = "model-binding",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "model-bindings", inJson = JsonGroupAsBehavior.LIST))
  private List<ModelBinding> _modelBindings;

  @BoundAssembly(
      formalName = "Metaschema Binding",
      description = "Defines a binding for a given metaschema identified by a relative URL.",
      useName = "metaschema-binding",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "metaschema-bindings", inJson = JsonGroupAsBehavior.LIST))
  private List<MetaschemaBinding> _metaschemaBindings;

  public MetaschemaBindings() {
    this(null);
  }

  public MetaschemaBindings(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  public List<ModelBinding> getModelBindings() {
    return _modelBindings;
  }

  public void setModelBindings(List<ModelBinding> value) {
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

  public List<MetaschemaBinding> getMetaschemaBindings() {
    return _metaschemaBindings;
  }

  public void setMetaschemaBindings(List<MetaschemaBinding> value) {
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
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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
     * "A URI referencing the namespace of one or more related metaschema
     * definitions."
     */
    @BoundFlag(
        formalName = "Namespace",
        description = "A URI referencing the namespace of one or more related metaschema definitions.",
        name = "namespace",
        typeAdapter = UriAdapter.class)
    private URI _namespace;

    @BoundAssembly(
        formalName = "Java Model Binding",
        description = "Java-specific binding configuration for a model namespace.",
        useName = "java")
    private Java _java;

    public ModelBinding() {
      this(null);
    }

    public ModelBinding(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    public URI getNamespace() {
      return _namespace;
    }

    public void setNamespace(URI value) {
      _namespace = value;
    }

    public Java getJava() {
      return _java;
    }

    public void setJava(Java value) {
      _java = value;
    }

    @Override
    public String toString() {
      return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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

      @BoundField(
          formalName = "Use Package Name",
          description = "The Java package name to use for classes generated from this namespace.",
          useName = "use-package-name",
          typeAdapter = TokenAdapter.class)
      private String _usePackageName;

      public Java() {
        this(null);
      }

      public Java(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      public String getUsePackageName() {
        return _usePackageName;
      }

      public void setUsePackageName(String value) {
        _usePackageName = value;
      }

      @Override
      public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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
     * "A URL relative to this binding configuration file, pointing to a metaschema
     * definition."
     */
    @BoundFlag(
        formalName = "Href",
        description = "A URL relative to this binding configuration file, pointing to a metaschema definition.",
        name = "href",
        typeAdapter = UriReferenceAdapter.class)
    private URI _href;

    @BoundAssembly(
        formalName = "Define Assembly Binding",
        description = "Provides binding configurations for a given defined assembly within the parent metaschema.",
        useName = "define-assembly-binding",
        maxOccurs = -1,
        groupAs = @GroupAs(name = "define-assembly-bindings", inJson = JsonGroupAsBehavior.LIST))
    private List<DefineAssemblyBinding> _defineAssemblyBindings;

    @BoundAssembly(
        formalName = "Define Field Binding",
        description = "Provides binding configurations for a given defined field within the parent metaschema.",
        useName = "define-field-binding",
        maxOccurs = -1,
        groupAs = @GroupAs(name = "define-field-bindings", inJson = JsonGroupAsBehavior.LIST))
    private List<DefineFieldBinding> _defineFieldBindings;

    public MetaschemaBinding() {
      this(null);
    }

    public MetaschemaBinding(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    public URI getHref() {
      return _href;
    }

    public void setHref(URI value) {
      _href = value;
    }

    public List<DefineAssemblyBinding> getDefineAssemblyBindings() {
      return _defineAssemblyBindings;
    }

    public void setDefineAssemblyBindings(List<DefineAssemblyBinding> value) {
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

    public List<DefineFieldBinding> getDefineFieldBindings() {
      return _defineFieldBindings;
    }

    public void setDefineFieldBindings(List<DefineFieldBinding> value) {
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
      return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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
       * "The name of the metaschema assembly."
       */
      @BoundFlag(
          formalName = "Name",
          description = "The name of the metaschema assembly.",
          name = "name",
          typeAdapter = TokenAdapter.class)
      private String _name;

      @BoundAssembly(
          formalName = "Java Object Definition Binding",
          description = "Field and assembly binding configurations for Java bound classes.",
          useName = "java")
      private Java _java;

      public DefineAssemblyBinding() {
        this(null);
      }

      public DefineAssemblyBinding(IMetaschemaData data) {
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

      public Java getJava() {
        return _java;
      }

      public void setJava(Java value) {
        _java = value;
      }

      @Override
      public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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

        @BoundField(
            formalName = "Use Class Name",
            description = "The Java class name to use for the generated class.",
            useName = "use-class-name",
            typeAdapter = TokenAdapter.class)
        private String _useClassName;

        @BoundField(
            formalName = "Implement Interface",
            description = "A fully qualified Java interface name that the generated class should implement.",
            useName = "implement-interface",
            maxOccurs = -1,
            groupAs = @GroupAs(name = "implement-interfaces", inJson = JsonGroupAsBehavior.LIST),
            typeAdapter = TokenAdapter.class)
        private List<String> _implementInterfaces;

        @BoundField(
            formalName = "Extend Base Class",
            description = "A fully qualified Java class name that the generated class should extend.",
            useName = "extend-base-class",
            typeAdapter = TokenAdapter.class)
        private String _extendBaseClass;

        public Java() {
          this(null);
        }

        public Java(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        public String getUseClassName() {
          return _useClassName;
        }

        public void setUseClassName(String value) {
          _useClassName = value;
        }

        public List<String> getImplementInterfaces() {
          return _implementInterfaces;
        }

        public void setImplementInterfaces(List<String> value) {
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

        public String getExtendBaseClass() {
          return _extendBaseClass;
        }

        public void setExtendBaseClass(String value) {
          _extendBaseClass = value;
        }

        @Override
        public String toString() {
          return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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
       * "The name of the metaschema field."
       */
      @BoundFlag(
          formalName = "Name",
          description = "The name of the metaschema field.",
          name = "name",
          typeAdapter = TokenAdapter.class)
      private String _name;

      @BoundAssembly(
          formalName = "Java Object Definition Binding",
          description = "Field and assembly binding configurations for Java bound classes.",
          useName = "java")
      private Java _java;

      public DefineFieldBinding() {
        this(null);
      }

      public DefineFieldBinding(IMetaschemaData data) {
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

      public Java getJava() {
        return _java;
      }

      public void setJava(Java value) {
        _java = value;
      }

      @Override
      public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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

        @BoundField(
            formalName = "Use Class Name",
            description = "The Java class name to use for the generated class.",
            useName = "use-class-name",
            typeAdapter = TokenAdapter.class)
        private String _useClassName;

        @BoundField(
            formalName = "Implement Interface",
            description = "A fully qualified Java interface name that the generated class should implement.",
            useName = "implement-interface",
            maxOccurs = -1,
            groupAs = @GroupAs(name = "implement-interfaces", inJson = JsonGroupAsBehavior.LIST),
            typeAdapter = TokenAdapter.class)
        private List<String> _implementInterfaces;

        @BoundField(
            formalName = "Extend Base Class",
            description = "A fully qualified Java class name that the generated class should extend.",
            useName = "extend-base-class",
            typeAdapter = TokenAdapter.class)
        private String _extendBaseClass;

        public Java() {
          this(null);
        }

        public Java(IMetaschemaData data) {
          this.__metaschemaData = data;
        }

        @Override
        public IMetaschemaData getMetaschemaData() {
          return __metaschemaData;
        }

        public String getUseClassName() {
          return _useClassName;
        }

        public void setUseClassName(String value) {
          _useClassName = value;
        }

        public List<String> getImplementInterfaces() {
          return _implementInterfaces;
        }

        public void setImplementInterfaces(List<String> value) {
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

        public String getExtendBaseClass() {
          return _extendBaseClass;
        }

        public void setExtendBaseClass(String value) {
          _extendBaseClass = value;
        }

        @Override
        public String toString() {
          return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
        }
      }
    }
  }
}
