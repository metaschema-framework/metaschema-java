/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.AbstractInlineFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IContainerFlagSupport;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.constraint.IValueConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.ValueConstraintSet;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingDefinitionModel;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingInstance;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FieldConstraints;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.JsonValueKeyFlag;

import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

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

  public InstanceModelGroupedFieldInline(
      @NonNull AssemblyModel.ChoiceGroup.DefineField binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IChoiceGroupInstance parent) {
    super(parent);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
    this.javaTypeAdapter = ModelSupport.dataType(binding.getAsType());
    this.defaultValue = ModelSupport.defaultValue(binding.getDefault(), this.javaTypeAdapter);
    this.flagContainer = ObjectUtils.notNull(Lazy.lazy(() -> FlagContainerSupport.newFlagContainer(
        binding.getFlags(),
        bindingInstance,
        this,
        getParentContainer().getJsonKeyFlagInstanceName())));
    this.valueConstraints = ObjectUtils.notNull(Lazy.lazy(() -> {
      IValueConstrained retval = new ValueConstraintSet();
      FieldConstraints constraints = binding.getConstraint();
      if (constraints != null) {
        ConstraintBindingSupport.parse(
            retval,
            constraints,
            parent.getOwningDefinition().getContainingModule().getSource());
      }
      return retval;
    }));
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.lazy(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingDefinition().getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getXmlQName())
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
      if (flagName != null) {
        String namespace = getXmlNamespace();
        retval = getFlagInstanceByName(
            namespace == null
                ? EQNameFactory.of(flagName)
                : EQNameFactory.of(namespace, flagName));
      }
    }
    return retval;
  }

  @Override
  public String getJsonValueKeyName() {
    return getBinding().getJsonValueKey();
  }
}
