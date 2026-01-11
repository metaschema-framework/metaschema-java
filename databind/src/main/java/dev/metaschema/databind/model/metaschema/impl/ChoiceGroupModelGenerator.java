/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.model.DefaultChoiceGroupModelBuilder;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IContainerModelSupport;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.binding.AssemblyModel;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Generates a model container for a choice group.
 * <p>
 * This method isn't thread safe.
 */
/**
 * Generates choice group model structures from binding data.
 * <p>
 * This class handles the creation of choice group containers for assemblies
 * that contain polymorphic model content.
 */
public final class ChoiceGroupModelGenerator
    extends DefaultChoiceGroupModelBuilder<
        INamedModelInstanceGrouped,
        IFieldInstanceGrouped,
        IAssemblyInstanceGrouped> {
  @NonNull
  private final IChoiceGroupInstance parent;
  @NonNull
  private final INodeItemFactory nodeItemFactory;

  // counters to track child positions
  private int assemblyReferencePosition; // 0
  private int assemblyInlineDefinitionPosition; // 0
  private int fieldReferencePosition; // 0
  private int fieldInlineDefinitionPosition; // 0

  /**
   * Construct a new assembly model container.
   *
   * @param binding
   *          the choice group model object bound to a Java class
   * @param bindingInstance
   *          the Metaschema binding instance
   * @param parent
   *          the choice group owning this container
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   * @return the container
   */
  public static IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped> of(
          @Nullable AssemblyModel.ChoiceGroup binding,
          @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
          @NonNull IChoiceGroupInstance parent,
          @NonNull INodeItemFactory nodeItemFactory) {
    return binding == null || binding.getChoices().isEmpty()
        ? IContainerModelSupport.empty()
        : newInstance(
            binding,
            bindingInstance,
            parent,
            nodeItemFactory);
  }

  @NonNull
  private static IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped> newInstance(
          @NonNull AssemblyModel.ChoiceGroup binding,
          @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
          @NonNull IChoiceGroupInstance parent,
          @NonNull INodeItemFactory nodeItemFactory) {

    ChoiceGroupModelGenerator generator = new ChoiceGroupModelGenerator(parent, nodeItemFactory);

    // TODO: make "instances" a constant
    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        bindingInstance.getDefinition().getChoiceGroupInstanceByName("choices"));
    for (Object obj : ObjectUtils.notNull(binding.getChoices())) {
      assert obj != null;

      IBoundInstanceModelGroupedAssembly objInstance
          = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

      if (obj instanceof AssemblyModel.ChoiceGroup.Assembly) {
        generator.addAssemblyInstance(
            (AssemblyModel.ChoiceGroup.Assembly) obj,
            objInstance);
      } else if (obj instanceof AssemblyModel.ChoiceGroup.DefineAssembly) {
        generator.addAssemblyInstance(
            (AssemblyModel.ChoiceGroup.DefineAssembly) obj,
            objInstance);
      } else if (obj instanceof AssemblyModel.ChoiceGroup.Field) {
        generator.addFieldInstance(
            (AssemblyModel.ChoiceGroup.Field) obj,
            objInstance);
      } else if (obj instanceof AssemblyModel.ChoiceGroup.DefineField) {
        generator.addFieldInstance(
            (AssemblyModel.ChoiceGroup.DefineField) obj,
            objInstance);
      } else {
        throw new UnsupportedOperationException(
            String.format("Unknown choice group model instance class: %s", obj.getClass()));
      }
    }

    return generator.buildChoiceGroup();
  }

  private ChoiceGroupModelGenerator(
      @NonNull IChoiceGroupInstance parent,
      @NonNull INodeItemFactory nodeItemFactory) {
    this.parent = parent;
    this.nodeItemFactory = nodeItemFactory;
  }

  @NonNull
  private IChoiceGroupInstance getParent() {
    return parent;
  }

  @NonNull
  private INodeItemFactory getNodeItemFactory() {
    return nodeItemFactory;
  }

  private void addAssemblyInstance(
      @NonNull AssemblyModel.ChoiceGroup.Assembly obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance) {
    IAssemblyDefinition owningDefinition = parent.getOwningDefinition();
    IModule module = owningDefinition.getContainingModule();

    IEnhancedQName name = ModuleUtils.parseModelName(
        parent.getContainingModule(),
        ObjectUtils.requireNonNull(obj.getRef()));
    IAssemblyDefinition definition = module.getScopedAssemblyDefinitionByName(name.getIndexPosition());

    if (definition == null) {
      throw new IllegalStateException(
          String.format("Unable to resolve assembly reference '%s' in definition '%s' in module '%s'",
              name,
              owningDefinition.getName(),
              module.getShortName()));
    }
    append(new InstanceModelGroupedAssemblyReference(
        obj,
        objInstance,
        assemblyReferencePosition++,
        definition,
        getParent()));
  }

  private void addAssemblyInstance(
      @NonNull AssemblyModel.ChoiceGroup.DefineAssembly obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance) {
    append(new InstanceModelGroupedAssemblyInline(
        obj,
        objInstance,
        assemblyInlineDefinitionPosition++,
        getParent(),
        getNodeItemFactory()));
  }

  private void addFieldInstance(
      @NonNull AssemblyModel.ChoiceGroup.Field obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance) {
    IAssemblyDefinition owningDefinition = parent.getOwningDefinition();
    IModule module = owningDefinition.getContainingModule();

    IEnhancedQName name = ModuleUtils.parseModelName(
        parent.getContainingModule(),
        ObjectUtils.requireNonNull(obj.getRef()));
    IFieldDefinition definition = module.getScopedFieldDefinitionByName(name.getIndexPosition());
    if (definition == null) {
      throw new IllegalStateException(
          String.format("Unable to resolve field reference '%s' in definition '%s' in module '%s'",
              name,
              owningDefinition.getName(),
              module.getShortName()));
    }
    append(new InstanceModelGroupedFieldReference(
        obj,
        objInstance,
        fieldReferencePosition++,
        definition,
        getParent()));
  }

  private void addFieldInstance(
      @NonNull AssemblyModel.ChoiceGroup.DefineField obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance) {
    append(new InstanceModelGroupedFieldInline(
        obj,
        objInstance,
        fieldInlineDefinitionPosition++,
        getParent()));
  }
}
