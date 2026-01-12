/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import java.util.Map;
import java.util.Set;

import dev.metaschema.core.datatype.IDataTypeAdapter;
import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.model.AbstractInlineFieldDefinition;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelElementVisitor;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IValueConstrained;
import dev.metaschema.core.model.constraint.ValueConstraintSet;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModel;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyModel;
import dev.metaschema.databind.model.metaschema.binding.FieldConstraints;
import dev.metaschema.databind.model.metaschema.binding.JsonValueKeyFlag;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of an inline grouped field instance from binding data.
 * <p>
 * This class represents an inline field member of a choice group.
 */
public class InstanceModelGroupedFieldInline
    extends AbstractInlineFieldDefinition<
        IChoiceGroupInstance,
        IFieldDefinition,
        IFieldInstanceGrouped,
        IBindingDefinitionModelAssembly,
        IFlagInstance>
    implements IFieldInstanceGrouped, IBindingInstance, IBindingDefinitionModel {
  @NonNull
  private final AssemblyModel.ChoiceGroup.DefineField binding;
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
   * Construct a new inline grouped field instance from binding data.
   *
   * @param binding
   *          the underlying bound inline field definition object
   * @param bindingInstance
   *          the assembly instance for the underlying bound class
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          siblings; must be a valid index within the model items
   * @param parent
   *          the parent choice group instance containing this field
   * @throws IndexOutOfBoundsException
   *           if the position is invalid when the source node item is accessed
   */
  public InstanceModelGroupedFieldInline(
      @NonNull AssemblyModel.ChoiceGroup.DefineField binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IChoiceGroupInstance parent) {
    super(parent);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));

    ISource source = parent.getContainingModule().getSource();

    this.javaTypeAdapter = ModelSupport.dataType(
        binding.getAsType(),
        source);
    this.defaultValue = ModelSupport.defaultValue(binding.getDefault(), this.javaTypeAdapter);
    this.flagContainer = ObjectUtils.notNull(Lazy.of(() -> FlagContainerSupport.newFlagContainer(
        binding.getFlags(),
        bindingInstance,
        this,
        getParentContainer().getJsonKeyFlagInstanceName())));
    this.valueConstraints = ObjectUtils.notNull(Lazy.of(() -> {
      IValueConstrained retval = new ValueConstraintSet(source);
      FieldConstraints constraints = binding.getConstraint();
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
  private AssemblyModel.ChoiceGroup.DefineField getBinding() {
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
  public IValueConstrained getConstraintSupport() {
    return ObjectUtils.notNull(valueConstraints.get());
  }

  @Override
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
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
  public <CONTEXT, RESULT> RESULT accept(IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return IFieldInstanceGrouped.super.accept(visitor, context);
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

  @Override
  public IFlagInstance getJsonValueKeyFlagInstance() {
    JsonValueKeyFlag obj = getBinding().getJsonValueKeyFlag();

    IFlagInstance retval = null;
    if (obj != null) {
      String flagName = obj.getFlagRef();
      String namespace = getQName().getNamespace();
      retval = getFlagInstanceByName(IEnhancedQName.of(namespace, flagName).getIndexPosition());
    }
    return retval;
  }

  @Override
  public String getJsonValueKeyName() {
    return getBinding().getJsonValueKey();
  }
}
