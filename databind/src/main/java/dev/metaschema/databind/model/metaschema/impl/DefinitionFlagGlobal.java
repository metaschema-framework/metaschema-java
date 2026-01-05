/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.AbstractGlobalFlagDefinition;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.model.constraint.ValueConstraintSet;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.FlagConstraints;
import dev.metaschema.databind.model.metaschema.binding.METASCHEMA;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of a global flag definition from binding data.
 * <p>
 * This class represents a flag definition that is declared at the module level
 * and can be referenced by instances.
 */
public class DefinitionFlagGlobal
    extends AbstractGlobalFlagDefinition<IBindingMetaschemaModule, IFlagInstance> {
  @NonNull
  private final METASCHEMA.DefineFlag binding;
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
   * Construct a new Metaschema module flag definition binding using an underlying
   * bound class that describes the flag.
   *
   * @param binding
   *          the underlying bound class
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings
   * @param module
   *          the Metaschema module containing this binding
   */
  public DefinitionFlagGlobal(
      @NonNull METASCHEMA.DefineFlag binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IBindingMetaschemaModule module) {
    super(module);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));

    ISource source = module.getSource();

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
    this.boundNodeItem = ObjectUtils.notNull(Lazy.of(() -> ObjectUtils.requireNonNull(ModelSupport.toNodeItem(
        module,
        bindingInstance.getQName(),
        position))));
  }

  /**
   * Gets the underlying binding object for this flag definition.
   *
   * @return the binding object
   */
  @NonNull
  protected METASCHEMA.DefineFlag getBinding() {
    return binding;
  }

  @Override
  public IValueConstrained getConstraintSupport() {
    return ObjectUtils.notNull(valueConstraints.get());
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
  public ModuleScope getModuleScope() {
    return ModelSupport.moduleScope(ObjectUtils.requireNonNull(getBinding().getScope()));
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

  /**
   * Gets the node item representing this flag definition in the Metapath data
   * model.
   *
   * @return the node item for this flag definition
   */
  @NonNull
  public INodeItem getNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }
}
