/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.constraint.DefaultConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.FindingCollectingConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidationHandler;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintValidator;
import gov.nist.secauto.metaschema.core.model.constraint.ValidationFeature;
import gov.nist.secauto.metaschema.core.model.validation.AggregateValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.IValidationResult;
import gov.nist.secauto.metaschema.core.model.validation.JsonSchemaContentValidator;
import gov.nist.secauto.metaschema.core.model.validation.XmlSchemaContentValidator;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
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
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides information supporting a binding between a set of Module models and
 * corresponding Java classes.
 */
public interface IBindingContext {

  /**
   * Get the singleton {@link IBindingContext} instance, which can be used to load
   * information that binds a model to a set of Java classes.
   *
   * @return a new binding context
   */
  @NonNull
  static IBindingContext instance() {
    return DefaultBindingContext.instance();
  }

  /**
   * Register a matcher used to identify a bound class by the definition's root
   * name.
   *
   * @param definition
   *          the definition to match for
   * @return the matcher
   */
  @NonNull
  IBindingMatcher registerBindingMatcher(@NonNull IBoundDefinitionModelAssembly definition);

  /**
   * Register a matcher used to identify a bound class by the definition's root
   * name.
   *
   * @param clazz
   *          the definition class to match for, which must represent a root
   *          assembly definition
   * @return the matcher
   */
  @NonNull
  IBindingMatcher registerBindingMatcher(@NonNull Class<? extends IBoundObject> clazz);

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
  <TYPE extends IDataTypeAdapter<?>> TYPE getJavaTypeAdapterInstance(@NonNull Class<TYPE> clazz);

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
   * Generate, compile, and load a set of generated Module annotated Java classes
   * based on the provided Module {@code module}.
   *
   * @param module
   *          the Module module to generate classes for
   * @param compilePath
   *          the path to the directory to generate classes in
   * @return this instance
   * @throws IOException
   *           if an error occurred while generating or loading the classes
   */
  @NonNull
  IBindingContext registerModule(
      @NonNull IModule module,
      @NonNull Path compilePath) throws IOException;

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
   * Get a new {@link IBoundLoader} instance.
   *
   * @return the instance
   */
  @NonNull
  IBoundLoader newBoundLoader();

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

    IValidationResult retval = schemaProvider.validateWithSchema(target, asFormat);

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

  interface IModuleLoaderStrategy {
    /**
     * Load the bound Metaschema module represented by the provided class.
     * <p>
     * Implementations are allowed to return a cached instance if the module has
     * already been loaded.
     *
     * @param clazz
     *          the Module class
     * @return the module
     * @throws IllegalStateException
     *           if an error occurred while processing the associated module
     *           information
     */
    @NonNull
    IBoundModule loadModule(@NonNull Class<? extends IBoundModule> clazz);

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
  }

  interface ISchemaValidationProvider {

    @NonNull
    default IValidationResult validateWithSchema(@NonNull URI target, @NonNull Format asFormat)
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
        retval = new JsonSchemaContentValidator(getJsonSchema(json)).validate(json, target);
        break;
      }
      case XML:
        try {
          List<Source> schemaSources = getXmlSchemas(targetResource);
          retval = new XmlSchemaContentValidator(schemaSources).validate(target);
        } catch (SAXException ex) {
          throw new IOException(ex);
        }
        break;
      case YAML: {
        JSONObject json = YamlOperations.yamlToJson(YamlOperations.parseYaml(target));
        assert json != null;
        retval = new JsonSchemaContentValidator(getJsonSchema(json)).validate(json, ObjectUtils.notNull(target));
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
     *
     * @return the JSON schema
     * @throws IOException
     *           if an error occurred while loading the schema
     */
    @NonNull
    JSONObject getJsonSchema(@NonNull JSONObject json) throws IOException;

    /**
     * Get a XML schema to use for content validation.
     *
     * @param targetResource
     *          the URL for the XML content to validate
     *
     * @return the XML schema sources
     * @throws IOException
     *           if an error occurred while loading the schema
     */
    @NonNull
    List<Source> getXmlSchemas(@NonNull URL targetResource) throws IOException;
  }

  /**
   * Implementations of this interface provide a means by which a bound class can
   * be found that corresponds to an XML element, JSON property, or YAML item
   * name.
   */
  interface IBindingMatcher {
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
