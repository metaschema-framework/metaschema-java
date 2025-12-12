/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMDocumentNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testsupport.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.testsupport.builder.IFieldBuilder;
import gov.nist.secauto.metaschema.core.testsupport.builder.IModuleBuilder;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Comprehensive tests for Metapath node item traversal and navigation.
 */
class NodeItemTraversalTest {

  private static final String TEST_NAMESPACE = "http://example.com/ns/traversal-test";
  private static final URI DOCUMENT_URI = URI.create("http://example.com/test-doc.xml");

  /**
   * Test creating a document node item with a root assembly.
   */
  @Test
  void testCreateDocumentNodeItem() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root-assembly")
            .rootName("root-assembly"))
        .toModule();

    IAssemblyDefinition rootDef = module.getRootAssemblyDefinitions().iterator().next();
    assertNotNull(rootDef, "Root assembly definition should exist");

    // Create a document node item
    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(DOCUMENT_URI, rootDef);

    assertNotNull(document, "Document node item should be created");
    assertEquals(INodeItem.NodeType.DOCUMENT, document.getNodeType(), "Should be a document node");
    assertEquals(DOCUMENT_URI, document.getDocumentUri(), "Document URI should match");
    assertNull(document.getParentNodeItem(), "Document should have no parent");

    IRootAssemblyNodeItem rootAssembly = document.getRootAssemblyNodeItem();
    assertNotNull(rootAssembly, "Root assembly should exist");
    assertEquals(INodeItem.NodeType.ASSEMBLY, rootAssembly.getNodeType(), "Should be an assembly node");
    assertEquals(document, rootAssembly.getParentNodeItem(), "Root assembly parent should be the document");
  }

  /**
   * Test creating assembly node items with flags.
   */
  @Test
  void testCreateAssemblyWithFlags() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("test-assembly")
            .rootName("test-assembly")
            .flags(List.of(
                mocking.flag().namespace(TEST_NAMESPACE).name("flag1"),
                mocking.flag().namespace(TEST_NAMESPACE).name("flag2"))))
        .toModule();

    IAssemblyDefinition assemblyDef = module.getRootAssemblyDefinitions().iterator().next();
    StaticContext staticContext = StaticContext.instance();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(assemblyDef, staticContext);

    // Add flag values - find flag instances by name to ensure correct mapping
    IFlagInstance flag1Instance = assemblyDef.getFlagInstanceByName(
        IEnhancedQName.of(TEST_NAMESPACE, "flag1").getIndexPosition());
    IFlagInstance flag2Instance = assemblyDef.getFlagInstanceByName(
        IEnhancedQName.of(TEST_NAMESPACE, "flag2").getIndexPosition());
    assertNotNull(flag1Instance, "flag1 instance should exist");
    assertNotNull(flag2Instance, "flag2 instance should exist");

    assembly.newFlag(flag1Instance, IStringItem.valueOf("value1"));
    assembly.newFlag(flag2Instance, IStringItem.valueOf("value2"));

    // Verify flags can be accessed
    List<? extends IFlagNodeItem> flags = assembly.getFlags().stream().collect(Collectors.toList());
    assertEquals(2, flags.size(), "Should have 2 flags");

    // Verify flag lookup by name
    IFlagNodeItem flag1 = assembly.getFlagByName(IEnhancedQName.of(TEST_NAMESPACE, "flag1"));
    assertNotNull(flag1, "Flag1 should be found by name");
    assertEquals("value1", flag1.toAtomicItem().asString(), "Flag1 value should match");

    IFlagNodeItem flag2 = assembly.getFlagByName(IEnhancedQName.of(TEST_NAMESPACE, "flag2"));
    assertNotNull(flag2, "Flag2 should be found by name");
    assertEquals("value2", flag2.toAtomicItem().asString(), "Flag2 value should match");

    // Verify flag parent is the assembly
    assertEquals(assembly, flag1.getParentNodeItem(), "Flag parent should be the assembly");
    assertEquals(INodeItem.NodeType.FLAG, flag1.getNodeType(), "Should be a flag node");
  }

  /**
   * Test parent-child navigation between assembly and field nodes.
   */
  @Test
  void testParentChildNavigation() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("parent-assembly")
            .rootName("parent-assembly")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("child-field"))))
        .toModule();

    IAssemblyDefinition assemblyDef = module.getRootAssemblyDefinitions().iterator().next();
    StaticContext staticContext = StaticContext.instance();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(assemblyDef, staticContext);

    // Add a child field
    List<IFieldInstance> fieldInstances = new ArrayList<>(assemblyDef.getFieldInstances());
    assertEquals(1, fieldInstances.size(), "Should have 1 field instance");

    IDMFieldNodeItem field = assembly.newField(fieldInstances.get(0), IStringItem.valueOf("field-value"));

    // Test parent-child relationships
    assertEquals(assembly, field.getParentNodeItem(), "Field parent should be the assembly");
    assertEquals(assembly, field.getParentContentNodeItem(), "Field parent content node should be the assembly");

    List<? extends IModelNodeItem<?, ?>> modelItems = assembly.modelItems().collect(Collectors.toList());
    assertEquals(1, modelItems.size(), "Assembly should have 1 child model item");
    assertEquals(field, modelItems.get(0), "Child should be the field we added");

    assertEquals(INodeItem.NodeType.FIELD, field.getNodeType(), "Should be a field node");
    assertEquals(INodeItem.NodeType.ASSEMBLY, assembly.getNodeType(), "Should be an assembly node");
  }

  /**
   * Test ancestor axis traversal.
   */
  @Test
  void testAncestorAxis() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("grandparent-assembly")
            .rootName("grandparent-assembly")
            .modelInstances(List.of(
                mocking.assemblyRef("child-assembly"))))
        .assembly(mocking.assembly()
            .name("child-assembly")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("grandchild-field"))))
        .toModule();

    IAssemblyDefinition grandparentDef = module.getRootAssemblyDefinitions().iterator().next();
    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(DOCUMENT_URI, grandparentDef);
    IRootAssemblyNodeItem grandparent = document.getRootAssemblyNodeItem();

    // Add child assembly
    IAssemblyInstance childAssemblyInstance = grandparentDef.getAssemblyInstances().iterator().next();
    IDMAssemblyNodeItem child = ((IDMAssemblyNodeItem) grandparent).newAssembly(childAssemblyInstance);

    // Add grandchild field
    IFieldInstance grandchildFieldInstance = child.getDefinition().getFieldInstances().iterator().next();
    IDMFieldNodeItem grandchild = child.newField(grandchildFieldInstance, IStringItem.valueOf("test-value"));

    // Test ancestor axis from grandchild
    // Note: ancestor() returns ancestors in document order (farthest to nearest)
    List<? extends INodeItem> ancestors = grandchild.ancestor().collect(Collectors.toList());
    assertEquals(3, ancestors.size(), "Grandchild should have 3 ancestors: document, grandparent, child");
    // Verify ancestors are in document order (root first)
    assertEquals(document, ancestors.get(0), "First ancestor should be the document (farthest)");
    assertEquals(grandparent, ancestors.get(1), "Second ancestor should be the grandparent assembly");
    assertEquals(child, ancestors.get(2), "Third ancestor should be the child assembly (nearest)");

    // Test ancestor-or-self axis
    // Note: ancestorOrSelf() returns ancestors then self (self is last)
    List<? extends INodeItem> ancestorsOrSelf = grandchild.ancestorOrSelf().collect(Collectors.toList());
    assertEquals(4, ancestorsOrSelf.size(), "Should have 4 items: 3 ancestors + self");
    assertEquals(grandchild, ancestorsOrSelf.get(3), "Last item should be self");
  }

  /**
   * Test descendant axis traversal.
   */
  @Test
  void testDescendantAxis() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("parent-assembly")
            .rootName("parent-assembly")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("field1"),
                mocking.field().namespace(TEST_NAMESPACE).name("field2"),
                mocking.assemblyRef("child-assembly"))))
        .assembly(mocking.assembly()
            .name("child-assembly")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("nested-field"))))
        .toModule();

    IAssemblyDefinition parentDef = module.getRootAssemblyDefinitions().iterator().next();
    StaticContext staticContext = StaticContext.instance();
    IDMAssemblyNodeItem parent = IDMAssemblyNodeItem.newInstance(parentDef, staticContext);

    // Add child fields and assembly - look up by name to ensure correct mapping
    IFieldInstance field1Instance = parentDef.getModelInstances().stream()
        .filter(instance -> instance instanceof IFieldInstance)
        .map(instance -> (IFieldInstance) instance)
        .filter(instance -> "field1".equals(instance.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("field1 instance not found"));
    IFieldInstance field2Instance = parentDef.getModelInstances().stream()
        .filter(instance -> instance instanceof IFieldInstance)
        .map(instance -> (IFieldInstance) instance)
        .filter(instance -> "field2".equals(instance.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("field2 instance not found"));

    parent.newField(field1Instance, IStringItem.valueOf("value1"));
    parent.newField(field2Instance, IStringItem.valueOf("value2"));

    IAssemblyInstance childAssemblyInstance = parentDef.getAssemblyInstances().iterator().next();
    IDMAssemblyNodeItem childAssembly = parent.newAssembly(childAssemblyInstance);

    IFieldInstance nestedFieldInstance = childAssembly.getDefinition().getFieldInstances().iterator().next();
    childAssembly.newField(nestedFieldInstance, IStringItem.valueOf("nested-value"));

    // Test descendant axis - should find all descendants in depth-first order
    List<? extends IModelNodeItem<?, ?>> descendants = parent.descendant().collect(Collectors.toList());
    assertEquals(4, descendants.size(), "Should have 4 descendants: field1, field2, child-assembly, nested-field");

    // Count descendant types - the exact order depends on modelItems() ordering
    long fieldCount = descendants.stream()
        .filter(item -> item.getNodeType() == INodeItem.NodeType.FIELD)
        .count();
    long assemblyCount = descendants.stream()
        .filter(item -> item.getNodeType() == INodeItem.NodeType.ASSEMBLY)
        .count();
    assertEquals(3, fieldCount, "Should have 3 field descendants (field1, field2, nested-field)");
    assertEquals(1, assemblyCount, "Should have 1 assembly descendant (child-assembly)");

    // Verify the last field is the nested-field (child of the child-assembly)
    // by checking its parent is an assembly
    IModelNodeItem<?, ?> nestedField = descendants.stream()
        .filter(item -> item.getNodeType() == INodeItem.NodeType.FIELD)
        .filter(item -> item.getParentNodeItem() != parent)
        .findFirst()
        .orElse(null);
    assertNotNull(nestedField, "Should have a nested field under child-assembly");
    assertEquals(INodeItem.NodeType.ASSEMBLY, nestedField.getParentNodeItem().getNodeType(),
        "Nested field's parent should be the child assembly");

    // Test descendant-or-self axis
    List<? extends INodeItem> descendantsOrSelf = parent.descendantOrSelf().collect(Collectors.toList());
    assertEquals(5, descendantsOrSelf.size(), "Should have 5 items: self + 4 descendants");
    assertEquals(parent, descendantsOrSelf.get(0), "First item should be self");
  }

  /**
   * Test flag access on field and assembly nodes.
   */
  @Test
  void testFlagAccessOnFieldsAndAssemblies() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    // Create field definition separately so we can configure flags on it properly
    IFieldBuilder fieldBuilder = mocking.field()
        .namespace(TEST_NAMESPACE)
        .name("test-field")
        .source(source)
        .flags(List.of(
            mocking.flag().namespace(TEST_NAMESPACE).name("field-flag").source(source)));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("test-assembly")
            .rootName("test-assembly")
            .source(source)
            .flags(List.of(
                mocking.flag().namespace(TEST_NAMESPACE).name("assembly-flag").source(source)))
            .modelInstances(List.of(fieldBuilder)))
        .toModule();

    IAssemblyDefinition assemblyDef = module.getRootAssemblyDefinitions().iterator().next();
    StaticContext staticContext = StaticContext.instance();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(assemblyDef, staticContext);

    // Add assembly flag
    List<IFlagInstance> assemblyFlags = new ArrayList<>(assemblyDef.getFlagInstances());
    assertEquals(1, assemblyFlags.size(), "Assembly should have 1 flag");
    assembly.newFlag(assemblyFlags.get(0), IStringItem.valueOf("assembly-flag-value"));

    // Add field with flag
    IFieldInstance fieldInstance = assemblyDef.getFieldInstances().iterator().next();
    IDMFieldNodeItem field = assembly.newField(fieldInstance, IStringItem.valueOf("field-value"));

    List<IFlagInstance> fieldFlags = new ArrayList<>(fieldInstance.getDefinition().getFlagInstances());
    assertEquals(1, fieldFlags.size(), "Field should have 1 flag");
    field.newFlag(fieldFlags.get(0), IStringItem.valueOf("field-flag-value"));

    // Test flag access on assembly
    List<? extends IFlagNodeItem> assemblyFlagItems = assembly.flags().collect(Collectors.toList());
    assertEquals(1, assemblyFlagItems.size(), "Assembly should have 1 flag item");
    assertEquals("assembly-flag-value", assemblyFlagItems.get(0).toAtomicItem().asString(),
        "Assembly flag value should match");

    // Test flag access on field
    List<? extends IFlagNodeItem> fieldFlagItems = field.flags().collect(Collectors.toList());
    assertEquals(1, fieldFlagItems.size(), "Field should have 1 flag item");
    assertEquals("field-flag-value", fieldFlagItems.get(0).toAtomicItem().asString(),
        "Field flag value should match");
  }

  /**
   * Test node type identification using getNodeType().
   */
  @Test
  void testNodeTypeIdentification() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("test-assembly")
            .rootName("test-assembly")
            .flags(List.of(
                mocking.flag().namespace(TEST_NAMESPACE).name("test-flag")))
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("test-field"))))
        .toModule();

    IAssemblyDefinition assemblyDef = module.getRootAssemblyDefinitions().iterator().next();
    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(DOCUMENT_URI, assemblyDef);
    IRootAssemblyNodeItem assembly = document.getRootAssemblyNodeItem();

    // Add flag
    List<IFlagInstance> flagInstances = new ArrayList<>(assemblyDef.getFlagInstances());
    ((IDMAssemblyNodeItem) assembly).newFlag(flagInstances.get(0), IStringItem.valueOf("flag-value"));

    // Add field
    IFieldInstance fieldInstance = assemblyDef.getFieldInstances().iterator().next();
    IDMFieldNodeItem field = ((IDMAssemblyNodeItem) assembly).newField(fieldInstance,
        IStringItem.valueOf("field-value"));

    // Get flag
    IFlagNodeItem flag = assembly.getFlagByName(IEnhancedQName.of(TEST_NAMESPACE, "test-flag"));

    // Test node types
    assertEquals(INodeItem.NodeType.DOCUMENT, document.getNodeType(), "Should identify as document");
    assertEquals(INodeItem.NodeType.ASSEMBLY, assembly.getNodeType(), "Should identify as assembly");
    assertEquals(INodeItem.NodeType.FIELD, field.getNodeType(), "Should identify as field");
    assertEquals(INodeItem.NodeType.FLAG, flag.getNodeType(), "Should identify as flag");
  }

  /**
   * Test Metapath generation from nodes using getMetapath().
   */
  @Test
  void testMetapathGeneration() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("root-assembly")
            .rootName("root-assembly")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("child-field"))))
        .toModule();

    IAssemblyDefinition assemblyDef = module.getRootAssemblyDefinitions().iterator().next();
    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(DOCUMENT_URI, assemblyDef);
    IRootAssemblyNodeItem assembly = document.getRootAssemblyNodeItem();

    // Add field
    IFieldInstance fieldInstance = assemblyDef.getFieldInstances().iterator().next();
    IDMFieldNodeItem field = ((IDMAssemblyNodeItem) assembly).newField(fieldInstance,
        IStringItem.valueOf("field-value"));

    // Test Metapath generation
    // Note: Document nodes may return empty string as they are the root
    String documentPath = document.getMetapath();
    assertNotNull(documentPath, "Document should have a metapath (even if empty)");

    String assemblyPath = assembly.getMetapath();
    assertNotNull(assemblyPath, "Assembly should have a metapath");
    // Root assembly path should reference the assembly name
    assertTrue(assemblyPath.length() > 0, "Root assembly metapath should not be empty");

    String fieldPath = field.getMetapath();
    assertNotNull(fieldPath, "Field should have a metapath");
    assertTrue(fieldPath.length() > 0, "Field metapath should not be empty");

    // Field path should be longer than or equal to assembly path
    // (it includes navigation from assembly to field)
    assertTrue(fieldPath.length() >= assemblyPath.length(),
        "Field metapath should be at least as long as assembly metapath");
  }

  /**
   * Test model items by name lookup.
   */
  @Test
  void testModelItemsByName() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("test-assembly")
            .rootName("test-assembly")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("field1"),
                mocking.field().namespace(TEST_NAMESPACE).name("field2"))))
        .toModule();

    IAssemblyDefinition assemblyDef = module.getRootAssemblyDefinitions().iterator().next();
    StaticContext staticContext = StaticContext.instance();
    IDMAssemblyNodeItem assembly = IDMAssemblyNodeItem.newInstance(assemblyDef, staticContext);

    // Add fields - look up by name to ensure correct mapping
    IFieldInstance field1Instance = assemblyDef.getModelInstances().stream()
        .filter(instance -> instance instanceof IFieldInstance)
        .map(instance -> (IFieldInstance) instance)
        .filter(instance -> "field1".equals(instance.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("field1 instance not found"));
    IFieldInstance field2Instance = assemblyDef.getModelInstances().stream()
        .filter(instance -> instance instanceof IFieldInstance)
        .map(instance -> (IFieldInstance) instance)
        .filter(instance -> "field2".equals(instance.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("field2 instance not found"));

    assembly.newField(field1Instance, IStringItem.valueOf("value1"));
    assembly.newField(field2Instance, IStringItem.valueOf("value2"));

    // Test lookup by name
    List<? extends IModelNodeItem<?, ?>> field1Items = assembly
        .getModelItemsByName(IEnhancedQName.of(TEST_NAMESPACE, "field1"));
    assertEquals(1, field1Items.size(), "Should find 1 item for field1");
    assertEquals("value1", field1Items.get(0).toAtomicItem().asString(), "Field1 value should match");

    List<? extends IModelNodeItem<?, ?>> field2Items = assembly
        .getModelItemsByName(IEnhancedQName.of(TEST_NAMESPACE, "field2"));
    assertEquals(1, field2Items.size(), "Should find 1 item for field2");
    assertEquals("value2", field2Items.get(0).toAtomicItem().asString(), "Field2 value should match");

    // Test lookup for non-existent name
    List<? extends IModelNodeItem<?, ?>> nonExistentItems = assembly
        .getModelItemsByName(IEnhancedQName.of(TEST_NAMESPACE, "non-existent"));
    assertTrue(nonExistentItems.isEmpty(), "Should return empty list for non-existent name");
  }

  /**
   * Test complex assembly hierarchy with multiple levels.
   */
  @Test
  void testComplexAssemblyHierarchy() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("level1")
            .rootName("level1")
            .modelInstances(List.of(
                mocking.assemblyRef("level2"))))
        .assembly(mocking.assembly()
            .name("level2")
            .modelInstances(List.of(
                mocking.assemblyRef("level3"))))
        .assembly(mocking.assembly()
            .name("level3")
            .modelInstances(List.of(
                mocking.field().namespace(TEST_NAMESPACE).name("leaf-field"))))
        .toModule();

    IAssemblyDefinition level1Def = module.getRootAssemblyDefinitions().iterator().next();
    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(DOCUMENT_URI, level1Def);
    IRootAssemblyNodeItem level1 = document.getRootAssemblyNodeItem();

    // Build level 2
    IAssemblyInstance level2Instance = level1Def.getAssemblyInstances().iterator().next();
    IDMAssemblyNodeItem level2 = ((IDMAssemblyNodeItem) level1).newAssembly(level2Instance);

    // Build level 3
    IAssemblyInstance level3Instance = level2.getDefinition().getAssemblyInstances().iterator().next();
    IDMAssemblyNodeItem level3 = level2.newAssembly(level3Instance);

    // Add leaf field
    IFieldInstance leafFieldInstance = level3.getDefinition().getFieldInstances().iterator().next();
    IDMFieldNodeItem leafField = level3.newField(leafFieldInstance, IStringItem.valueOf("leaf-value"));

    // Test navigation from leaf to root
    assertEquals(level3, leafField.getParentNodeItem(), "Leaf field parent should be level3");
    assertEquals(level2, level3.getParentNodeItem(), "Level3 parent should be level2");
    assertEquals(level1, level2.getParentNodeItem(), "Level2 parent should be level1");
    assertEquals(document, level1.getParentNodeItem(), "Level1 parent should be document");

    // Test descendant axis from root
    List<? extends IModelNodeItem<?, ?>> allDescendants = level1.descendant().collect(Collectors.toList());
    assertEquals(3, allDescendants.size(), "Should have 3 descendants from root: level2, level3, leaf-field");

    // Test ancestor axis from leaf
    List<? extends INodeItem> allAncestors = leafField.ancestor().collect(Collectors.toList());
    assertEquals(4, allAncestors.size(), "Should have 4 ancestors from leaf: level3, level2, level1, document");
  }

  /**
   * Test module node item creation and traversal.
   */
  @Test
  void testModuleNodeItem() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();
    ISource source = ISource.externalSource(URI.create(TEST_NAMESPACE));

    IModule module = IModuleBuilder.builder()
        .namespace(TEST_NAMESPACE)
        .shortName("traversal-test")
        .version("1.0.0")
        .source(source)
        .assembly(mocking.assembly()
            .name("test-assembly")
            .rootName("test-assembly"))
        .toModule();

    // Create module node item
    IModuleNodeItem moduleNode = INodeItemFactory.instance().newModuleNodeItem(module);

    assertNotNull(moduleNode, "Module node item should be created");
    assertEquals(INodeItem.NodeType.MODULE, moduleNode.getNodeType(), "Should be a module node");
    assertNull(moduleNode.getParentNodeItem(), "Module node should have no parent");
    assertEquals(module, moduleNode.getModule(), "Module should match");

    // Module nodes should have model items for exported definitions
    List<? extends IModelNodeItem<?, ?>> modelItems = moduleNode.modelItems().collect(Collectors.toList());
    assertTrue(modelItems.size() > 0, "Module should have model items for exported definitions");
  }
}
