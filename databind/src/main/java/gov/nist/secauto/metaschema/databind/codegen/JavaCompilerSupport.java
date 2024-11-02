/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
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

public class JavaCompilerSupport {
  private static final Logger LOGGER = LogManager.getLogger(JavaCompilerSupport.class);

  @NonNull
  private final Path classDir;
  @NonNull
  private final Set<String> classPath = new LinkedHashSet<>();
  @NonNull
  private final Set<String> modulePath = new LinkedHashSet<>();
  @NonNull
  private final Set<String> rootModuleNames = new LinkedHashSet<>();

  public JavaCompilerSupport(@NonNull Path classDir) {
    this.classDir = classDir;
  }

  public Set<String> getClassPath() {
    return classPath;
  }

  public Set<String> getModulePath() {
    return modulePath;
  }

  public Set<String> getRootModuleNames() {
    return rootModuleNames;
  }

  public void addToClassPath(@NonNull String entry) {
    classPath.add(entry);
  }

  public void addToModulePath(@NonNull String entry) {
    modulePath.add(entry);
  }

  public void addRootModule(@NonNull String entry) {
    rootModuleNames.add(entry);
  }

  @NonNull
  protected List<String> generateCompilerOptions() {
    List<String> options = new LinkedList<>();
    // options.add("-verbose");
    // options.add("-g");
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
   * Generate and compile Java classes.
   *
   * @param classFiles
   *          the files to compile
   * @param compileOut
   *          a Writer for additional output from the compiler; use System.err if
   *          null
   * @return information about the generated classes
   * @throws IOException
   *           if an error occurred while compiling the classes
   * @throws IllegalArgumentException
   *           if any of the options are invalid, or if any of the given
   *           compilation units are of other kind than
   *           {@link javax.tools.JavaFileObject.Kind#SOURCE}
   */

  public CompilationResult compile(@NonNull List<Path> classFiles, @Nullable Writer compileOut) throws IOException {
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

      if (LOGGER.isDebugEnabled()) {
        LOGGER.atDebug().log("Using options: {}", options);
      }

      JavaCompiler.CompilationTask task = compiler.getTask(
          compileOut,
          fileManager,
          diagnostics,
          options,
          null,
          compilationUnits);

      task.addModules(rootModuleNames);

      return new CompilationResult(task.call(), diagnostics);
    }
  }

  public static final class CompilationResult {
    private final boolean successful;
    @NonNull
    private final DiagnosticCollector<JavaFileObject> diagnostics;

    private CompilationResult(boolean successful, @NonNull DiagnosticCollector<JavaFileObject> diagnostics) {
      this.successful = successful;
      this.diagnostics = diagnostics;
    }

    public boolean isSuccessful() {
      return successful;
    }

    public DiagnosticCollector<?> getDiagnostics() {
      return diagnostics;
    }
  }
}
