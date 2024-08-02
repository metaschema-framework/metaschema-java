/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.test.RootBoundAssembly;

import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.Reader;

import edu.umd.cs.findbugs.annotations.NonNull;

public class AbstractBoundModelTestSupport {
  @RegisterExtension
  private final JUnit5Mockery context = new JUnit5Mockery();

  @NonNull
  private final IBindingContext bindingContext = DefaultBindingContext.instance();
  //
  // @BeforeAll
  // void initContext() {
  // /**
  // * Setup bound classes
  // */
  // registerMetaschema(TestMetaschema.class);
  // registerClassBinding(CollapsibleFlaggedBoundField.class);
  // registerClassBinding(EmptyBoundAssembly.class);
  // registerClassBinding(FlaggedBoundAssembly.class);
  // registerClassBinding(FlaggedBoundField.class);
  // registerClassBinding(OnlyModelBoundAssembly.class);
  // registerClassBinding(RootBoundAssembly.class);
  // }

  @NonNull
  protected JUnit5Mockery getJUnit5Mockery() {
    return ObjectUtils.requireNonNull(context);
  }

  @NonNull
  protected IBindingContext getBindingContext() {
    return bindingContext;
  }

  @NonNull
  protected IBoundDefinitionModelComplex registerClassBinding(@NonNull Class<? extends IBoundObject> clazz) {
    IBoundDefinitionModelComplex definition = getBindingContext().getBoundDefinitionForClass(clazz);
    if (definition == null) {
      throw new IllegalArgumentException(String.format("Unable to find bound definition for class '%s'.",
          clazz.getName()));
    }
    return definition;
  }

  @NonNull
  protected IBoundModule registerModule(@NonNull Class<? extends IBoundModule> clazz) {
    return getBindingContext().registerModule(clazz);
  }

  @NonNull
  protected IBoundDefinitionModelAssembly getRootAssemblyClassBinding() {
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
