/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package dev.metaschema.databind.model.metaschema.binding;

import dev.metaschema.core.datatype.adapter.StringAdapter;
import dev.metaschema.core.datatype.adapter.TokenAdapter;
import dev.metaschema.core.datatype.adapter.UriAdapter;
import dev.metaschema.core.datatype.adapter.UriReferenceAdapter;
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
import dev.metaschema.databind.model.annotations.Expect;
import dev.metaschema.databind.model.annotations.GroupAs;
import dev.metaschema.databind.model.annotations.Let;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import dev.metaschema.databind.model.annotations.ValueConstraints;
import dev.metaschema.databind.model.metaschema.IConstraintBase;
import dev.metaschema.databind.model.metaschema.ITargetedConstraintBase;
import dev.metaschema.databind.model.metaschema.IValueConstraintsBase;
import dev.metaschema.databind.model.metaschema.IValueTargetedConstraintsBase;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Defines constraint rules to be applied to an existing set of Metaschema
 * module-based models.
 */
@MetaschemaAssembly(
    formalName = "External Module Constraints",
    description = "Defines constraint rules to be applied to an existing set of Metaschema module-based models.",
    name = "metaschema-module-constraints",
    moduleClass = MetaschemaModelModule.class,
    rootName = "METASCHEMA-CONSTRAINTS",
    valueConstraints = @ValueConstraints(lets = @Let(name = "deprecated-type-map",
        target = "map { 'base64Binary':'base64','dateTime':'date-time','dateTime-with-timezone':'date-time-with-timezone','email':'email-address','nonNegativeInteger':'non-negative-integer','positiveInteger':'positive-integer' }"),
        expect = @Expect(id = "metaschema-deprecated-types", formalName = "Avoid Deprecated Data Type Use",
            description = "Ensure that the data type specified is not one of the legacy Metaschema data types which have been deprecated (i.e. base64Binary, dateTime, dateTime-with-timezone, email, nonNegativeInteger, positiveInteger).",
            level = IConstraint.Level.WARNING, target = ".//matches/@datatype|.//(define-field|define-flag)/@as-type",
            test = "not(data(.)=('base64Binary','dateTime','dateTime-with-timezone','email','nonNegativeInteger','positiveInteger'))",
            message = "Use of the type '{ data(.) }' is deprecated. Use '{ $deprecated-type-map(data(.))}' instead.")))
public class MetaschemaModuleConstraints implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * The name of this constraint set.
   */
  @BoundField(
      description = "The name of this constraint set.",
      useName = "name",
      minOccurs = 1,
      typeAdapter = StringAdapter.class)
  private String _name;

  /**
   * The version of this constraint set. A version string used to distinguish
   * between multiple revisions of the same resource.
   */
  @BoundField(
      description = "The version of this constraint set. A version string used to distinguish between multiple revisions of the same resource.",
      useName = "version",
      minOccurs = 1,
      typeAdapter = StringAdapter.class)
  private String _version;

  /**
   * Declares a set of Metaschema constraints from an out-of-line resource to
   * import, supporting composition of constraint sets.
   */
  @BoundAssembly(
      description = "Declares a set of Metaschema constraints from an out-of-line resource to import, supporting composition of constraint sets.",
      useName = "import",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "imports", inJson = JsonGroupAsBehavior.LIST))
  private List<Import> _imports;

  /**
   * Assigns a Metapath namespace to a prefix for use in a Metapath expression in
   * a lexical qualified name.
   */
  @BoundAssembly(
      formalName = "Metapath Namespace Declaration",
      description = "Assigns a Metapath namespace to a prefix for use in a Metapath expression in a lexical qualified name.",
      useName = "namespace-binding",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "namespace-bindings", inJson = JsonGroupAsBehavior.LIST))
  private List<MetapathNamespace> _namespaceBindings;

  @BoundAssembly(
      useName = "scope",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "scopes", inJson = JsonGroupAsBehavior.LIST))
  private List<Scope> _scopes;

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints}
   * instance with no metadata.
   */
  public MetaschemaModuleConstraints() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public MetaschemaModuleConstraints(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the {@code name} property.
   *
   * <p>
   * The name of this constraint set.
   *
   * @return the name value
   */
  @NonNull
  public String getName() {
    return _name;
  }

  /**
   * Set the {@code name} property.
   *
   * <p>
   * The name of this constraint set.
   *
   * @param value
   *          the name value to set
   */
  public void setName(@NonNull String value) {
    _name = value;
  }

  /**
   * Get the {@code version} property.
   *
   * <p>
   * The version of this constraint set. A version string used to distinguish
   * between multiple revisions of the same resource.
   *
   * @return the version value
   */
  @NonNull
  public String getVersion() {
    return _version;
  }

  /**
   * Set the {@code version} property.
   *
   * <p>
   * The version of this constraint set. A version string used to distinguish
   * between multiple revisions of the same resource.
   *
   * @param value
   *          the version value to set
   */
  public void setVersion(@NonNull String value) {
    _version = value;
  }

  /**
   * Get the {@code import} property.
   *
   * <p>
   * Declares a set of Metaschema constraints from an out-of-line resource to
   * import, supporting composition of constraint sets.
   *
   * @return the import value
   */
  @NonNull
  public List<Import> getImports() {
    if (_imports == null) {
      _imports = new LinkedList<>();
    }
    return ObjectUtils.notNull(_imports);
  }

  /**
   * Set the {@code import} property.
   *
   * <p>
   * Declares a set of Metaschema constraints from an out-of-line resource to
   * import, supporting composition of constraint sets.
   *
   * @param value
   *          the import value to set
   */
  public void setImports(@NonNull List<Import> value) {
    _imports = value;
  }

  /**
   * Add a new {@link Import} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addImport(Import item) {
    Import value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_imports == null) {
      _imports = new LinkedList<>();
    }
    return _imports.add(value);
  }

  /**
   * Remove the first matching {@link Import} item from the underlying collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeImport(Import item) {
    Import value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _imports != null && _imports.remove(value);
  }

  /**
   * Get the metapath Namespace Declaration.
   *
   * <p>
   * Assigns a Metapath namespace to a prefix for use in a Metapath expression in
   * a lexical qualified name.
   *
   * @return the namespace-binding value
   */
  @NonNull
  public List<MetapathNamespace> getNamespaceBindings() {
    if (_namespaceBindings == null) {
      _namespaceBindings = new LinkedList<>();
    }
    return ObjectUtils.notNull(_namespaceBindings);
  }

  /**
   * Set the metapath Namespace Declaration.
   *
   * <p>
   * Assigns a Metapath namespace to a prefix for use in a Metapath expression in
   * a lexical qualified name.
   *
   * @param value
   *          the namespace-binding value to set
   */
  public void setNamespaceBindings(@NonNull List<MetapathNamespace> value) {
    _namespaceBindings = value;
  }

  /**
   * Add a new {@link MetapathNamespace} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addNamespaceBinding(MetapathNamespace item) {
    MetapathNamespace value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_namespaceBindings == null) {
      _namespaceBindings = new LinkedList<>();
    }
    return _namespaceBindings.add(value);
  }

  /**
   * Remove the first matching {@link MetapathNamespace} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeNamespaceBinding(MetapathNamespace item) {
    MetapathNamespace value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _namespaceBindings != null && _namespaceBindings.remove(value);
  }

  /**
   * Get the {@code scope} property.
   *
   * @return the scope value
   */
  @NonNull
  public List<Scope> getScopes() {
    if (_scopes == null) {
      _scopes = new LinkedList<>();
    }
    return ObjectUtils.notNull(_scopes);
  }

  /**
   * Set the {@code scope} property.
   *
   * @param value
   *          the scope value to set
   */
  public void setScopes(@NonNull List<Scope> value) {
    _scopes = value;
  }

  /**
   * Add a new {@link Scope} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addScope(Scope item) {
    Scope value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_scopes == null) {
      _scopes = new LinkedList<>();
    }
    return _scopes.add(value);
  }

  /**
   * Remove the first matching {@link Scope} item from the underlying collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeScope(Scope item) {
    Scope value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _scopes != null && _scopes.remove(value);
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }

  /**
   * Declares a set of Metaschema constraints from an out-of-line resource to
   * import, supporting composition of constraint sets.
   */
  @MetaschemaAssembly(
      description = "Declares a set of Metaschema constraints from an out-of-line resource to import, supporting composition of constraint sets.",
      name = "import",
      moduleClass = MetaschemaModelModule.class)
  public static class Import implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    /**
     * A relative or absolute URI for retrieving an out-of-line Metaschema
     * constraint definition.
     */
    @BoundFlag(
        description = "A relative or absolute URI for retrieving an out-of-line Metaschema constraint definition.",
        name = "href",
        required = true,
        typeAdapter = UriReferenceAdapter.class)
    private URI _href;

    /**
     * Constructs a new
     * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Import}
     * instance with no metadata.
     */
    public Import() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Import}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public Import(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the {@code href} property.
     *
     * <p>
     * A relative or absolute URI for retrieving an out-of-line Metaschema
     * constraint definition.
     *
     * @return the href value
     */
    @NonNull
    public URI getHref() {
      return _href;
    }

    /**
     * Set the {@code href} property.
     *
     * <p>
     * A relative or absolute URI for retrieving an out-of-line Metaschema
     * constraint definition.
     *
     * @param value
     *          the href value to set
     */
    public void setHref(@NonNull URI value) {
      _href = value;
    }

    @Override
    public String toString() {
      return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
    }
  }

  /**
   * A binding class for the {@code scope} definition.
   */
  @MetaschemaAssembly(
      name = "scope",
      moduleClass = MetaschemaModelModule.class)
  public static class Scope implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundFlag(
        name = "metaschema-namespace",
        required = true,
        typeAdapter = UriAdapter.class)
    private URI _metaschemaNamespace;

    @BoundFlag(
        name = "metaschema-short-name",
        required = true,
        typeAdapter = TokenAdapter.class)
    private String _metaschemaShortName;

    @BoundChoiceGroup(
        minOccurs = 1,
        maxOccurs = -1,
        groupAs = @GroupAs(name = "constraints", inJson = JsonGroupAsBehavior.LIST),
        assemblies = {
            @BoundGroupedAssembly(useName = "assembly", binding = Assembly.class),
            @BoundGroupedAssembly(useName = "field", binding = Field.class),
            @BoundGroupedAssembly(useName = "flag", binding = Flag.class)
        })
    private List<? extends IValueConstraintsBase> _constraints;

    @BoundField(
        formalName = "Constraint Condition Violation Message",
        useName = "message",
        typeAdapter = StringAdapter.class)
    private String _message;

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
     * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope}
     * instance with no metadata.
     */
    public Scope() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public Scope(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the {@code metaschema-namespace} property.
     *
     * @return the metaschema-namespace value
     */
    @NonNull
    public URI getMetaschemaNamespace() {
      return _metaschemaNamespace;
    }

    /**
     * Set the {@code metaschema-namespace} property.
     *
     * @param value
     *          the metaschema-namespace value to set
     */
    public void setMetaschemaNamespace(@NonNull URI value) {
      _metaschemaNamespace = value;
    }

    /**
     * Get the {@code metaschema-short-name} property.
     *
     * @return the metaschema-short-name value
     */
    @NonNull
    public String getMetaschemaShortName() {
      return _metaschemaShortName;
    }

    /**
     * Set the {@code metaschema-short-name} property.
     *
     * @param value
     *          the metaschema-short-name value to set
     */
    public void setMetaschemaShortName(@NonNull String value) {
      _metaschemaShortName = value;
    }

    /**
     * Get the {@code constraints} choice group items.
     *
     * <p>
     * Items in this collection implement {@link IValueConstraintsBase}.
     *
     * @return the constraints items
     */
    @NonNull
    public List<? extends IValueConstraintsBase> getConstraints() {
      if (_constraints == null) {
        _constraints = new LinkedList<>();
      }
      return ObjectUtils.notNull(_constraints);
    }

    /**
     * Set the {@code constraints} choice group items.
     *
     * <p>
     * Items in this collection must implement {@link IValueConstraintsBase}.
     *
     * @param value
     *          the constraints items to set
     */
    public void setConstraints(@NonNull List<? extends IValueConstraintsBase> value) {
      _constraints = value;
    }

    /**
     * Get the constraint Condition Violation Message.
     *
     * @return the message value, or {@code null} if not set
     */
    @Nullable
    public String getMessage() {
      return _message;
    }

    /**
     * Set the constraint Condition Violation Message.
     *
     * @param value
     *          the message value to set, or {@code null} to clear
     */
    public void setMessage(@Nullable String value) {
      _message = value;
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

    /**
     * A binding class for the {@code assembly} definition.
     */
    @MetaschemaAssembly(
        name = "assembly",
        moduleClass = MetaschemaModelModule.class)
    public static class Assembly implements IValueTargetedConstraintsBase {
      private final IMetaschemaData __metaschemaData;

      @BoundFlag(
          formalName = "Constraint Target Metapath Expression",
          name = "target",
          required = true,
          typeAdapter = StringAdapter.class)
      private String _target;

      @BoundChoiceGroup(
          minOccurs = 1,
          maxOccurs = -1,
          groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST),
          assemblies = {
              @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
                  binding = TargetedAllowedValuesConstraint.class),
              @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
                  binding = TargetedExpectConstraint.class),
              @BoundGroupedAssembly(formalName = "Targeted Index Has Key Constraint", useName = "index-has-key",
                  binding = TargetedIndexHasKeyConstraint.class),
              @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
                  binding = TargetedMatchesConstraint.class),
              @BoundGroupedAssembly(formalName = "Targeted Unique Constraint", useName = "is-unique",
                  binding = TargetedIsUniqueConstraint.class),
              @BoundGroupedAssembly(formalName = "Targeted Index Constraint", useName = "index",
                  binding = TargetedIndexConstraint.class),
              @BoundGroupedAssembly(formalName = "Targeted Cardinality Constraint", useName = "has-cardinality",
                  binding = TargetedHasCardinalityConstraint.class),
              @BoundGroupedAssembly(formalName = "Report Condition Constraint", useName = "report",
                  binding = TargetedReportConstraint.class)
          })
      private List<? extends ITargetedConstraintBase> _rules;

      /**
       * Constructs a new
       * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope.Assembly}
       * instance with no metadata.
       */
      public Assembly() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope.Assembly}
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
       * Get the constraint Target Metapath Expression.
       *
       * @return the target value
       */
      @NonNull
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
       * Get the {@code rules} choice group items.
       *
       * <p>
       * Items in this collection implement {@link ITargetedConstraintBase}.
       *
       * @return the rules items
       */
      @NonNull
      @Override
      public List<? extends ITargetedConstraintBase> getRules() {
        if (_rules == null) {
          _rules = new LinkedList<>();
        }
        return ObjectUtils.notNull(_rules);
      }

      /**
       * Set the {@code rules} choice group items.
       *
       * <p>
       * Items in this collection must implement {@link ITargetedConstraintBase}.
       *
       * @param value
       *          the rules items to set
       */
      public void setRules(@NonNull List<? extends ITargetedConstraintBase> value) {
        _rules = value;
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }

    /**
     * A binding class for the {@code field} definition.
     */
    @MetaschemaAssembly(
        name = "field",
        moduleClass = MetaschemaModelModule.class)
    public static class Field implements IValueTargetedConstraintsBase {
      private final IMetaschemaData __metaschemaData;

      @BoundFlag(
          formalName = "Constraint Target Metapath Expression",
          name = "target",
          required = true,
          typeAdapter = StringAdapter.class)
      private String _target;

      @BoundChoiceGroup(
          minOccurs = 1,
          maxOccurs = -1,
          groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST),
          assemblies = {
              @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
                  binding = TargetedAllowedValuesConstraint.class),
              @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
                  binding = TargetedExpectConstraint.class),
              @BoundGroupedAssembly(formalName = "Targeted Index Has Key Constraint", useName = "index-has-key",
                  binding = TargetedIndexHasKeyConstraint.class),
              @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
                  binding = TargetedMatchesConstraint.class),
              @BoundGroupedAssembly(formalName = "Report Condition Constraint", useName = "report",
                  binding = TargetedReportConstraint.class)
          })
      private List<? extends ITargetedConstraintBase> _rules;

      /**
       * Constructs a new
       * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope.Field}
       * instance with no metadata.
       */
      public Field() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope.Field}
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
       * Get the constraint Target Metapath Expression.
       *
       * @return the target value
       */
      @NonNull
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
       * Get the {@code rules} choice group items.
       *
       * <p>
       * Items in this collection implement {@link ITargetedConstraintBase}.
       *
       * @return the rules items
       */
      @NonNull
      @Override
      public List<? extends ITargetedConstraintBase> getRules() {
        if (_rules == null) {
          _rules = new LinkedList<>();
        }
        return ObjectUtils.notNull(_rules);
      }

      /**
       * Set the {@code rules} choice group items.
       *
       * <p>
       * Items in this collection must implement {@link ITargetedConstraintBase}.
       *
       * @param value
       *          the rules items to set
       */
      public void setRules(@NonNull List<? extends ITargetedConstraintBase> value) {
        _rules = value;
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }

    /**
     * A binding class for the {@code flag} definition.
     */
    @MetaschemaAssembly(
        name = "flag",
        moduleClass = MetaschemaModelModule.class)
    public static class Flag implements IValueConstraintsBase {
      private final IMetaschemaData __metaschemaData;

      @BoundFlag(
          formalName = "Constraint Target Metapath Expression",
          name = "target",
          required = true,
          typeAdapter = StringAdapter.class)
      private String _target;

      @BoundChoiceGroup(
          minOccurs = 1,
          maxOccurs = -1,
          groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST),
          assemblies = {
              @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
                  binding = FlagAllowedValues.class),
              @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
                  binding = FlagExpect.class),
              @BoundGroupedAssembly(formalName = "Index Has Key Constraint", useName = "index-has-key",
                  binding = FlagIndexHasKey.class),
              @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
                  binding = FlagMatches.class),
              @BoundGroupedAssembly(formalName = "Report Condition Constraint", useName = "report",
                  binding = FlagReport.class)
          })
      private List<? extends IConstraintBase> _rules;

      /**
       * Constructs a new
       * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope.Flag}
       * instance with no metadata.
       */
      public Flag() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code dev.metaschema.databind.model.metaschema.binding.MetaschemaModuleConstraints.Scope.Flag}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public Flag(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the constraint Target Metapath Expression.
       *
       * @return the target value
       */
      @NonNull
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
       * Get the {@code rules} choice group items.
       *
       * <p>
       * Items in this collection implement {@link IConstraintBase}.
       *
       * @return the rules items
       */
      @NonNull
      @Override
      public List<? extends IConstraintBase> getRules() {
        if (_rules == null) {
          _rules = new LinkedList<>();
        }
        return ObjectUtils.notNull(_rules);
      }

      /**
       * Set the {@code rules} choice group items.
       *
       * <p>
       * Items in this collection must implement {@link IConstraintBase}.
       *
       * @param value
       *          the rules items to set
       */
      public void setRules(@NonNull List<? extends IConstraintBase> value) {
        _rules = value;
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }
  }
}
