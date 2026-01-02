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
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;
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
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFieldValue;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.Expect;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.Index;
import gov.nist.secauto.metaschema.databind.model.annotations.IsUnique;
import gov.nist.secauto.metaschema.databind.model.annotations.KeyField;
import gov.nist.secauto.metaschema.databind.model.annotations.Let;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import gov.nist.secauto.metaschema.databind.model.annotations.ValueConstraints;
import java.math.BigInteger;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A declaration of the Metaschema module.
 */
@MetaschemaAssembly(
    formalName = "Metaschema Module",
    description = "A declaration of the Metaschema module.",
    name = "METASCHEMA",
    moduleClass = MetaschemaModelModule.class,
    rootName = "METASCHEMA",
    valueConstraints = @ValueConstraints(lets = {
        @Let(name = "all-imports",
            target = "recurse-depth('for $import in ./import return doc(resolve-uri($import/@href))/METASCHEMA')"),
        @Let(name = "deprecated-type-map",
            target = "map { 'base64Binary':'base64','dateTime':'date-time','dateTime-with-timezone':'date-time-with-timezone','email':'email-address','nonNegativeInteger':'non-negative-integer','positiveInteger':'positive-integer' }") },
        expect = { @Expect(id = "module-top-level-version-required",
            formalName = "Require Schema Version for Top-Level Modules",
            description = "A top-level module, a module that is not marked as @abstract='yes', must have a schema version specified.",
            level = IConstraint.Level.WARNING, target = ".[not(@abstract) or @abstract='no']", test = "schema-version",
            message = "Unless marked as @abstract='yes', a Metaschema module (or an imported module) should have a schema version."),
            @Expect(id = "module-top-level-root-required", formalName = "Require Root Assembly for Top-Level Modules",
                description = "A top-level module, a module that is not marked as @abstract='yes', must have at least one assembly with a root-name.",
                level = IConstraint.Level.WARNING, target = ".[not(@abstract) or @abstract='no']",
                test = "exists($all-imports/define-assembly/root-name)",
                message = "Unless marked as @abstract='yes', a Metaschema module (or an imported module) should have at least one assembly with a root-name."),
            @Expect(id = "module-import-href-available", formalName = "Import is Resolvable",
                description = "Ensure each import has a resolvable @href.", level = IConstraint.Level.ERROR,
                target = "import", test = "doc-available(resolve-uri(@href))",
                message = "Unable to access a Metaschema module at '{ resolve-uri(@href) }'."),
            @Expect(id = "module-import-href-is-module", formalName = "Import is a Metaschema module",
                description = "Ensure each import is a Metaschema module.", level = IConstraint.Level.ERROR,
                target = "import", test = "doc(resolve-uri(@href))/METASCHEMA ! exists(.)",
                message = "The resource at '{ resolve-uri(@href) }' is not a Metaschema module."),
            @Expect(id = "module-model-group-a-invalid", formalName = "Group-As unneeded with max-occurs='1'",
                description = "A field or assembly instance with a max occurrence of \\`1\\` must not have a \\`group-as\\` child.",
                level = IConstraint.Level.ERROR,
                target = ".//(define-assembly|define-field|assembly|field)[@max-occurs=1]", test = ".[not(group-as)]",
                message = "Use of `group-as` in the location '{ path() }' requires a parent with a max-occurs that is greater than 1; otherwise the `group-as` needs to be removed."),
            @Expect(id = "metaschema-deprecated-types", formalName = "Avoid Deprecated Data Type Use",
                description = "Ensure that the data type specified is not one of the legacy Metaschema data types which have been deprecated (i.e. base64Binary, dateTime, dateTime-with-timezone, email, nonNegativeInteger, positiveInteger).",
                level = IConstraint.Level.WARNING,
                target = ".//matches/@datatype|.//(define-field|define-flag)/@as-type",
                test = "not(data(.)=('base64Binary','dateTime','dateTime-with-timezone','email','nonNegativeInteger','positiveInteger'))",
                message = "Use of the type '{ data(.) }' is deprecated. Use '{ $deprecated-type-map(data(.))}' instead.") }),
    modelConstraints = @gov.nist.secauto.metaschema.databind.model.annotations.AssemblyConstraints(
        index = @Index(id = "module-short-name-unique", formalName = "Index Module Short Names",
            description = "Ensures that the current and all imported modules have a unique short name.",
            level = IConstraint.Level.ERROR, target = "(.|$all-imports)", name = "metaschema-metadata-short-name-index",
            keyFields = @KeyField(target = "@short-name")),
        unique = { @IsUnique(id = "module-namespace-unique-entry", formalName = "Require Unique Namespace Entries",
            description = "Ensures that all declared namespace entries are unique.", level = IConstraint.Level.ERROR,
            target = "namespace-binding", keyFields = { @KeyField(target = "@prefix"), @KeyField(target = "@uri") }),
            @IsUnique(id = "module-namespace-unique-prefix", formalName = "Require Unique Namespace Entry Prefixes",
                description = "Ensures that all declared namespace entries have a unique prefix.",
                level = IConstraint.Level.ERROR, target = "namespace-binding",
                keyFields = @KeyField(target = "@prefix")) }))
public class METASCHEMA implements IBoundObject {
  private final IMetaschemaData __metaschemaData;

  /**
   * Determines if the Metaschema module is abstract (&lsquo;yes&rsquo;) or not
   * (&lsquo;no&rsquo;).
   */
  @BoundFlag(
      formalName = "Is Abstract?",
      description = "Determines if the Metaschema module is abstract ('yes') or not ('no').",
      name = "abstract",
      defaultValue = "no",
      typeAdapter = TokenAdapter.class,
      valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
          values = { @AllowedValue(value = "yes", description = "The module is abstract."),
              @AllowedValue(value = "no", description = "The module is not abstract.") })))
  private String _abstract;

  /**
   * The name of the information model represented by this Metaschema definition.
   */
  @BoundField(
      formalName = "Module Name",
      description = "The name of the information model represented by this Metaschema definition.",
      useName = "schema-name",
      minOccurs = 1,
      typeAdapter = MarkupLineAdapter.class)
  private MarkupLine _schemaName;

  /**
   * A version string used to distinguish between multiple revisions of the same
   * Metaschema module.
   */
  @BoundField(
      description = "A version string used to distinguish between multiple revisions of the same Metaschema module.",
      useName = "schema-version",
      minOccurs = 1,
      typeAdapter = StringAdapter.class)
  private String _schemaVersion;

  /**
   * A short (code) name to be used for the Metaschema module. This name may be
   * used as a constituent of names assigned to derived artifacts, such as schemas
   * and conversion utilities.
   */
  @BoundField(
      formalName = "Module Short Name",
      description = "A short (code) name to be used for the Metaschema module. This name may be used as a constituent of names assigned to derived artifacts, such as schemas and conversion utilities.",
      useName = "short-name",
      minOccurs = 1,
      typeAdapter = TokenAdapter.class)
  private String _shortName;

  /**
   * The namespace for the collection of Metaschema module this Metaschema module
   * belongs to. This value is also used as the XML namespace governing the names
   * of elements in XML documents. By using this namespace, documents and document
   * fragments used in mixed-format environments may be distinguished from
   * neighbor XML formats using another namespaces. This value is not reflected in
   * Metaschema JSON.
   */
  @BoundField(
      formalName = "Module Collection Namespace",
      description = "The namespace for the collection of Metaschema module this Metaschema module belongs to. This value is also used as the XML namespace governing the names of elements in XML documents. By using this namespace, documents and document fragments used in mixed-format environments may be distinguished from neighbor XML formats using another namespaces. This value is not reflected in Metaschema JSON.",
      useName = "namespace",
      minOccurs = 1,
      typeAdapter = UriAdapter.class)
  private URI _namespace;

  /**
   * The JSON Base URI is the nominal base URI assigned to a JSON Schema instance
   * expressing the model defined by this Metaschema module.
   */
  @BoundField(
      formalName = "JSON Base URI",
      description = "The JSON Base URI is the nominal base URI assigned to a JSON Schema instance expressing the model defined by this Metaschema module.",
      useName = "json-base-uri",
      minOccurs = 1,
      typeAdapter = UriAdapter.class)
  private URI _jsonBaseUri;

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
   * Imports a set of Metaschema modules contained in another resource. Imports
   * support the reuse of common information structures.
   */
  @BoundAssembly(
      formalName = "Module Import",
      description = "Imports a set of Metaschema modules contained in another resource. Imports support the reuse of common information structures.",
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

  @BoundChoiceGroup(
      maxOccurs = -1,
      groupAs = @GroupAs(name = "definitions", inJson = JsonGroupAsBehavior.LIST),
      assemblies = {
          @BoundGroupedAssembly(formalName = "Global Assembly Definition",
              description = "In XML, an element with structured element content. In JSON, an object with properties. Defined globally, an assembly can be assigned to appear in the `model` of any assembly (another assembly type, or itself), by `assembly` reference.",
              useName = "define-assembly", discriminatorValue = "assembly", binding = DefineAssembly.class),
          @BoundGroupedAssembly(formalName = "Global Field Definition", useName = "define-field",
              discriminatorValue = "field", binding = DefineField.class),
          @BoundGroupedAssembly(formalName = "Global Flag Definition", useName = "define-flag",
              discriminatorValue = "flag", binding = DefineFlag.class)
      })
  private List<Object> _definitions;

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA}
   * instance with no metadata.
   */
  public METASCHEMA() {
    this(null);
  }

  /**
   * Constructs a new
   * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA}
   * instance with the specified metadata.
   *
   * @param data
   *          the metaschema data, or {@code null} if none
   */
  public METASCHEMA(IMetaschemaData data) {
    this.__metaschemaData = data;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return __metaschemaData;
  }

  /**
   * Get the is Abstract?.
   *
   * <p>
   * Determines if the Metaschema module is abstract (&lsquo;yes&rsquo;) or not
   * (&lsquo;no&rsquo;).
   *
   * @return the abstract value, or {@code null} if not set
   */
  @Nullable
  public String getAbstract() {
    return _abstract;
  }

  /**
   * Set the is Abstract?.
   *
   * <p>
   * Determines if the Metaschema module is abstract (&lsquo;yes&rsquo;) or not
   * (&lsquo;no&rsquo;).
   *
   * @param value
   *          the abstract value to set, or {@code null} to clear
   */
  public void setAbstract(@Nullable String value) {
    _abstract = value;
  }

  /**
   * Get the module Name.
   *
   * <p>
   * The name of the information model represented by this Metaschema definition.
   *
   * @return the schema-name value
   */
  @NonNull
  public MarkupLine getSchemaName() {
    return _schemaName;
  }

  /**
   * Set the module Name.
   *
   * <p>
   * The name of the information model represented by this Metaschema definition.
   *
   * @param value
   *          the schema-name value to set
   */
  public void setSchemaName(@NonNull MarkupLine value) {
    _schemaName = value;
  }

  /**
   * Get the {@code schema-version} property.
   *
   * <p>
   * A version string used to distinguish between multiple revisions of the same
   * Metaschema module.
   *
   * @return the schema-version value
   */
  @NonNull
  public String getSchemaVersion() {
    return _schemaVersion;
  }

  /**
   * Set the {@code schema-version} property.
   *
   * <p>
   * A version string used to distinguish between multiple revisions of the same
   * Metaschema module.
   *
   * @param value
   *          the schema-version value to set
   */
  public void setSchemaVersion(@NonNull String value) {
    _schemaVersion = value;
  }

  /**
   * Get the module Short Name.
   *
   * <p>
   * A short (code) name to be used for the Metaschema module. This name may be
   * used as a constituent of names assigned to derived artifacts, such as schemas
   * and conversion utilities.
   *
   * @return the short-name value
   */
  @NonNull
  public String getShortName() {
    return _shortName;
  }

  /**
   * Set the module Short Name.
   *
   * <p>
   * A short (code) name to be used for the Metaschema module. This name may be
   * used as a constituent of names assigned to derived artifacts, such as schemas
   * and conversion utilities.
   *
   * @param value
   *          the short-name value to set
   */
  public void setShortName(@NonNull String value) {
    _shortName = value;
  }

  /**
   * Get the module Collection Namespace.
   *
   * <p>
   * The namespace for the collection of Metaschema module this Metaschema module
   * belongs to. This value is also used as the XML namespace governing the names
   * of elements in XML documents. By using this namespace, documents and document
   * fragments used in mixed-format environments may be distinguished from
   * neighbor XML formats using another namespaces. This value is not reflected in
   * Metaschema JSON.
   *
   * @return the namespace value
   */
  @NonNull
  public URI getNamespace() {
    return _namespace;
  }

  /**
   * Set the module Collection Namespace.
   *
   * <p>
   * The namespace for the collection of Metaschema module this Metaschema module
   * belongs to. This value is also used as the XML namespace governing the names
   * of elements in XML documents. By using this namespace, documents and document
   * fragments used in mixed-format environments may be distinguished from
   * neighbor XML formats using another namespaces. This value is not reflected in
   * Metaschema JSON.
   *
   * @param value
   *          the namespace value to set
   */
  public void setNamespace(@NonNull URI value) {
    _namespace = value;
  }

  /**
   * Get the jSON Base URI.
   *
   * <p>
   * The JSON Base URI is the nominal base URI assigned to a JSON Schema instance
   * expressing the model defined by this Metaschema module.
   *
   * @return the json-base-uri value
   */
  @NonNull
  public URI getJsonBaseUri() {
    return _jsonBaseUri;
  }

  /**
   * Set the jSON Base URI.
   *
   * <p>
   * The JSON Base URI is the nominal base URI assigned to a JSON Schema instance
   * expressing the model defined by this Metaschema module.
   *
   * @param value
   *          the json-base-uri value to set
   */
  public void setJsonBaseUri(@NonNull URI value) {
    _jsonBaseUri = value;
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
   * Get the module Import.
   *
   * <p>
   * Imports a set of Metaschema modules contained in another resource. Imports
   * support the reuse of common information structures.
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
   * Set the module Import.
   *
   * <p>
   * Imports a set of Metaschema modules contained in another resource. Imports
   * support the reuse of common information structures.
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
   * Get the {@code definitions} choice group items.
   *
   * @return the definitions items
   */
  @NonNull
  public List<Object> getDefinitions() {
    if (_definitions == null) {
      _definitions = new LinkedList<>();
    }
    return ObjectUtils.notNull(_definitions);
  }

  /**
   * Set the {@code definitions} choice group items.
   *
   * @param value
   *          the definitions items to set
   */
  public void setDefinitions(@NonNull List<Object> value) {
    _definitions = value;
  }

  @Override
  public String toString() {
    return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
  }

  /**
   * Imports a set of Metaschema modules contained in another resource. Imports
   * support the reuse of common information structures.
   */
  @MetaschemaAssembly(
      formalName = "Module Import",
      description = "Imports a set of Metaschema modules contained in another resource. Imports support the reuse of common information structures.",
      name = "import",
      moduleClass = MetaschemaModelModule.class)
  public static class Import implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    /**
     * A relative or absolute URI for retrieving an out-of-line Metaschema
     * definition.
     */
    @BoundFlag(
        formalName = "Import URI Reference",
        description = "A relative or absolute URI for retrieving an out-of-line Metaschema definition.",
        name = "href",
        required = true,
        typeAdapter = UriReferenceAdapter.class)
    private URI _href;

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.Import}
     * instance with no metadata.
     */
    public Import() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.Import}
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
     * Get the import URI Reference.
     *
     * <p>
     * A relative or absolute URI for retrieving an out-of-line Metaschema
     * definition.
     *
     * @return the href value
     */
    @NonNull
    public URI getHref() {
      return _href;
    }

    /**
     * Set the import URI Reference.
     *
     * <p>
     * A relative or absolute URI for retrieving an out-of-line Metaschema
     * definition.
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
   * In XML, an element with structured element content. In JSON, an object with
   * properties. Defined globally, an assembly can be assigned to appear in the
   * <code>model</code> of any assembly (another assembly type, or itself), by
   * <code>assembly</code> reference.
   */
  @MetaschemaAssembly(
      formalName = "Global Assembly Definition",
      description = "In XML, an element with structured element content. In JSON, an object with properties. Defined globally, an assembly can be assigned to appear in the `model` of any assembly (another assembly type, or itself), by `assembly` reference.",
      name = "define-assembly",
      moduleClass = MetaschemaModelModule.class)
  public static class DefineAssembly implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundFlag(
        formalName = "Global Assembly Name",
        name = "name",
        required = true,
        typeAdapter = TokenAdapter.class)
    private String _name;

    @BoundFlag(
        formalName = "Global Assembly Binary Name",
        name = "index",
        typeAdapter = PositiveIntegerAdapter.class)
    private BigInteger _index;

    @BoundFlag(
        formalName = "Definition Scope",
        name = "scope",
        defaultValue = "global",
        typeAdapter = TokenAdapter.class,
        valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
            @AllowedValue(value = "local",
                description = "This definition is only available in the context of the current Metaschema module."),
            @AllowedValue(value = "global",
                description = "This definition will be made available to any Metaschema module that includes this one either directly or indirectly through a chain of imported Metaschemas.") })))
    private String _scope;

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
    @BoundChoice(
        choiceId = "choice-1")
    private UseName _useName;

    /**
     * Provides a root name, for when the definition is used as the root of a node
     * hierarchy.
     */
    @BoundField(
        formalName = "Root Name",
        description = "Provides a root name, for when the definition is used as the root of a node hierarchy.",
        useName = "root-name",
        minOccurs = 1)
    @BoundChoice(
        choiceId = "choice-1")
    private RootName _rootName;

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
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineAssembly}
     * instance with no metadata.
     */
    public DefineAssembly() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineAssembly}
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
     * Get the global Assembly Name.
     *
     * @return the name value
     */
    @NonNull
    public String getName() {
      return _name;
    }

    /**
     * Set the global Assembly Name.
     *
     * @param value
     *          the name value to set
     */
    public void setName(@NonNull String value) {
      _name = value;
    }

    /**
     * Get the global Assembly Binary Name.
     *
     * @return the index value, or {@code null} if not set
     */
    @Nullable
    public BigInteger getIndex() {
      return _index;
    }

    /**
     * Set the global Assembly Binary Name.
     *
     * @param value
     *          the index value to set, or {@code null} to clear
     */
    public void setIndex(@Nullable BigInteger value) {
      _index = value;
    }

    /**
     * Get the definition Scope.
     *
     * @return the scope value, or {@code null} if not set
     */
    @Nullable
    public String getScope() {
      return _scope;
    }

    /**
     * Set the definition Scope.
     *
     * @param value
     *          the scope value to set, or {@code null} to clear
     */
    public void setScope(@Nullable String value) {
      _scope = value;
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
     * Get the root Name.
     *
     * <p>
     * Provides a root name, for when the definition is used as the root of a node
     * hierarchy.
     *
     * @return the root-name value, or {@code null} if not set
     */
    @Nullable
    public RootName getRootName() {
      return _rootName;
    }

    /**
     * Set the root Name.
     *
     * <p>
     * Provides a root name, for when the definition is used as the root of a node
     * hierarchy.
     *
     * @param value
     *          the root-name value to set, or {@code null} to clear
     */
    public void setRootName(@Nullable RootName value) {
      _rootName = value;
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

    /**
     * Provides a root name, for when the definition is used as the root of a node
     * hierarchy.
     */
    @MetaschemaField(
        formalName = "Root Name",
        description = "Provides a root name, for when the definition is used as the root of a node hierarchy.",
        name = "root-name",
        moduleClass = MetaschemaModelModule.class)
    public static class RootName implements IBoundObject {
      private final IMetaschemaData __metaschemaData;

      /**
       * Used for binary formats instead of the textual name.
       */
      @BoundFlag(
          formalName = "Numeric Index",
          description = "Used for binary formats instead of the textual name.",
          name = "index",
          typeAdapter = NonNegativeIntegerAdapter.class)
      private BigInteger _index;

      /**
       * The field value.
       */
      @BoundFieldValue(
          valueKeyName = "name",
          typeAdapter = TokenAdapter.class)
      private String _name;

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineAssembly.RootName}
       * instance with no metadata.
       */
      public RootName() {
        this(null);
      }

      /**
       * Constructs a new
       * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineAssembly.RootName}
       * instance with the specified metadata.
       *
       * @param data
       *          the metaschema data, or {@code null} if none
       */
      public RootName(IMetaschemaData data) {
        this.__metaschemaData = data;
      }

      @Override
      public IMetaschemaData getMetaschemaData() {
        return __metaschemaData;
      }

      /**
       * Get the numeric Index.
       *
       * <p>
       * Used for binary formats instead of the textual name.
       *
       * @return the index value, or {@code null} if not set
       */
      @Nullable
      public BigInteger getIndex() {
        return _index;
      }

      /**
       * Set the numeric Index.
       *
       * <p>
       * Used for binary formats instead of the textual name.
       *
       * @param value
       *          the index value to set, or {@code null} to clear
       */
      public void setIndex(@Nullable BigInteger value) {
        _index = value;
      }

      /**
       * Get the field value.
       *
       * @return the value
       */
      @Nullable
      public String getName() {
        return _name;
      }

      /**
       * Set the field value.
       *
       * @param value
       *          the value to set
       */
      public void setName(@Nullable String value) {
        _name = value;
      }

      @Override
      public String toString() {
        return ObjectUtils.notNull(new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString());
      }
    }
  }

  /**
   * Global Field Definition.
   */
  @MetaschemaAssembly(
      formalName = "Global Field Definition",
      name = "define-field",
      moduleClass = MetaschemaModelModule.class)
  public static class DefineField implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundFlag(
        formalName = "Global Field Name",
        name = "name",
        required = true,
        typeAdapter = TokenAdapter.class)
    private String _name;

    @BoundFlag(
        formalName = "Global Field Binary Name",
        name = "index",
        typeAdapter = PositiveIntegerAdapter.class)
    private BigInteger _index;

    @BoundFlag(
        formalName = "Definition Scope",
        name = "scope",
        defaultValue = "global",
        typeAdapter = TokenAdapter.class,
        valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
            @AllowedValue(value = "local",
                description = "This definition is only available in the context of the current Metaschema module."),
            @AllowedValue(value = "global",
                description = "This definition will be made available to any Metaschema module that includes this one either directly or indirectly through a chain of imported Metaschemas.") })))
    private String _scope;

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

    /**
     * Allows the name of the definition to be overridden.
     */
    @BoundField(
        formalName = "Use Name",
        description = "Allows the name of the definition to be overridden.",
        useName = "use-name")
    private UseName _useName;

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
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineField}
     * instance with no metadata.
     */
    public DefineField() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineField}
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
     * Get the global Field Name.
     *
     * @return the name value
     */
    @NonNull
    public String getName() {
      return _name;
    }

    /**
     * Set the global Field Name.
     *
     * @param value
     *          the name value to set
     */
    public void setName(@NonNull String value) {
      _name = value;
    }

    /**
     * Get the global Field Binary Name.
     *
     * @return the index value, or {@code null} if not set
     */
    @Nullable
    public BigInteger getIndex() {
      return _index;
    }

    /**
     * Set the global Field Binary Name.
     *
     * @param value
     *          the index value to set, or {@code null} to clear
     */
    public void setIndex(@Nullable BigInteger value) {
      _index = value;
    }

    /**
     * Get the definition Scope.
     *
     * @return the scope value, or {@code null} if not set
     */
    @Nullable
    public String getScope() {
      return _scope;
    }

    /**
     * Set the definition Scope.
     *
     * @param value
     *          the scope value to set, or {@code null} to clear
     */
    public void setScope(@Nullable String value) {
      _scope = value;
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

  /**
   * Global Flag Definition.
   */
  @MetaschemaAssembly(
      formalName = "Global Flag Definition",
      name = "define-flag",
      moduleClass = MetaschemaModelModule.class)
  public static class DefineFlag implements IBoundObject {
    private final IMetaschemaData __metaschemaData;

    @BoundFlag(
        formalName = "Global Flag Name",
        name = "name",
        required = true,
        typeAdapter = TokenAdapter.class)
    private String _name;

    @BoundFlag(
        formalName = "Global Flag Binary Name",
        name = "index",
        typeAdapter = PositiveIntegerAdapter.class)
    private BigInteger _index;

    @BoundFlag(
        formalName = "Definition Scope",
        name = "scope",
        defaultValue = "global",
        typeAdapter = TokenAdapter.class,
        valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR, values = {
            @AllowedValue(value = "local",
                description = "This definition is only available in the context of the current Metaschema module."),
            @AllowedValue(value = "global",
                description = "This definition will be made available to any Metaschema module that includes this one either directly or indirectly through a chain of imported Metaschemas.") })))
    private String _scope;

    @BoundFlag(
        formalName = "Deprecated Version",
        name = "deprecated",
        typeAdapter = StringAdapter.class)
    private String _deprecated;

    @BoundFlag(
        formalName = "Flag Value Data Type",
        name = "as-type",
        defaultValue = "string",
        typeAdapter = TokenAdapter.class,
        valueConstraints = @ValueConstraints(allowedValues = @AllowedValues(level = IConstraint.Level.ERROR,
            allowOthers = true,
            values = { @AllowedValue(value = "base64",
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
        formalName = "Default Flag Value",
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

    /**
     * Allows the name of the definition to be overridden.
     */
    @BoundField(
        formalName = "Use Name",
        description = "Allows the name of the definition to be overridden.",
        useName = "use-name")
    private UseName _useName;

    @BoundAssembly(
        useName = "constraint")
    private FlagConstraints _constraint;

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
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineFlag}
     * instance with no metadata.
     */
    public DefineFlag() {
      this(null);
    }

    /**
     * Constructs a new
     * {@code gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA.DefineFlag}
     * instance with the specified metadata.
     *
     * @param data
     *          the metaschema data, or {@code null} if none
     */
    public DefineFlag(IMetaschemaData data) {
      this.__metaschemaData = data;
    }

    @Override
    public IMetaschemaData getMetaschemaData() {
      return __metaschemaData;
    }

    /**
     * Get the global Flag Name.
     *
     * @return the name value
     */
    @NonNull
    public String getName() {
      return _name;
    }

    /**
     * Set the global Flag Name.
     *
     * @param value
     *          the name value to set
     */
    public void setName(@NonNull String value) {
      _name = value;
    }

    /**
     * Get the global Flag Binary Name.
     *
     * @return the index value, or {@code null} if not set
     */
    @Nullable
    public BigInteger getIndex() {
      return _index;
    }

    /**
     * Set the global Flag Binary Name.
     *
     * @param value
     *          the index value to set, or {@code null} to clear
     */
    public void setIndex(@Nullable BigInteger value) {
      _index = value;
    }

    /**
     * Get the definition Scope.
     *
     * @return the scope value, or {@code null} if not set
     */
    @Nullable
    public String getScope() {
      return _scope;
    }

    /**
     * Set the definition Scope.
     *
     * @param value
     *          the scope value to set, or {@code null} to clear
     */
    public void setScope(@Nullable String value) {
      _scope = value;
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
     * Get the flag Value Data Type.
     *
     * @return the as-type value, or {@code null} if not set
     */
    @Nullable
    public String getAsType() {
      return _asType;
    }

    /**
     * Set the flag Value Data Type.
     *
     * @param value
     *          the as-type value to set, or {@code null} to clear
     */
    public void setAsType(@Nullable String value) {
      _asType = value;
    }

    /**
     * Get the default Flag Value.
     *
     * @return the default value, or {@code null} if not set
     */
    @Nullable
    public String getDefault() {
      return _default;
    }

    /**
     * Set the default Flag Value.
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
     * Get the {@code constraint} property.
     *
     * @return the constraint value, or {@code null} if not set
     */
    @Nullable
    public FlagConstraints getConstraint() {
      return _constraint;
    }

    /**
     * Set the {@code constraint} property.
     *
     * @param value
     *          the constraint value to set, or {@code null} to clear
     */
    public void setConstraint(@Nullable FlagConstraints value) {
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
