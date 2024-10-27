/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.AbstractModule;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.MetapathNamespace;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.talsmasoftware.lazy4j.Lazy;

@SuppressWarnings("PMD.CouplingBetweenObjects")
public class BindingModule
    extends AbstractModule<
        IBindingMetaschemaModule,
        IModelDefinition,
        IFlagDefinition,
        IFieldDefinition,
        IAssemblyDefinition>
    implements IBindingMetaschemaModule {
  @NonNull
  private final Lazy<StaticContext> staticContext;
  @NonNull
  private final METASCHEMA binding;
  @NonNull
  private final Lazy<IDocumentNodeItem> documentNodeItem;
  @NonNull
  private final Lazy<IModuleNodeItem> moduleNodeItem;
  @NonNull
  private final Lazy<Definitions> definitions;
  @NonNull
  private final ISource source;

  /**
   * Constructs a new Metaschema instance.
   *
   * @param resource
   *          the resource from which the module was loaded
   * @param rootDefinition
   *          the underlying definition binding for the module
   * @param binding
   *          the module definition object bound to a Java object
   * @param importedModules
   *          the modules imported by this module
   * @throws MetaschemaException
   *           if a processing error occurs
   */
  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public BindingModule( // NOPMD - unavoidable
      @NonNull URI resource,
      @NonNull IBoundDefinitionModelAssembly rootDefinition,
      @NonNull METASCHEMA binding,
      @NonNull List<? extends IBindingMetaschemaModule> importedModules) throws MetaschemaException {
    super(importedModules);

    this.binding = binding;

    this.staticContext = ObjectUtils.notNull(Lazy.lazy(() -> {
      StaticContext.Builder builder = StaticContext.builder()
          .baseUri(resource)
          .defaultModelNamespace(getXmlNamespace());

      getNamespaceBindings()
          .forEach((prefix, ns) -> builder.namespace(
              ObjectUtils.notNull(prefix), ObjectUtils.notNull(ns)));
      return builder.build();
    }));

    INodeItemFactory nodeItemFactory = INodeItemFactory.instance();
    this.definitions = ObjectUtils.notNull(Lazy.lazy(() -> new Definitions(resource, rootDefinition, nodeItemFactory)));
    this.documentNodeItem
        = ObjectUtils.notNull(Lazy.lazy(() -> nodeItemFactory.newDocumentNodeItem(rootDefinition, resource, binding)));
    this.moduleNodeItem
        = ObjectUtils.notNull(Lazy.lazy(() -> nodeItemFactory.newModuleNodeItem(this)));
    this.source = ISource.moduleSource(this);
  }

  @Override
  public ISource getSource() {
    return source;
  }

  @Override
  public final METASCHEMA getBinding() {
    return binding;
  }

  @Override
  public IDocumentNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(documentNodeItem.get());
  }

  @Override
  public IModuleNodeItem getModuleNodeItem() {
    return ObjectUtils.notNull(moduleNodeItem.get());
  }

  @Override
  public final StaticContext getModuleStaticContext() {
    return ObjectUtils.notNull(staticContext.get());
  }

  @Override
  @NonNull
  public final URI getLocation() {
    return ObjectUtils.notNull(getModuleStaticContext().getBaseUri());
  }

  @Override
  public MarkupLine getName() {
    MarkupLine retval = getBinding().getSchemaName();
    if (retval == null) {
      throw new IllegalStateException(String.format("The schema name is NULL for module '%s'.", getLocation()));
    }
    return retval;
  }

  @Override
  public String getVersion() {
    String retval = getBinding().getSchemaVersion();
    if (retval == null) {
      throw new IllegalStateException(String.format("The schema version is NULL for module '%s'.", getLocation()));
    }
    return retval;
  }

  @Override
  public MarkupMultiline getRemarks() {
    return ModelSupport.remarks(getBinding().getRemarks());
  }

  @Override
  public String getShortName() {
    String retval = getBinding().getShortName();
    if (retval == null) {
      throw new IllegalStateException(String.format("The schema short name is NULL for module '%s'.", getLocation()));
    }
    return retval;
  }

  @Override
  public final URI getXmlNamespace() {
    URI retval = getBinding().getNamespace();
    if (retval == null) {
      throw new IllegalStateException(
          String.format("The XML schema namespace is NULL for module '%s'.", getLocation()));
    }
    return retval;
  }

  @Override
  public URI getJsonBaseUri() {
    URI retval = getBinding().getJsonBaseUri();
    if (retval == null) {
      throw new IllegalStateException(String.format("The JSON schema URI is NULL for module '%s'.", getLocation()));
    }
    return retval;
  }

  @Override
  public Map<String, String> getNamespaceBindings() {
    return ObjectUtils.notNull(CollectionUtil.listOrEmpty(binding.getNamespaceBindings()).stream()
        .collect(Collectors.toMap(
            MetapathNamespace::getPrefix,
            binding -> binding.getUri().toASCIIString(),
            (v1, v2) -> v2,
            LinkedHashMap::new)));
  }

  @NonNull
  private Definitions getDefinitions() {
    return ObjectUtils.notNull(definitions.get());
  }

  @NonNull
  private Map<QName, IAssemblyDefinition> getAssemblyDefinitionMap() {
    return getDefinitions().assemblyDefinitions;
  }

  @Override
  public Collection<IAssemblyDefinition> getAssemblyDefinitions() {
    return ObjectUtils.notNull(getAssemblyDefinitionMap().values());
  }

  @Override
  public IAssemblyDefinition getAssemblyDefinitionByName(@NonNull QName name) {
    return getAssemblyDefinitionMap().get(name);
  }

  @NonNull
  private Map<QName, IFieldDefinition> getFieldDefinitionMap() {
    return getDefinitions().fieldDefinitions;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<IFieldDefinition> getFieldDefinitions() {
    return getFieldDefinitionMap().values();
  }

  @Override
  public IFieldDefinition getFieldDefinitionByName(@NonNull QName name) {
    return getFieldDefinitionMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  public List<IModelDefinition> getAssemblyAndFieldDefinitions() {
    return Stream.concat(getAssemblyDefinitions().stream(), getFieldDefinitions().stream())
        .collect(Collectors.toList());
  }

  @NonNull
  private Map<QName, IFlagDefinition> getFlagDefinitionMap() {
    return getDefinitions().flagDefinitions;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<IFlagDefinition> getFlagDefinitions() {
    return getFlagDefinitionMap().values();
  }

  @Override
  public IFlagDefinition getFlagDefinitionByName(@NonNull QName name) {
    return getFlagDefinitionMap().get(name);
  }

  private Map<QName, ? extends IAssemblyDefinition> getRootAssemblyDefinitionMap() {
    return getDefinitions().rootAssemblyDefinitions;
  }

  @SuppressWarnings("null")
  @Override
  public Collection<? extends IAssemblyDefinition> getRootAssemblyDefinitions() {
    return getRootAssemblyDefinitionMap().values();
  }

  private final class Definitions {
    @NonNull
    private final Map<QName, IFlagDefinition> flagDefinitions;
    @NonNull
    private final Map<QName, IFieldDefinition> fieldDefinitions;
    @NonNull
    private final Map<QName, IAssemblyDefinition> assemblyDefinitions;
    @NonNull
    private final Map<QName, IAssemblyDefinition> rootAssemblyDefinitions;

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Definitions(
        @NonNull URI resource,
        @NonNull IBoundDefinitionModelAssembly rootDefinition,
        @NonNull INodeItemFactory nodeItemFactory) {

      this.flagDefinitions = new LinkedHashMap<>();
      this.fieldDefinitions = new LinkedHashMap<>();
      this.assemblyDefinitions = new LinkedHashMap<>();
      this.rootAssemblyDefinitions = new LinkedHashMap<>();

      // create instance position counters
      int globalFlagPosition = 0;
      int globalFieldPosition = 0;
      int globalAssemblyPosition = 0;

      IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
          rootDefinition.getChoiceGroupInstanceByName("definitions"));

      for (Object obj : binding.getDefinitions()) {
        assert obj != null : "Object was null";

        IBoundInstanceModelGroupedAssembly objInstance
            = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

        if (obj instanceof METASCHEMA.DefineAssembly) {
          IAssemblyDefinition definition = new DefinitionAssemblyGlobal(
              (METASCHEMA.DefineAssembly) obj,
              objInstance,
              globalAssemblyPosition++,
              BindingModule.this,
              nodeItemFactory);
          QName name = definition.getDefinitionQName();
          assemblyDefinitions.put(name, definition);
          if (definition.isRoot()) {
            rootAssemblyDefinitions.put(name, definition);
          }
        } else if (obj instanceof METASCHEMA.DefineField) {
          IFieldDefinition definition = new DefinitionFieldGlobal(
              (METASCHEMA.DefineField) obj,
              objInstance,
              globalFieldPosition++,
              BindingModule.this);
          QName name = definition.getDefinitionQName();
          fieldDefinitions.put(name, definition);
        } else if (obj instanceof METASCHEMA.DefineFlag) {
          IFlagDefinition definition = new DefinitionFlagGlobal(
              (METASCHEMA.DefineFlag) obj,
              objInstance,
              globalFlagPosition++,
              BindingModule.this);
          QName name = definition.getDefinitionQName();
          flagDefinitions.put(name, definition);
        } else {
          throw new IllegalStateException(
              String.format("Unrecognized definition class '%s' in module '%s'.",
                  obj.getClass(),
                  resource.toASCIIString()));
        }
      }
    }
  }

}
