/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.model.AbstractInlineAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IContainerModelAbsolute;
import dev.metaschema.core.model.IContainerModelAssemblySupport;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelElementVisitor;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.AssemblyConstraintSet;
import dev.metaschema.core.model.constraint.IModelConstrained;
import dev.metaschema.core.model.MetaschemaModelConstants;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.IGroupAs;
import dev.metaschema.databind.model.impl.IFeatureInstanceModelGroupAs;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingInstance;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyConstraints;
import dev.metaschema.databind.model.metaschema.binding.InlineDefineAssembly;
import dev.metaschema.databind.model.metaschema.binding.JsonKey;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of an inline assembly instance from binding data.
 * <p>
 * This class represents an assembly that is defined inline within its
 * containing assembly rather than as a reference.
 */
public class InstanceModelAssemblyInline
    extends AbstractInlineAssemblyDefinition<
        IContainerModelAbsolute,
        IAssemblyDefinition,
        IAssemblyInstanceAbsolute,
        IBindingDefinitionModelAssembly,
        IFlagInstance,
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute,
        IChoiceInstance,
        IChoiceGroupInstance>
    implements IAssemblyInstanceAbsolute, IBindingInstance, IBindingDefinitionModelAssembly,
    IFeatureInstanceModelGroupAs {
  @NonNull
  private final InlineDefineAssembly binding;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
  @NonNull
  private final IGroupAs groupAs;
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;
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

  /**
   * Construct a new assembly instance that defines the assembly inline.
   *
   * @param binding
   *          the assembly reference instance object bound to a Java class
   * @param bindingInstance
   *          the Metaschema module instance for the bound object
   * @param position
   *          the zero-based position of this bound object relative to its bound
   *          object siblings
   * @param parent
   *          the assembly definition containing this binding
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   */
  public InstanceModelAssemblyInline(
      @NonNull InlineDefineAssembly binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IContainerModelAbsolute parent,
      @NonNull INodeItemFactory nodeItemFactory) {
    super(parent);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
    this.groupAs = ModelSupport.groupAs(binding.getGroupAs(), parent.getOwningDefinition().getContainingModule());
    this.boundNodeItem = ObjectUtils.notNull(
        Lazy.of(() -> (IAssemblyNodeItem) ObjectUtils.notNull(getContainingDefinition().getSourceNodeItem())
            .getModelItemsByName(bindingInstance.getQName())
            .get(position)));
    this.flagContainer = ObjectUtils.notNull(Lazy.of(() -> {
      JsonKey jsonKey = getBinding().getJsonKey();
      return FlagContainerSupport.newFlagContainer(
          binding.getFlags(),
          bindingInstance,
          this,
          jsonKey == null ? null : jsonKey.getFlagRef());
    }));
    this.modelContainer = ObjectUtils.notNull(Lazy.of(() -> AssemblyModelGenerator.of(
        binding.getModel(),
        ObjectUtils.requireNonNull(bindingInstance.getDefinition()
            .getAssemblyInstanceByName(MetaschemaModelConstants.MODEL_QNAME.getIndexPosition())),
        this,
        nodeItemFactory)));

    ISource source = parent.getOwningDefinition().getContainingModule().getSource();

    this.modelConstraints = ObjectUtils.notNull(Lazy.of(() -> {
      IModelConstrained retval = new AssemblyConstraintSet(source);
      AssemblyConstraints constraints = getBinding().getConstraint();
      if (constraints != null) {
        ConstraintBindingSupport.parse(
            retval,
            constraints,
            source);
      }
      return retval;
    }));
  }

  /**
   * Gets the underlying binding object for this inline assembly instance.
   *
   * @return the binding object
   */
  @NonNull
  protected InlineDefineAssembly getBinding() {
    getContainingDefinition();
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
  public <CONTEXT, RESULT> RESULT accept(IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return IAssemblyInstanceAbsolute.super.accept(visitor, context);
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
