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
import dev.metaschema.core.model.AbstractFlagInstance;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IFeatureDefinitionReferenceInstance;
import dev.metaschema.core.model.IFeatureValueless;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModel;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.FlagReference;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of a flag instance reference from binding data.
 * <p>
 * This class represents a reference to a globally defined flag within a
 * containing definition.
 */
public class InstanceFlagReference
    extends AbstractFlagInstance<
        IBindingDefinitionModel,
        IFlagDefinition, IFlagInstance>
    implements IFeatureValueless, IBindingInstance,
    IFeatureDefinitionReferenceInstance<IFlagDefinition, IFlagInstance> {
  @NonNull
  private final FlagReference binding;
  @NonNull
  private final IFlagDefinition definition;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @Nullable
  private final Object defaultValue;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new flag reference instance from binding data.
   *
   * @param binding
   *          the underlying bound flag reference object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings
   * @param definition
   *          the global flag definition being referenced
   * @param parent
   *          the parent definition model containing this reference
   */
  public InstanceFlagReference(
      @NonNull FlagReference binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IFlagDefinition definition,
      @NonNull IBindingDefinitionModel parent) {
    super(parent);
    this.binding = binding;
    this.definition = definition;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
    this.defaultValue = ModelSupport.defaultValue(binding.getDefault(), definition.getJavaTypeAdapter());
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(parent.getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
  }

  /**
   * Gets the underlying binding object for this flag reference.
   *
   * @return the binding object
   */
  @NonNull
  protected FlagReference getBinding() {
    return binding;
  }

  @Override
  public IBindingMetaschemaModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @Override
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  @Override
  public IFlagDefinition getDefinition() {
    return definition;
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return properties;
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String getName() {
    return getDefinition().getName();
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
  public MarkupMultiline getRemarks() {
    return ModelSupport.remarks(getBinding().getRemarks());
  }

  @Override
  public boolean isRequired() {
    return ModelSupport.yesOrNo(getBinding().getRequired());
  }
}
