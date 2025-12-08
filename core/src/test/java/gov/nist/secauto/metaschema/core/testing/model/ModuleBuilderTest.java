/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;

import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * Unit tests for {@link IModuleBuilder}.
 */
class ModuleBuilderTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/test";
  private static final String TEST_SHORT_NAME = "test-module";
  private static final String TEST_VERSION = "1.0.0";

  @Test
  void testBasicModuleCreation() {
    // Given
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When
    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .toModule();

    // Then
    assertNotNull(module, "Module should not be null");
    assertEquals(URI.create(TEST_NAMESPACE), module.getXmlNamespace(), "Namespace should match");
    assertEquals(TEST_SHORT_NAME, module.getShortName(), "Short name should match");
    assertEquals(TEST_VERSION, module.getVersion(), "Version should match");
    assertEquals(source, module.getSource(), "Source should match");
  }

  @Test
  void testModuleBuilderFromFactory() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .toModule();

    // Then
    assertNotNull(module, "Module should not be null");
    assertEquals(TEST_SHORT_NAME, module.getShortName(), "Short name should match");
  }
}
