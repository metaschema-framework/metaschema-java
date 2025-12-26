/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.squareup.javapoet.ClassName;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.codegen.config.DefaultBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.DefaultChoiceGroupBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.DefaultDefinitionBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.IBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.IChoiceGroupBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.IDefinitionBindingConfiguration;
import gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindings;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link DefaultTypeResolver} choice group type resolution.
 */
class DefaultTypeResolverTest {

  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  private final IAssemblyDefinition assemblyDefinition = context.mock(IAssemblyDefinition.class);
  private final IChoiceGroupInstance choiceGroupInstance = context.mock(IChoiceGroupInstance.class);

  /**
   * Test that getClassName returns Object when no binding configuration exists
   * for the choice group.
   */
  @Test
  void testGetClassNameReturnsObjectWhenNoBindingConfig() {
    IBindingConfiguration bindingConfig = context.mock(IBindingConfiguration.class);
    DefaultTypeResolver resolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        oneOf(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));
        oneOf(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(null));
      }
    });

    ClassName result = resolver.getClassName(choiceGroupInstance);

    assertNotNull(result);
    assertEquals(ClassName.get(Object.class), result);
  }

  /**
   * Test that getClassName returns Object when binding configuration exists but
   * has no choice group binding.
   */
  @Test
  void testGetClassNameReturnsObjectWhenNoChoiceGroupBinding() {
    IBindingConfiguration bindingConfig = context.mock(IBindingConfiguration.class);
    IDefinitionBindingConfiguration defConfig = context.mock(IDefinitionBindingConfiguration.class);
    DefaultTypeResolver resolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        oneOf(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));
        oneOf(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));
        oneOf(choiceGroupInstance).getGroupAsName();
        will(returnValue("test-choices"));
        oneOf(defConfig).getChoiceGroupBindings();
        will(returnValue(new HashMap<>()));
      }
    });

    ClassName result = resolver.getClassName(choiceGroupInstance);

    assertNotNull(result);
    assertEquals(ClassName.get(Object.class), result);
  }

  /**
   * Test that getClassName returns configured item type when binding
   * configuration specifies one.
   */
  @Test
  void testGetClassNameReturnsConfiguredItemType() {
    IBindingConfiguration bindingConfig = context.mock(IBindingConfiguration.class);
    IDefinitionBindingConfiguration defConfig = context.mock(IDefinitionBindingConfiguration.class);
    IChoiceGroupBindingConfiguration choiceConfig = context.mock(IChoiceGroupBindingConfiguration.class);
    DefaultTypeResolver resolver = new DefaultTypeResolver(bindingConfig);

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.ITestInterface";

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    context.checking(new Expectations() {
      {
        oneOf(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));
        oneOf(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));
        oneOf(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));
        oneOf(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));
        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));
      }
    });

    ClassName result = resolver.getClassName(choiceGroupInstance);

    assertNotNull(result);
    assertEquals("com.example", result.packageName());
    assertEquals("ITestInterface", result.simpleName());
  }

  /**
   * Test that getClassName returns Object when choice group binding exists but
   * item type is null.
   */
  @Test
  void testGetClassNameReturnsObjectWhenItemTypeIsNull() {
    IBindingConfiguration bindingConfig = context.mock(IBindingConfiguration.class);
    IDefinitionBindingConfiguration defConfig = context.mock(IDefinitionBindingConfiguration.class);
    IChoiceGroupBindingConfiguration choiceConfig = context.mock(IChoiceGroupBindingConfiguration.class);
    DefaultTypeResolver resolver = new DefaultTypeResolver(bindingConfig);

    String groupAsName = "test-choices";

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    context.checking(new Expectations() {
      {
        oneOf(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));
        oneOf(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));
        oneOf(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));
        oneOf(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));
        allowing(choiceConfig).getItemTypeName();
        will(returnValue(null));
      }
    });

    ClassName result = resolver.getClassName(choiceGroupInstance);

    assertNotNull(result);
    assertEquals(ClassName.get(Object.class), result);
  }

  /**
   * Test edge case: getClassName with nested class item type name.
   */
  @Test
  void testGetClassNameWithNestedClassType() {
    IBindingConfiguration bindingConfig = context.mock(IBindingConfiguration.class);
    IDefinitionBindingConfiguration defConfig = context.mock(IDefinitionBindingConfiguration.class);
    IChoiceGroupBindingConfiguration choiceConfig = context.mock(IChoiceGroupBindingConfiguration.class);
    DefaultTypeResolver resolver = new DefaultTypeResolver(bindingConfig);

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.OuterClass.InnerInterface";

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    context.checking(new Expectations() {
      {
        oneOf(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));
        oneOf(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));
        oneOf(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));
        oneOf(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));
        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));
      }
    });

    ClassName result = resolver.getClassName(choiceGroupInstance);

    assertNotNull(result);
    assertEquals("com.example", result.packageName());
    assertEquals("InnerInterface", result.simpleName());
  }

  /**
   * Test that getClassName ignores isUseWildcard setting and always returns the
   * simple class name. The wildcard behavior is handled by
   * ChoiceGroupTypeInfoImpl when generating collection types, not by the type
   * resolver.
   */
  @Test
  void testGetClassNameIgnoresWildcardSetting() {
    IBindingConfiguration bindingConfig = context.mock(IBindingConfiguration.class);
    IDefinitionBindingConfiguration defConfig = context.mock(IDefinitionBindingConfiguration.class);
    IChoiceGroupBindingConfiguration choiceConfig = context.mock(IChoiceGroupBindingConfiguration.class);
    DefaultTypeResolver resolver = new DefaultTypeResolver(bindingConfig);

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.ITestInterface";

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    context.checking(new Expectations() {
      {
        oneOf(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));
        oneOf(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));
        oneOf(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));
        oneOf(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));
        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));
        // isUseWildcard is available but not used by getClassName
        allowing(choiceConfig).isUseWildcard();
        will(returnValue(true));
      }
    });

    ClassName result = resolver.getClassName(choiceGroupInstance);

    // getClassName returns the base class, not a wildcard type
    // The wildcard wrapping is done in ChoiceGroupTypeInfoImpl
    assertNotNull(result);
    assertEquals("com.example", result.packageName());
    assertEquals("ITestInterface", result.simpleName());
  }
}
