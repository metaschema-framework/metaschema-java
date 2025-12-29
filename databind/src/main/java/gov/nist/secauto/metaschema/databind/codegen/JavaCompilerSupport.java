/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides support for compiling Java source files using the system Java
 * compiler.
 * <p>
 * This class wraps the {@link javax.tools.JavaCompiler} API to provide a
 * simplified interface for compiling generated Java source files. It supports
 * configuring the classpath, module path, and output directory.
 */
public class JavaCompilerSupport {
  @Nullable
  private Logger logger;
  @NonNull
  private final Path classDir;
  @NonNull
  private final Set<String> classPath = new LinkedHashSet<>();
  @NonNull
  private final Set<String> modulePath = new LinkedHashSet<>();
  @NonNull
  private final Set<String> rootModuleNames = new LinkedHashSet<>();

  /**
   * Construct a new compiler support instance.
   *
   * @param classDir
   *          the directory where compiled class files will be written
   */
  public JavaCompilerSupport(@NonNull Path classDir) {
    this.classDir = classDir;
  }

  /**
   * Get the configured classpath entries.
   *
   * @return the classpath entries
   */
  public Set<String> getClassPath() {
    return classPath;
  }

  /**
   * Get the configured module path entries.
   *
   * @return the module path entries
   */
  public Set<String> getModulePath() {
    return modulePath;
  }

  /**
   * Get the configured root module names.
   *
   * @return the root module names
   */
  public Set<String> getRootModuleNames() {
    return rootModuleNames;
  }

  /**
   * Add an entry to the classpath.
   *
   * @param entry
   *          the classpath entry to add
   */
  public void addToClassPath(@NonNull String entry) {
    classPath.add(entry);
  }

  /**
   * Add an entry to the module path.
   *
   * @param entry
   *          the module path entry to add
   */
  public void addToModulePath(@NonNull String entry) {
    modulePath.add(entry);
  }

  /**
   * Add a root module name.
   *
   * @param entry
   *          the root module name to add
   */
  public void addRootModule(@NonNull String entry) {
    rootModuleNames.add(entry);
  }

  /**
   * Set the logger for compilation messages.
   *
   * @param logger
   *          the logger to use
   */
  public void setLogger(@NonNull Logger logger) {
    this.logger = logger;
  }

  /**
   * Generate the compiler options based on the current configuration.
   *
   * @return the list of compiler options
   */
  @NonNull
  protected List<String> generateCompilerOptions() {
    List<String> options = new LinkedList<>();
    options.add("-d");
    options.add(classDir.toString());

    if (!classPath.isEmpty()) {
      options.add("-classpath");
      options.add(classPath.stream()
          .collect(Collectors.joining(":")));
    }

    if (!modulePath.isEmpty()) {
      options.add("-p");
      options.add(modulePath.stream()
          .collect(Collectors.joining(":")));
    }

    return options;
  }

  /**
   * Compile the provided Java source files.
   *
   * @param classFiles
   *          the source files to compile
   * @return information about the compilation result
   * @throws IOException
   *           if an error occurred while compiling the classes
   * @throws IllegalArgumentException
   *           if any of the options are invalid, or if any of the given
   *           compilation units are of other kind than
   *           {@link javax.tools.JavaFileObject.Kind#SOURCE}
   */
  public CompilationResult compile(@NonNull List<Path> classFiles) throws IOException {
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    List<JavaFileObject> compilationUnits;
    try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {

      compilationUnits = classFiles.stream()
          .map(fileManager::getJavaFileObjects)
          .map(CollectionUtil::toList)
          .flatMap(List::stream)
          .collect(Collectors.toUnmodifiableList());

      List<String> options = generateCompilerOptions();

      Logger logger = this.logger;
      if (logger != null && logger.isDebugEnabled()) {
        logger.debug(String.format("Using options: %s", options));
      }

      boolean result;
      try (StringWriter writer = new StringWriter()) {
        JavaCompiler.CompilationTask task = compiler.getTask(
            writer,
            fileManager,
            diagnostics,
            options,
            null,
            compilationUnits);
        task.addModules(rootModuleNames);

        result = task.call();
        writer.flush();
        String output = writer.toString();
        if (!output.isBlank() && logger != null && logger.isInfoEnabled()) {
          logger.info(String.format("compiler output: %s", writer.toString()));
        }
      }
      return new CompilationResult(result, diagnostics);
    }
  }

  /**
   * Contains the result of a compilation operation.
   */
  public static final class CompilationResult {
    private final boolean successful;
    @NonNull
    private final DiagnosticCollector<JavaFileObject> diagnostics;

    private CompilationResult(boolean successful, @NonNull DiagnosticCollector<JavaFileObject> diagnostics) {
      this.successful = successful;
      this.diagnostics = diagnostics;
    }

    /**
     * Check if the compilation was successful.
     *
     * @return {@code true} if compilation succeeded, {@code false} otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Get the compilation diagnostics.
     *
     * @return the diagnostics collector containing any warnings or errors
     */
    public DiagnosticCollector<?> getDiagnostics() {
      return diagnostics;
    }
  }

  /**
   * A logging interface for compilation messages.
   */
  public interface Logger {
    /**
     * Check if debug logging is enabled.
     *
     * @return {@code true} if debug logging is enabled
     */
    boolean isDebugEnabled();

    /**
     * Check if info logging is enabled.
     *
     * @return {@code true} if info logging is enabled
     */
    boolean isInfoEnabled();

    /**
     * Log a debug message.
     *
     * @param msg
     *          the message to log
     */
    void debug(String msg);

    /**
     * Log an info message.
     *
     * @param msg
     *          the message to log
     */
    void info(String msg);
  }
}
