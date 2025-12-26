/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.ModelType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

class DefaultBindingConfigurationTest {
  private static final URI METASCHEMA_LOCATION
      = new File("src/test/resources/metaschema/metaschema.xml").getAbsoluteFile().toURI();
  private static final String DEFINITION_NAME = "grandchild";
  private static final ModelType DEFINITION_MODEL_TYPE = ModelType.ASSEMBLY;
  private static final String DEFINITION__CLASS_NAME = "TheChild";

  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };
  private final IModelDefinition definition = context.mock(IModelDefinition.class);
  private final IModule module = context.mock(IModule.class);

  @Test
  void testLoader() throws MalformedURLException, IOException {
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config.xml");

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    assertEquals("gov.nist.itl.metaschema.codegen.xml.example.assembly",
        config.getPackageNameForNamespace("http://csrc.nist.gov/ns/metaschema/testing/assembly"));

    context.checking(new Expectations() {
      { // NOPMD - intentional
        oneOf(module).getLocation();
        will(returnValue(METASCHEMA_LOCATION));
        allowing(definition).getContainingModule();
        will(returnValue(module));
        allowing(definition).getModelType();
        will(returnValue(DEFINITION_MODEL_TYPE));
        allowing(definition).getName();
        will(returnValue(DEFINITION_NAME));
      }
    });
    IDefinitionBindingConfiguration defConfig = config.getBindingConfigurationForDefinition(
        ObjectUtils.notNull(definition));
    assertNotNull(defConfig);
    assertEquals(DEFINITION__CLASS_NAME, defConfig.getClassName());
  }

  @Test
  void testCollectionClassOverride() throws IOException {
    // Test loading binding configuration with collection-class overrides
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-with-collection-class.xml");
    URI assemblyMetaschemaLocation = new File("src/test/resources/metaschema/assembly/metaschema.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    // Create mock for the top-level assembly definition
    IAssemblyDefinition topLevelDefinition = context.mock(IAssemblyDefinition.class, "topLevel");
    IModule assemblyModule = context.mock(IModule.class, "assemblyModule");
    INamedModelInstanceAbsolute childrenInstance = context.mock(INamedModelInstanceAbsolute.class, "childrenInstance");

    context.checking(new Expectations() {
      {
        // Module expectations
        allowing(assemblyModule).getLocation();
        will(returnValue(assemblyMetaschemaLocation));

        // Definition expectations
        allowing(topLevelDefinition).getContainingModule();
        will(returnValue(assemblyModule));
        allowing(topLevelDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(topLevelDefinition).getName();
        will(returnValue("top-level"));

        // Instance expectations
        allowing(childrenInstance).getContainingDefinition();
        will(returnValue(topLevelDefinition));
        allowing(childrenInstance).getName();
        will(returnValue("children"));
      }
    });

    // Get the property binding configuration
    IPropertyBindingConfiguration propertyConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(topLevelDefinition),
        "children");

    // Verify collection class override is available
    assertNotNull(propertyConfig, "Property binding configuration should exist for 'children'");
    assertEquals(ArrayList.class.getName(), propertyConfig.getCollectionClassName(),
        "Collection class should be ArrayList");

    // Test grandchild's fields property
    IAssemblyDefinition grandchildDefinition = context.mock(IAssemblyDefinition.class, "grandchild");

    context.checking(new Expectations() {
      {
        allowing(grandchildDefinition).getContainingModule();
        will(returnValue(assemblyModule));
        allowing(grandchildDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(grandchildDefinition).getName();
        will(returnValue("grandchild"));
      }
    });

    IPropertyBindingConfiguration fieldsPropertyConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(grandchildDefinition),
        "fields");

    assertNotNull(fieldsPropertyConfig, "Property binding configuration should exist for 'fields'");
    assertEquals(CopyOnWriteArrayList.class.getName(), fieldsPropertyConfig.getCollectionClassName(),
        "Collection class should be CopyOnWriteArrayList");

    // Test that unconfigured property returns null
    IPropertyBindingConfiguration unconfiguredProperty = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(topLevelDefinition),
        "nonexistent");
    assertNull(unconfiguredProperty, "Unconfigured property should return null");
  }

  @Test
  void testPropertyBindingWithoutJavaElement() throws IOException {
    // Test property-binding element that has no <java> child
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-edge-cases.xml");
    URI assemblyMetaschemaLocation = new File("src/test/resources/metaschema/assembly/metaschema.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    IAssemblyDefinition testDefinition = context.mock(IAssemblyDefinition.class, "testDef");
    IModule testModule = context.mock(IModule.class, "testModule");

    context.checking(new Expectations() {
      {
        allowing(testModule).getLocation();
        will(returnValue(assemblyMetaschemaLocation));
        allowing(testDefinition).getContainingModule();
        will(returnValue(testModule));
        allowing(testDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(testDefinition).getName();
        will(returnValue("edge-case-assembly"));
      }
    });

    // Property with no <java> element should return null
    IPropertyBindingConfiguration noJavaConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(testDefinition),
        "no-java-element");
    assertNull(noJavaConfig, "Property binding without <java> element should return null");

    // Property with <java> but no <collection-class> should return null
    IPropertyBindingConfiguration noCollectionClassConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(testDefinition),
        "no-collection-class");
    assertNull(noCollectionClassConfig, "Property binding without <collection-class> should return null");
  }

  @Test
  void testDefinitionNotInBindingConfig() throws IOException {
    // Test querying a definition from a module that has no binding config
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-with-collection-class.xml");
    URI unknownModuleLocation = new File("src/test/resources/metaschema/unknown-module.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    IAssemblyDefinition unknownDefinition = context.mock(IAssemblyDefinition.class, "unknownDef");
    IModule unknownModule = context.mock(IModule.class, "unknownModule");

    context.checking(new Expectations() {
      {
        allowing(unknownModule).getLocation();
        will(returnValue(unknownModuleLocation));
        allowing(unknownDefinition).getContainingModule();
        will(returnValue(unknownModule));
        allowing(unknownDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(unknownDefinition).getName();
        will(returnValue("unknown-definition"));
      }
    });

    // Definition from unknown module should return null
    IPropertyBindingConfiguration unknownConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(unknownDefinition),
        "some-property");
    assertNull(unknownConfig, "Property from unknown module should return null");
  }

  @Test
  void testFieldDefinitionPropertyBinding() throws IOException {
    // Test property binding on a field definition (not just assembly)
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-edge-cases.xml");
    URI assemblyMetaschemaLocation = new File("src/test/resources/metaschema/assembly/metaschema.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    IModelDefinition fieldDefinition = context.mock(IModelDefinition.class, "fieldDef");
    IModule fieldModule = context.mock(IModule.class, "fieldModule");

    context.checking(new Expectations() {
      {
        allowing(fieldModule).getLocation();
        will(returnValue(assemblyMetaschemaLocation));
        allowing(fieldDefinition).getContainingModule();
        will(returnValue(fieldModule));
        allowing(fieldDefinition).getModelType();
        will(returnValue(ModelType.FIELD));
        allowing(fieldDefinition).getName();
        will(returnValue("test-field"));
      }
    });

    // Field definition property binding should work
    IPropertyBindingConfiguration fieldPropertyConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(fieldDefinition),
        "field-items");
    assertNotNull(fieldPropertyConfig, "Property binding should exist for field definition");
    assertEquals("java.util.LinkedHashSet", fieldPropertyConfig.getCollectionClassName(),
        "Collection class should be LinkedHashSet");
  }

  @Test
  void testDuplicatePropertyBindingLastWins() throws IOException {
    // Test that duplicate property bindings use last-wins semantics
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-edge-cases.xml");
    URI assemblyMetaschemaLocation = new File("src/test/resources/metaschema/assembly/metaschema.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    IAssemblyDefinition duplicateDefinition = context.mock(IAssemblyDefinition.class, "duplicateDef");
    IModule duplicateModule = context.mock(IModule.class, "duplicateModule");

    context.checking(new Expectations() {
      {
        allowing(duplicateModule).getLocation();
        will(returnValue(assemblyMetaschemaLocation));
        allowing(duplicateDefinition).getContainingModule();
        will(returnValue(duplicateModule));
        allowing(duplicateDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(duplicateDefinition).getName();
        will(returnValue("edge-case-assembly"));
      }
    });

    // When duplicate property bindings exist, the last one should win
    // The config has ArrayList first, then LinkedList - LinkedList should be
    // returned
    IPropertyBindingConfiguration duplicateConfig = config.getPropertyBindingConfiguration(
        ObjectUtils.notNull(duplicateDefinition),
        "duplicate-property");
    assertNotNull(duplicateConfig, "Duplicate property binding should return a configuration");
    assertEquals(LinkedList.class.getName(), duplicateConfig.getCollectionClassName(),
        "Duplicate property bindings should use last-wins semantics");
  }

  @Test
  void testChoiceGroupBindingParsing() throws IOException {
    // Test that choice-group-binding elements are parsed from XML config
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-with-choice-groups.xml");
    URI assemblyMetaschemaLocation = new File("src/test/resources/metaschema/assembly/metaschema.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    IAssemblyDefinition testAssemblyDefinition = context.mock(IAssemblyDefinition.class, "testAssembly");
    IModule testModule = context.mock(IModule.class, "testModule");

    context.checking(new Expectations() {
      {
        allowing(testModule).getLocation();
        will(returnValue(assemblyMetaschemaLocation));
        allowing(testAssemblyDefinition).getContainingModule();
        will(returnValue(testModule));
        allowing(testAssemblyDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(testAssemblyDefinition).getName();
        will(returnValue("test-assembly"));
      }
    });

    // Get the definition binding configuration
    IDefinitionBindingConfiguration defConfig = config.getBindingConfigurationForDefinition(
        ObjectUtils.notNull(testAssemblyDefinition));

    assertNotNull(defConfig, "Definition binding configuration should exist");

    // Verify choice group bindings are accessible
    assertNotNull(defConfig.getChoiceGroupBindings(),
        "Choice group bindings map should not be null");
    assertEquals(3, defConfig.getChoiceGroupBindings().size(),
        "Should have 3 choice group bindings");

    // Verify "mixed-content" choice group binding
    IChoiceGroupBindingConfiguration mixedContentConfig
        = defConfig.getChoiceGroupBindings().get("mixed-content");
    assertNotNull(mixedContentConfig, "mixed-content choice group binding should exist");
    assertEquals("mixed-content", mixedContentConfig.getGroupAsName());
    assertEquals("gov.nist.secauto.metaschema.core.model.IModelElement",
        mixedContentConfig.getItemTypeName());
    assertEquals(true, mixedContentConfig.isUseWildcard(),
        "mixed-content should use wildcard");

    // Verify "typed-items" choice group binding
    IChoiceGroupBindingConfiguration typedItemsConfig
        = defConfig.getChoiceGroupBindings().get("typed-items");
    assertNotNull(typedItemsConfig, "typed-items choice group binding should exist");
    assertEquals("typed-items", typedItemsConfig.getGroupAsName());
    assertEquals("java.lang.String", typedItemsConfig.getItemTypeName());
    assertEquals(false, typedItemsConfig.isUseWildcard(),
        "typed-items should not use wildcard");

    // Verify "untyped-items" choice group binding
    IChoiceGroupBindingConfiguration untypedItemsConfig
        = defConfig.getChoiceGroupBindings().get("untyped-items");
    assertNotNull(untypedItemsConfig, "untyped-items choice group binding should exist");
    assertEquals("untyped-items", untypedItemsConfig.getGroupAsName());
    assertNull(untypedItemsConfig.getItemTypeName(),
        "untyped-items should not have an item type");
  }

  @Test
  void testEmptyChoiceGroupBindings() throws IOException {
    // Test that empty choice group bindings map is returned when none configured
    File bindingConfigFile = new File("src/test/resources/metaschema/binding-config-with-choice-groups.xml");
    URI assemblyMetaschemaLocation = new File("src/test/resources/metaschema/assembly/metaschema.xml")
        .getAbsoluteFile().toURI();

    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(bindingConfigFile);

    IAssemblyDefinition simpleAssemblyDefinition = context.mock(IAssemblyDefinition.class, "simpleAssembly");
    IModule simpleModule = context.mock(IModule.class, "simpleModule");

    context.checking(new Expectations() {
      {
        allowing(simpleModule).getLocation();
        will(returnValue(assemblyMetaschemaLocation));
        allowing(simpleAssemblyDefinition).getContainingModule();
        will(returnValue(simpleModule));
        allowing(simpleAssemblyDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(simpleAssemblyDefinition).getName();
        will(returnValue("simple-assembly"));
      }
    });

    // Get the definition binding configuration
    IDefinitionBindingConfiguration defConfig = config.getBindingConfigurationForDefinition(
        ObjectUtils.notNull(simpleAssemblyDefinition));

    assertNotNull(defConfig, "Definition binding configuration should exist");

    // Verify choice group bindings map is empty but not null
    assertNotNull(defConfig.getChoiceGroupBindings(),
        "Choice group bindings map should not be null");
    assertEquals(0, defConfig.getChoiceGroupBindings().size(),
        "Choice group bindings map should be empty");
  }

}
