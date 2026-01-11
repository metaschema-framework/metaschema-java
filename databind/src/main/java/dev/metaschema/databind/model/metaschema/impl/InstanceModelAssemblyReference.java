/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.model.AbstractAssemblyInstance;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IContainerModelAbsolute;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.impl.IFeatureInstanceModelGroupAs;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyReference;
import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of an assembly instance reference from binding data.
 * <p>
 * This class represents a reference to a globally defined assembly within a
 * containing assembly's model.
 */
public class InstanceModelAssemblyReference
    extends AbstractAssemblyInstance<
        IContainerModelAbsolute,
        IAssemblyDefinition,
        IAssemblyInstanceAbsolute,
        IBindingDefinitionModelAssembly>
    implements IAssemblyInstanceAbsolute, IBindingInstance,
    IFeatureInstanceModelGroupAs {
  @NonNull
  private final AssemblyReference binding;
  @NonNull
  private final IAssemblyDefinition definition;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @NonNull
  private final IGroupAs groupAs;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new assembly reference.
   *
   * @param binding
   *          the assembly reference instance object bound to a Java class
   * @param bindingInstance
   *          the Metaschema instance for the bound object
   * @param position
   *          the zero-based position of this bound object relative to its bound
   *          object siblings
   * @param definition
   *          the referenced global assembly definition
   * @param parent
   *          the assembly definition containing this binding
   */
  public InstanceModelAssemblyReference(
      @NonNull AssemblyReference binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IAssemblyDefinition definition,
      @NonNull IContainerModelAbsolute parent) {
    super(parent);
    this.binding = binding;
    this.definition = definition;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
    this.groupAs = ModelSupport.groupAs(binding.getGroupAs(), parent.getOwningDefinition().getContainingModule());
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingModule().getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
  }

  @Override
  public IAssemblyDefinition getDefinition() {
    return definition;
  }

  /**
   * Gets the underlying binding object for this assembly reference.
   *
   * @return the binding object
   */
  @NonNull
  protected AssemblyReference getBinding() {
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
  public String getName() {
    return getDefinition().getName();
  }

  @Override
  public Integer getIndex() {
    return ModelSupport.index(getBinding().getIndex());
  }

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
  public int getMinOccurs() {
    BigInteger min = getBinding().getMinOccurs();
    return min == null ? DEFAULT_GROUP_AS_MIN_OCCURS : min.intValueExact();
  }

  @Override
  public int getMaxOccurs() {
    String max = getBinding().getMaxOccurs();
    return max == null ? DEFAULT_GROUP_AS_MAX_OCCURS : ModelSupport.maxOccurs(max);
  }
}
