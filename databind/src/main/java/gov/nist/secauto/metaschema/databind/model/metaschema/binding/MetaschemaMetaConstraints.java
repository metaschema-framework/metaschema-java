/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../../../../../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.databind.model.metaschema.binding;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import gov.nist.secauto.metaschema.core.datatype.adapter.TokenAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.Expect;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.IsUnique;
import gov.nist.secauto.metaschema.databind.model.annotations.KeyField;
import gov.nist.secauto.metaschema.databind.model.annotations.Let;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
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
    name = "metaschema-meta-constraints",
    moduleClass = MetaschemaModelModule.class,
    rootName = "metaschema-meta-constraints",
    valueConstraints = @ValueConstraints(lets = @Let(name = "deprecated-type-map",
        target = "map { 'base64Binary':'base64','dateTime':'date-time','dateTime-with-timezone':'date-time-with-timezone','email':'email-address','nonNegativeInteger':'non-negative-integer','positiveInteger':'positive-integer' }"),
        expect = @Expect(id = "metaschema-deprecated-types", formalName = "Avoid Deprecated Data Type Use",
            description = "Ensure that the data type specified is not one of the legacy Metaschema data types which have been deprecated (i.e. base64Binary, dateTime, dateTime-with-timezone, email, nonNegativeInteger, positiveInteger).",
            level = IConstraint.Level.WARNING, target = ".//matches/@datatype|.//(define-field|define-flag)/@as-type",
            test = "not(data(.)=('base64Binary','dateTime','dateTime-with-timezone','email','nonNegativeInteger','positiveInteger'))",
            message = "Use of the type '{ data(.) }' is deprecated. Use '{ $deprecated-type-map(data(.))}' instead.")),
    modelConstraints = @gov.nist.secauto.metaschema.databind.model.annotations.AssemblyConstraints(unique = {
        @IsUnique(id = "meta-constraints-namespace-unique-entry", formalName = "Require Unique Namespace Entries",
            description = "Ensures that all declared namespace entries are unique.", level = IConstraint.Level.ERROR,
            target = "namespace-binding", keyFields = { @KeyField(target = "@prefix"), @KeyField(target = "@uri") }),
        @IsUnique(id = "meta-constraints-namespace-unique-prefix",
            formalName = "Require Unique Namespace Entry Prefixes",
            description = "Ensures that all declared namespace entries have a unique prefix.",
            level = IConstraint.Level.ERROR, target = "namespace-binding",
            keyFields = @KeyField(target = "@prefix")) }))
public class MetaschemaMetaConstraints implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

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
      useName = "definition-context")
  private DefinitionContext _definitionContext;

  @BoundAssembly(
      useName = "context",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "contexts", inJson = JsonGroupAsBehavior.LIST))
  private List<MetapathContext> _contexts;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints}
   * instance with no metadata.
   */
  public MetaschemaMetaConstraints() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public MetaschemaMetaConstraints(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
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
    return _imports;
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
    return _namespaceBindings;
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
   * Get the {@code definition-context} property.
   *
   * @return the definition-context value, or {@code null} if not set
   */
  @Nullable
  public DefinitionContext getDefinitionContext() {
    return _definitionContext;
  }

  /**
   * Set the {@code definition-context} property.
   *
   * @param value
   *          the definition-context value to set
   */
  public void setDefinitionContext(@Nullable DefinitionContext value) {
    _definitionContext = value;
  }

  /**
   * Get the {@code context} property.
   *
   * @return the context value
   */
  @NonNull
  public List<MetapathContext> getContexts() {
    if (_contexts == null) {
      _contexts = new LinkedList<>();
    }
    return _contexts;
  }

  /**
   * Set the {@code context} property.
   *
   * @param value
   *          the context value to set
   */
  public void setContexts(@NonNull List<MetapathContext> value) {
    _contexts = value;
  }

  /**
   * Add a new {@link MetapathContext} item to the underlying collection.
   *
   * @param item
   *          the item to add
   * @return {@code true}
   */
  public boolean addContext(MetapathContext item) {
    MetapathContext value = ObjectUtils.requireNonNull(item, "item cannot be null");
    if (_contexts == null) {
      _contexts = new LinkedList<>();
    }
    return _contexts.add(value);
  }

  /**
   * Remove the first matching {@link MetapathContext} item from the underlying
   * collection.
   *
   * @param item
   *          the item to remove
   * @return {@code true} if the item was removed or {@code false} otherwise
   */
  public boolean removeContext(MetapathContext item) {
    MetapathContext value = ObjectUtils.requireNonNull(item, "item cannot be null");
    return _contexts != null && _contexts.remove(value);
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
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints.Import}
     * instance with no metadata.
     */
    public Import() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints.Import}
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
      return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
    }
  }

  @MetaschemaAssembly(
      name = "definition-context",
      moduleClass = MetaschemaModelModule.class)
  public static class DefinitionContext implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundFlag(
        name = "name",
        required = true,
        typeAdapter = TokenAdapter.class)
    private String _name;

    @BoundFlag(
        name = "namespace",
        required = true,
        typeAdapter = UriAdapter.class)
    private URI _namespace;

    @BoundAssembly(
        useName = "constraints",
        minOccurs = 1)
    private AssemblyConstraints _constraints;

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
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints.DefinitionContext}
     * instance with no metadata.
     */
    public DefinitionContext() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetaschemaMetaConstraints.DefinitionContext}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public DefinitionContext(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the {@code name} property.
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
     * @param value
     *          the name value to set
     */
    public void setName(@NonNull String value) {
      _name = value;
    }

    /**
     * Get the {@code namespace} property.
     *
     * @return the namespace value
     */
    @NonNull
    public URI getNamespace() {
      return _namespace;
    }

    /**
     * Set the {@code namespace} property.
     *
     * @param value
     *          the namespace value to set
     */
    public void setNamespace(@NonNull URI value) {
      _namespace = value;
    }

    /**
     * Get the {@code constraints} property.
     *
     * @return the constraints value
     */
    @NonNull
    public AssemblyConstraints getConstraints() {
      return _constraints;
    }

    /**
     * Set the {@code constraints} property.
     *
     * @param value
     *          the constraints value to set
     */
    public void setConstraints(@NonNull AssemblyConstraints value) {
      _constraints = value;
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
      return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
    }
  }
}
