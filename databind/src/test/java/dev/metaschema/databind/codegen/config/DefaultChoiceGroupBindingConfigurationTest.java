/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ModelType;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.io.BindingException;

/**
 * Tests for {@link DefaultChoiceGroupBindingConfiguration}.
 * <p>
 * These tests use XML binding configuration files to verify parsing and
 * behavior of choice group binding configurations.
 */
class DefaultChoiceGroupBindingConfigurationTest {

  private static final File BINDING_CONFIG_FILE
      = new File("src/test/resources/metaschema/binding-config-with-choice-groups.xml");
  private static final URI ASSEMBLY_METASCHEMA_LOCATION
      = new File("src/test/resources/metaschema/assembly/metaschema.xml").getAbsoluteFile().toURI();

  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  private final AtomicInteger mockCounter = new AtomicInteger(0);
  private DefaultBindingConfiguration bindingConfig;

  @BeforeEach
  void setUp() throws IOException, BindingException {
    bindingConfig = new DefaultBindingConfiguration();
    bindingConfig.load(BINDING_CONFIG_FILE);
  }

  /**
   * Creates a mock assembly definition with standard expectations and retrieves
   * the binding configuration for it.
   *
   * @return the definition binding configuration for the mock assembly
   */
  private IDefinitionBindingConfiguration getDefinitionConfig() {
    int id = mockCounter.incrementAndGet();
    IAssemblyDefinition testAssemblyDefinition = context.mock(IAssemblyDefinition.class, "testAssembly" + id);
    IModule testModule = context.mock(IModule.class, "testModule" + id);

    context.checking(new Expectations() {
      {
        allowing(testModule).getLocation();
        will(returnValue(ASSEMBLY_METASCHEMA_LOCATION));
        allowing(testAssemblyDefinition).getContainingModule();
        will(returnValue(testModule));
        allowing(testAssemblyDefinition).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(testAssemblyDefinition).getName();
        will(returnValue("test-assembly"));
      }
    });

    return bindingConfig.getBindingConfigurationForDefinition(ObjectUtils.notNull(testAssemblyDefinition));
  }

  @Test
  void testGetGroupAsName() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    IChoiceGroupBindingConfiguration choiceGroupConfig
        = defConfig.getChoiceGroupBindings().get("mixed-content");
    assertNotNull(choiceGroupConfig);
    assertEquals("mixed-content", choiceGroupConfig.getGroupAsName());
  }

  @Test
  void testGetItemTypeNameWhenSpecified() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    IChoiceGroupBindingConfiguration typedItemsConfig
        = defConfig.getChoiceGroupBindings().get("typed-items");
    assertNotNull(typedItemsConfig);
    assertEquals("java.lang.String", typedItemsConfig.getItemTypeName());
  }

  @Test
  void testGetItemTypeNameWhenNotSpecified() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    IChoiceGroupBindingConfiguration untypedItemsConfig
        = defConfig.getChoiceGroupBindings().get("untyped-items");
    assertNotNull(untypedItemsConfig);
    assertNull(untypedItemsConfig.getItemTypeName(),
        "Item type should be null when not specified");
  }

  @Test
  void testIsUseWildcardDefaultsToTrue() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    // "mixed-content" has use-wildcard="true" explicitly set
    IChoiceGroupBindingConfiguration mixedContentConfig
        = defConfig.getChoiceGroupBindings().get("mixed-content");
    assertNotNull(mixedContentConfig);
    assertTrue(mixedContentConfig.isUseWildcard(),
        "useWildcard should be true when explicitly set");
  }

  @Test
  void testIsUseWildcardWhenExplicitlyFalse() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    // "typed-items" has use-wildcard="false"
    IChoiceGroupBindingConfiguration typedItemsConfig
        = defConfig.getChoiceGroupBindings().get("typed-items");
    assertNotNull(typedItemsConfig);
    assertFalse(typedItemsConfig.isUseWildcard(),
        "useWildcard should be false when explicitly set to false");
  }

  @Test
  void testIsUseWildcardWhenNoItemType() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    // "untyped-items" has no item-type element
    IChoiceGroupBindingConfiguration untypedItemsConfig
        = defConfig.getChoiceGroupBindings().get("untyped-items");
    assertNotNull(untypedItemsConfig);
    assertTrue(untypedItemsConfig.isUseWildcard(),
        "useWildcard should default to true when no item type specified");
  }

  // --- Negative Test Cases ---

  @Test
  void testNonExistentGroupNameReturnsNull() {
    IDefinitionBindingConfiguration defConfig = getDefinitionConfig();

    IChoiceGroupBindingConfiguration result
        = defConfig.getChoiceGroupBindings().get("non-existent-group");
    assertNull(result, "Non-existent group name should return null");
  }

  @Test
  void testMissingResourceFileThrowsException() {
    File nonExistentFile = new File("src/test/resources/metaschema/does-not-exist.xml");
    DefaultBindingConfiguration config = new DefaultBindingConfiguration();

    assertThrows(IOException.class, () -> config.load(nonExistentFile),
        "Loading non-existent file should throw IOException");
  }

  @Test
  void testEmptyChoiceGroupBindingsMap() {
    // Test definition that has no choice group bindings configured
    int id = mockCounter.incrementAndGet();
    IAssemblyDefinition unconfiguredAssembly = context.mock(IAssemblyDefinition.class, "unconfiguredAssembly" + id);
    IModule unconfiguredModule = context.mock(IModule.class, "unconfiguredModule" + id);

    // Use a different metaschema location that won't match any binding config
    URI differentLocation = new File("src/test/resources/metaschema/other/metaschema.xml")
        .getAbsoluteFile().toURI();

    context.checking(new Expectations() {
      {
        allowing(unconfiguredModule).getLocation();
        will(returnValue(differentLocation));
        allowing(unconfiguredAssembly).getContainingModule();
        will(returnValue(unconfiguredModule));
        allowing(unconfiguredAssembly).getModelType();
        will(returnValue(ModelType.ASSEMBLY));
        allowing(unconfiguredAssembly).getName();
        will(returnValue("unconfigured-assembly"));
      }
    });

    IDefinitionBindingConfiguration defConfig = bindingConfig.getBindingConfigurationForDefinition(
        ObjectUtils.notNull(unconfiguredAssembly));

    // When no binding config matches, should return null
    assertNull(defConfig, "Unconfigured assembly should have no binding configuration");
  }
}
