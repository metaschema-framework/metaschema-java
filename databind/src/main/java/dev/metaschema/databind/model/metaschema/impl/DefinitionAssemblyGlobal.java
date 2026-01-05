/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.datatype.markup.MarkupMultiline;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItemFactory;
import dev.metaschema.core.model.AbstractGlobalAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstance;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IAttributable;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IContainerModelAssemblySupport;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.AssemblyConstraintSet;
import dev.metaschema.core.model.constraint.IModelConstrained;
import dev.metaschema.core.model.MetaschemaModelConstants;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import dev.metaschema.databind.model.metaschema.IBindingMetaschemaModule;
import dev.metaschema.databind.model.metaschema.binding.AssemblyConstraints;
import dev.metaschema.databind.model.metaschema.binding.JsonKey;
import dev.metaschema.databind.model.metaschema.binding.METASCHEMA;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Implementation of a global assembly definition from binding data.
 * <p>
 * This class represents an assembly definition that is declared at the module
 * level and can be referenced by instances.
 */
public class DefinitionAssemblyGlobal
    extends AbstractGlobalAssemblyDefinition<
        IBindingMetaschemaModule,
        IAssemblyInstance,
        IFlagInstance,
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute,
        IChoiceInstance,
        IChoiceGroupInstance>
    implements IBindingDefinitionModelAssembly {
  @NonNull
  private final METASCHEMA.DefineAssembly binding;
  @NonNull
  private final Map<IAttributable.Key, Set<String>> properties;
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
  @NonNull
  private final Lazy<IAssemblyNodeItem> boundNodeItem;

  /**
   * Construct a new global assembly definition.
   *
   * @param binding
   *          the assembly reference object bound to a Java class
   * @param bindingInstance
   *          the Metaschema module instance for the bound assembly reference
   *          object
   * @param position
   *          the zero-based position of this instance relative to its bound
   *          object siblings
   * @param module
   *          the containing Metaschema module
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   */
  public DefinitionAssemblyGlobal(
      @NonNull METASCHEMA.DefineAssembly binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      int position,
      @NonNull IBindingMetaschemaModule module,
      @NonNull INodeItemFactory nodeItemFactory) {
    super(module);
    this.binding = binding;
    this.properties = ModelSupport.parseProperties(ObjectUtils.requireNonNull(binding.getProps()));
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

    ISource source = module.getSource();

    this.modelConstraints = ObjectUtils.notNull(Lazy.of(() -> {
      IModelConstrained retval = new AssemblyConstraintSet(source);
      AssemblyConstraints constraints = getBinding().getConstraint();
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

  @NonNull
  private METASCHEMA.DefineAssembly getBinding() {
    return binding;
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

  /**
   * {@inheritDoc}
   * <p>
   * Returns choice instances from the model container.
   */
  @Override
  public List<IChoiceInstance> getChoiceInstances() {
    return getModelContainer().getChoiceInstances();
  }

  @Override
  public IModelConstrained getConstraintSupport() {
    return ObjectUtils.notNull(modelConstraints.get());
  }

  @Override
  public IAssemblyNodeItem getSourceNodeItem() {
    return ObjectUtils.notNull(boundNodeItem.get());
  }

  @Override
  public Map<IAttributable.Key, Set<String>> getProperties() {
    return properties;
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
  public boolean isRoot() {
    return getRootName() != null || getRootIndex() != null;
  }

  @Override
  public String getRootName() {
    return ModelSupport.rootName(getBinding().getRootName());
  }

  @Override
  public Integer getRootIndex() {
    return ModelSupport.rootIndex(getBinding().getRootName());
  }
}
