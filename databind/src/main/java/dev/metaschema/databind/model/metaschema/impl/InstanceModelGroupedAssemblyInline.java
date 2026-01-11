/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import java.util.Map;
import java.util.Set;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.model.AbstractInlineAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IContainerModelAssemblySupport;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelElementVisitor;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.MetaschemaModelConstants;
import dev.metaschema.core.model.constraint.AssemblyConstraintSet;
import dev.metaschema.core.model.constraint.IModelConstrained;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyConstraints;
import dev.metaschema.databind.model.metaschema.binding.AssemblyModel;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of an inline grouped assembly instance from binding data.
 * <p>
 * This class represents an inline assembly member of a choice group.
 */
public class InstanceModelGroupedAssemblyInline
    extends AbstractInlineAssemblyDefinition<
        IChoiceGroupInstance,
        IAssemblyDefinition,
        IAssemblyInstanceGrouped,
        IBindingDefinitionModelAssembly,
        IFlagInstance,
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute,
        IChoiceInstance,
        IChoiceGroupInstance>
    implements IAssemblyInstanceGrouped, IBindingInstance, IBindingDefinitionModelAssembly {
  @NonNull
  private final AssemblyModel.ChoiceGroup.DefineAssembly binding;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @NonNull
  private final Lazy<IContainerFlagSupport<IFlagInstance>> flagContainer;
  @NonNull
  private final Lazy<IContainerModelAssemblySupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute,
      IChoiceInstance,
      IChoiceGroupInstance>> modelContainer;
  @NonNull
  private final Lazy<IModelConstrained> modelConstraints;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new inline grouped assembly instance from binding data.
   *
   * @param binding
   *          the underlying bound inline assembly definition object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings
   * @param parent
   *          the parent choice group instance containing this assembly
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   */
  public InstanceModelGroupedAssemblyInline(
      @NonNull AssemblyModel.ChoiceGroup.DefineAssembly binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IChoiceGroupInstance parent,
      @NonNull INodeItemFactory nodeItemFactory) {
    super(parent);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
    this.flagContainer = ObjectUtils.notNull(Lazy.of(() -> FlagContainerSupport.newFlagContainer(
        binding.getFlags(),
        bindingInstance,
        this,
        getParentContainer().getJsonKeyFlagInstanceName())));
    this.modelContainer = ObjectUtils.notNull(Lazy.of(() -> AssemblyModelGenerator.of(
        binding.getModel(),
        ObjectUtils.requireNonNull(bindingInstance.getDefinition()
            .getAssemblyInstanceByName(MetaschemaModelConstants.MODEL_QNAME.getIndexPosition())),
        this,
        nodeItemFactory)));

    ISource source = parent.getOwningDefinition().getContainingModule().getSource();

    this.modelConstraints = ObjectUtils.notNull(Lazy.of(() -> {
      IModelConstrained retval = new AssemblyConstraintSet(source);
      AssemblyConstraints constraints = binding.getConstraint();
      if (constraints != null) {
        ConstraintBindingSupport.parse(
            retval,
            constraints,
            source);
      }
      return retval;
    }));
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingDefinition().getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
  }

  @NonNull
  private AssemblyModel.ChoiceGroup.DefineAssembly getBinding() {
    return binding;
  }

  @Override
  public IBindingMetaschemaModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return properties;
  }

  @Override
  public IContainerFlagSupport<IFlagInstance> getFlagContainer() {
    return ObjectUtils.notNull(flagContainer.get());
  }

  @Override
  public IContainerModelAssemblySupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute,
      IChoiceInstance,
      IChoiceGroupInstance> getModelContainer() {
    return ObjectUtils.notNull(modelContainer.get());
  }

  @Override
  public IModelConstrained getConstraintSupport() {
    return ObjectUtils.notNull(modelConstraints.get());
  }

  @Override
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  @Override
  public <CONTEXT, RESULT> RESULT accept(IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return IAssemblyInstanceGrouped.super.accept(visitor, context);
  }

  // ---------------------------------------
  // - Start binding driven code - CPD-OFF -
  // ---------------------------------------

  @Override
  public String getName() {
    return ObjectUtils.notNull(getBinding().getName());
  }

  @Override
  public Integer getIndex() {
    return ModelSupport.index(getBinding().getIndex());
  }

  @Override
  public String getFormalName() {
    return getBinding().getFormalName();
  }

  @Override
  public MarkupLine getDescription() {
    return getBinding().getDescription();
  }

  @Override
  public MarkupMultiline getRemarks() {
    return ModelSupport.remarks(getBinding().getRemarks());
  }

  @Override
  public String getDiscriminatorValue() {
    return getBinding().getDiscriminatorValue();
  }
}
