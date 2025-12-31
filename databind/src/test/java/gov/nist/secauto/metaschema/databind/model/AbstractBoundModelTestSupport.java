/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.AbstractMetaschemaTest;
import gov.nist.secauto.metaschema.databind.model.test.RootBoundAssembly;

import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.Reader;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Base class providing test support for bound model tests with mocking
 * capabilities.
 */
public class AbstractBoundModelTestSupport
    extends AbstractMetaschemaTest {
  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  /**
   * Gets the JUnit5 mockery context for creating mocks.
   *
   * @return the mockery context
   */
  @NonNull
  protected JUnit5Mockery getJUnit5Mockery() {
    return ObjectUtils.requireNonNull(context);
  }

  /**
   * Gets the bound assembly definition for the root test assembly.
   *
   * @return the bound assembly definition
   * @throws IOException
   *           if an I/O error occurs
   */
  @NonNull
  protected IBoundDefinitionModelAssembly getRootAssemblyClassBinding() throws IOException {
    return ObjectUtils.requireNonNull((IBoundDefinitionModelAssembly) newBindingContext()
        .getBoundDefinitionForClass(RootBoundAssembly.class));
  }

  /**
   * Creates a new JSON parser for the given reader.
   *
   * @param reader
   *          the reader to parse from
   * @return a new JSON parser
   * @throws JsonParseException
   *           if a JSON parsing error occurs
   * @throws IOException
   *           if an I/O error occurs
   */
  @SuppressWarnings("resource")
  @NonNull
  protected JsonParser newJsonParser(@NonNull Reader reader) throws JsonParseException, IOException {
    JsonFactory factory = new JsonFactory();
    JsonParser jsonParser = factory.createParser(reader); // NOPMD - reader not owned by this method
    return ObjectUtils.notNull(jsonParser);
  }
}
