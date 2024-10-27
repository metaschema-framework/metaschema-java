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
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.Reader;

import edu.umd.cs.findbugs.annotations.NonNull;

public class AbstractBoundModelTestSupport
    extends AbstractMetaschemaTest {
  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery();

  @NonNull
  protected JUnit5Mockery getJUnit5Mockery() {
    return ObjectUtils.requireNonNull(context);
  }

  @NonNull
  protected IBoundDefinitionModelAssembly getRootAssemblyClassBinding() throws IOException {
    return ObjectUtils.requireNonNull((IBoundDefinitionModelAssembly) getBindingContext()
        .getBoundDefinitionForClass(RootBoundAssembly.class));
  }

  @SuppressWarnings("resource")
  @NonNull
  protected JsonParser newJsonParser(@NonNull Reader reader) throws JsonParseException, IOException {
    JsonFactory factory = new JsonFactory();
    JsonParser jsonParser = factory.createParser(reader); // NOPMD - reader not owned by this method
    return ObjectUtils.notNull(jsonParser);
  }
}
