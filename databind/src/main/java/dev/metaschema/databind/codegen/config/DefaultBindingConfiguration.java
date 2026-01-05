/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedInstance;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.ClassUtils;
import dev.metaschema.databind.config.binding.MetaschemaBindings;
import dev.metaschema.databind.io.BindingException;
import dev.metaschema.databind.io.Format;
import dev.metaschema.databind.io.IDeserializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Default implementation of {@link IBindingConfiguration} that provides binding
 * configuration for Java class generation from Metaschema modules.
 * <p>
 * This implementation supports loading configuration from XML files and
 * provides namespace-to-package mappings and definition-specific binding
 * configurations.
 */
public class DefaultBindingConfiguration implements IBindingConfiguration {
  private static final Logger LOGGER = LogManager.getLogger(DefaultBindingConfiguration.class);

  private final Map<String, String> namespaceToPackageNameMap = new ConcurrentHashMap<>();
  // metaschema location -> ModelType -> Definition name -> IBindingConfiguration
  private final Map<String, MetaschemaBindingConfiguration> moduleUrlToMetaschemaBindingConfigurationMap
      = new ConcurrentHashMap<>();

  @Override
  public String getPackageNameForModule(IModule module) {
    URI namespace = module.getXmlNamespace();
    return getPackageNameForNamespace(ObjectUtils.notNull(namespace.toASCIIString()));
  }

  /**
   * Retrieve the binding configuration for the provided {@code definition}.
   * <p>
   * This method first checks for a binding by the definition's name. If not found
   * and the definition is inline, it also checks for a binding by the
   * definition's path (using "/" separated ancestor names).
   *
   * @param definition
   *          the definition to get the config for
   * @return the binding configuration or {@code null} if there is not
   *         configuration
   */
  @Override
  @Nullable
  public IDefinitionBindingConfiguration getBindingConfigurationForDefinition(
      @NonNull IModelDefinition definition) {
    String moduleUri = ObjectUtils.notNull(definition.getContainingModule().getLocation().toASCIIString());
    String definitionName = definition.getName();

    MetaschemaBindingConfiguration metaschemaConfig = getMetaschemaBindingConfiguration(moduleUri);

    IDefinitionBindingConfiguration retval = null;
    if (metaschemaConfig != null) {
      switch (definition.getModelType()) {
      case ASSEMBLY:
        // First try by name
        retval = metaschemaConfig.getAssemblyDefinitionBindingConfig(definitionName);
        // If not found and inline, try by path
        if (retval == null && definition.isInline()) {
          String path = computeDefinitionPath(definition);
          retval = metaschemaConfig.getAssemblyDefinitionBindingConfig(path);
        }
        break;
      case FIELD:
        // First try by name
        retval = metaschemaConfig.getFieldDefinitionBindingConfig(definitionName);
        // If not found and inline, try by path
        if (retval == null && definition.isInline()) {
          String path = computeDefinitionPath(definition);
          retval = metaschemaConfig.getFieldDefinitionBindingConfig(path);
        }
        break;
      default:
        throw new UnsupportedOperationException(
            String.format("Unsupported definition type '%s'", definition.getModelType()));
      }
    }
    return retval;
  }

  /**
   * Compute the path for an inline definition.
   * <p>
   * The path is constructed by walking up the definition hierarchy and
   * concatenating ancestor definition names with "/" separators. For example, an
   * inline assembly "assembly" within "scope" within
   * "metaschema-module-constraints" would have path "scope/assembly".
   *
   * @param definition
   *          the definition to compute the path for
   * @return the computed path, or just the definition name if not inline
   */
  @NonNull
  private static String computeDefinitionPath(@NonNull IModelDefinition definition) {
    StringBuilder path = new StringBuilder();
    IModelDefinition current = definition;

    while (current.isInline()) {
      if (path.length() > 0) {
        path.insert(0, "/");
      }
      path.insert(0, current.getName());

      // Walk up to the parent definition
      INamedInstance inlineInstance = current.getInlineInstance();
      if (inlineInstance != null) {
        current = inlineInstance.getContainingDefinition();
      } else {
        break;
      }
    }

    return ObjectUtils.notNull(path.toString());
  }

  @Override
  public String getQualifiedBaseClassName(IModelDefinition definition) {
    IDefinitionBindingConfiguration config = getBindingConfigurationForDefinition(definition);
    return config == null
        ? null
        : config.getQualifiedBaseClassName();
  }

  @Override
  public String getClassName(IModelDefinition definition) {
    IDefinitionBindingConfiguration config = getBindingConfigurationForDefinition(definition);

    String retval = null;
    if (config != null) {
      retval = config.getClassName();
    }

    if (retval == null) {
      retval = ClassUtils.toClassName(definition.getName());
    }
    return retval;
  }

  @NonNull
  @Override
  public String getClassName(@NonNull IModule module) {
    // TODO: make this configurable
    return ClassUtils.toClassName(module.getShortName() + "Module");
  }

  @Override
  public List<String> getQualifiedSuperinterfaceClassNames(IModelDefinition definition) {
    IDefinitionBindingConfiguration config = getBindingConfigurationForDefinition(definition);
    return config == null
        ? CollectionUtil.emptyList()
        : config.getInterfacesToImplement();
  }

  /**
   * Get the property binding configuration for a specific property within a
   * definition.
   *
   * @param definition
   *          the containing definition
   * @param propertyName
   *          the name of the property
   * @return the property binding configuration, or {@code null} if none is
   *         configured
   */
  @Nullable
  public IPropertyBindingConfiguration getPropertyBindingConfiguration(
      @NonNull IModelDefinition definition,
      @NonNull String propertyName) {
    String moduleUri = ObjectUtils.notNull(definition.getContainingModule().getLocation().toASCIIString());
    String definitionName = definition.getName();

    MetaschemaBindingConfiguration metaschemaConfig = getMetaschemaBindingConfiguration(moduleUri);
    if (metaschemaConfig == null) {
      return null;
    }

    return metaschemaConfig.getPropertyBindingConfig(definitionName, propertyName);
  }

  /**
   * Binds an XML namespace, which is normally associated with one or more Module,
   * with a provided Java package name.
   *
   * @param namespace
   *          an XML namespace URI
   * @param packageName
   *          the package name to associate with the namespace
   * @throws IllegalStateException
   *           if the binding configuration is changing a previously changed
   *           namespace to package binding
   */
  public void addModelBindingConfig(String namespace, String packageName) {
    if (namespaceToPackageNameMap.containsKey(namespace)) {
      String oldPackageName = namespaceToPackageNameMap.get(namespace);
      if (!oldPackageName.equals(packageName)) {
        throw new IllegalStateException(
            String.format("Attempt to redefine existing package name '%s' to '%s' for namespace '%s'",
                oldPackageName,
                packageName,
                namespace));
      } // else the same package name, so do nothing
    } else {
      namespaceToPackageNameMap.put(namespace, packageName);
    }
  }

  /**
   * Based on the current binding configuration, generate a Java package name for
   * the provided namespace. If the namespace is already mapped, such as through
   * the use of {@link #addModelBindingConfig(String, String)}, then the provided
   * package name will be used. If the namespace is not mapped, then the namespace
   * URI will be translated into a Java package name.
   *
   * @param namespace
   *          the namespace to generate a Java package name for
   * @return a Java package name
   */
  @NonNull
  protected String getPackageNameForNamespace(@NonNull String namespace) {
    String packageName = namespaceToPackageNameMap.get(namespace);
    if (packageName == null) {
      packageName = ClassUtils.toPackageName(namespace);
    }
    return packageName;
  }

  /**
   * Get the binding configuration for the provided Module.
   *
   * @param module
   *          the Module module
   * @return the configuration for the Module or {@code null} if there is no
   *         configuration
   */
  protected MetaschemaBindingConfiguration getMetaschemaBindingConfiguration(@NonNull IModule module) {
    String moduleUri = ObjectUtils.notNull(module.getLocation().toString());
    return getMetaschemaBindingConfiguration(moduleUri);

  }

  /**
   * Get the binding configuration for the Module modulke located at the provided
   * {@code moduleUri}.
   *
   * @param moduleUri
   *          the location of the Module module
   * @return the configuration for the Module module or {@code null} if there is
   *         no configuration
   */
  @Nullable
  protected MetaschemaBindingConfiguration getMetaschemaBindingConfiguration(@NonNull String moduleUri) {
    return moduleUrlToMetaschemaBindingConfigurationMap.get(moduleUri);
  }

  /**
   * Set the binding configuration for the Module module located at the provided
   * {@code moduleUri}.
   *
   * @param moduleUri
   *          the location of the Module module
   * @param config
   *          the Module binding configuration
   * @return the old configuration for the Module module or {@code null} if there
   *         was no previous configuration
   */
  public MetaschemaBindingConfiguration addMetaschemaBindingConfiguration(
      @NonNull String moduleUri,
      @NonNull MetaschemaBindingConfiguration config) {
    Objects.requireNonNull(moduleUri, "moduleUri");
    Objects.requireNonNull(config, "config");
    return moduleUrlToMetaschemaBindingConfigurationMap.put(moduleUri, config);
  }

  /**
   * Load the binding configuration from the provided {@code file}.
   *
   * @param file
   *          the configuration resource
   * @throws IOException
   *           if an error occurred while reading the {@code file}
   * @throws BindingException
   *           if an error occurred while processing the binding configuration
   */
  public void load(Path file) throws IOException, BindingException {
    URL resource = ObjectUtils.notNull(file.toAbsolutePath().normalize().toUri().toURL());
    load(resource);
  }

  /**
   * Load the binding configuration from the provided {@code file}.
   *
   * @param file
   *          the configuration resource
   * @throws IOException
   *           if an error occurred while reading the {@code file}
   * @throws BindingException
   *           if an error occurred while processing the binding configuration
   */
  public void load(File file) throws IOException, BindingException {
    load(file.toPath());
  }

  /**
   * Load the binding configuration from the provided {@code resource}.
   *
   * @param resource
   *          the configuration resource
   * @throws IOException
   *           if an error occurred while reading the {@code resource}
   * @throws BindingException
   *           if an error occurred while processing the binding configuration
   */
  public void load(@NonNull URL resource) throws IOException, BindingException {
    IBindingContext context = IBindingContext.newInstance();
    IDeserializer<MetaschemaBindings> deserializer = context.newDeserializer(Format.XML, MetaschemaBindings.class);

    MetaschemaBindings bindings;
    try {
      bindings = deserializer.deserialize(resource);
    } catch (IOException | URISyntaxException ex) {
      throw new IOException("Failed to parse binding configuration: " + resource, ex);
    }

    List<MetaschemaBindings.ModelBinding> modelBindings = bindings.getModelBindings();
    for (MetaschemaBindings.ModelBinding model : modelBindings) {
      processModelBindingConfig(model);
    }

    List<MetaschemaBindings.MetaschemaBinding> metaschemaBindings = bindings.getMetaschemaBindings();
    for (MetaschemaBindings.MetaschemaBinding metaschema : metaschemaBindings) {
      try {
        processMetaschemaBindingConfig(resource, metaschema);
      } catch (MalformedURLException | URISyntaxException ex) {
        throw new IOException(ex);
      }
    }
  }

  private void processModelBindingConfig(MetaschemaBindings.ModelBinding model) {
    String namespace = model.getNamespace().toString();

    MetaschemaBindings.ModelBinding.Java java = model.getJava();
    if (java != null) {
      String packageName = java.getUsePackageName();
      if (packageName != null) {
        addModelBindingConfig(namespace, packageName);
      }
    }
  }

  private void processMetaschemaBindingConfig(URL configResource, MetaschemaBindings.MetaschemaBinding metaschema)
      throws MalformedURLException, URISyntaxException, BindingException {
    String href = metaschema.getHref().toString();
    URL moduleUrl = new URL(configResource, href);
    String moduleUri = ObjectUtils.notNull(moduleUrl.toURI().normalize().toString());

    MetaschemaBindingConfiguration metaschemaConfig = getMetaschemaBindingConfiguration(moduleUri);
    if (metaschemaConfig == null) {
      metaschemaConfig = new MetaschemaBindingConfiguration();
      addMetaschemaBindingConfiguration(moduleUri, metaschemaConfig);
    }

    List<MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding> assemblyBindings
        = metaschema.getDefineAssemblyBindings();
    for (MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding assemblyBinding : assemblyBindings) {
      String name = assemblyBinding.getName();
      String target = assemblyBinding.getTarget();

      // Determine the lookup key - use name if provided, otherwise use target
      String lookupKey = name != null ? name : target;
      if (lookupKey != null) {
        IDefinitionBindingConfiguration config = metaschemaConfig.getAssemblyDefinitionBindingConfig(lookupKey);
        config = processDefinitionBindingConfiguration(config, assemblyBinding.getJava());
        metaschemaConfig.addAssemblyDefinitionBindingConfig(lookupKey, config);

        // Process property bindings for this assembly
        processAssemblyPropertyBindings(metaschemaConfig, lookupKey, assemblyBinding.getPropertyBindings());

        // Process choice group bindings for this assembly
        processChoiceGroupBindings(config, assemblyBinding.getChoiceGroupBindings());
      } else {
        LOGGER.warn("Assembly binding in metaschema '{}' has neither 'name' nor 'target' attribute; skipping",
            moduleUri);
      }
    }

    List<MetaschemaBindings.MetaschemaBinding.DefineFieldBinding> fieldBindings
        = metaschema.getDefineFieldBindings();
    for (MetaschemaBindings.MetaschemaBinding.DefineFieldBinding fieldBinding : fieldBindings) {
      String name = fieldBinding.getName();
      String target = fieldBinding.getTarget();

      // Determine the lookup key - use name if provided, otherwise use target
      String lookupKey = name != null ? name : target;
      if (lookupKey != null) {
        IDefinitionBindingConfiguration config = metaschemaConfig.getFieldDefinitionBindingConfig(lookupKey);
        config = processDefinitionBindingConfiguration(config, fieldBinding.getJava());
        metaschemaConfig.addFieldDefinitionBindingConfig(lookupKey, config);

        // Process property bindings for this field
        processFieldPropertyBindings(metaschemaConfig, lookupKey, fieldBinding.getPropertyBindings());
      } else {
        LOGGER.warn("Field binding in metaschema '{}' has neither 'name' nor 'target' attribute; skipping",
            moduleUri);
      }
    }
  }

  /**
   * Process property bindings from a definition binding element.
   * <p>
   * This generic helper method consolidates the common logic for processing
   * property bindings from both assembly and field definition bindings.
   *
   * @param <P>
   *          the property binding type
   * @param <J>
   *          the Java configuration type
   * @param metaschemaConfig
   *          the metaschema binding configuration to add property bindings to
   * @param definitionName
   *          the name of the containing definition
   * @param propertyBindings
   *          the list of property bindings to process
   * @param nameAccessor
   *          function to extract the property name from a binding
   * @param javaAccessor
   *          function to extract the Java config from a binding
   * @param collectionClassAccessor
   *          function to extract the collection class name from a Java config
   * @throws BindingException
   *           if the collection class is invalid or cannot be found
   */
  private static <P, J> void processPropertyBindings(
      @NonNull MetaschemaBindingConfiguration metaschemaConfig,
      @NonNull String definitionName,
      @Nullable List<P> propertyBindings,
      @NonNull Function<P, String> nameAccessor,
      @NonNull Function<P, J> javaAccessor,
      @NonNull Function<J, String> collectionClassAccessor) throws BindingException {
    if (propertyBindings == null) {
      return;
    }

    for (P propertyBinding : propertyBindings) {
      String propertyName = nameAccessor.apply(propertyBinding);
      if (propertyName == null) {
        continue;
      }

      J java = javaAccessor.apply(propertyBinding);
      if (java == null) {
        continue;
      }

      String collectionClassName = collectionClassAccessor.apply(java);
      if (collectionClassName != null) {
        // Validate the collection class
        validateCollectionClass(collectionClassName, definitionName, propertyName);

        IMutablePropertyBindingConfiguration config = new DefaultPropertyBindingConfiguration();
        config.setCollectionClassName(collectionClassName);
        metaschemaConfig.addPropertyBindingConfig(definitionName, propertyName, config);
      }
    }
  }

  /**
   * Validate that the specified collection class exists and implements a
   * supported collection interface (Collection or Map).
   *
   * @param collectionClassName
   *          the fully qualified class name to validate
   * @param definitionName
   *          the name of the containing definition (for error messages)
   * @param propertyName
   *          the name of the property (for error messages)
   * @throws BindingException
   *           if the class cannot be found or does not implement a supported
   *           collection interface
   */
  private static void validateCollectionClass(
      @NonNull String collectionClassName,
      @NonNull String definitionName,
      @NonNull String propertyName) throws BindingException {
    Class<?> collectionClass;
    try {
      collectionClass = Class.forName(collectionClassName);
    } catch (ClassNotFoundException ex) {
      throw new BindingException(String.format(
          "Collection class '%s' for property '%s' in definition '%s' could not be found",
          collectionClassName, propertyName, definitionName), ex);
    }

    // Check if the class implements Collection or Map
    if (!Collection.class.isAssignableFrom(collectionClass) && !Map.class.isAssignableFrom(collectionClass)) {
      throw new BindingException(String.format(
          "Collection class '%s' for property '%s' in definition '%s' must implement "
              + "java.util.Collection or java.util.Map",
          collectionClassName, propertyName, definitionName));
    }
  }

  /**
   * Process property bindings from a define-assembly-binding element.
   *
   * @param metaschemaConfig
   *          the metaschema binding configuration to add property bindings to
   * @param definitionName
   *          the name of the containing definition
   * @param propertyBindings
   *          the list of property bindings to process
   * @throws BindingException
   *           if the collection class is invalid or cannot be found
   */
  private static void processAssemblyPropertyBindings(
      @NonNull MetaschemaBindingConfiguration metaschemaConfig,
      @NonNull String definitionName,
      @Nullable List<MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding> propertyBindings)
      throws BindingException {
    processPropertyBindings(
        metaschemaConfig,
        definitionName,
        propertyBindings,
        MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding::getName,
        MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding::getJava,
        MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.PropertyBinding.Java::getCollectionClass);
  }

  /**
   * Process property bindings from a define-field-binding element.
   *
   * @param metaschemaConfig
   *          the metaschema binding configuration to add property bindings to
   * @param definitionName
   *          the name of the containing definition
   * @param propertyBindings
   *          the list of property bindings to process
   * @throws BindingException
   *           if the collection class is invalid or cannot be found
   */
  private static void processFieldPropertyBindings(
      @NonNull MetaschemaBindingConfiguration metaschemaConfig,
      @NonNull String definitionName,
      @Nullable List<MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding> propertyBindings)
      throws BindingException {
    processPropertyBindings(
        metaschemaConfig,
        definitionName,
        propertyBindings,
        MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding::getName,
        MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding::getJava,
        MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.PropertyBinding.Java::getCollectionClass);
  }

  /**
   * Process choice group bindings from a define-assembly-binding element.
   *
   * @param config
   *          the definition binding configuration to add choice group bindings to
   * @param choiceGroupBindings
   *          the list of choice group bindings to process
   */
  private static void processChoiceGroupBindings(
      @NonNull IDefinitionBindingConfiguration config,
      @Nullable List<
          MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding> choiceGroupBindings) {
    if (choiceGroupBindings == null || !(config instanceof DefaultDefinitionBindingConfiguration)) {
      return;
    }

    DefaultDefinitionBindingConfiguration mutableConfig = (DefaultDefinitionBindingConfiguration) config;
    for (MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding choiceGroupBinding : choiceGroupBindings) {
      String groupAsName = choiceGroupBinding.getName();
      IChoiceGroupBindingConfiguration choiceGroupConfig
          = new DefaultChoiceGroupBindingConfiguration(choiceGroupBinding);
      mutableConfig.addChoiceGroupBinding(groupAsName, choiceGroupConfig);
    }
  }

  @NonNull
  private static IMutableDefinitionBindingConfiguration processDefinitionBindingConfiguration(
      @Nullable IDefinitionBindingConfiguration oldConfig,
      @Nullable MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.Java java) {
    IMutableDefinitionBindingConfiguration config = oldConfig == null
        ? new DefaultDefinitionBindingConfiguration()
        : new DefaultDefinitionBindingConfiguration(oldConfig);

    if (java != null) {
      String className = java.getUseClassName();
      if (className != null) {
        config.setClassName(ObjectUtils.notNull(className));
      }

      String baseClass = java.getExtendBaseClass();
      if (baseClass != null) {
        config.setQualifiedBaseClassName(ObjectUtils.notNull(baseClass));
      }

      List<String> interfaces = java.getImplementInterfaces();
      for (String interfaceName : interfaces) {
        config.addInterfaceToImplement(Objects.requireNonNull(interfaceName,
            "interface name cannot be null in implement-interfaces configuration"));
      }
    }
    return config;
  }

  @NonNull
  private static IMutableDefinitionBindingConfiguration processDefinitionBindingConfiguration(
      @Nullable IDefinitionBindingConfiguration oldConfig,
      @Nullable MetaschemaBindings.MetaschemaBinding.DefineFieldBinding.Java java) {
    IMutableDefinitionBindingConfiguration config = oldConfig == null
        ? new DefaultDefinitionBindingConfiguration()
        : new DefaultDefinitionBindingConfiguration(oldConfig);

    if (java != null) {
      String className = java.getUseClassName();
      if (className != null) {
        config.setClassName(ObjectUtils.notNull(className));
      }

      String baseClass = java.getExtendBaseClass();
      if (baseClass != null) {
        config.setQualifiedBaseClassName(ObjectUtils.notNull(baseClass));
      }

      List<String> interfaces = java.getImplementInterfaces();
      for (String interfaceName : interfaces) {
        config.addInterfaceToImplement(Objects.requireNonNull(interfaceName,
            "interface name cannot be null in implement-interfaces configuration"));
      }
    }
    return config;
  }

  /**
   * Holds binding configurations for a specific Metaschema module.
   * <p>
   * This class maintains mappings from definition names to their binding
   * configurations for both assembly and field definitions.
   */
  public static final class MetaschemaBindingConfiguration {
    private final Map<String, IDefinitionBindingConfiguration> assemblyBindingConfigs = new ConcurrentHashMap<>();
    private final Map<String, IDefinitionBindingConfiguration> fieldBindingConfigs = new ConcurrentHashMap<>();
    // Map structure: definition name -> property name -> property binding config
    private final Map<String, Map<String, IPropertyBindingConfiguration>> propertyBindingConfigs
        = new ConcurrentHashMap<>();

    private MetaschemaBindingConfiguration() {
    }

    /**
     * Get the binding configuration for the {@link IAssemblyDefinition} with the
     * provided {@code name}.
     *
     * @param name
     *          the definition name
     * @return the definition's binding configuration or {@code null} if no
     *         configuration is provided
     */
    @Nullable
    public IDefinitionBindingConfiguration getAssemblyDefinitionBindingConfig(@NonNull String name) {
      return assemblyBindingConfigs.get(name);
    }

    /**
     * Get the binding configuration for the {@link IFieldDefinition} with the
     * provided {@code name}.
     *
     * @param name
     *          the definition name
     * @return the definition's binding configuration or {@code null} if no
     *         configuration is provided
     */
    @Nullable
    public IDefinitionBindingConfiguration getFieldDefinitionBindingConfig(@NonNull String name) {
      return fieldBindingConfigs.get(name);
    }

    /**
     * Set the binding configuration for the {@link IAssemblyDefinition} with the
     * provided {@code name}.
     *
     * @param name
     *          the definition name
     * @param config
     *          the new binding configuration for the definition
     * @return the definition's old binding configuration or {@code null} if no
     *         configuration was previously provided
     */
    @Nullable
    public IDefinitionBindingConfiguration addAssemblyDefinitionBindingConfig(@NonNull String name,
        @NonNull IDefinitionBindingConfiguration config) {
      return assemblyBindingConfigs.put(name, config);
    }

    /**
     * Set the binding configuration for the {@link IFieldDefinition} with the
     * provided {@code name}.
     *
     * @param name
     *          the definition name
     * @param config
     *          the new binding configuration for the definition
     * @return the definition's old binding configuration or {@code null} if no
     *         configuration was previously provided
     */
    @Nullable
    public IDefinitionBindingConfiguration addFieldDefinitionBindingConfig(@NonNull String name,
        @NonNull IDefinitionBindingConfiguration config) {
      return fieldBindingConfigs.put(name, config);
    }

    /**
     * Get the property binding configuration for a specific property within a
     * definition.
     *
     * @param definitionName
     *          the name of the containing definition
     * @param propertyName
     *          the name of the property
     * @return the property binding configuration, or {@code null} if none is
     *         configured
     */
    @Nullable
    public IPropertyBindingConfiguration getPropertyBindingConfig(
        @NonNull String definitionName,
        @NonNull String propertyName) {
      Map<String, IPropertyBindingConfiguration> defProps = propertyBindingConfigs.get(definitionName);
      return defProps == null ? null : defProps.get(propertyName);
    }

    /**
     * Set the property binding configuration for a specific property within a
     * definition.
     *
     * @param definitionName
     *          the name of the containing definition
     * @param propertyName
     *          the name of the property
     * @param config
     *          the property binding configuration
     * @return the old property binding configuration, or {@code null} if none was
     *         previously configured
     */
    @Nullable
    public IPropertyBindingConfiguration addPropertyBindingConfig(
        @NonNull String definitionName,
        @NonNull String propertyName,
        @NonNull IPropertyBindingConfiguration config) {
      return propertyBindingConfigs
          .computeIfAbsent(definitionName, k -> new ConcurrentHashMap<>())
          .put(propertyName, config);
    }
  }
}
