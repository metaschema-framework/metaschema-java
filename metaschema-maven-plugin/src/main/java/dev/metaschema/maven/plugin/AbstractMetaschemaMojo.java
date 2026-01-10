/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.maven.plugin;

import dev.metaschema.core.model.IConstraintLoader;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.IModuleLoader;
import dev.metaschema.core.model.IResourceLocation;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.constraint.ConstraintValidationException;
import dev.metaschema.core.model.constraint.ConstraintValidationFinding;
import dev.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.core.model.validation.AbstractValidationResultProcessor;
import dev.metaschema.core.model.validation.IValidationFinding;
import dev.metaschema.core.model.validation.IValidationResult;
import dev.metaschema.core.model.validation.JsonSchemaContentValidator.JsonValidationFinding;
import dev.metaschema.core.model.validation.XmlSchemaContentValidator.XmlValidationFinding;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.DefaultBindingContext;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.PostProcessingModuleLoaderStrategy;
import dev.metaschema.databind.SimpleModuleLoaderStrategy;
import dev.metaschema.databind.codegen.IGeneratedClass;
import dev.metaschema.databind.codegen.IGeneratedModuleClass;
import dev.metaschema.databind.codegen.IModuleBindingGenerator;
import dev.metaschema.databind.codegen.IProduction;
import dev.metaschema.databind.codegen.JavaCompilerSupport;
import dev.metaschema.databind.codegen.JavaGenerator;
import dev.metaschema.databind.codegen.ModuleCompilerHelper;
import dev.metaschema.databind.codegen.config.DefaultBindingConfiguration;
import dev.metaschema.databind.codegen.config.IBindingConfiguration;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.metaschema.BindingModuleLoader;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;
import dev.metaschema.databind.model.metaschema.binding.MetaschemaModelModule;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.tools.DiagnosticCollector;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Abstract base class for Metaschema Maven plugin goals.
 * <p>
 * This class provides common functionality for loading Metaschema modules,
 * managing constraint sets, handling incremental builds, and performing
 * code/schema generation. Concrete implementations should override the
 * {@link #generate(Set)} method to provide specific generation behavior.
 * <p>
 * The plugin supports:
 * <ul>
 * <li>Loading multiple Metaschema modules from a configured directory</li>
 * <li>Applying external constraint sets to modules</li>
 * <li>Incremental build support through stale file tracking</li>
 * <li>Configurable file encoding for generated sources</li>
 * </ul>
 *
 * @see GenerateSourcesMojo
 * @see GenerateSchemaMojo
 */
public abstract class AbstractMetaschemaMojo
    extends AbstractMojo {
  private static final String[] DEFAULT_INCLUDES = { "**/*.xml" };

  /**
   * The Maven project context.
   *
   * @required
   * @readonly
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject mavenProject;

  /**
   * This will be injected if this plugin is executed as part of the standard
   * Maven lifecycle. If the mojo is directly invoked, this parameter will not be
   * injected.
   */
  @Parameter(defaultValue = "${mojoExecution}", readonly = true)
  private MojoExecution mojoExecution;

  @Inject
  private BuildContext buildContext;

  @Parameter(defaultValue = "${plugin.artifacts}", readonly = true, required = true)
  private List<Artifact> pluginArtifacts;

  /**
   * <p>
   * The directory where the staleFile is found. The staleFile is used to
   * determine if re-generation of generated Java classes is needed, by recording
   * when the last build occurred.
   * </p>
   * <p>
   * This directory is expected to be located within the
   * <code>${project.build.directory}</code>, to ensure that code (re)generation
   * occurs after cleaning the project.
   * </p>
   */
  @Parameter(defaultValue = "${project.build.directory}/metaschema", readonly = true, required = true)
  protected File staleFileDirectory;

  /**
   * <p>
   * Defines the encoding used for generating Java Source files.
   * </p>
   * <p>
   * The algorithm for finding the encoding to use is as follows (where the first
   * non-null value found is used for encoding):
   * <ol>
   * <li>If the configuration property is explicitly given within the plugin's
   * configuration, use that value.
   * <li>If the Maven property <code>project.build.sourceEncoding</code> is
   * defined, use its value.
   * <li>Otherwise use the value from the system property
   * <code>file.encoding</code>.
   * </ol>
   * </p>
   *
   * @see #getEncoding()
   * @since 2.0
   */
  @Parameter(defaultValue = "${project.build.sourceEncoding}")
  private String encoding;

  /**
   * Location to generate Java source files in.
   */
  @Parameter(
      defaultValue = "${project.build.directory}/generated-sources/metaschema",
      required = true,
      property = "outputDirectory")
  private File outputDirectory;

  /**
   * The directory to read source metaschema from.
   */
  @Parameter(defaultValue = "${basedir}/src/main/metaschema")
  private File metaschemaDir;

  /**
   * A list of <code>files</code> containing Metaschema module constraints files.
   */
  @Parameter(property = "constraints")
  private File[] constraints;

  /**
   * A set of inclusion patterns used to select which Metaschema modules are to be
   * processed. By default, all files are processed.
   */
  @Parameter
  protected String[] includes;

  /**
   * A set of exclusion patterns used to prevent certain files from being
   * processed. By default, this set is empty such that no files are excluded.
   */
  @Parameter
  protected String[] excludes;

  /**
   * Indicate if the execution should be skipped.
   */
  @Parameter(property = "metaschema.skip", defaultValue = "false")
  private boolean skip;

  /**
   * The BuildContext is used to identify which files or directories were modified
   * since last build. This is used to determine if Module-based generation must
   * be performed again.
   *
   * @return the active Plexus BuildContext.
   */
  protected final BuildContext getBuildContext() {
    return buildContext;
  }

  /**
   * Retrieve the Maven project context.
   *
   * @return The active MavenProject.
   */
  protected final MavenProject getMavenProject() {
    return mavenProject;
  }

  /**
   * Retrieve the plugin artifacts available to this mojo.
   *
   * @return the list of plugin artifacts
   */
  protected final List<Artifact> getPluginArtifacts() {
    return pluginArtifacts;
  }

  /**
   * Retrieve the mojo execution context.
   *
   * @return The active MojoExecution.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "this is a data holder")
  public MojoExecution getMojoExecution() {
    return mojoExecution;
  }

  /**
   * Retrieve the directory where generated classes will be stored.
   *
   * @return the directory
   */
  protected File getOutputDirectory() {
    return outputDirectory;
  }

  /**
   * Set the directory where generated classes will be stored.
   *
   * @param outputDirectory
   *          the directory to use
   */
  protected void setOutputDirectory(File outputDirectory) {
    Objects.requireNonNull(outputDirectory, "outputDirectory");
    this.outputDirectory = outputDirectory;
  }

  /**
   * Gets the file encoding to use for generated classes.
   * <p>
   * The algorithm for finding the encoding to use is as follows (where the first
   * non-null value found is used for encoding):
   * </p>
   * <ol>
   * <li>If the configuration property is explicitly given within the plugin's
   * configuration, use that value.
   * <li>If the Maven property <code>project.build.sourceEncoding</code> is
   * defined, use its value.
   * <li>Otherwise use the value from the system property
   * <code>file.encoding</code>.
   * </ol>
   *
   * @return The encoding to be used by this AbstractJaxbMojo and its tools.
   */
  protected final String getEncoding() {
    String encoding;
    if (this.encoding != null) {
      // first try to use the provided encoding
      encoding = this.encoding;
      if (getLog().isDebugEnabled()) {
        getLog().debug(String.format("Using configured encoding [%s].", encoding));
      }
    } else {
      encoding = Charset.defaultCharset().displayName();
      if (getLog().isWarnEnabled()) {
        getLog().warn(String.format("Using system encoding [%s]. This build is platform dependent!", encoding));
      }
    }
    return encoding;
  }

  /**
   * Retrieve a stream of Module file sources.
   *
   * @return the stream
   */
  protected Stream<File> getModuleSources() {
    DirectoryScanner ds = new DirectoryScanner();
    ds.setBasedir(metaschemaDir);
    ds.setIncludes(includes != null && includes.length > 0 ? includes : DEFAULT_INCLUDES);
    ds.setExcludes(excludes != null && excludes.length > 0 ? excludes : null);
    ds.addDefaultExcludes();
    ds.setCaseSensitive(true);
    ds.setFollowSymlinks(false);
    ds.scan();
    return Stream.of(ds.getIncludedFiles()).map(filename -> new File(metaschemaDir, filename)).distinct();
  }

  /**
   * Create a new binding context configured with the specified module post
   * processor.
   *
   * @param modulePostProcessor
   *          the post processor to apply to loaded modules
   * @return the configured binding context
   * @throws IOException
   *           if an I/O error occurs during context creation
   * @throws MetaschemaException
   *           if an error occurs while processing the Metaschema module
   */
  @NonNull
  protected IBindingContext newBindingContext(
      @NonNull IModuleLoader.IModulePostProcessor modulePostProcessor) throws IOException, MetaschemaException {
    // generate Java sources based on provided metaschema sources
    return new DefaultBindingContext(
        new PostProcessingModuleLoaderStrategy(
            // ensure that the external constraints do not apply to the built in module
            CollectionUtil.singletonList(modulePostProcessor),
            new SimpleModuleLoaderStrategy(
                // this is used instead of the default generator to ensure that plugin classpath
                // entries are used for compilation
                new ModuleBindingGenerator(
                    ObjectUtils.notNull(Files.createDirectories(Paths.get("target/metaschema-codegen-modules"))),
                    new DefaultBindingConfiguration()))));
  }

  /**
   * Get the configured collection of constraints.
   *
   * @return the loaded constraints
   * @throws MojoExecutionException
   *           if an error occurred while loading the constraints
   */
  @NonNull
  protected List<IConstraintSet> getConstraints() throws MojoExecutionException {
    IConstraintLoader loader = IBindingContext.getConstraintLoader();
    List<IConstraintSet> constraintSets = new ArrayList<>(constraints.length);
    for (File constraint : this.constraints) {
      try {
        constraintSets.addAll(loader.load(ObjectUtils.notNull(constraint)));
      } catch (IOException | MetaschemaException ex) {
        throw new MojoExecutionException("Loading of external constraints failed", ex);
      }
    }
    return CollectionUtil.unmodifiableList(constraintSets);
  }

  /**
   * Determine if the execution of this mojo should be skipped.
   *
   * @return {@code true} if the mojo execution should be skipped, or
   *         {@code false} otherwise
   */
  protected boolean shouldExecutionBeSkipped() {
    return skip;
  }

  /**
   * Get the name of the file that is used to detect staleness.
   *
   * @return the name
   */
  protected abstract String getStaleFileName();

  /**
   * Gets the staleFile for this execution.
   *
   * @return the staleFile
   */
  protected final File getStaleFile() {
    StringBuilder builder = new StringBuilder();
    if (getMojoExecution() != null) {
      builder.append(getMojoExecution().getExecutionId()).append('-');
    }
    builder.append(getStaleFileName());
    return new File(staleFileDirectory, builder.toString());
  }

  /**
   * Determine if code generation is required. This is done by comparing the last
   * modified time of each Module source file against the stale file managed by
   * this plugin.
   *
   * @return {@code true} if the code generation is needed, or {@code false}
   *         otherwise
   */
  protected boolean isGenerationRequired() {
    final File staleFile = getStaleFile();
    boolean generate = !staleFile.exists();
    if (generate) {
      if (getLog().isInfoEnabled()) {
        getLog().info(String.format("Stale file '%s' doesn't exist! Generating source files.", staleFile.getPath()));
      }
      generate = true;
    } else {
      generate = false;
      // check for staleness
      long staleLastModified = staleFile.lastModified();

      BuildContext buildContext = getBuildContext();
      URI metaschemaDirRelative = getMavenProject().getBasedir().toURI().relativize(metaschemaDir.toURI());

      if (buildContext.isIncremental() && buildContext.hasDelta(metaschemaDirRelative.toString())) {
        if (getLog().isInfoEnabled()) {
          getLog().info("metaschemaDirRelative: " + metaschemaDirRelative.toString());
        }
        generate = true;
      }

      if (!generate) {
        for (File sourceFile : getModuleSources().collect(Collectors.toList())) {
          if (getLog().isInfoEnabled()) {
            getLog().info("Source file: " + sourceFile.getPath());
          }
          if (sourceFile.lastModified() > staleLastModified) {
            generate = true;
          }
        }
      }
    }
    return generate;
  }

  /**
   * Retrieve the combined classpath containing both project dependencies and
   * plugin artifacts.
   *
   * @return a set of classpath elements as absolute paths
   * @throws DependencyResolutionRequiredException
   *           if the project dependencies cannot be resolved
   */
  protected Set<String> getClassPath() throws DependencyResolutionRequiredException {
    Set<String> pathElements;
    try {
      pathElements = new LinkedHashSet<>(getMavenProject().getCompileClasspathElements());
    } catch (DependencyResolutionRequiredException ex) {
      getLog().warn("exception calling getCompileClasspathElements", ex);
      throw ex;
    }

    if (pluginArtifacts != null) {
      for (Artifact a : getPluginArtifacts()) {
        if (a.getFile() != null) {
          pathElements.add(a.getFile().getAbsolutePath());
        }
      }
    }
    return pathElements;
  }

  /**
   * Load and validate the Metaschema modules to generate sources or schemas for.
   *
   * @param bindingContext
   *          the binding context to use for module loading and validation
   * @param modulePostProcessor
   *          the post processor to apply to each loaded module
   * @return the set of loaded and validated modules
   * @throws MetaschemaException
   *           if an error occurs while processing the Metaschema module
   * @throws IOException
   *           if an I/O error occurs while loading a module
   * @throws ConstraintValidationException
   *           if constraint validation fails on a loaded module
   */
  @NonNull
  protected Set<IModule> getModulesToGenerateFor(
      @NonNull IBindingContext bindingContext,
      @NonNull IModuleLoader.IModulePostProcessor modulePostProcessor)
      throws MetaschemaException, IOException, ConstraintValidationException {

    // Don't use the normal loader, since it attempts to register and compile the
    // module.
    // We only care about the module content for generating sources and schemas
    IBindingModuleLoader loader = new BindingModuleLoader(bindingContext, (module, ctx) -> {
      modulePostProcessor.processModule(module);
    });
    loader.allowEntityResolution();

    LoggingValidationHandler validationHandler = new LoggingValidationHandler();

    Set<IModule> modules = new HashSet<>();
    for (File source : getModuleSources().collect(Collectors.toList())) {
      assert source != null;
      if (getLog().isInfoEnabled()) {
        getLog().info("Using metaschema source: " + source.getPath());
      }
      IBindingMetaschemaModule module = loader.load(source);

      IValidationResult result = bindingContext.validate(
          module.getSourceNodeItem(),
          loader.getBindingContext().newBoundLoader(),
          null);

      validationHandler.handleResults(result);

      modules.add(module);
    }
    return modules;
  }

  /**
   * Create or update the stale file to record the current build time.
   *
   * @param staleFile
   *          the stale file to create or update
   * @throws MojoExecutionException
   *           if the stale file cannot be created
   */
  protected void createStaleFile(@NonNull File staleFile) throws MojoExecutionException {
    // create the stale file
    if (!staleFileDirectory.exists() && !staleFileDirectory.mkdirs()) {
      throw new MojoExecutionException("Unable to create output directory: " + staleFileDirectory);
    }
    try (OutputStream os
        = Files.newOutputStream(staleFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING)) {
      os.close();
      if (getLog().isInfoEnabled()) {
        getLog().info("Created stale file: " + staleFile);
      }
    } catch (IOException ex) {
      throw new MojoExecutionException("Failed to write stale file: " + staleFile.getPath(), ex);
    }
  }

  @Override
  public void execute() throws MojoExecutionException {
    File staleFile = getStaleFile();
    try {
      staleFile = ObjectUtils.notNull(staleFile.getCanonicalFile());
    } catch (IOException ex) {
      if (getLog().isWarnEnabled()) {
        getLog().warn("Unable to resolve canonical path to stale file. Treating it as not existing.", ex);
      }
    }

    boolean generate;
    if (shouldExecutionBeSkipped()) {
      if (getLog().isDebugEnabled()) {
        getLog().debug(String.format("Generation is configured to be skipped. Skipping."));
      }
      generate = false;
    } else if (staleFile.exists()) {
      generate = isGenerationRequired();
    } else {
      if (getLog().isInfoEnabled()) {
        getLog().info(String.format("Stale file '%s' doesn't exist! Generation is required.", staleFile.getPath()));
      }
      generate = true;
    }

    if (generate) {
      List<IConstraintSet> constraints = getConstraints();
      IModuleLoader.IModulePostProcessor modulePostProcessor
          = new LimitedExternalConstraintsModulePostProcessor(constraints);

      List<File> generatedFiles;
      try {
        generatedFiles = performGeneration(modulePostProcessor);
      } finally {
        // ensure the stale file is created to ensure that regeneration is only
        // performed when a
        // change is made
        createStaleFile(staleFile);
      }

      if (getLog().isInfoEnabled()) {
        getLog().info(String.format("Generated %d files.", generatedFiles.size()));
      }

      // for m2e
      for (File file : generatedFiles) {
        getBuildContext().refresh(file);
      }
    }
  }

  @NonNull
  private List<File> performGeneration(
      @NonNull IModuleLoader.IModulePostProcessor modulePostProcessor) throws MojoExecutionException {
    File outputDir = getOutputDirectory();
    if (getLog().isDebugEnabled()) {
      getLog().debug(String.format("Using outputDirectory: %s", outputDir.getPath()));
    }

    if (!outputDir.exists() && !outputDir.mkdirs()) {
      throw new MojoExecutionException("Unable to create output directory: " + outputDir);
    }

    IBindingContext bindingContext;
    try {
      bindingContext = newBindingContext(modulePostProcessor);
    } catch (MetaschemaException | IOException ex) {
      throw new MojoExecutionException("Failed to create the binding context", ex);
    }

    // generate Java sources based on provided metaschema sources
    Set<IModule> modules;
    try {
      modules = getModulesToGenerateFor(bindingContext, modulePostProcessor);
    } catch (Exception ex) {
      throw new MojoExecutionException("Loading of metaschema modules failed", ex);
    }

    return generate(modules);
  }

  /**
   * Perform the generation operation.
   *
   * @param modules
   *          the modules to generate resources/sources for
   *
   * @return the files generated during the operation
   * @throws MojoExecutionException
   *           if an error occurred while performing the generation operation
   */
  @NonNull
  protected abstract List<File> generate(@NonNull Set<IModule> modules) throws MojoExecutionException;

  /**
   * A validation result handler that logs validation findings using the Maven
   * plugin logger.
   * <p>
   * Findings are logged at different levels based on their severity:
   * <ul>
   * <li>CRITICAL and ERROR - logged at error level</li>
   * <li>WARNING - logged at warn level</li>
   * <li>INFORMATIONAL - logged at info level</li>
   * <li>All other severities - logged at debug level</li>
   * </ul>
   */
  protected final class LoggingValidationHandler
      extends AbstractValidationResultProcessor {

    private <T extends IValidationFinding> void handleFinding(
        @NonNull T finding,
        @NonNull Function<T, CharSequence> formatter) {

      Log log = getLog();

      switch (finding.getSeverity()) {
      case CRITICAL:
      case ERROR:
        if (log.isErrorEnabled()) {
          log.error(formatter.apply(finding), finding.getCause());
        }
        break;
      case WARNING:
        if (log.isWarnEnabled()) {
          getLog().warn(formatter.apply(finding), finding.getCause());
        }
        break;
      case INFORMATIONAL:
        if (log.isInfoEnabled()) {
          getLog().info(formatter.apply(finding), finding.getCause());
        }
        break;
      default:
        if (log.isDebugEnabled()) {
          getLog().debug(formatter.apply(finding), finding.getCause());
        }
        break;
      }
    }

    @Override
    protected void handleJsonValidationFinding(JsonValidationFinding finding) {
      handleFinding(finding, this::getMessage);
    }

    @Override
    protected void handleXmlValidationFinding(XmlValidationFinding finding) {
      handleFinding(finding, this::getMessage);
    }

    @Override
    protected void handleConstraintValidationFinding(ConstraintValidationFinding finding) {
      handleFinding(finding, this::getMessage);
    }

    @NonNull
    private CharSequence getMessage(JsonValidationFinding finding) {
      StringBuilder builder = new StringBuilder();
      builder.append('[')
          .append(finding.getCause().getPointerToViolation())
          .append("] ")
          .append(finding.getMessage());

      URI documentUri = finding.getDocumentUri();
      if (documentUri != null) {
        builder.append(" [")
            .append(documentUri.toString())
            .append(']');
      }
      return builder;
    }

    @NonNull
    private CharSequence getMessage(XmlValidationFinding finding) {
      StringBuilder builder = new StringBuilder();

      builder.append(finding.getMessage())
          .append(" [");

      URI documentUri = finding.getDocumentUri();
      if (documentUri != null) {
        builder.append(documentUri.toString());
      }

      SAXParseException ex = finding.getCause();
      builder.append(finding.getMessage())
          .append('{')
          .append(ex.getLineNumber())
          .append(',')
          .append(ex.getColumnNumber())
          .append("}]");
      return builder;
    }

    @NonNull
    private CharSequence getMessage(@NonNull ConstraintValidationFinding finding) {
      StringBuilder builder = new StringBuilder();
      builder.append('[')
          .append(finding.getTarget().getMetapath())
          .append(']');

      String id = finding.getIdentifier();
      if (id != null) {
        builder.append(' ')
            .append(id);
      }

      builder.append(' ')
          .append(finding.getMessage());

      URI documentUri = finding.getTarget().getBaseUri();
      IResourceLocation location = finding.getLocation();
      if (documentUri != null || location != null) {
        builder.append(" [");
      }

      if (documentUri != null) {
        builder.append(documentUri.toString());
      }

      if (location != null) {
        builder.append('{')
            .append(location.getLine())
            .append(',')
            .append(location.getColumn())
            .append('}');
      }
      if (documentUri != null || location != null) {
        builder.append(']');
      }
      return builder;
    }
  }

  /**
   * A module binding generator that generates and compiles Java classes for
   * Metaschema modules during plugin execution.
   * <p>
   * This generator uses the plugin's classpath for compilation, ensuring that all
   * necessary dependencies are available during the code generation process.
   */
  public class ModuleBindingGenerator implements IModuleBindingGenerator {
    @NonNull
    private final Path compilePath;
    @NonNull
    private final ClassLoader classLoader;
    @NonNull
    private final IBindingConfiguration bindingConfiguration;

    /**
     * Construct a new module binding generator.
     *
     * @param compilePath
     *          the directory path where generated classes will be compiled to
     * @param bindingConfiguration
     *          the binding configuration to use for code generation
     */
    public ModuleBindingGenerator(
        @NonNull Path compilePath,
        @NonNull IBindingConfiguration bindingConfiguration) {
      this.compilePath = compilePath;
      this.classLoader = ModuleCompilerHelper.newClassLoader(
          compilePath,
          ObjectUtils.notNull(Thread.currentThread().getContextClassLoader()));
      this.bindingConfiguration = bindingConfiguration;
    }

    /**
     * Generate Java source files for the specified module.
     *
     * @param module
     *          the Metaschema module to generate classes for
     * @return the production containing the generated class information
     * @throws MetaschemaException
     *           if an error occurs during class generation
     */
    @NonNull
    public IProduction generateClasses(@NonNull IModule module) throws MetaschemaException {
      IProduction production;
      try {
        production = JavaGenerator.generate(module, compilePath, bindingConfiguration);
      } catch (IOException ex) {
        throw new MetaschemaException(
            String.format("Unable to generate and compile classes for module '%s'.", module.getLocation()),
            ex);
      }
      return production;
    }

    private void compileClasses(@NonNull IProduction production, @NonNull Path classDir)
        throws IOException, DependencyResolutionRequiredException {
      List<IGeneratedClass> classesToCompile = production.getGeneratedClasses().collect(Collectors.toList());

      List<Path> classes = ObjectUtils.notNull(classesToCompile.stream()
          .map(IGeneratedClass::getClassFile)
          .collect(Collectors.toUnmodifiableList()));

      JavaCompilerSupport compiler = new JavaCompilerSupport(classDir);
      compiler.setLogger(new JavaCompilerSupport.Logger() {

        @Override
        public boolean isDebugEnabled() {
          return getLog().isDebugEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
          return getLog().isInfoEnabled();
        }

        @Override
        public void debug(String msg) {
          getLog().debug(msg);
        }

        @Override
        public void info(String msg) {
          getLog().info(msg);
        }
      });

      getClassPath().forEach(compiler::addToClassPath);

      JavaCompilerSupport.CompilationResult result = compiler.compile(classes);

      if (!result.isSuccessful()) {
        DiagnosticCollector<?> diagnostics = new DiagnosticCollector<>();
        if (getLog().isErrorEnabled()) {
          getLog().error("diagnostics: " + diagnostics.getDiagnostics().toString());
        }
        throw new IllegalStateException(String.format("failed to compile classes: %s",
            classesToCompile.stream()
                .map(clazz -> clazz.getClassName().canonicalName())
                .collect(Collectors.joining(","))));
      }
    }

    @Override
    public Class<? extends IBoundModule> generate(IModule module) throws MetaschemaException {
      IProduction production = generateClasses(module);
      try {
        compileClasses(production, compilePath);
      } catch (IOException | DependencyResolutionRequiredException ex) {
        throw new IllegalStateException("failed to compile classes", ex);
      }
      IGeneratedModuleClass moduleClass = ObjectUtils.requireNonNull(production.getModuleProduction(module));

      try {
        return moduleClass.load(classLoader);
      } catch (ClassNotFoundException ex) {
        throw new IllegalStateException(ex);
      }
    }
  }

  /**
   * A module post processor that applies external constraints to modules,
   * excluding the built-in Metaschema module to avoid duplicate constraint
   * application.
   */
  private static class LimitedExternalConstraintsModulePostProcessor
      extends ExternalConstraintsModulePostProcessor {

    /**
     * Construct a new post processor with the specified constraint sets.
     *
     * @param additionalConstraintSets
     *          the constraint sets to apply to modules
     */
    public LimitedExternalConstraintsModulePostProcessor(
        @NonNull Collection<IConstraintSet> additionalConstraintSets) {
      super(additionalConstraintSets);
    }

    /**
     * This method ensures that constraints are not applied to the built-in
     * Metaschema module module twice, when this module is selected as the source
     * for generation.
     */
    @Override
    public void processModule(IModule module) {
      if (!(module instanceof MetaschemaModelModule)) {
        super.processModule(module);
      }
    }
  }
}
