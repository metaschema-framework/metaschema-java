/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.maven.plugin;

import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingConstraintLoader;
import gov.nist.secauto.metaschema.databind.model.metaschema.BindingModuleLoader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class AbstractMetaschemaMojo
    extends AbstractMojo {
  private static final String[] DEFAULT_INCLUDES = { "**/*.xml" };

  /**
   * The Maven project context.
   *
   * @parameter default-value="${project}"
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

  @Component
  private BuildContext buildContext;

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
   * configuration, use that value.</li>
   * <li>If the Maven property <code>project.build.sourceEncoding</code> is
   * defined, use its value.</li>
   * <li>Otherwise use the value from the system property
   * <code>file.encoding</code>.</li>
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
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/metaschema", required = true)
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
   * configuration, use that value.</li>
   * <li>If the Maven property <code>project.build.sourceEncoding</code> is
   * defined, use its value.</li>
   * <li>Otherwise use the value from the system property
   * <code>file.encoding</code>.</li>
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
   * Get the configured collection of constraints.
   *
   * @param bindingContext
   *          the Metaschema binding context to use when loading the constraints
   * @return the loaded constraints
   * @throws MetaschemaException
   *           if a binding exception occurred while loading the constraints
   * @throws IOException
   *           if an error occurred while reading the constraints
   */
  protected List<IConstraintSet> getConstraints(@NonNull IBindingContext bindingContext)
      throws MetaschemaException, IOException {
    IConstraintLoader loader = new BindingConstraintLoader(bindingContext);
    List<IConstraintSet> constraintSets = new ArrayList<>(constraints.length);
    for (File constraint : this.constraints) {
      constraintSets.addAll(loader.load(ObjectUtils.notNull(constraint)));
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
   * Construct a new module loader based on the provided mojo configuration.
   *
   * @return the module loader
   * @throws MojoExecutionException
   *           if an error occurred while loading the configured constraints
   */
  @NonNull
  protected BindingModuleLoader newModuleLoader() throws MojoExecutionException {
    IBindingContext bindingContext = IBindingContext.instance();

    List<IConstraintSet> constraints;
    try {
      constraints = getConstraints(bindingContext);
    } catch (MetaschemaException | IOException ex) {
      throw new MojoExecutionException("Unable to load external constraints.", ex);
    }

    // generate Java sources based on provided metaschema sources
    BindingModuleLoader loader = constraints.isEmpty()
        ? new BindingModuleLoader(bindingContext)
        : new BindingModuleLoader(
            bindingContext,
            CollectionUtil.singletonList(new ExternalConstraintsModulePostProcessor(constraints)));
    loader.allowEntityResolution();
    return loader;
  }
}
