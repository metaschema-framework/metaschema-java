/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../../../metaschema/unit-tests.yaml
// Do not edit - changes will be lost when regenerated.

package gov.nist.secauto.metaschema.model.testing.testsuite;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.AbstractBoundModule;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaModule;
import java.net.URI;
import java.util.List;

/**
 * Metaschema Test Suite
 */
@MetaschemaModule(
    assemblies = {
        TestSuite.class,
        TestCollection.class,
        TestScenario.class,
        GenerateSchema.class,
        Metaschema.class,
        ValidationCase.class,
        GenerationCase.class
    })
public final class MetaschemaTestSuiteModule
    extends AbstractBoundModule {
  private static final MarkupLine NAME = MarkupLine.fromMarkdown("Metaschema Test Suite");

  private static final String SHORT_NAME = "metaschema-test-suite";

  private static final String VERSION = "1.0.0";

  private static final URI XML_NAMESPACE = URI.create("http://csrc.nist.gov/ns/metaschema/test-suite/1.0");

  private static final URI JSON_BASE_URI = URI.create("http://csrc.nist.gov/ns/metaschema/test-suite/1.0");

  /**
   * Construct a new module instance.
   *
   * @param importedModules
   *          modules imported by this module
   * @param bindingContext
   *          the binding context to associate with this module
   */
  public MetaschemaTestSuiteModule(List<? extends IBoundModule> importedModules,
      IBindingContext bindingContext) {
    super(importedModules, bindingContext);
  }

  @Override
  public MarkupLine getName() {
    return NAME;
  }

  @Override
  public String getShortName() {
    return SHORT_NAME;
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public URI getXmlNamespace() {
    return XML_NAMESPACE;
  }

  @Override
  public URI getJsonBaseUri() {
    return JSON_BASE_URI;
  }

  @Override
  public MarkupMultiline getRemarks() {
    return null;
  }
}
