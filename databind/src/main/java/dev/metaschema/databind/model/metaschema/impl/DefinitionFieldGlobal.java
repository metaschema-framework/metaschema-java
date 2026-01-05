/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.model.AbstractGlobalFieldDefinition;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.model.constraint.ValueConstraintSet;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModel;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.FieldConstraints;
import dev.metaschema.databind.model.metaschema.binding.JsonKey;
import dev.metaschema.databind.model.metaschema.binding.JsonValueKeyFlag;
import dev.metaschema.databind.model.metaschema.binding.METASCHEMA;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of a global field definition from binding data.
 * <p>
 * This class represents a field definition that is declared at the module level
 * and can be referenced by instances.
 */
public class DefinitionFieldGlobal
    extends AbstractGlobalFieldDefinition<IBindingMetaschemaModule, IFieldInstance, IFlagInstance>
    implements IBindingDefinitionModel {
  @NonNull
  private final METASCHEMA.DefineField binding;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @NonNull
  private final IDataTypeAdapter<?> javaTypeAdapter;
  @Nullable
  private final Object defaultValue;
  @NonNull
  private final Lazy<IContainerFlagSupport<IFlagInstance>> flagContainer;
  @NonNull
  private final Lazy<IValueConstrained> valueConstraints;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new global field definition from binding data.
   *
   * @param binding
   *          the underlying bound field definition object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings
   * @param module
   *          the Metaschema module containing this binding
   */
  public DefinitionFieldGlobal(
      @NonNull METASCHEMA.DefineField binding,
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
    this.flagContainer = ObjectUtils.notNull(Lazy.of(() -> {
      JsonKey jsonKey = binding.getJsonKey();
      return FlagContainerSupport.newFlagContainer(
          binding.getFlags(),
          bindingInstance,
          this,
          jsonKey == null ? null : jsonKey.getFlagRef());
    }));
    this.valueConstraints = ObjectUtils.notNull(Lazy.of(() -> {
      IValueConstrained retval = new ValueConstraintSet(source);
      FieldConstraints constraints = binding.getConstraint();
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
   * Gets the underlying binding object for this field definition.
   *
   * @return the binding object
   */
  @NonNull
  protected METASCHEMA.DefineField getBinding() {
    return binding;
  }

  @Override
  public IContainerFlagSupport<IFlagInstance> getFlagContainer() {
    return ObjectUtils.notNull(flagContainer.get());
  }

  @Override
  public IValueConstrained getConstraintSupport() {
    return ObjectUtils.notNull(valueConstraints.get());
  }

  @Override
  public IDataTypeAdapter<?> getJavaTypeAdapter() {
    return javaTypeAdapter;
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
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  // ---------------------------------------
  // - Start binding driven code - CPD-OFF -
  // ---------------------------------------

  @Override
  public String getFormalName() {
    return getBinding().getFormalName();
  }

  @Override
  public MarkupLine getDescription() {
    return getBinding().getDescription();
  }

  @Override
  public String getName() {
    return ObjectUtils.notNull(getBinding().getName());
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

  @Override
  public IFlagInstance getJsonValueKeyFlagInstance() {
    JsonValueKeyFlag obj = getBinding().getJsonValueKeyFlag();
    String name = obj == null ? null : obj.getFlagRef();
    return name == null ? null
        : ObjectUtils.requireNonNull(getFlagInstanceByName(
            ModuleUtils.parseFlagName(getContainingModule(), name).getIndexPosition()));
  }

  @Override
  public String getJsonValueKeyName() {
    return getBinding().getJsonValueKey();
  }

}
