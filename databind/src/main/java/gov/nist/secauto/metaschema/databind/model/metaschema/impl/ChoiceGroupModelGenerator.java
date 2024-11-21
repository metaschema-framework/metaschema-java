/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.DefaultChoiceGroupModelBuilder;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.util.ModuleUtils;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Generates a model container for a choice group.
 * <p>
 * This method isn't thread safe.
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
  @SuppressWarnings("PMD.ShortMethodName")
  public static IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped> of(
          @Nullable AssemblyModel.ChoiceGroup binding,
          @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
          @NonNull IChoiceGroupInstance parent,
          @NonNull INodeItemFactory nodeItemFactory) {
    List<Object> instances;
    return binding == null || (instances = binding.getChoices()) == null || instances.isEmpty()
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
