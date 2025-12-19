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
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testsupport.mocking.MockNodeItemFactory;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

/**
 * Unit tests for {@link XPathFormatter}.
 */
class XPathFormatterTest {

  private static final String TEST_NS = "http://example.com/test";
  private static final String EMPTY_NS = "";

  private XPathFormatter formatter;
  private MockNodeItemFactory mockFactory;

  @BeforeEach
  void setUp() {
    formatter = new XPathFormatter();
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
    @DisplayName("formatRootAssembly with namespace returns EQName format")
    void testFormatRootAssemblyWithNamespace() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "catalog");
      IRootAssemblyNodeItem root = mock(IRootAssemblyNodeItem.class);

      doReturn(qname).when(root).getQName();

      String result = formatter.formatRootAssembly(root);

      assertEquals("Q{" + TEST_NS + "}catalog", result);
    }

    @Test
    @DisplayName("formatRootAssembly without namespace returns local name only")
    void testFormatRootAssemblyWithoutNamespace() {
      IEnhancedQName qname = IEnhancedQName.of(EMPTY_NS, "catalog");
      IRootAssemblyNodeItem root = mock(IRootAssemblyNodeItem.class);

      doReturn(qname).when(root).getQName();

      String result = formatter.formatRootAssembly(root);

      assertEquals("catalog", result);
    }
  }

  @Nested
  @DisplayName("Assembly Formatting")
  class AssemblyTests {

    @Test
    @DisplayName("formatAssembly UNGROUPED returns EQName with position")
    void testFormatAssemblyUngrouped() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(2).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}control[2]", result);
    }

    @Test
    @DisplayName("formatAssembly GROUPED returns wrapper + EQName with position")
    void testFormatAssemblyGrouped() {
      IEnhancedQName elementQname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName wrapperQname = IEnhancedQName.of(TEST_NS, "controls");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(elementQname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.GROUPED).when(instance).getXmlGroupAsBehavior();
      doReturn(wrapperQname).when(instance).getEffectiveXmlGroupAsQName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}controls[1]/Q{" + TEST_NS + "}control[1]", result);
    }

    @Test
    @DisplayName("formatAssembly without instance returns EQName with position")
    void testFormatAssemblyWithoutInstance() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(null).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}control[1]", result);
    }

    @Test
    @DisplayName("formatAssembly without namespace returns local name with position")
    void testFormatAssemblyWithoutNamespace() {
      IEnhancedQName qname = IEnhancedQName.of(EMPTY_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(3).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

      String result = formatter.formatAssembly(assembly);

      assertEquals("control[3]", result);
    }

    @Test
    @DisplayName("formatAssembly GROUPED with high position keeps wrapper at [1]")
    void testFormatAssemblyGroupedWrapperAlwaysPositionOne() {
      IEnhancedQName elementQname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName wrapperQname = IEnhancedQName.of(TEST_NS, "controls");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(elementQname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(5).when(assembly).getPosition(); // 5th element in the group
      doReturn(XmlGroupAsBehavior.GROUPED).when(instance).getXmlGroupAsBehavior();
      doReturn(wrapperQname).when(instance).getEffectiveXmlGroupAsQName();

      String result = formatter.formatAssembly(assembly);

      // Wrapper is always [1], element position varies
      assertEquals("Q{" + TEST_NS + "}controls[1]/Q{" + TEST_NS + "}control[5]", result);
    }

    @Test
    @DisplayName("formatAssembly GROUPED with null wrapper QName falls back to element only")
    void testFormatAssemblyGroupedNullWrapper() {
      IEnhancedQName elementQname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(elementQname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.GROUPED).when(instance).getXmlGroupAsBehavior();
      doReturn(null).when(instance).getEffectiveXmlGroupAsQName(); // null wrapper

      String result = formatter.formatAssembly(assembly);

      // Should gracefully handle null wrapper
      assertEquals("Q{" + TEST_NS + "}control[1]", result);
    }
  }

  @Nested
  @DisplayName("Grouped Assembly Instance Formatting")
  class GroupedAssemblyInstanceTests {

    @Test
    @DisplayName("formatAssembly(IAssemblyInstanceGroupedNodeItem) UNGROUPED")
    void testFormatGroupedAssemblyInstanceUngrouped() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");
      IAssemblyInstanceGroupedNodeItem assembly = mock(IAssemblyInstanceGroupedNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(2).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}control[2]", result);
    }

    @Test
    @DisplayName("formatAssembly(IAssemblyInstanceGroupedNodeItem) GROUPED")
    void testFormatGroupedAssemblyInstanceGrouped() {
      IEnhancedQName elementQname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName wrapperQname = IEnhancedQName.of(TEST_NS, "controls");
      IAssemblyInstanceGroupedNodeItem assembly = mock(IAssemblyInstanceGroupedNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(elementQname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(3).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.GROUPED).when(instance).getXmlGroupAsBehavior();
      doReturn(wrapperQname).when(instance).getEffectiveXmlGroupAsQName();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}controls[1]/Q{" + TEST_NS + "}control[3]", result);
    }
  }

  @Nested
  @DisplayName("Field Formatting")
  class FieldTests {

    @Test
    @DisplayName("formatField UNGROUPED returns EQName with position")
    void testFormatFieldUngrouped() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "title");
      IFieldNodeItem field = mock(IFieldNodeItem.class);
      IFieldInstance instance = mock(IFieldInstance.class);

      doReturn(qname).when(field).getQName();
      doReturn(instance).when(field).getInstance();
      doReturn(1).when(field).getPosition();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

      String result = formatter.formatField(field);

      assertEquals("Q{" + TEST_NS + "}title[1]", result);
    }

    @Test
    @DisplayName("formatField GROUPED returns wrapper + EQName with position")
    void testFormatFieldGrouped() {
      IEnhancedQName elementQname = IEnhancedQName.of(TEST_NS, "prop");
      IEnhancedQName wrapperQname = IEnhancedQName.of(TEST_NS, "props");
      IFieldNodeItem field = mock(IFieldNodeItem.class);
      IFieldInstance instance = mock(IFieldInstance.class);

      doReturn(elementQname).when(field).getQName();
      doReturn(instance).when(field).getInstance();
      doReturn(2).when(field).getPosition();
      doReturn(XmlGroupAsBehavior.GROUPED).when(instance).getXmlGroupAsBehavior();
      doReturn(wrapperQname).when(instance).getEffectiveXmlGroupAsQName();

      String result = formatter.formatField(field);

      assertEquals("Q{" + TEST_NS + "}props[1]/Q{" + TEST_NS + "}prop[2]", result);
    }

    @Test
    @DisplayName("formatField without instance returns EQName with position")
    void testFormatFieldWithoutInstance() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "value");
      IFieldNodeItem field = mock(IFieldNodeItem.class);

      doReturn(qname).when(field).getQName();
      doReturn(null).when(field).getInstance();
      doReturn(1).when(field).getPosition();

      String result = formatter.formatField(field);

      assertEquals("Q{" + TEST_NS + "}value[1]", result);
    }
  }

  @Nested
  @DisplayName("Flag Formatting")
  class FlagTests {

    @Test
    @DisplayName("formatFlag with namespace returns @EQName")
    void testFormatFlagWithNamespace() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "id");
      IFlagNodeItem flag = mock(IFlagNodeItem.class);

      doReturn(qname).when(flag).getQName();

      String result = formatter.formatFlag(flag);

      assertEquals("@Q{" + TEST_NS + "}id", result);
    }

    @Test
    @DisplayName("formatFlag without namespace returns @localname")
    void testFormatFlagWithoutNamespace() {
      IEnhancedQName qname = IEnhancedQName.of(EMPTY_NS, "id");
      IFlagNodeItem flag = mock(IFlagNodeItem.class);

      doReturn(qname).when(flag).getQName();

      String result = formatter.formatFlag(flag);

      assertEquals("@id", result);
    }
  }

  @Nested
  @DisplayName("Special Characters and Edge Cases")
  class SpecialCharacterTests {

    @Test
    @DisplayName("formatAssembly with hyphenated name")
    void testFormatAssemblyHyphenatedName() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "my-control-element");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}my-control-element[1]", result);
    }

    @Test
    @DisplayName("formatAssembly with underscore name")
    void testFormatAssemblyUnderscoreName() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "my_control_element");
      IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
      IAssemblyInstance instance = mock(IAssemblyInstance.class);

      doReturn(qname).when(assembly).getQName();
      doReturn(instance).when(assembly).getInstance();
      doReturn(1).when(assembly).getPosition();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

      String result = formatter.formatAssembly(assembly);

      assertEquals("Q{" + TEST_NS + "}my_control_element[1]", result);
    }

    @Test
    @DisplayName("formatFlag with numeric suffix in name")
    void testFormatFlagNumericSuffix() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "id2");
      IFlagNodeItem flag = mock(IFlagNodeItem.class);

      doReturn(qname).when(flag).getQName();

      String result = formatter.formatFlag(flag);

      assertEquals("@Q{" + TEST_NS + "}id2", result);
    }
  }

  @Nested
  @DisplayName("Multiple Siblings Tests")
  class MultipleSiblingsTests {

    @Test
    @DisplayName("Multiple assemblies at different positions")
    void testMultipleAssembliesAtDifferentPositions() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "control");

      // Create three assemblies with positions 1, 2, 3
      for (int position = 1; position <= 3; position++) {
        IAssemblyNodeItem assembly = mock(IAssemblyNodeItem.class);
        IAssemblyInstance instance = mock(IAssemblyInstance.class);

        doReturn(qname).when(assembly).getQName();
        doReturn(instance).when(assembly).getInstance();
        doReturn(position).when(assembly).getPosition();
        doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

        String result = formatter.formatAssembly(assembly);

        assertEquals("Q{" + TEST_NS + "}control[" + position + "]", result);
      }
    }

    @Test
    @DisplayName("Multiple fields at different positions")
    void testMultipleFieldsAtDifferentPositions() {
      IEnhancedQName qname = IEnhancedQName.of(TEST_NS, "prop");

      // Create three fields with positions 1, 2, 3
      for (int position = 1; position <= 3; position++) {
        IFieldNodeItem field = mock(IFieldNodeItem.class);
        IFieldInstance instance = mock(IFieldInstance.class);

        doReturn(qname).when(field).getQName();
        doReturn(instance).when(field).getInstance();
        doReturn(position).when(field).getPosition();
        doReturn(XmlGroupAsBehavior.UNGROUPED).when(instance).getXmlGroupAsBehavior();

        String result = formatter.formatField(field);

        assertEquals("Q{" + TEST_NS + "}prop[" + position + "]", result);
      }
    }
  }

  @Nested
  @DisplayName("Full Path Formatting")
  class FullPathTests {

    @Test
    @DisplayName("format produces complete XPath from document to flag")
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
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(assemblyInstance).getXmlGroupAsBehavior();

      IDocumentNodeItem document = mockFactory.document(
          URI.create("https://example.com/catalog.xml"),
          rootQname,
          CollectionUtil.emptyList(),
          CollectionUtil.singletonList(assembly));

      // Format from flag - the path should traverse up through parent nodes
      String result = formatter.format(flag);

      // Expected: /Q{ns}catalog/Q{ns}control[1]/@Q{ns}id
      assertEquals("/Q{" + TEST_NS + "}catalog/Q{" + TEST_NS + "}control[1]/@Q{" + TEST_NS + "}id", result);
    }

    @Test
    @DisplayName("format with nested assemblies produces correct path")
    void testFormatNestedAssemblies() {
      IEnhancedQName rootQname = IEnhancedQName.of(TEST_NS, "catalog");
      IEnhancedQName groupQname = IEnhancedQName.of(TEST_NS, "group");
      IEnhancedQName controlQname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName flagQname = IEnhancedQName.of(TEST_NS, "id");

      IFlagNodeItem flag = mockFactory.flag(flagQname, IStringItem.valueOf("ac-1"));

      IAssemblyNodeItem control = mockFactory.assembly(
          controlQname,
          CollectionUtil.singletonList(flag),
          CollectionUtil.emptyList());
      IAssemblyInstance controlInstance = mock(IAssemblyInstance.class);
      doReturn(controlInstance).when(control).getInstance();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(controlInstance).getXmlGroupAsBehavior();

      IAssemblyNodeItem group = mockFactory.assembly(
          groupQname,
          CollectionUtil.emptyList(),
          CollectionUtil.singletonList(control));
      IAssemblyInstance groupInstance = mock(IAssemblyInstance.class);
      doReturn(groupInstance).when(group).getInstance();
      doReturn(XmlGroupAsBehavior.UNGROUPED).when(groupInstance).getXmlGroupAsBehavior();

      IDocumentNodeItem document = mockFactory.document(
          URI.create("https://example.com/catalog.xml"),
          rootQname,
          CollectionUtil.emptyList(),
          CollectionUtil.singletonList(group));

      String result = formatter.format(flag);

      assertEquals(
          "/Q{" + TEST_NS + "}catalog/Q{" + TEST_NS + "}group[1]/Q{" + TEST_NS + "}control[1]/@Q{" + TEST_NS + "}id",
          result);
    }

    @Test
    @DisplayName("format with GROUPED assembly in path")
    void testFormatWithGroupedAssemblyInPath() {
      IEnhancedQName rootQname = IEnhancedQName.of(TEST_NS, "catalog");
      IEnhancedQName controlQname = IEnhancedQName.of(TEST_NS, "control");
      IEnhancedQName wrapperQname = IEnhancedQName.of(TEST_NS, "controls");
      IEnhancedQName flagQname = IEnhancedQName.of(TEST_NS, "id");

      IFlagNodeItem flag = mockFactory.flag(flagQname, IStringItem.valueOf("ac-1"));

      IAssemblyNodeItem control = mockFactory.assembly(
          controlQname,
          CollectionUtil.singletonList(flag),
          CollectionUtil.emptyList());
      IAssemblyInstance controlInstance = mock(IAssemblyInstance.class);
      doReturn(controlInstance).when(control).getInstance();
      doReturn(XmlGroupAsBehavior.GROUPED).when(controlInstance).getXmlGroupAsBehavior();
      doReturn(wrapperQname).when(controlInstance).getEffectiveXmlGroupAsQName();

      IDocumentNodeItem document = mockFactory.document(
          URI.create("https://example.com/catalog.xml"),
          rootQname,
          CollectionUtil.emptyList(),
          CollectionUtil.singletonList(control));

      String result = formatter.format(flag);

      assertEquals(
          "/Q{" + TEST_NS + "}catalog/Q{" + TEST_NS + "}controls[1]/Q{" + TEST_NS + "}control[1]/@Q{" + TEST_NS + "}id",
          result);
    }

    @Test
    @DisplayName("format document node produces single slash")
    void testFormatDocumentNode() {
      IEnhancedQName rootQname = IEnhancedQName.of(TEST_NS, "catalog");

      IDocumentNodeItem document = mockFactory.document(
          URI.create("https://example.com/catalog.xml"),
          rootQname,
          CollectionUtil.emptyList(),
          CollectionUtil.emptyList());

      String result = formatter.format(document);

      // Document alone should produce empty string (the leading "/" comes from
      // joining)
      assertEquals("", result);
    }
  }
}
