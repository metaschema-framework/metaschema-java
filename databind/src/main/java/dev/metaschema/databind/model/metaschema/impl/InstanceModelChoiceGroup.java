/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import java.math.BigInteger;

import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.model.AbstractChoiceGroupInstance;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.model.IContainerModelSupport;
import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.impl.IFeatureInstanceModelGroupAs;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyModel;
import dev.metaschema.databind.model.metaschema.binding.JsonKey;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of a choice group instance from binding data.
 * <p>
 * This class represents a collection of polymorphic model instances that can
 * contain different types of members.
 */
public class InstanceModelChoiceGroup
    extends AbstractChoiceGroupInstance<
        IBindingDefinitionModelAssembly,
        INamedModelInstanceGrouped,
        IFieldInstanceGrouped,
        IAssemblyInstanceGrouped>
    implements IFeatureInstanceModelGroupAs, IBindingInstance {
  @NonNull
  private final AssemblyModel.ChoiceGroup binding;
  @NonNull
  private final IGroupAs groupAs;
  @NonNull
  private final Lazy<IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped>> modelContainer;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new choice group instance from binding data.
   *
   * @param binding
   *          the underlying bound choice group object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings
   * @param parent
   *          the parent assembly definition containing this choice group
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   */
  public InstanceModelChoiceGroup(
      @NonNull AssemblyModel.ChoiceGroup binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IBindingDefinitionModelAssembly parent,
      @NonNull INodeItemFactory nodeItemFactory) {
    super(parent);
    this.binding = binding;
    this.groupAs = ModelSupport.groupAs(binding.getGroupAs(), parent.getContainingModule());
    this.modelContainer = ObjectUtils.notNull(Lazy.of(() -> ChoiceGroupModelGenerator.of(
        binding,
        bindingInstance,
        this,
        nodeItemFactory)));
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingDefinition().getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
  }

  @NonNull
  private AssemblyModel.ChoiceGroup getBinding() {
    return binding;
  }

  @Override
  public IBindingMetaschemaModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @Override
  public IContainerModelSupport<
      INamedModelInstanceGrouped,
      INamedModelInstanceGrouped,
      IFieldInstanceGrouped,
      IAssemblyInstanceGrouped> getModelContainer() {
    return ObjectUtils.notNull(modelContainer.get());
  }

  @Override
  public IGroupAs getGroupAs() {
    return groupAs;
  }

  @Override
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  // ---------------------------------------
  // - Start binding driven code - CPD-OFF -
  // ---------------------------------------

  @Override
  public int getMinOccurs() {
    BigInteger min = getBinding().getMinOccurs();
    return min == null ? DEFAULT_GROUP_AS_MIN_OCCURS : min.intValueExact();
  }

  @Override
  public int getMaxOccurs() {
    String max = getBinding().getMaxOccurs();
    return max == null ? DEFAULT_GROUP_AS_MAX_OCCURS : ModelSupport.maxOccurs(max);
  }

  @Override
  public IAssemblyDefinition getOwningDefinition() {
    return getParentContainer();
  }

  @Override
  public String getJsonDiscriminatorProperty() {
    String discriminator = getBinding().getDiscriminator();
    return discriminator == null ? DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME : discriminator;
  }

  @Override
  public String getJsonKeyFlagInstanceName() {
    JsonKey jsonKey = getBinding().getJsonKey();
    return jsonKey == null ? null : jsonKey.getFlagRef();
  }
}
