/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertAll;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractMetaschemaTest {

  private static final Logger LOGGER = LogManager.getLogger(AbstractMetaschemaTest.class);
  // @TempDir
  // Path generationDir;
  @NonNull
  protected Path generationDir = ObjectUtils.notNull(Paths.get("target/generated-test-sources/metaschema"));

  @NonNull
  protected IBindingContext getBindingContext() throws IOException {
    return IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(Paths.get("target"), "modules-")))
        .build();
  }

  @NonNull
  public Class<? extends IBoundObject> compileModule(
      @NonNull Path moduleFile,
      @Nullable Path bindingFile,
      @NonNull String rootClassName,
      @NonNull Path classDir)
      throws IOException, ClassNotFoundException, MetaschemaException {
    IModule module = getBindingContext().loadMetaschema(moduleFile);

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
    @SuppressWarnings("unchecked") Class<T> clazz = (Class<T>) rootObject.getClass();

    try (Writer writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      assert writer != null;
      context.newSerializer(format, clazz).serialize(rootObject, writer);
    }
  }

  public void runTests(@NonNull String testPath, @NonNull String rootClassName, @NonNull Path classDir)
      throws ClassNotFoundException, IOException, MetaschemaException, BindingException {
    runTests(testPath, rootClassName, classDir, null);
  }

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

  public <T extends IBoundObject> void runTests(
      @Nullable Path examplePath,
      @NonNull Class<? extends T> rootClass,
      java.util.function.Consumer<Object> assertions)
      throws ClassNotFoundException, IOException, MetaschemaException, BindingException {

    if (examplePath != null && Files.exists(examplePath)) {
      IBindingContext context = getBindingContext();
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
