/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.model.AbstractInlineFlagDefinition;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IFeatureValueless;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.model.constraint.ValueConstraintSet;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModel;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.FlagConstraints;
import dev.metaschema.databind.model.metaschema.binding.InlineDefineFlag;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of an inline flag instance from binding data.
 * <p>
 * This class represents a flag that is defined inline within its containing
 * definition rather than as a reference.
 */
public class InstanceFlagInline
    extends AbstractInlineFlagDefinition<
        IBindingDefinitionModel,
        IFlagDefinition,
        IFlagInstance>
    implements IFeatureValueless, IBindingInstance {
  @NonNull
  private final InlineDefineFlag binding;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @NonNull
  private final IDataTypeAdapter<?> javaTypeAdapter;
  @Nullable
  private final Object defaultValue;
  @NonNull
  private final Lazy<IValueConstrained> valueConstraints;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new inline flag instance from binding data.
   *
   * @param binding
   *          the underlying bound inline flag definition object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings
   * @param parent
   *          the parent definition model containing this flag
   */
  public InstanceFlagInline(
      @NonNull InlineDefineFlag binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IBindingDefinitionModel parent) {
    super(parent);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));

    ISource source = parent.getContainingModule().getSource();

    this.javaTypeAdapter = ModelSupport.dataType(
        binding.getAsType(),
        source);
    this.defaultValue = ModelSupport.defaultValue(binding.getDefault(), this.javaTypeAdapter);
    this.valueConstraints = ObjectUtils.notNull(Lazy.of(() -> {
      IValueConstrained retval = new ValueConstraintSet(source);
      FlagConstraints constraints = binding.getConstraint();
      if (constraints != null) {
        ConstraintBindingSupport.parse(retval, constraints, source);
      }
      return retval;
    }));
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(parent.getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
  }

  /**
   * Gets the underlying binding object for this inline flag instance.
   *
   * @return the binding object
   */
  @NonNull
  protected InlineDefineFlag getBinding() {
    return binding;
  }

  @Override
  public IBindingMetaschemaModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @SuppressWarnings("null")
  @Override
  public IValueConstrained getConstraintSupport() {
    return valueConstraints.get();
  }

  @Override
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return properties;
  }

  @Override
  public IDataTypeAdapter<?> getJavaTypeAdapter() {
    return javaTypeAdapter;
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String getName() {
    return ObjectUtils.notNull(getBinding().getName());
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
  public MarkupMultiline getRemarks() {
    return ModelSupport.remarks(getBinding().getRemarks());
  }

  @Override
  public boolean isRequired() {
    return ModelSupport.yesOrNo(getBinding().getRequired());
  }

  // =================================
  // IAssemnblyNodeItem implementation
  // =================================
}
