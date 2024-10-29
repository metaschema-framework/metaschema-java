/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.datatype.DataTypeService;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.constraint.FindingCollectingConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.ValidationFeature;
import gov.nist.secauto.metaschema.core.model.validation.AggregateValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.IValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator;
import gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.DefaultModuleBindingGenerator;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.io.DefaultBoundLoader;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.io.IDeserializer;
import gov.nist.secauto.metaschema.databind.io.ISerializer;
import gov.nist.secauto.metaschema.databind.io.yaml.YamlOperations;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModel;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaField;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingConstraintLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingModuleLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.ModuleLoadingPostProcessor;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides information supporting a binding between a set of Module models and
 * corresponding Java classes.
 */
public interface IBindingContext {
  /**
   * Get a new builder that can produce a new, configured binding context.
   *
   * @return the builder
   * @since 2.0.0
   */
  static BindingContextBuilder builder() {
    return new BindingContextBuilder();
  }

  /**
   * Get a new {@link IBindingContext} instance, which can be used to load
   * information that binds a model to a set of Java classes.
   *
   * @return a new binding context
   * @since 2.0.0
   */
  @NonNull
  static IBindingContext newInstance() {
    return new DefaultBindingContext();
  }

  /**
   * Get a new {@link IBindingContext} instance, which can be used to load
   * information that binds a model to a set of Java classes.
   *
   * @param strategy
   *          the loader strategy to use when loading Metaschema modules
   * @return a new binding context
   * @since 2.0.0
   */
  @NonNull
  static IBindingContext newInstance(@NonNull IBindingContext.IModuleLoaderStrategy strategy) {
    return new DefaultBindingContext(strategy);
  }

  /**
   * Get the Metaschema module loader strategy used by this binding context to
   * load modules.
   *
   * @return the strategy instance
   * @since 2.0.0
   */
  @NonNull
  IModuleLoaderStrategy getModuleLoaderStrategy();

  /**
   * Get a loader that supports loading a Metaschema module from a specified
   * resource.
   * <p>
   * Modules loaded with this loader are automatically registered with this
   * binding context.
   * <p>
   * Use of this method requires that the binding context is initialized using a
   * {@link IModuleLoaderStrategy} that supports dynamic bound module loading.
   * This can be accomplished using the {@link SimpleModuleLoaderStrategy}
   * initialized using the {@link DefaultModuleBindingGenerator}. * @return the
   * loader
   *
   * @return the loader
   * @since 2.0.0
   */
  @NonNull
  IBindingModuleLoader newModuleLoader();

  /**
   * Loads a Metaschema module from the specified path.
   * <p>
   * This method automatically registers the module with this binding context.
   * <p>
   * Use of this method requires that the binding context is initialized using a
   * {@link IModuleLoaderStrategy} that supports dynamic bound module loading.
   * This can be accomplished using the {@link SimpleModuleLoaderStrategy}
   * initialized using the {@link DefaultModuleBindingGenerator}.
   *
   * @param path
   *          the path to load the module from
   * @return the loaded Metaschema module
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws IOException
   *           if an error occurred parsing the resource
   * @throws UnsupportedOperationException
   *           if this binding context is not configured to support dynamic bound
   *           module loading
   * @since 2.0.0
   */
  @NonNull
  default IBindingMetaschemaModule loadMetaschema(@NonNull Path path) throws MetaschemaException, IOException {
    return newModuleLoader().load(path);
  }

  /**
   * Loads a Metaschema module from the specified URL.
   * <p>
   * This method automatically registers the module with this binding context.
   * <p>
   * Use of this method requires that the binding context is initialized using a
   * {@link IModuleLoaderStrategy} that supports dynamic bound module loading.
   * This can be accomplished using the {@link SimpleModuleLoaderStrategy}
   * initialized using the {@link DefaultModuleBindingGenerator}.
   *
   * @param url
   *          the URL to load the module from
   * @return the loaded Metaschema module
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws IOException
   *           if an error occurred parsing the resource
   * @throws UnsupportedOperationException
   *           if this binding context is not configured to support dynamic bound
   *           module loading
   * @since 2.0.0
   */
  @NonNull
  default IBindingMetaschemaModule loadMetaschema(@NonNull URL url) throws MetaschemaException, IOException {
    return newModuleLoader().load(url);
  }

  /**
   * Get a loader that supports loading Metaschema module constraints from a
   * specified resource.
   * <p>
   * Metaschema module constraints loaded this need to be used with a new
   * {@link IBindingContext} instance to be applied to loaded modules. The new
   * binding context must initialized using the
   * {@link PostProcessingModuleLoaderStrategy} that is initialized with a
   * {@link ExternalConstraintsModulePostProcessor} instance.
   *
   * @return the loader
   * @since 2.0.0
   */
  @NonNull
  static IConstraintLoader getConstraintLoader() {
    return new BindingConstraintLoader(DefaultBindingContext.instance());
  }

  /**
   * Get a loader that supports loading Metaschema module constraints from a
   * specified resource.
   * <p>
   * Metaschema module constraints loaded this need to be used with a new
   * {@link IBindingContext} instance to be applied to loaded modules. The new
   * binding context must initialized using the
   * {@link PostProcessingModuleLoaderStrategy} that is initialized with a
   * {@link ExternalConstraintsModulePostProcessor} instance.
   *
   * @return the loader
   * @since 2.0.0
   */
  @NonNull
  default IConstraintLoader newConstraintLoader() {
    return new BindingConstraintLoader(this);
  }

  /**
   * Load a bound Metaschema module implemented by the provided class.
   * <p>
   * Also registers any associated bound classes.
   * <p>
   * Implementations are expected to return the same IModule instance for multiple
   * calls to this method with the same class argument.
   *
   * @param clazz
   *          the class implementing a bound Metaschema module
   * @return the loaded module
   */
  @NonNull
  IBoundModule registerModule(@NonNull Class<? extends IBoundModule> clazz);

  /**
   * Registers the provided Metaschema module with this binding context.
   * <p>
   * If the provided instance is not an instance of {@link IBoundModule}, then
   * annotated Java classes for this module will be generated, compiled, and
   * loaded based on the provided Module.
   *
   * @param module
   *          the Module module to generate classes for
   * @param compilePath
   *          the path to the directory to generate classes in
   * @return the registered module, which may be a different instance than what
   *         was provided if dynamic compilation was performed
   * @throws UnsupportedOperationException
   *           if this binding context is not configured to support dynamic bound
   *           module loading and the module instance is not a subclass of
   *           {@link IBoundModule}
   * @since 2.0.0
   */
  @NonNull
  default IBoundModule registerModule(@NonNull IModule module) {
    return getModuleLoaderStrategy().registerModule(module, this);
  }

  /**
   * Register a class binding for a given bound class.
   *
   * @param definition
   *          the bound class information to register
   * @return the old bound class information or {@code null} if no binding existed
   *         for the associated class
   */
  @Nullable
  IBoundDefinitionModelComplex registerClassBinding(@NonNull IBoundDefinitionModelComplex definition);

  /**
   * Get the {@link IBoundDefinitionModel} instance associated with the provided
   * Java class.
   * <p>
   * Typically the class will have a {@link MetaschemaAssembly} or
   * {@link MetaschemaField} annotation.
   *
   * @param clazz
   *          the class binding to load
   * @return the associated class binding instance or {@code null} if the class is
   *         not bound
   */
  @Nullable
  IBoundDefinitionModelComplex getBoundDefinitionForClass(@NonNull Class<? extends IBoundObject> clazz);

  /**
   * Determine the bound class for the provided XML {@link QName}.
   *
   * @param rootQName
   *          the root XML element's QName
   * @return the bound class or {@code null} if not recognized
   * @see IBindingContext#registerBindingMatcher(Class)
   */
  @Nullable
  Class<? extends IBoundObject> getBoundClassForRootXmlQName(@NonNull QName rootQName);

  /**
   * Determine the bound class for the provided JSON/YAML property/item name using
   * any registered matchers.
   *
   * @param rootName
   *          the JSON/YAML property/item name
   * @return the bound class or {@code null} if not recognized
   * @see IBindingContext#registerBindingMatcher(Class)
   */
  @Nullable
  Class<? extends IBoundObject> getBoundClassForRootJsonName(@NonNull String rootName);

  /**
   * Get's the {@link IDataTypeAdapter} associated with the specified Java class,
   * which is used to read and write XML, JSON, and YAML data to and from
   * instances of that class. Thus, this adapter supports a direct binding between
   * the Java class and structured data in one of the supported formats. Adapters
   * are used to support bindings for simple data objects (e.g., {@link String},
   * {@link BigInteger}, {@link ZonedDateTime}, etc).
   *
   * @param <TYPE>
   *          the class type of the adapter
   * @param clazz
   *          the Java {@link Class} for the bound type
   * @return the adapter instance or {@code null} if the provided class is not
   *         bound
   */
  @Nullable
  default <TYPE extends IDataTypeAdapter<?>> TYPE getJavaTypeAdapterInstance(@NonNull Class<TYPE> clazz) {
    return DataTypeService.getInstance().getJavaTypeAdapterByClass(clazz);
  }

  /**
   * Gets a data {@link ISerializer} which can be used to write Java instance data
   * for the provided class in the requested format.
   * <p>
   * The provided class must be a bound Java class with a
   * {@link MetaschemaAssembly} or {@link MetaschemaField} annotation for which a
   * {@link IBoundDefinitionModel} exists.
   *
   * @param <CLASS>
   *          the Java type this serializer can write data from
   * @param format
   *          the format to serialize into
   * @param clazz
   *          the Java data object to serialize
   * @return the serializer instance
   * @throws NullPointerException
   *           if any of the provided arguments, except the configuration, are
   *           {@code null}
   * @throws IllegalArgumentException
   *           if the provided class is not bound to a Module assembly or field
   * @throws UnsupportedOperationException
   *           if the requested format is not supported by the implementation
   * @see #getBoundDefinitionForClass(Class)
   */
  @NonNull
  <CLASS extends IBoundObject> ISerializer<CLASS> newSerializer(
      @NonNull Format format,
      @NonNull Class<CLASS> clazz);

  /**
   * Gets a data {@link IDeserializer} which can be used to read Java instance
   * data for the provided class from the requested format.
   * <p>
   * The provided class must be a bound Java class with a
   * {@link MetaschemaAssembly} or {@link MetaschemaField} annotation for which a
   * {@link IBoundDefinitionModel} exists.
   *
   * @param <CLASS>
   *          the Java type this deserializer can read data into
   * @param format
   *          the format to serialize into
   * @param clazz
   *          the Java data type to serialize
   * @return the deserializer instance
   * @throws NullPointerException
   *           if any of the provided arguments, except the configuration, are
   *           {@code null}
   * @throws IllegalArgumentException
   *           if the provided class is not bound to a Module assembly or field
   * @throws UnsupportedOperationException
   *           if the requested format is not supported by the implementation
   * @see #getBoundDefinitionForClass(Class)
   */
  @NonNull
  <CLASS extends IBoundObject> IDeserializer<CLASS> newDeserializer(
      @NonNull Format format,
      @NonNull Class<CLASS> clazz);

  /**
   * Get a new {@link IBoundLoader} instance to load bound content instances.
   *
   * @return the instance
   */
  @NonNull
  default IBoundLoader newBoundLoader() {
    return new DefaultBoundLoader(this);
  }

  /**
   * Create a deep copy of the provided bound object.
   *
   * @param <CLASS>
   *          the bound object type
   * @param other
   *          the object to copy
   * @param parentInstance
   *          the object's parent or {@code null}
   * @return a deep copy of the provided object
   * @throws BindingException
   *           if an error occurred copying content between java instances
   * @throws NullPointerException
   *           if the provided object is {@code null}
   * @throws IllegalArgumentException
   *           if the provided class is not bound to a Module assembly or field
   */
  @NonNull
  <CLASS extends IBoundObject> CLASS deepCopy(@NonNull CLASS other, IBoundObject parentInstance)
      throws BindingException;

  /**
   * Get a new single use constraint validator.
   *
   * @param handler
   *          the validation handler to use to process the validation results
   * @param config
   *          the validation configuration
   *
   * @return the validator
   */
  default IConstraintValidator newValidator(
      @NonNull IConstraintValidationHandler handler,
      @Nullable IConfiguration<ValidationFeature<?>> config) {
    IBoundLoader loader = newBoundLoader();
    loader.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);

    DynamicContext context = new DynamicContext();
    context.setDocumentLoader(loader);

    DefaultConstraintValidator retval = new DefaultConstraintValidator(handler);
    if (config != null) {
      retval.applyConfiguration(config);
    }
    return retval;
  }

  /**
   * Perform constraint validation on the provided bound object represented as an
   * {@link IDocumentNodeItem}.
   *
   * @param nodeItem
   *          the node item to validate
   * @param loader
   *          a module loader used to load and resolve referenced resources
   * @param config
   *          the validation configuration
   * @return the validation result
   * @throws IllegalArgumentException
   *           if the provided class is not bound to a Module assembly or field
   */
  default IValidationResult validate(
      @NonNull IDocumentNodeItem nodeItem,
      @NonNull IBoundLoader loader,
      @Nullable IConfiguration<ValidationFeature<?>> config) {
    IRootAssemblyNodeItem root = nodeItem.getRootAssemblyNodeItem();
    return validate(root, loader, config);
  }

  /**
   * Perform constraint validation on the provided bound object represented as an
   * {@link IDefinitionNodeItem}.
   *
   * @param nodeItem
   *          the node item to validate
   * @param loader
   *          a module loader used to load and resolve referenced resources
   * @param config
   *          the validation configuration
   * @return the validation result
   * @throws IllegalArgumentException
   *           if the provided class is not bound to a Module assembly or field
   */
  default IValidationResult validate(
      @NonNull IDefinitionNodeItem<?, ?> nodeItem,
      @NonNull IBoundLoader loader,
      @Nullable IConfiguration<ValidationFeature<?>> config) {

    FindingCollectingConstraintValidationHandler handler = new FindingCollectingConstraintValidationHandler();
    IConstraintValidator validator = newValidator(handler, config);

    DynamicContext dynamicContext = new DynamicContext(nodeItem.getStaticContext());
    dynamicContext.setDocumentLoader(loader);

    validator.validate(nodeItem, dynamicContext);
    validator.finalizeValidation(dynamicContext);
    return handler;
  }

  /**
   * Load and perform schema and constraint validation on the target. The
   * constraint validation will only be performed if the schema validation passes.
   *
   * @param target
   *          the target to validate
   * @param asFormat
   *          the schema format to use to validate the target
   * @param schemaProvider
   *          provides callbacks to get the appropriate schemas
   * @param config
   *          the validation configuration
   * @return the validation result
   * @throws IOException
   *           if an error occurred while reading the target
   */
  default IValidationResult validate(
      @NonNull URI target,
      @NonNull Format asFormat,
      @NonNull ISchemaValidationProvider schemaProvider,
      @Nullable IConfiguration<ValidationFeature<?>> config) throws IOException {

    IValidationResult retval = schemaProvider.validateWithSchema(target, asFormat, this);

    if (retval.isPassing()) {
      IValidationResult constraintValidationResult = validateWithConstraints(target, config);
      retval = AggregateValidationResult.aggregate(retval, constraintValidationResult);
    }
    return retval;
  }

  /**
   * Load and validate the provided {@code target} using the associated Module
   * module constraints.
   *
   * @param target
   *          the file to load and validate
   * @param config
   *          the validation configuration
   * @return the validation results
   * @throws IOException
   *           if an error occurred while parsing the target
   */
  default IValidationResult validateWithConstraints(
      @NonNull URI target,
      @Nullable IConfiguration<ValidationFeature<?>> config)
      throws IOException {
    IBoundLoader loader = newBoundLoader();
    loader.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_CONSTRAINTS);
    IDocumentNodeItem nodeItem = loader.loadAsNodeItem(target);

    return validate(nodeItem, loader, config);
  }

  /**
   * A behavioral class used by the binding context to load Metaschema modules.
   * <p>
   * A module will flow through the following process.
   * <ol>
   * <li><b>Loading:</b> The module is read from its source.</li>
   * <li><b>Post Processing:</b> The module is prepared for use.</li>
   * <li><b>Registration:</b> The module is registered for use.</li>
   * </ol>
   * <p>
   * A module will be loaded when either the module or one of its global
   * definitions is accessed the first time.
   */
  interface IModuleLoaderStrategy extends ModuleLoadingPostProcessor {
    /**
     * Load the bound Metaschema module represented by the provided class.
     * <p>
     * This is the primary entry point for loading an already bound module. This
     * method must ensure that the loaded module is post-processed and registered.
     * <p>
     * Implementations are allowed to return a cached instance if the module has
     * already been loaded by this method.
     *
     * @param clazz
     *          the Module class
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @return the module
     * @throws IllegalStateException
     *           if an error occurred while processing the associated module
     *           information
     * @since 2.0.0
     */
    @NonNull
    IBoundModule loadModule(
        @NonNull Class<? extends IBoundModule> clazz,
        @NonNull IBindingContext bindingContext);

    /**
     * Perform post-processing on the module.
     *
     * @param module
     *          the Metaschema module to post-process
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @since 2.0.0
     */
    @Override
    default void postProcessModule(
        @NonNull IModule module,
        @NonNull IBindingContext bindingContext) {
      // do nothing by default
    }

    /**
     * Registers the provided Metaschema module.
     * <p>
     * If this module has not been post-processed, this method is expected to drive
     * post-processing first.
     * <p>
     * If the provided instance is not an instance of {@link IBoundModule}, then
     * annotated Java classes for this module will be generated, compiled, and
     * loaded based on the provided Module.
     *
     * @param module
     *          the Module module to generate classes for
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @return the registered module, which may be a different instance than what
     *         was provided if dynamic compilation was performed
     * @throws UnsupportedOperationException
     *           if this binding context is not configured to support dynamic bound
     *           module loading and the module instance is not a subclass of
     *           {@link IBoundModule}
     * @since 2.0.0
     */
    @NonNull
    IBoundModule registerModule(
        @NonNull IModule module,
        @NonNull IBindingContext bindingContext);
    //
    // /**
    // * Register a matcher used to identify a bound class by the definition's root
    // * name.
    // *
    // * @param definition
    // * the definition to match for
    // * @return the matcher
    // */
    // @NonNull
    // IBindingMatcher registerBindingMatcher(@NonNull IBoundDefinitionModelAssembly
    // definition);

    /**
     * Get the matchers used to identify the bound class associated with the
     * definition's root name.
     *
     * @return the matchers
     */
    @NonNull
    Collection<IBindingMatcher> getBindingMatchers();

    /**
     * Get the {@link IBoundDefinitionModel} instance associated with the provided
     * Java class.
     * <p>
     * Typically the class will have a {@link MetaschemaAssembly} or
     * {@link MetaschemaField} annotation.
     *
     * @param clazz
     *          the class binding to load
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @return the associated class binding instance
     * @throws IllegalArgumentException
     *           if the class is not a bound definition with a
     *           {@link MetaschemaAssembly} or {@link MetaschemaField} annotation
     */
    @NonNull
    IBoundDefinitionModelComplex getBoundDefinitionForClass(
        @NonNull Class<? extends IBoundObject> clazz,
        @NonNull IBindingContext bindingContext);
  }

  /**
   * Enables building a {@link IBindingContext} using common configuration options
   * based on the builder pattern.
   *
   * @since 2.0.0
   */
  final class BindingContextBuilder {
    private Path compilePath;
    private final List<IModuleLoader.IModulePostProcessor> postProcessors = new LinkedList<>();
    private final List<IConstraintSet> constraintSets = new LinkedList<>();
    @NonNull
    private final Function<IBindingContext.IModuleLoaderStrategy, IBindingContext> initializer;

    private BindingContextBuilder() {
      this(DefaultBindingContext::new);
    }

    /**
     * Construct a new builder.
     *
     * @param initializer
     *          the callback to use to get a new binding context instance
     */
    public BindingContextBuilder(
        @NonNull Function<IBindingContext.IModuleLoaderStrategy, IBindingContext> initializer) {
      this.initializer = initializer;
    }

    /**
     * Enable dynamic code generation and compilation for Metaschema module-based
     * classes.
     *
     * @param path
     *          the path to use to generate and compile Metaschema module-based
     *          classes
     * @return this builder
     */
    @NonNull
    public BindingContextBuilder compilePath(@NonNull Path path) {
      compilePath = path;
      return this;
    }

    /**
     * Configure a Metaschema module post processor.
     *
     * @param processor
     *          the post processor to configure
     * @return this builder
     */
    @NonNull
    public BindingContextBuilder postProcessor(@NonNull IModuleLoader.IModulePostProcessor processor) {
      postProcessors.add(processor);
      return this;
    }

    /**
     * Configure a set of constraints targeting Metaschema modules.
     *
     * @param set
     *          the constraint set to configure
     * @return this builder
     */
    @NonNull
    public BindingContextBuilder constraintSet(@NonNull IConstraintSet set) {
      constraintSets.add(set);
      return this;
    }

    /**
     * Configure a collection of constraint sets targeting Metaschema modules.
     *
     * @param set
     *          the constraint sets to configure
     * @return this builder
     */
    @NonNull
    public BindingContextBuilder constraintSet(@NonNull Collection<IConstraintSet> set) {
      constraintSets.addAll(set);
      return this;
    }

    /**
     * Build a {@link IBindingContext} using the configuration options provided to
     * the builder.
     *
     * @return a new, configured binding context
     */
    @NonNull
    public IBindingContext build() {
      // get loader strategy based on if code generation is configured
      IBindingContext.IModuleLoaderStrategy strategy = compilePath == null
          ? new SimpleModuleLoaderStrategy()
          : new SimpleModuleLoaderStrategy(new DefaultModuleBindingGenerator(compilePath));

      // determine if any post processors are configured or need to be
      List<IModuleLoader.IModulePostProcessor> processors = new LinkedList<>(postProcessors);
      if (!constraintSets.isEmpty()) {
        processors.add(new ExternalConstraintsModulePostProcessor(constraintSets));
      }

      if (!processors.isEmpty()) {
        // post processors are configured, configure the loader strategy to handle them
        strategy = new PostProcessingModuleLoaderStrategy(
            CollectionUtil.unmodifiableList(processors),
            strategy);
      }

      return ObjectUtils.notNull(initializer.apply(strategy));
    }
  }

  /**
   * Provides schema validation capabilities.
   */
  interface ISchemaValidationProvider {

    /**
     * Validate the target resource.
     *
     * @param target
     *          the resource to validate
     * @param asFormat
     *          the format to validate the content as
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @return the validation result
     * @throws FileNotFoundException
     *           if the resource was not found
     * @throws IOException
     *           if an error occurred while reading the resource
     */
    @NonNull
    default IValidationResult validateWithSchema(
        @NonNull URI target,
        @NonNull Format asFormat,
        @NonNull IBindingContext bindingContext)
        throws FileNotFoundException, IOException {
      URL targetResource = ObjectUtils.notNull(target.toURL());

      IValidationResult retval;
      switch (asFormat) {
      case JSON: {
        JSONObject json;
        try (@SuppressWarnings("resource") InputStream is
            = new BufferedInputStream(ObjectUtils.notNull(targetResource.openStream()))) {
          json = new JSONObject(new JSONTokener(is));
        }
        retval = getJsonSchema(json, bindingContext).validate(json, target);
        break;
      }
      case XML:
        try {
          retval = getXmlSchemas(targetResource, bindingContext).validate(target);
        } catch (SAXException ex) {
          throw new IOException(ex);
        }
        break;
      case YAML: {
        JSONObject json = YamlOperations.yamlToJson(YamlOperations.parseYaml(target));
        assert json != null;
        retval = getJsonSchema(json, bindingContext).validate(json, ObjectUtils.notNull(target));
        break;
      }
      default:
        throw new UnsupportedOperationException("Unsupported format: " + asFormat.name());
      }
      return retval;
    }

    /**
     * Get a JSON schema to use for content validation.
     *
     * @param json
     *          the JSON content to validate
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @return the JSON schema validator
     * @throws IOException
     *           if an error occurred while loading the schema
     * @since 2.0.0
     */
    @NonNull
    JsonSchemaContentValidator getJsonSchema(@NonNull JSONObject json, @NonNull IBindingContext bindingContext)
        throws IOException;

    /**
     * Get a XML schema to use for content validation.
     *
     * @param targetResource
     *          the URL for the XML content to validate
     * @param bindingContext
     *          the Metaschema binding context used to load bound resources
     * @return the XML schema validator
     * @throws IOException
     *           if an error occurred while loading the schema
     * @throws SAXException
     *           if an error occurred while parsing the schema
     * @since 2.0.0
     */
    @NonNull
    XmlSchemaContentValidator getXmlSchemas(@NonNull URL targetResource, @NonNull IBindingContext bindingContext)
        throws IOException, SAXException;
  }

  /**
   * Implementations of this interface provide a means by which a bound class can
   * be found that corresponds to an XML element, JSON property, or YAML item
   * name.
   */
  interface IBindingMatcher {
    /**
     * Construct a new binding matcher for the provided assembly definition.
     *
     * @param assembly
     *          the assembly definition that matcher is for
     * @return the matcher
     */
    @SuppressWarnings("PMD.ShortMethodName")
    @NonNull
    static IBindingMatcher of(IBoundDefinitionModelAssembly assembly) {
      if (!assembly.isRoot()) {
        throw new IllegalArgumentException(
            String.format("The provided class '%s' is not a root assembly.", assembly.getBoundClass().getName()));
      }
      return new RootAssemblyBindingMatcher(assembly);
    }

    /**
     * Determine the bound class for the provided XML {@link QName}.
     *
     * @param rootQName
     *          the root XML element's QName
     * @return the bound class for the XML qualified name or {@code null} if not
     *         recognized
     */
    Class<? extends IBoundObject> getBoundClassForXmlQName(QName rootQName);

    /**
     * Determine the bound class for the provided JSON/YAML property/item name.
     *
     * @param rootName
     *          the JSON/YAML property/item name
     * @return the bound class for the JSON property name or {@code null} if not
     *         recognized
     */
    Class<? extends IBoundObject> getBoundClassForJsonName(String rootName);
  }
}
