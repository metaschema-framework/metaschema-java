/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyInstanceGroupedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link JsonPointerFormatter}.
 */
class JsonPointerFormatterTest {

  private static final String TEST_NS = "http://example.com/test";

  private JsonPointerFormatter formatter;
  private MockNodeItemFactory mockFactory;

  @BeforeEach
  void setUp() {
    formatter = new JsonPointerFormatter();
    mockFactory = new MockNodeItemFactory();
  }

  @Nested
  @DisplayName("Document Node Formatting")
  class DocumentNodeTests {

    @Test
    @DisplayName("formatDocument returns empty string")
    void testFormatDocumentReturnsEmptyString() {
      IDocumentNodeItem document = mock(IDocumentNodeItem.class);

      String result = formatter.formatDocument(document);

      assertEquals("", result);
    }
  }

  @Nested
  @DisplayName("Module Node Formatting")
  class ModuleNodeTests {

    @Test
    @DisplayName("formatMetaschema returns empty string")
    void testFormatMetaschemaReturnsEmptyString() {
      IModuleNodeItem module = mock(IModuleNodeItem.class);

      String result = formatter.formatMetaschema(module);

      assertEquals("", result);
    }
  }

  @Nested
  @DisplayName("Root Assembly Formatting")
  class RootAssemblyTests {

    @Test
    @DisplayName("formatRootAssembly returns JSON name")
    void testFormatRootAssemblyReturnsJsonName() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "catalog");
      IRootAssemblyNodeItem root = mock(IRootAssemblyNodeItem.class);
      IAssemblyDefinition definition = mock(IAssemblyDefinition.class);

      doReturn(qname).when(root).getQName();
      doReturn(definition).when(root).getDefinition();
      doReturn("catalog").when(definition).getJsonName();

      String result = formatter.formatRootAssembly(root);

      assertEquals("catalog", result);
    }

    @Test
    @DisplayName("formatRootAssembly uses effective name when no JSON name")
    void testFormatRootAssemblyUsesEffectiveName() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "catalog");
      IRootAssemblyNodeItem root = mock(IRootAssemblyNodeItem.class);
      IAssemblyDefinition definition = mock(IAssemblyDefinition.class);

      doReturn(qname).when(root).getQName();
      doReturn(definition).when(root).getDefinition();
      doReturn("catalog").when(definition).getJsonName();

      String result = formatter.formatRootAssembly(root);

      assertEquals("catalog", result);
    }
  }

  @Nested
  @DisplayName("Assembly Formatting - NONE Grouping")
  class AssemblyNoneGroupingTests {

    @Test
    @DisplayName("formatAssembly NONE returns JSON name only")
    void testFormatAssemblyNone() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.NONE).when(instance).getJsonGroupAsBehavior();
      doReturn("control").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("control", result);
    }
  }

  @Nested
  @DisplayName("Assembly Formatting - LIST Grouping")
  class AssemblyListGroupingTests {

    @Test
    @DisplayName("formatAssembly LIST returns JSON name with 0-based index")
    void testFormatAssemblyList() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition(); // 1-based position
      doReturn(JsonGroupAsBehavior.LIST).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      // Should be 0-based index
      assertEquals("controls/0", result);
    }

    @Test
    @DisplayName("formatAssembly LIST with position 3 returns index 2")
    void testFormatAssemblyListPosition3() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(3).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.LIST).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("controls/2", result);
    }
  }

  @Nested
  @DisplayName("Assembly Formatting - SINGLETON_OR_LIST Grouping")
  class AssemblySingletonOrListTests {

    @Test
    @DisplayName("formatAssembly SINGLETON_OR_LIST with single sibling returns name only")
    void testFormatAssemblySingletonOrListSingle() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);
      IAssemblyNodeItem parent = mock(IAssemblyNodeItem.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.SINGLETON_OR_LIST).when(instance).getJsonGroupAsBehavior();
      doReturn("control").when(instance).getJsonName();
      doReturn(parent).when(assembly).getParentContentNodeItem();
      // Single sibling - return list with just this item
      doReturn(List.of(assembly)).when(parent).getModelItemsByName(qname);

      String result = formatter.formatAssembly(assembly);

      assertEquals("control", result);
    }

    @Test
    @DisplayName("formatAssembly SINGLETON_OR_LIST with multiple siblings returns name with index")
    void testFormatAssemblySingletonOrListMultiple() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);
      IAssemblyNodeItem parent = mock(IAssemblyNodeItem.class);
      IAssemblyNodeItem sibling = mock(IAssemblyNodeItem.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(2).when(assembly).getPosition(); // Second item
      doReturn(JsonGroupAsBehavior.SINGLETON_OR_LIST).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();
      doReturn(parent).when(assembly).getParentContentNodeItem();
      // Multiple siblings
      doReturn(List.of(sibling, assembly)).when(parent).getModelItemsByName(qname);

      String result = formatter.formatAssembly(assembly);

      // Should use 0-based index
      assertEquals("controls/1", result);
    }
  }

  @Nested
  @DisplayName("Assembly Formatting - KEYED Grouping")
  class AssemblyKeyedGroupingTests {

    @Test
    @DisplayName("formatAssembly KEYED returns JSON name with key value")
    void testFormatAssemblyKeyed() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName keyFlagQname = IEnhancedQName.of(TEST_NS, "id");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);
      IFlagInstance keyFlagInstance = mock(IFlagInstance.class);
      IFlagNodeItem keyFlagItem = mock(IFlagNodeItem.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.KEYED).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();
      doReturn(keyFlagInstance).when(instance).getEffectiveJsonKey();
      doReturn(keyFlagQname).when(keyFlagInstance).getQName();
      doReturn(keyFlagItem).when(assembly).getFlagByName(keyFlagQname);
      doReturn(IStringItem.valueOf("ac-1")).when(keyFlagItem).toAtomicItem();

      String result = formatter.formatAssembly(assembly);

      assertEquals("controls/ac-1", result);
    }

    @Test
    @DisplayName("formatAssembly KEYED with no key flag falls back to index")
    void testFormatAssemblyKeyedNoKeyFlag() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.KEYED).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();
      doReturn(null).when(instance).getEffectiveJsonKey(); // No key flag configured

      String result = formatter.formatAssembly(assembly);

      // Falls back to 0-based index
      assertEquals("controls/0", result);
    }
  }

  @Nested
  @DisplayName("Assembly without Instance")
  class AssemblyWithoutInstanceTests {

    @Test
    @DisplayName("formatAssembly without instance returns local name")
    void testFormatAssemblyWithoutInstance() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(null).when(assembly).getInstance();

      String result = formatter.formatAssembly(assembly);

      assertEquals("control", result);
    }
  }

  @Nested
  @DisplayName("Grouped Assembly Instance Formatting")
  class GroupedAssemblyInstanceTests {

    @Test
    @DisplayName("formatAssembly(IAssemblyInstanceGroupedNodeItem) LIST")
    void testFormatGroupedAssemblyInstanceList() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyInstanceGroupedNodeItem assembly = mock(IAssemblyInstanceGroupedNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(2).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.LIST).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("controls/1", result);
    }
  }

  @Nested
  @DisplayName("Field Formatting")
  class FieldTests {

    @Test
    @DisplayName("formatField NONE returns JSON name only")
    void testFormatFieldNone() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "title");
      IFieldNodeItem field = mock(IFieldNodeItem.class);
      IFieldInstance instance = mock(IFieldInstance.class);

      doReturn(qname).when(field).getQName();
      doReturn(instance).when(field).getInstance();
      doReturn(1).when(field).getPosition();
      doReturn(JsonGroupAsBehavior.NONE).when(instance).getJsonGroupAsBehavior();
      doReturn("title").when(instance).getJsonName();

      String result = formatter.formatField(field);

      assertEquals("title", result);
    }

    @Test
    @DisplayName("formatField LIST returns JSON name with 0-based index")
    void testFormatFieldList() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "prop");
      IFieldNodeItem field = mock(IFieldNodeItem.class);
      IFieldInstance instance = mock(IFieldInstance.class);

      doReturn(qname).when(field).getQName();
      doReturn(instance).when(field).getInstance();
      doReturn(2).when(field).getPosition();
      doReturn(JsonGroupAsBehavior.LIST).when(instance).getJsonGroupAsBehavior();
      doReturn("props").when(instance).getJsonName();

      String result = formatter.formatField(field);

      assertEquals("props/1", result);
    }
  }

  @Nested
  @DisplayName("Flag Formatting")
  class FlagTests {

    @Test
    @DisplayName("formatFlag returns JSON name without @ prefix")
    void testFormatFlagReturnsJsonNameNoAtPrefix() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "id");
      IFlagNodeItem flag = mock(IFlagNodeItem.class);

      doReturn(qname).when(flag).getQName();

      String result = formatter.formatFlag(flag);

      // JSON Pointer does not use @ for attributes
      assertEquals("id", result);
    }
  }

  @Nested
  @DisplayName("RFC 6901 Escaping")
  class Rfc6901EscapingTests {

    @Test
    @DisplayName("Escapes tilde character as ~0")
    void testEscapesTilde() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "my~name");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.NONE).when(instance).getJsonGroupAsBehavior();
      doReturn("my~name").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("my~0name", result);
    }

    @Test
    @DisplayName("Escapes forward slash as ~1")
    void testEscapesForwardSlash() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "my/name");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.NONE).when(instance).getJsonGroupAsBehavior();
      doReturn("my/name").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("my~1name", result);
    }

    @Test
    @DisplayName("Escapes both tilde and slash correctly (order matters)")
    void testEscapesBothTildeAndSlash() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "a~b/c");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.NONE).when(instance).getJsonGroupAsBehavior();
      doReturn("a~b/c").when(instance).getJsonName();

      String result = formatter.formatAssembly(assembly);

      // ~ must be escaped first, then /
      assertEquals("a~0b~1c", result);
    }

    @Test
    @DisplayName("Escapes KEYED key value")
    void testEscapesKeyedKeyValue() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName keyFlagQname = IEnhancedQName.of(TEST_NS, "id");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);
      IFlagInstance keyFlagInstance = mock(IFlagInstance.class);
      IFlagNodeItem keyFlagItem = mock(IFlagNodeItem.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(JsonGroupAsBehavior.KEYED).when(instance).getJsonGroupAsBehavior();
      doReturn("controls").when(instance).getJsonName();
      doReturn(keyFlagInstance).when(instance).getEffectiveJsonKey();
      doReturn(keyFlagQname).when(keyFlagInstance).getQName();
      doReturn(keyFlagItem).when(assembly).getFlagByName(keyFlagQname);
      doReturn(IStringItem.valueOf("ac~1/test")).when(keyFlagItem).toAtomicItem();

      String result = formatter.formatAssembly(assembly);

      // Key value should also be escaped:
      // ac~1/test -> ac~01/test (~ -> ~0) -> ac~01~1test (/ -> ~1)
      assertEquals("controls/ac~01~1test", result);
    }
  }

  @Nested
  @DisplayName("Full Path Formatting")
  class FullPathTests {

    @Test
    @DisplayName("format produces complete JSON Pointer path")
    void testFormatCompletePath() {
      IEnhancedQName rootQname = IEnhancedQName.of(TEST_NS, "catalog");
      IEnhancedQName assemblyQname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName flagQname = IEnhancedQName.of(TEST_NS, "id");

      IFlagNodeItem flag = mockFactory.flag(flagQname, IStringItem.valueOf("ac-1"));
      IAssemblyNodeItem assembly = mockFactory.assembly(
          assemblyQname,
          CollectionUtil.singletonList(flag),
          CollectionUtil.emptyList());

      // Set up instance for assembly
      IAssemblyInstance assemblyInstance = mock(IAssemblyInstance.class);
      doReturn(assemblyInstance).when(assembly).getInstance();
      doReturn(JsonGroupAsBehavior.LIST).when(assemblyInstance).getJsonGroupAsBehavior();
      doReturn("controls").when(assemblyInstance).getJsonName();

      // Create document - this sets up the root assembly with a definition
      IDocumentNodeItem document = mockFactory.document(
          URI.create("https://example.com/catalog.json"),
          rootQname,
          CollectionUtil.emptyList(),
          CollectionUtil.singletonList(assembly));

      // Get the root and set up JSON name on its definition
      IRootAssemblyNodeItem root = document.getRootAssemblyNodeItem();
      IAssemblyDefinition rootDef = root.getDefinition();
      doReturn("catalog").when(rootDef).getJsonName();

      String result = formatter.format(flag);

      // Expected: /catalog/controls/0/id
      assertEquals("/catalog/controls/0/id", result);
    }
  }
}
