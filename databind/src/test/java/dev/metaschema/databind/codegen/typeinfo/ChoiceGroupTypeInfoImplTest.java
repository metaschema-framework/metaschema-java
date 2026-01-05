/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;

import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.codegen.config.IBindingConfiguration;
import dev.metaschema.databind.codegen.config.IChoiceGroupBindingConfiguration;
import dev.metaschema.databind.codegen.config.IDefinitionBindingConfiguration;
import dev.metaschema.databind.codegen.typeinfo.def.IAssemblyDefinitionTypeInfo;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Unit tests for {@link ChoiceGroupTypeInfoImpl} field type generation.
 */
class ChoiceGroupTypeInfoImplTest {

  @RegisterExtension
  JUnit5Mockery context = new JUnit5Mockery() {
    {
      setThreadingPolicy(new Synchroniser());
    }
  };

  @NonNull
  private final IChoiceGroupInstance choiceGroupInstance
      = ObjectUtils.notNull(context.mock(IChoiceGroupInstance.class));
  @NonNull
  private final IAssemblyDefinition assemblyDefinition = ObjectUtils.notNull(context.mock(IAssemblyDefinition.class));
  @NonNull
  private final IAssemblyDefinitionTypeInfo parentTypeInfo
      = ObjectUtils.notNull(context.mock(IAssemblyDefinitionTypeInfo.class));

  /**
   * Test that getJavaFieldType returns List with wildcard type when useWildcard
   * is true.
   */
  @Test
  void testGetJavaFieldTypeReturnsWildcardTypeWhenConfigured() {
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    IDefinitionBindingConfiguration defConfig
        = ObjectUtils.notNull(context.mock(IDefinitionBindingConfiguration.class));
    IChoiceGroupBindingConfiguration choiceConfig
        = ObjectUtils.notNull(context.mock(IChoiceGroupBindingConfiguration.class));

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.ITestInterface";
    ClassName itemClass = ClassName.bestGuess(itemTypeName);

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    // Create a real resolver with mocked binding configuration
    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        // Setup expectations for parent and instance
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(choiceGroupInstance).getMaxOccurs();
        will(returnValue(-1)); // Unbounded collection

        allowing(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));

        allowing(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));

        allowing(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));

        allowing(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));

        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));

        allowing(choiceConfig).isUseWildcard();
        will(returnValue(true));

        allowing(choiceGroupInstance).getJsonGroupAsBehavior();
        will(returnValue(JsonGroupAsBehavior.SINGLETON_OR_LIST));
      }
    });

    ChoiceGroupTypeInfoImpl typeInfo = new ChoiceGroupTypeInfoImpl(choiceGroupInstance, parentTypeInfo);
    TypeName result = typeInfo.getJavaFieldType();

    assertNotNull(result);
    assertTrue(result instanceof ParameterizedTypeName, "Expected ParameterizedTypeName");

    ParameterizedTypeName paramType = (ParameterizedTypeName) result;
    assertEquals(ClassName.get(List.class), paramType.rawType);
    assertEquals(1, paramType.typeArguments.size());

    TypeName itemType = paramType.typeArguments.get(0);
    assertTrue(itemType instanceof WildcardTypeName, "Expected WildcardTypeName for item type");

    WildcardTypeName wildcardType = (WildcardTypeName) itemType;
    assertEquals(1, wildcardType.upperBounds.size());
    assertEquals(itemClass, wildcardType.upperBounds.get(0));
  }

  /**
   * Test that getJavaFieldType returns List with non-wildcard type when
   * useWildcard is false.
   */
  @Test
  void testGetJavaFieldTypeReturnsNonWildcardTypeWhenNotConfigured() {
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    IDefinitionBindingConfiguration defConfig
        = ObjectUtils.notNull(context.mock(IDefinitionBindingConfiguration.class));
    IChoiceGroupBindingConfiguration choiceConfig
        = ObjectUtils.notNull(context.mock(IChoiceGroupBindingConfiguration.class));

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.ITestInterface";
    ClassName itemClass = ClassName.bestGuess(itemTypeName);

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(choiceGroupInstance).getMaxOccurs();
        will(returnValue(-1));

        allowing(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));

        allowing(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));

        allowing(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));

        allowing(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));

        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));

        allowing(choiceConfig).isUseWildcard();
        will(returnValue(false)); // No wildcard

        allowing(choiceGroupInstance).getJsonGroupAsBehavior();
        will(returnValue(JsonGroupAsBehavior.SINGLETON_OR_LIST));
      }
    });

    ChoiceGroupTypeInfoImpl typeInfo = new ChoiceGroupTypeInfoImpl(choiceGroupInstance, parentTypeInfo);
    TypeName result = typeInfo.getJavaFieldType();

    assertNotNull(result);
    assertTrue(result instanceof ParameterizedTypeName, "Expected ParameterizedTypeName");

    ParameterizedTypeName paramType = (ParameterizedTypeName) result;
    assertEquals(ClassName.get(List.class), paramType.rawType);
    assertEquals(1, paramType.typeArguments.size());

    // Should be the item class directly, not a wildcard
    TypeName itemType = paramType.typeArguments.get(0);
    assertEquals(itemClass, itemType);
  }

  /**
   * Test that getJavaFieldType returns Map with wildcard type when keyed.
   */
  @Test
  void testGetJavaFieldTypeReturnsMapWithWildcardForKeyedGroups() {
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    IDefinitionBindingConfiguration defConfig
        = ObjectUtils.notNull(context.mock(IDefinitionBindingConfiguration.class));
    IChoiceGroupBindingConfiguration choiceConfig
        = ObjectUtils.notNull(context.mock(IChoiceGroupBindingConfiguration.class));

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.ITestInterface";
    ClassName itemClass = ClassName.bestGuess(itemTypeName);

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(choiceGroupInstance).getMaxOccurs();
        will(returnValue(-1));

        allowing(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));

        allowing(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));

        allowing(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));

        allowing(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));

        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));

        allowing(choiceConfig).isUseWildcard();
        will(returnValue(true));

        allowing(choiceGroupInstance).getJsonGroupAsBehavior();
        will(returnValue(JsonGroupAsBehavior.KEYED)); // Map collection type
      }
    });

    ChoiceGroupTypeInfoImpl typeInfo = new ChoiceGroupTypeInfoImpl(choiceGroupInstance, parentTypeInfo);
    TypeName result = typeInfo.getJavaFieldType();

    assertNotNull(result);
    assertTrue(result instanceof ParameterizedTypeName, "Expected ParameterizedTypeName");

    ParameterizedTypeName paramType = (ParameterizedTypeName) result;
    assertEquals(ClassName.get(Map.class), paramType.rawType);
    assertEquals(2, paramType.typeArguments.size());

    // Key should be String
    TypeName keyType = paramType.typeArguments.get(0);
    assertEquals(ClassName.get(String.class), keyType);

    // Value should be wildcard
    TypeName valueType = paramType.typeArguments.get(1);
    assertTrue(valueType instanceof WildcardTypeName, "Expected WildcardTypeName for value type");

    WildcardTypeName wildcardType = (WildcardTypeName) valueType;
    assertEquals(1, wildcardType.upperBounds.size());
    assertEquals(itemClass, wildcardType.upperBounds.get(0));
  }

  /**
   * Test that getJavaFieldType returns non-collection type when maxOccurs is 1.
   */
  @Test
  void testGetJavaFieldTypeReturnsSingletonWhenMaxOccursIsOne() {
    ClassName itemClass = ClassName.get(Object.class);
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(choiceGroupInstance).getMaxOccurs();
        will(returnValue(1)); // Single occurrence

        allowing(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));

        allowing(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(null));
      }
    });

    ChoiceGroupTypeInfoImpl typeInfo = new ChoiceGroupTypeInfoImpl(choiceGroupInstance, parentTypeInfo);
    TypeName result = typeInfo.getJavaFieldType();

    assertNotNull(result);
    assertEquals(itemClass, result);
  }

  /**
   * Test that getJavaFieldType returns List of Object when no binding
   * configuration exists.
   */
  @Test
  void testGetJavaFieldTypeReturnsListOfObjectWhenNoBindingConfig() {
    ClassName itemClass = ClassName.get(Object.class);
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(choiceGroupInstance).getMaxOccurs();
        will(returnValue(-1));

        allowing(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));

        allowing(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(null)); // No binding config

        allowing(choiceGroupInstance).getJsonGroupAsBehavior();
        will(returnValue(JsonGroupAsBehavior.SINGLETON_OR_LIST));
      }
    });

    ChoiceGroupTypeInfoImpl typeInfo = new ChoiceGroupTypeInfoImpl(choiceGroupInstance, parentTypeInfo);
    TypeName result = typeInfo.getJavaFieldType();

    assertNotNull(result);
    assertTrue(result instanceof ParameterizedTypeName, "Expected ParameterizedTypeName");

    ParameterizedTypeName paramType = (ParameterizedTypeName) result;
    assertEquals(ClassName.get(List.class), paramType.rawType);
    assertEquals(1, paramType.typeArguments.size());
    assertEquals(itemClass, paramType.typeArguments.get(0));
  }

  /**
   * Test that getJavaFieldType handles maxOccurs greater than 1.
   */
  @Test
  void testGetJavaFieldTypeWithMaxOccursGreaterThanOne() {
    IBindingConfiguration bindingConfig = ObjectUtils.notNull(context.mock(IBindingConfiguration.class));
    IDefinitionBindingConfiguration defConfig
        = ObjectUtils.notNull(context.mock(IDefinitionBindingConfiguration.class));
    IChoiceGroupBindingConfiguration choiceConfig
        = ObjectUtils.notNull(context.mock(IChoiceGroupBindingConfiguration.class));

    String groupAsName = "test-choices";
    String itemTypeName = "com.example.ITestInterface";

    Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new HashMap<>();
    choiceGroupBindings.put(groupAsName, choiceConfig);

    DefaultTypeResolver typeResolver = new DefaultTypeResolver(bindingConfig);

    context.checking(new Expectations() {
      {
        allowing(parentTypeInfo).getTypeResolver();
        will(returnValue(typeResolver));

        allowing(choiceGroupInstance).getMaxOccurs();
        will(returnValue(5)); // Fixed upper bound > 1

        allowing(choiceGroupInstance).getContainingDefinition();
        will(returnValue(assemblyDefinition));

        allowing(bindingConfig).getBindingConfigurationForDefinition(assemblyDefinition);
        will(returnValue(defConfig));

        allowing(choiceGroupInstance).getGroupAsName();
        will(returnValue(groupAsName));

        allowing(defConfig).getChoiceGroupBindings();
        will(returnValue(choiceGroupBindings));

        allowing(choiceConfig).getItemTypeName();
        will(returnValue(itemTypeName));

        allowing(choiceConfig).isUseWildcard();
        will(returnValue(true));

        allowing(choiceGroupInstance).getJsonGroupAsBehavior();
        will(returnValue(JsonGroupAsBehavior.SINGLETON_OR_LIST));
      }
    });

    ChoiceGroupTypeInfoImpl typeInfo = new ChoiceGroupTypeInfoImpl(choiceGroupInstance, parentTypeInfo);
    TypeName result = typeInfo.getJavaFieldType();

    assertNotNull(result);
    assertTrue(result instanceof ParameterizedTypeName, "Expected ParameterizedTypeName");

    ParameterizedTypeName paramType = (ParameterizedTypeName) result;
    assertEquals(ClassName.get(List.class), paramType.rawType);

    TypeName itemType = paramType.typeArguments.get(0);
    assertTrue(itemType instanceof WildcardTypeName, "Expected WildcardTypeName");
  }
}
