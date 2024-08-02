/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.EQNameUtils;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IModule {

  String XML_NAMESPACE = "http://csrc.nist.gov/ns/oscal/metaschema/1.0";

  /**
   * Retrieves the location where the Metaschema module was loaded from.
   *
   * @return the location, or {@code null} if this information is not available
   */
  URI getLocation();

  /**
   * Get the long name for the Metaschema module.
   *
   * @return the name
   */
  @NonNull
  MarkupLine getName();

  /**
   * Get the revision of the Metaschema module.
   *
   * @return the revision
   */
  @NonNull
  String getVersion();

  /**
   * Retrieve the remarks associated with this Metaschema module, if any.
   *
   * @return the remarks or {@code null} if no remarks are defined
   */
  @Nullable
  MarkupMultiline getRemarks();

  /**
   * Retrieves the unique short name for the Metaschema module, which provides a
   * textual identifier for the Metaschema module.
   *
   * @return the short name
   */
  @NonNull
  String getShortName();

  /**
   * Retrieves the XML namespace associated with the Metaschema module.
   *
   * @return a namespace
   */
  @NonNull
  URI getXmlNamespace();

  /**
   * Retrieve the JSON schema base URI associated with the Metaschema module.
   *
   * @return the base URI
   */
  @NonNull
  URI getJsonBaseUri();

  /**
   * Get the qualified name associated with the Metaschema module.
   *
   * @return the qualified name
   */
  default QName getQName() {
    return new QName(getXmlNamespace().toASCIIString(), getShortName());
  }

  /**
   * Retrieves all Metaschema modules imported by this Metaschema module.
   *
   * @return a list of imported Metaschema modules
   */
  @NonNull
  List<? extends IModule> getImportedModules();

  /**
   * Retrieve the imported Metaschema module with the specified name, if it
   * exists.
   *
   * @param name
   *          the short name of the Metschema module to retrieve
   * @return the imported Metaschema module or {@code null} if it doesn't exist
   */
  @Nullable
  IModule getImportedModuleByShortName(String name);

  /**
   * Retrieves the top-level assembly definitions in this Metaschema module.
   *
   * @return the collection of assembly definitions
   */
  @NonNull
  Collection<? extends IAssemblyDefinition> getAssemblyDefinitions();

  /**
   * Retrieves the top-level assembly definition in this Metaschema module with
   * the matching name, if it exists.
   *
   * @param name
   *          the definition name
   *
   * @return the matching assembly definition, or {@code null} if none match
   */
  @Nullable
  IAssemblyDefinition getAssemblyDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the top-level field definitions in this Metaschema module.
   *
   * @return the collection of field definitions
   */
  @NonNull
  Collection<? extends IFieldDefinition> getFieldDefinitions();

  /**
   * Retrieves the top-level field definition in this Metaschema module with the
   * matching name, if it exists.
   *
   * @param name
   *          the definition name
   *
   * @return the matching field definition, or {@code null} if none match
   */
  @Nullable
  IFieldDefinition getFieldDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the top-level assembly and field definitions in this Metaschema
   * module.
   *
   * @return a listing of assembly and field definitions
   */
  @SuppressWarnings("unchecked")
  @NonNull
  List<? extends IModelDefinition> getAssemblyAndFieldDefinitions();

  /**
   * Retrieves the top-level flag definitions in this Metaschema module.
   *
   * @return the collection of flag definitions
   */
  @NonNull
  Collection<? extends IFlagDefinition> getFlagDefinitions();

  /**
   * Retrieves the top-level flag definition in this Metaschema module with the
   * matching name, if it exists.
   *
   * @param name
   *          the definition name
   *
   * @return the matching flag definition, or {@code null} if none match
   */
  @Nullable
  IFlagDefinition getFlagDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the assembly definition with a matching name from either: 1) the
   * top-level assembly definitions from this Metaschema module, or 2) global
   * assembly definitions from each imported Metaschema module in reverse order of
   * import.
   *
   * @param name
   *          the name of the assembly to find
   * @return the assembly definition
   */
  @Nullable
  IAssemblyDefinition getScopedAssemblyDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the field definition with a matching name from either: 1) the
   * top-level field definitions from this Metaschema module, or 2) global field
   * definitions from each imported Metaschema module in reverse order of import.
   *
   * @param name
   *          the name of the field definition to find
   * @return the field definition
   */
  @Nullable
  IFieldDefinition getScopedFieldDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the flag definition with a matching name from either: 1) the
   * top-level flag definitions from this Metaschema module, or 2) global flag
   * definitions from each imported Metaschema module in reverse order of import.
   *
   * @param name
   *          the name of the flag definition to find
   * @return the flag definition
   */
  @Nullable
  IFlagDefinition getScopedFlagDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the top-level assembly definitions that are marked as roots from
   * the current Metaschema module.
   *
   * @return a listing of assembly definitions marked as root
   */
  @NonNull
  Collection<? extends IAssemblyDefinition> getRootAssemblyDefinitions();

  /**
   * Retrieve the top-level flag definitions that are marked global in this
   * Metaschema module or in any imported Metaschema modules. The resulting
   * collection is built by adding global definitions from each imported
   * Metaschema module in order of import, then adding global definitions from the
   * current Metaschema module. Such a map is built in this way for each imported
   * Metaschema module in the chain. Values for clashing keys will be replaced in
   * this order, giving preference to the "closest" definition.
   *
   * @return the collection of exported flag definitions
   */
  @NonNull
  Collection<? extends IFlagDefinition> getExportedFlagDefinitions();

  /**
   * Retrieves the exported named flag definition, if it exists.
   * <p>
   * For information about how flag definitions are exported see
   * {@link #getExportedFlagDefinitions()}.
   *
   * @param name
   *          the definition name
   * @return the flag definition, or {@code null} if it doesn't exist.
   */
  @Nullable
  IFlagDefinition getExportedFlagDefinitionByName(@NonNull QName name);

  /**
   * Retrieve the top-level field definitions that are marked global in this
   * Metaschema module or in any imported Metaschema module. The resulting
   * collection is built by adding global definitions from each imported
   * Metaschema module in order of import, then adding global definitions from the
   * current Metaschema module. Such a map is built in this way for each imported
   * Metaschema module in the chain. Values for clashing keys will be replaced in
   * this order, giving preference to the "closest" definition
   *
   * @return the collection of exported field definitions
   */
  @NonNull
  Collection<? extends IFieldDefinition> getExportedFieldDefinitions();

  /**
   * Retrieves the exported named field definition, if it exists.
   * <p>
   * For information about how field definitions are exported see
   * {@link #getExportedFieldDefinitions()}.
   *
   * @param name
   *          the definition name
   * @return the field definition, or {@code null} if it doesn't exist.
   */
  @Nullable
  IFieldDefinition getExportedFieldDefinitionByName(@NonNull QName name);

  /**
   * Retrieve the top-level assembly definitions that are marked global in this
   * Metaschema module or in any imported Metaschema module. The resulting
   * collection is built by adding global definitions from each imported
   * Metaschema module in order of import, then adding global definitions from the
   * current Metaschema module. This collection is built in this way for each
   * imported Metaschema module in the chain. Items with duplicate names will be
   * replaced in this order, giving preference to the "closest" definition
   *
   * @return the collection of exported assembly definitions
   */
  @NonNull
  Collection<? extends IAssemblyDefinition> getExportedAssemblyDefinitions();

  /**
   * Retrieves the exported named assembly definition, if it exists.
   * <p>
   * For information about how assembly definitions are exported see
   * {@link #getExportedAssemblyDefinitions()}.
   *
   * @param name
   *          the definition name
   * @return the assembly definition, or {@code null} if it doesn't exist.
   */
  @Nullable
  IAssemblyDefinition getExportedAssemblyDefinitionByName(@NonNull QName name);

  /**
   * Retrieves the top-level assembly definitions that are marked as roots from
   * the current Metaschema module and any imported Metaschema modules.
   *
   * @return a listing of assembly definitions marked as root
   */
  @NonNull
  Collection<? extends IAssemblyDefinition> getExportedRootAssemblyDefinitions();

  /**
   * Retrieves the exported named root assembly definition, if it exists.
   * <p>
   * For information about how assembly definitions are exported see
   * {@link #getExportedAssemblyDefinitions()}.
   *
   * @param name
   *          the root name
   * @return the assembly definition, or {@code null} if it doesn't exist.
   */
  @Nullable
  IAssemblyDefinition getExportedRootAssemblyDefinitionByName(QName name);

  /**
   * Get the mapping of prefix to namespace URI for use in resolving the namespace
   * of lexical qualified named in Metapath.
   *
   * @return the mapping
   */
  @NonNull
  Map<String, String> getNamespaceBindings();

  /**
   * Used to parse a flag name reference to produce a qualified name.
   *
   * @param nameRef
   *          the name reference
   * @return the qualified name
   */
  @NonNull
  default QName toFlagQName(@NonNull String nameRef) {
    return EQNameUtils.parseName(
        nameRef,
        getModuleStaticContext().getFlagPrefixResolver());
  }

  /**
   * Used to parse a flag name reference to produce a qualified name.
   *
   * @param modelNamespace
   *          the namespace to use or {@code null}
   * @param nameRef
   *          the name reference
   * @return the qualified name
   */
  @NonNull
  default QName toFlagQName(@Nullable String modelNamespace, @NonNull String nameRef) {
    return modelNamespace == null
        ? new QName(nameRef)
        : new QName(modelNamespace, nameRef);
  }

  /**
   * Used to parse a model name reference to produce a qualified name.
   *
   * @param nameRef
   *          the name reference
   * @return the qualified name
   */
  @NonNull
  default QName toModelQName(@NonNull String nameRef) {
    return EQNameUtils.parseName(
        nameRef,
        getModuleStaticContext().getModelPrefixResolver());
  }

  /**
   * Used to parse a flag name reference to produce a qualified name.
   *
   * @param modelNamespace
   *          the namespace to use or {@code null}
   * @param nameRef
   *          the name reference
   * @return the qualified name
   */
  @NonNull
  default QName toModelQName(@Nullable String modelNamespace, @NonNull String nameRef) {
    String namespace = modelNamespace == null ? getXmlNamespace().toASCIIString() : modelNamespace;
    return new QName(namespace, nameRef);
  }

  /**
   * Get the Metapath static context for compiling Metapath expressions that query
   * instances of this model.
   *
   * @return the static context
   */
  @NonNull
  StaticContext getModuleStaticContext();
}
