/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

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

  @Test
  void testModuleWithFlagDefinition() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .flag(mocking.flag().name("test-flag"))
        .toModule();

    // Then
    assertEquals(1, module.getFlagDefinitions().size(), "Should have one flag definition");
    IFlagDefinition flagDef = module.getFlagDefinitions().iterator().next();
    assertNotNull(flagDef, "Flag definition should not be null");
    assertEquals("test-flag", flagDef.getName(), "Flag name should match");
    assertSame(module, flagDef.getContainingModule(), "Flag should reference containing module");
  }

  @Test
  void testModuleWithFieldDefinition() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .field(mocking.field().name("test-field"))
        .toModule();

    // Then
    assertEquals(1, module.getFieldDefinitions().size(), "Should have one field definition");
    IFieldDefinition fieldDef = module.getFieldDefinitions().iterator().next();
    assertNotNull(fieldDef, "Field definition should not be null");
    assertEquals("test-field", fieldDef.getName(), "Field name should match");
    assertSame(module, fieldDef.getContainingModule(), "Field should reference containing module");
  }

  @Test
  void testModuleWithAssemblyDefinition() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .assembly(mocking.assembly().name("test-assembly"))
        .toModule();

    // Then
    assertEquals(1, module.getAssemblyDefinitions().size(), "Should have one assembly definition");
    IAssemblyDefinition assemblyDef = module.getAssemblyDefinitions().iterator().next();
    assertNotNull(assemblyDef, "Assembly definition should not be null");
    assertEquals("test-assembly", assemblyDef.getName(), "Assembly name should match");
    assertSame(module, assemblyDef.getContainingModule(), "Assembly should reference containing module");
  }

  @Test
  void testRecursiveAssembly() {
    // Given - an assembly that references itself (like a "parent" that can contain
    // children of same type)
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When - build a module with self-referencing assembly
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .assembly(mocking.assembly()
            .name("node")
            .modelInstances(java.util.List.of(
                mocking.assemblyRef("node")))) // Reference to self
        .toModule();

    // Then - assembly should exist and be resolvable
    assertEquals(1, module.getAssemblyDefinitions().size(), "Should have one assembly definition");
    IAssemblyDefinition nodeDef = module.getAssemblyDefinitions().iterator().next();
    assertEquals("node", nodeDef.getName(), "Assembly name should match");

    // The model instance should reference the same definition
    var modelInstances = nodeDef.getModelInstances();
    assertEquals(1, modelInstances.size(), "Should have one model instance");
    INamedModelInstanceAbsolute childInstance = (INamedModelInstanceAbsolute) modelInstances.iterator().next();
    assertNotNull(childInstance, "Child instance should not be null");
    assertEquals("node", childInstance.getName(), "Child instance name should match");
    assertSame(nodeDef, childInstance.getDefinition(), "Child should reference parent definition");
  }

  @Test
  void testCrossReferenceAssemblies() {
    // Given - two assemblies that reference each other
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When - build a module with cross-referencing assemblies
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .assembly(mocking.assembly()
            .name("parent")
            .modelInstances(java.util.List.of(
                mocking.assemblyRef("child"))))
        .assembly(mocking.assembly()
            .name("child")
            .modelInstances(java.util.List.of(
                mocking.assemblyRef("parent"))))
        .toModule();

    // Then - both assemblies should be defined
    assertEquals(2, module.getAssemblyDefinitions().size(), "Should have two assembly definitions");

    // Verify cross-references resolve correctly
    IEnhancedQName parentQName = IEnhancedQName.of(TEST_NAMESPACE, "parent");
    IEnhancedQName childQName = IEnhancedQName.of(TEST_NAMESPACE, "child");

    IAssemblyDefinition parentDef = module.getAssemblyDefinitionByName(parentQName.getIndexPosition());
    IAssemblyDefinition childDef = module.getAssemblyDefinitionByName(childQName.getIndexPosition());

    assertNotNull(parentDef, "Parent should be found");
    assertNotNull(childDef, "Child should be found");

    // Parent's child instance should reference child definition
    INamedModelInstanceAbsolute childInParent
        = (INamedModelInstanceAbsolute) parentDef.getModelInstances().iterator().next();
    assertSame(childDef, childInParent.getDefinition(), "Parent's child should reference child definition");

    // Child's parent instance should reference parent definition
    INamedModelInstanceAbsolute parentInChild
        = (INamedModelInstanceAbsolute) childDef.getModelInstances().iterator().next();
    assertSame(parentDef, parentInChild.getDefinition(), "Child's parent should reference parent definition");
  }

  @Test
  void testModuleDefinitionLookup() {
    // Given
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create("https://example.com/test"));

    // When
    IModule module = mocking.module()
        .namespace(TEST_NAMESPACE)
        .shortName(TEST_SHORT_NAME)
        .version(TEST_VERSION)
        .source(source)
        .flag(mocking.flag().name("my-flag"))
        .field(mocking.field().name("my-field"))
        .assembly(mocking.assembly().name("my-assembly"))
        .toModule();

    // Then - lookup by name should work
    IEnhancedQName flagQName = IEnhancedQName.of(TEST_NAMESPACE, "my-flag");
    IFlagDefinition foundFlag = module.getFlagDefinitionByName(flagQName);
    assertNotNull(foundFlag, "Should find flag by name");
    assertEquals("my-flag", foundFlag.getName());

    IEnhancedQName fieldQName = IEnhancedQName.of(TEST_NAMESPACE, "my-field");
    IFieldDefinition foundField = module.getFieldDefinitionByName(fieldQName.getIndexPosition());
    assertNotNull(foundField, "Should find field by name");
    assertEquals("my-field", foundField.getName());

    IEnhancedQName assemblyQName = IEnhancedQName.of(TEST_NAMESPACE, "my-assembly");
    IAssemblyDefinition foundAssembly = module.getAssemblyDefinitionByName(assemblyQName.getIndexPosition());
    assertNotNull(foundAssembly, "Should find assembly by name");
    assertEquals("my-assembly", foundAssembly.getName());
  }
}
