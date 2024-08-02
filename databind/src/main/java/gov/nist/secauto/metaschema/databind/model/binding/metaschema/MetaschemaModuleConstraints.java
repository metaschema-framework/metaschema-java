/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.binding.metaschema;

import gov.nist.secauto.metaschema.core.datatype.adapter.StringAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.Expect;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.Let;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import gov.nist.secauto.metaschema.databind.model.metaschema.IModelConstraintsBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.ITargetedConstraintBase;
import gov.nist.secauto.metaschema.databind.model.metaschema.IValueConstraintsBase;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines constraint rules to be applied to an existing set of Metaschema
 * module-based models.
 */
@SuppressWarnings({
    "PMD.DataClass",
    "PMD.FieldNamingConventions",
    "PMD.ShortClassName"
})
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
            test = "not(.=('base64Binary','dateTime','dateTime-with-timezone','email','nonNegativeInteger','positiveInteger'))",
            message = "Use of the type '{ . }' is deprecated. Use '{ $deprecated-type-map(.)}' instead.")))
public class MetaschemaModuleConstraints implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  @BoundField(
      description = "The name of this constraint set.",
      useName = "name",
      minOccurs = 1)
  private String _name;

  @BoundField(
      description = "The version of this constraint set. A version string used to distinguish between multiple revisions of the same resource.",
      useName = "version",
      minOccurs = 1)
  private String _version;

  @BoundAssembly(
      description = "Declares a set of Metaschema constraints from an out-of-line resource to import, supporting composition of constraint sets.",
      useName = "import",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "imports", inJson = JsonGroupAsBehavior.LIST))
  private List<Import> _imports;

  @BoundAssembly(
      formalName = "Metapath Namespace Declaration",
      description = "Assigns a Metapath namespace to a prefix for use in a Metapath expression in a lexical qualified name.",
      useName = "namespace-binding",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "namespace-bindings"))
  private List<MetapathNamespace> _namespaceBindings;

  @BoundAssembly(
      useName = "scope",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "scopes", inJson = JsonGroupAsBehavior.LIST))
  private List<Scope> _scopes;

  public MetaschemaModuleConstraints() {
    this(null);
  }

  public MetaschemaModuleConstraints(IMetaschemaData data) {
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

  public String getVersion() {
    return _version;
  }

  public void setVersion(String value) {
    _version = value;
  }

  public List<Import> getImports() {
    return _imports;
  }

  public void setImports(List<Import> value) {
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

  public List<MetapathNamespace> getNamespaceBindings() {
    return _namespaceBindings;
  }

  public void setNamespaceBindings(List<MetapathNamespace> value) {
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

  public List<Scope> getScopes() {
    return _scopes;
  }

  public void setScopes(List<Scope> value) {
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
    return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
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
     * "A relative or absolute URI for retrieving an out-of-line Metaschema
     * constraint definition."
     */
    @BoundFlag(
        description = "A relative or absolute URI for retrieving an out-of-line Metaschema constraint definition.",
        name = "href",
        required = true,
        typeAdapter = UriReferenceAdapter.class)
    private URI _href;

    public Import() {
      this(null);
    }

    public Import(IMetaschemaData data) {
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

    @Override
    public String toString() {
      return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
    }
  }

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
        assemblies = {
            @BoundGroupedAssembly(useName = "assembly", binding = Assembly.class),
            @BoundGroupedAssembly(useName = "field", binding = Field.class),
            @BoundGroupedAssembly(useName = "flag", binding = Flag.class)
        },
        groupAs = @GroupAs(name = "constraints", inJson = JsonGroupAsBehavior.LIST))
    private List<? extends IValueConstraintsBase> _constraints;

    @BoundField(
        formalName = "Remarks",
        description = "Any explanatory or helpful information to be provided about the remarks parent.",
        useName = "remarks")
    private Remarks _remarks;

    public Scope() {
      this(null);
    }

    public Scope(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    public URI getMetaschemaNamespace() {
      return _metaschemaNamespace;
    }

    public void setMetaschemaNamespace(URI value) {
      _metaschemaNamespace = value;
    }

    public String getMetaschemaShortName() {
      return _metaschemaShortName;
    }

    public void setMetaschemaShortName(String value) {
      _metaschemaShortName = value;
    }

    public List<? extends IValueConstraintsBase> getConstraints() {
      return _constraints;
    }

    public void setConstraints(List<? extends IValueConstraintsBase> value) {
      _constraints = value;
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

    @MetaschemaAssembly(
        name = "assembly",
        moduleClass = MetaschemaModelModule.class)
    public static class Assembly implements IModelConstraintsBase {
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
                  binding = TargetedHasCardinalityConstraint.class)
          },
          groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST))
      private List<? extends ITargetedConstraintBase> _rules;

      public Assembly() {
        this(null);
      }

      public Assembly(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      public String getTarget() {
        return _target;
      }

      public void setTarget(String value) {
        _target = value;
      }

      @Override
      public List<ConstraintLetExpression> getLets() {
        return CollectionUtil.emptyList();
      }

      @Override
      public List<? extends ITargetedConstraintBase> getRules() {
        return _rules;
      }

      public void setRules(List<? extends ITargetedConstraintBase> value) {
        _rules = value;
      }

      @Override
      public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
      }
    }

    @MetaschemaAssembly(
        name = "field",
        moduleClass = MetaschemaModelModule.class)
    public static class Field implements IValueConstraintsBase {
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
          assemblies = {
              @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
                  binding = TargetedAllowedValuesConstraint.class),
              @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
                  binding = TargetedExpectConstraint.class),
              @BoundGroupedAssembly(formalName = "Targeted Index Has Key Constraint", useName = "index-has-key",
                  binding = TargetedIndexHasKeyConstraint.class),
              @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
                  binding = TargetedMatchesConstraint.class)
          },
          groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST))
      private List<? extends ITargetedConstraintBase> _rules;

      public Field() {
        this(null);
      }

      public Field(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      public String getTarget() {
        return _target;
      }

      public void setTarget(String value) {
        _target = value;
      }

      @Override
      public List<ConstraintLetExpression> getLets() {
        return CollectionUtil.emptyList();
      }

      @Override
      public List<? extends ITargetedConstraintBase> getRules() {
        return _rules;
      }

      public void setRules(List<? extends ITargetedConstraintBase> value) {
        _rules = value;
      }

      @Override
      public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
      }
    }

    @MetaschemaAssembly(
        name = "flag",
        moduleClass = MetaschemaModelModule.class)
    public static class Flag implements IBoundObject, IValueConstraintsBase {
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
          assemblies = {
              @BoundGroupedAssembly(formalName = "Allowed Values Constraint", useName = "allowed-values",
                  binding = FlagAllowedValues.class),
              @BoundGroupedAssembly(formalName = "Expect Condition Constraint", useName = "expect",
                  binding = FlagExpect.class),
              @BoundGroupedAssembly(formalName = "Index Has Key Constraint", useName = "index-has-key",
                  binding = FlagIndexHasKey.class),
              @BoundGroupedAssembly(formalName = "Value Matches Constraint", useName = "matches",
                  binding = FlagMatches.class)
          },
          groupAs = @GroupAs(name = "rules", inJson = JsonGroupAsBehavior.LIST))
      private List<? extends ITargetedConstraintBase> _rules;

      public Flag() {
        this(null);
      }

      public Flag(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      @Override
      public List<ConstraintLetExpression> getLets() {
        return CollectionUtil.emptyList();
      }

      public String getTarget() {
        return _target;
      }

      public void setTarget(String value) {
        _target = value;
      }

      @Override
      public List<? extends ITargetedConstraintBase> getRules() {
        return _rules;
      }

      public void setRules(List<? extends ITargetedConstraintBase> value) {
        _rules = value;
      }

      @Override
      public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
      }
    }
  }
}
