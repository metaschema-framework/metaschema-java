/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.model.AbstractAssemblyInstance;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyModel;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of a grouped assembly reference from binding data.
 * <p>
 * This class represents a reference to a global assembly as a member of a
 * choice group.
 */
public class InstanceModelGroupedAssemblyReference
    extends AbstractAssemblyInstance<
        IChoiceGroupInstance,
        IAssemblyDefinition,
        IAssemblyInstanceGrouped,
        IBindingDefinitionModelAssembly>
    implements IAssemblyInstanceGrouped, IBindingInstance {
  @NonNull
  private final AssemblyModel.ChoiceGroup.Assembly binding;
  @NonNull
  private final IAssemblyDefinition definition;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new grouped assembly reference from binding data.
   *
   * @param binding
   *          the underlying bound assembly reference object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings; must be a valid index within the model items
   * @param definition
   *          the global assembly definition being referenced
   * @param parent
   *          the parent choice group instance containing this reference
   * @throws IndexOutOfBoundsException
   *           if the position is invalid when the source node item is accessed
   */
  public InstanceModelGroupedAssemblyReference(
      @NonNull AssemblyModel.ChoiceGroup.Assembly binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IAssemblyDefinition definition,
      @NonNull IChoiceGroupInstance parent) {
    super(parent);
    this.binding = binding;
    this.definition = definition;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingDefinition().getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
  }

  @Override
  public IAssemblyDefinition getDefinition() {
    return definition;
  }

  @NonNull
  private AssemblyModel.ChoiceGroup.Assembly getBinding() {
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
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  @Override
  public String getName() {
    return getDefinition().getName();
  }

  // ---------------------------------------
  // - Start binding driven code - CPD-OFF -
  // ---------------------------------------

  @Override
  public String getUseName() {
    return ModelSupport.useName(getBinding().getUseName());
  }

  @Override
  public Integer getUseIndex() {
    return ModelSupport.useIndex(getBinding().getUseName());
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
