/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertAll;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.codegen.config.DefaultBindingConfiguration;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.IDeserializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Base class providing utilities for testing Metaschema module compilation and
 * binding.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractMetaschemaTest {

  private static final Logger LOGGER = LogManager.getLogger(AbstractMetaschemaTest.class);
  // @TempDir
  // Path generationDir;
  @NonNull
  protected Path generationDir = ObjectUtils.notNull(Paths.get("target/generated-test-sources/metaschema"));

  /**
   * Creates a new binding context with no constraints.
   *
   * @return a new binding context instance
   * @throws IOException
   *           if an I/O error occurs while creating the context
   */
  @NonNull
  protected static IBindingContext newBindingContext() throws IOException {
    return newBindingContext(CollectionUtil.emptyList());
  }

  /**
   * Creates a new binding context with the specified constraints.
   *
   * @param constraints
   *          the constraint sets to apply
   * @return a new binding context instance
   * @throws IOException
   *           if an I/O error occurs while creating the context
   */
  @NonNull
  protected static IBindingContext newBindingContext(@NonNull Collection<IConstraintSet> constraints)
      throws IOException {
    Path generationDir = Paths.get("target/generated-modules");
    Files.createDirectories(generationDir);

    return IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(generationDir, "modules-")))
        .constraintSet(constraints)
        .build();
  }

  /**
   * Compiles a Metaschema module and returns the root class.
   *
   * @param moduleFile
   *          the path to the Metaschema module file
   * @param bindingFile
   *          the path to the binding configuration file, or {@code null}
   * @param rootClassName
   *          the fully-qualified name of the root class
   * @param classDir
   *          the directory where compiled classes are output
   * @return the compiled root class
   * @throws IOException
   *           if an I/O error occurs
   * @throws ClassNotFoundException
   *           if the root class cannot be found
   * @throws MetaschemaException
   *           if a Metaschema error occurs
   * @throws BindingException
   *           if a binding error occurs
   */
  @NonNull
  public Class<? extends IBoundObject> compileModule(
      @NonNull Path moduleFile,
      @Nullable Path bindingFile,
      @NonNull String rootClassName,
      @NonNull Path classDir)
      throws IOException, ClassNotFoundException, MetaschemaException, BindingException {
    IModule module = newBindingContext().loadMetaschema(moduleFile);

    DefaultBindingConfiguration bindingConfiguration = new DefaultBindingConfiguration();
    if (bindingFile != null && Files.exists(bindingFile) && Files.isRegularFile(bindingFile)) {
      bindingConfiguration.load(bindingFile);
    }

    ModuleCompilerHelper.compileModule(module, classDir, bindingConfiguration);

    // Load classes
    return ObjectUtils.asType(ObjectUtils.notNull(ModuleCompilerHelper.newClassLoader(
        classDir,
        ObjectUtils.notNull(Thread.currentThread().getContextClassLoader()))
        .loadClass(rootClassName)));
  }

  @NonNull
  private static <T extends IBoundObject> T read(
      @NonNull Format format,
      @NonNull Path file,
      @NonNull Class<T> rootClass,
      @NonNull IBindingContext context)
      throws IOException {
    IDeserializer<T> deserializer = context.newDeserializer(format, rootClass);
    LOGGER.info("Reading content: {}", file);
    return deserializer.deserialize(file);
  }

  private static <T extends IBoundObject> void write(
      @NonNull Format format,
      @NonNull Path file,
      @NonNull T rootObject,
      @NonNull IBindingContext context) throws IOException {
    @SuppressWarnings("unchecked")
    Class<T> clazz = (Class<T>) rootObject.getClass();

    try (Writer writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      assert writer != null;
      context.newSerializer(format, clazz).serialize(rootObject, writer);
    }
  }

  /**
   * Runs tests using the default test resources location.
   *
   * @param testPath
   *          the relative path within the test resources
   * @param rootClassName
   *          the fully-qualified name of the root class
   * @param classDir
   *          the directory where compiled classes are output
   * @throws ClassNotFoundException
   *           if the root class cannot be found
   * @throws IOException
   *           if an I/O error occurs
   * @throws MetaschemaException
   *           if a Metaschema error occurs
   * @throws BindingException
   *           if a binding error occurs
   */
  public void runTests(@NonNull String testPath, @NonNull String rootClassName, @NonNull Path classDir)
      throws ClassNotFoundException, IOException, MetaschemaException, BindingException {
    runTests(testPath, rootClassName, classDir, null);
  }

  /**
   * Runs tests using the default test resources location with assertions.
   *
   * @param testPath
   *          the relative path within the test resources
   * @param rootClassName
   *          the fully-qualified name of the root class
   * @param classDir
   *          the directory where compiled classes are output
   * @param assertions
   *          optional assertions to run on the deserialized object
   * @throws ClassNotFoundException
   *           if the root class cannot be found
   * @throws IOException
   *           if an I/O error occurs
   * @throws MetaschemaException
   *           if a Metaschema error occurs
   * @throws BindingException
   *           if a binding error occurs
   */
  public void runTests(
      @NonNull String testPath,
      @NonNull String rootClassName,
      @NonNull Path classDir,
      java.util.function.Consumer<Object> assertions)
      throws ClassNotFoundException, IOException, MetaschemaException, BindingException {
    runTests(
        ObjectUtils.notNull(Paths.get(String.format("src/test/resources/metaschema/%s/metaschema.xml", testPath))),
        ObjectUtils.notNull(Paths.get(String.format("src/test/resources/metaschema/%s/binding.xml", testPath))),
        ObjectUtils.notNull(Paths.get(String.format("src/test/resources/metaschema/%s/example.xml", testPath))),
        rootClassName,
        classDir,
        assertions);
  }

  /**
   * Runs tests using explicit paths with assertions.
   *
   * @param metaschemaPath
   *          the path to the Metaschema module file
   * @param bindingPath
   *          the path to the binding configuration file
   * @param examplePath
   *          the path to an example content file, or {@code null}
   * @param rootClassName
   *          the fully-qualified name of the root class
   * @param classDir
   *          the directory where compiled classes are output
   * @param assertions
   *          optional assertions to run on the deserialized object
   * @throws ClassNotFoundException
   *           if the root class cannot be found
   * @throws IOException
   *           if an I/O error occurs
   * @throws MetaschemaException
   *           if a Metaschema error occurs
   * @throws BindingException
   *           if a binding error occurs
   */
  public void runTests(
      @NonNull Path metaschemaPath,
      @NonNull Path bindingPath,
      @Nullable Path examplePath,
      @NonNull String rootClassName,
      @NonNull Path classDir,
      java.util.function.Consumer<Object> assertions)
      throws ClassNotFoundException, IOException, MetaschemaException, BindingException {

    Class<? extends IBoundObject> rootClass = compileModule(
        metaschemaPath,
        bindingPath,
        rootClassName,
        classDir);
    runTests(examplePath, rootClass, assertions);
  }

  /**
   * Runs tests on the specified example content using the given root class.
   *
   * @param <T>
   *          the type of the root object
   * @param examplePath
   *          the path to an example content file, or {@code null}
   * @param rootClass
   *          the root class to deserialize into
   * @param assertions
   *          optional assertions to run on the deserialized object
   * @throws IOException
   *           if an I/O error occurs
   */
  public <T extends IBoundObject> void runTests(
      @Nullable Path examplePath,
      @NonNull Class<? extends T> rootClass,
      java.util.function.Consumer<Object> assertions) throws IOException {

    if (examplePath != null && Files.exists(examplePath)) {
      IBindingContext context = newBindingContext();
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Testing XML file: {}", examplePath.toString());
      }

      {

        T root = read(Format.XML, examplePath, rootClass, context);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.atDebug().log("Read XML: Object: {}", root.toString());
        }
        if (assertions != null) {
          assertAll("Deserialize XML", () -> {
            assertions.accept(root);
          });
        }

        if (LOGGER.isDebugEnabled()) {
          LOGGER.atDebug().log("Write XML:");
        }
        write(Format.XML, ObjectUtils.notNull(Paths.get("target/out.xml")), root, context);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.atDebug().log("Write JSON:");
        }
        write(Format.XML, ObjectUtils.notNull(Paths.get("target/out.json")), root, context);
      }

      Object root = read(Format.XML, ObjectUtils.notNull(Paths.get("target/out.xml")), rootClass, context);
      if (assertions != null) {
        assertAll("Deserialize XML (roundtrip)", () -> assertions.accept(root));
      }
    }
  }

}
