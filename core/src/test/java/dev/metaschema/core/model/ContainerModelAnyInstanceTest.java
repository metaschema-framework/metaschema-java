/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.metaschema.core.model.impl.DefaultContainerModelAssemblySupport;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.CollectionUtil;

/**
 * Tests for {@code any} instance support in container model classes.
 */
class ContainerModelAnyInstanceTest {

  @Test
  void testInterfaceDefaultReturnsNull() {
    IContainerModelAssemblySupport<?, ?, ?, ?, ?, ?> empty
        = IContainerModelAssemblySupport.empty();
    assertNull(empty.getAnyInstance(), "Default getAnyInstance() should return null");
  }

  @Test
  void testEmptyContainerReturnsNull() {
    @SuppressWarnings("unchecked")
    DefaultContainerModelAssemblySupport<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> empty
            = DefaultContainerModelAssemblySupport.EMPTY;
    assertNull(empty.getAnyInstance(), "EMPTY container should return null for getAnyInstance()");
  }

  @Test
  void testContainerStoresAnyInstance() {
    IAnyInstance mockAny = Mockito.mock(IAnyInstance.class);

    DefaultContainerModelAssemblySupport<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> container
            = new DefaultContainerModelAssemblySupport<>(
                CollectionUtil.emptyList(),
                CollectionUtil.emptyMap(),
                CollectionUtil.emptyMap(),
                CollectionUtil.emptyMap(),
                CollectionUtil.emptyList(),
                CollectionUtil.emptyMap(),
                mockAny);
    assertSame(mockAny, container.getAnyInstance(),
        "Container should return the IAnyInstance passed to the constructor");
  }

  @Test
  void testContainerConstructorWithNullAnyInstance() {
    DefaultContainerModelAssemblySupport<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> container
            = new DefaultContainerModelAssemblySupport<>(
                CollectionUtil.emptyList(),
                CollectionUtil.emptyMap(),
                CollectionUtil.emptyMap(),
                CollectionUtil.emptyMap(),
                CollectionUtil.emptyList(),
                CollectionUtil.emptyMap(),
                null);
    assertNull(container.getAnyInstance(),
        "Container should return null when constructed with null anyInstance");
  }

  @SuppressWarnings("unchecked")
  @Test
  void testBuilderDefaultAnyInstanceIsNull() {
    DefaultAssemblyModelBuilder<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> builder
            = new DefaultAssemblyModelBuilder<>();

    assertNull(builder.getAnyInstance(),
        "Builder should return null by default for getAnyInstance()");
  }

  @SuppressWarnings("unchecked")
  @Test
  void testBuilderStoresAnyInstance() {
    IAnyInstance mockAny = Mockito.mock(IAnyInstance.class);

    DefaultAssemblyModelBuilder<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> builder
            = new DefaultAssemblyModelBuilder<>();
    builder.setAnyInstance(mockAny);

    assertSame(mockAny, builder.getAnyInstance(),
        "Builder should return the IAnyInstance that was set");
  }

  @SuppressWarnings("unchecked")
  @Test
  void testBuilderPassesAnyInstanceToContainer() {
    IAnyInstance mockAny = Mockito.mock(IAnyInstance.class);
    IFieldInstance mockField = Mockito.mock(IFieldInstance.class);
    IEnhancedQName mockQName = Mockito.mock(IEnhancedQName.class);
    Mockito.when(mockQName.getIndexPosition()).thenReturn(1);
    Mockito.when(mockField.getQName()).thenReturn(mockQName);

    DefaultAssemblyModelBuilder<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> builder
            = new DefaultAssemblyModelBuilder<>();

    // Add a model instance so the builder doesn't return EMPTY
    builder.append(mockField);
    builder.setAnyInstance(mockAny);

    IContainerModelAssemblySupport<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> container
            = builder.buildAssembly();

    assertSame(mockAny, container.getAnyInstance(),
        "Built container should contain the IAnyInstance from the builder");
    assertEquals(1, container.getModelInstances().size(),
        "Built container should contain the field instance");
  }

  @SuppressWarnings("unchecked")
  @Test
  void testBuilderBuildEmptyStillReturnsNullAny() {
    DefaultAssemblyModelBuilder<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> builder
            = new DefaultAssemblyModelBuilder<>();

    // Don't add any instances - should return empty container
    IContainerModelAssemblySupport<IModelInstance, INamedModelInstance,
        IFieldInstance, IAssemblyInstance, IChoiceInstance, IChoiceGroupInstance> container
            = builder.buildAssembly();

    assertNull(container.getAnyInstance(),
        "Empty built container should return null for getAnyInstance()");
  }
}
