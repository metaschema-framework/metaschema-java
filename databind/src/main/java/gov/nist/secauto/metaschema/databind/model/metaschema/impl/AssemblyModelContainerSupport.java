/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAssemblySupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.Choice;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel.ChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyReference;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FieldReference;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.InlineDefineAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.InlineDefineField;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class AssemblyModelContainerSupport
    extends AbstractBindingModelContainerSupport
    implements IContainerModelAssemblySupport<
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute,
        IChoiceInstance,
        IChoiceGroupInstance> {
  @NonNull
  private final List<IModelInstanceAbsolute> modelInstances;
  @NonNull
  private final Map<IEnhancedQName, INamedModelInstanceAbsolute> namedModelInstances;
  @NonNull
  private final Map<IEnhancedQName, IFieldInstanceAbsolute> fieldInstances;
  @NonNull
  private final Map<IEnhancedQName, IAssemblyInstanceAbsolute> assemblyInstances;
  @NonNull
  private final List<IChoiceInstance> choiceInstances;
  @NonNull
  private final Map<String, IChoiceGroupInstance> choiceGroupInstances;

  @SuppressWarnings("PMD.ShortMethodName")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public static IContainerModelAssemblySupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute,
      IChoiceInstance,
      IChoiceGroupInstance> of(
          @Nullable AssemblyModel binding,
          @NonNull IBoundInstanceModelAssembly bindingInstance,
          @NonNull IBindingDefinitionModelAssembly parent,
          @NonNull INodeItemFactory nodeItemFactory) {
    List<Object> instances;
    return binding == null || (instances = binding.getInstances()) == null || instances.isEmpty()
        ? IContainerModelAssemblySupport.empty()
        : new AssemblyModelContainerSupport(
            binding,
            bindingInstance,
            parent,
            nodeItemFactory);
  }

  /**
   * Construct a new assembly model container.
   *
   * @param model
   *          the assembly model object bound to a Java class
   * @param modelInstance
   *          the Metaschema module instance for the bound model object
   * @param parent
   *          the assembly definition containing this container
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   */
  @SuppressWarnings({
      "PMD.AvoidInstantiatingObjectsInLoops",
      "PMD.UseConcurrentHashMap",
      "PMD.PrematureDeclaration",
      "PMD.NPathComplexity" })
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  protected AssemblyModelContainerSupport(
      @NonNull AssemblyModel model,
      @NonNull IBoundInstanceModelAssembly modelInstance,
      @NonNull IBindingDefinitionModelAssembly parent,
      @NonNull INodeItemFactory nodeItemFactory) {

    // create temporary collections to store the child binding objects
    final List<IModelInstanceAbsolute> modelInstances = new LinkedList<>();
    final Map<IEnhancedQName, INamedModelInstanceAbsolute> namedModelInstances = new LinkedHashMap<>();
    final Map<IEnhancedQName, IFieldInstanceAbsolute> fieldInstances = new LinkedHashMap<>();
    final Map<IEnhancedQName, IAssemblyInstanceAbsolute> assemblyInstances = new LinkedHashMap<>();
    final List<IChoiceInstance> choiceInstances = new LinkedList<>();
    final Map<String, IChoiceGroupInstance> choiceGroupInstances = new LinkedHashMap<>();

    // create counters to track child positions
    AtomicInteger assemblyReferencePosition = new AtomicInteger();
    AtomicInteger assemblyInlineDefinitionPosition = new AtomicInteger();
    AtomicInteger fieldReferencePosition = new AtomicInteger();
    AtomicInteger fieldInlineDefinitionPosition = new AtomicInteger();
    AtomicInteger choicePosition = new AtomicInteger();
    AtomicInteger choiceGroupPosition = new AtomicInteger();

    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        modelInstance.getDefinition()
            .getChoiceGroupInstanceByName(BindingConstants.METASCHEMA_CHOICE_GROUP_GROUP_AS_NAME));
    ObjectUtils.notNull(model.getInstances()).forEach(obj -> {
      assert obj != null;

      IBoundInstanceModelGroupedAssembly objInstance
          = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

      if (obj instanceof AssemblyReference) {
        IAssemblyInstanceAbsolute assembly = newInstance(
            (AssemblyReference) obj,
            objInstance,
            assemblyReferencePosition.getAndIncrement(),
            parent);
        addInstance(assembly, modelInstances, namedModelInstances, assemblyInstances);
      } else if (obj instanceof InlineDefineAssembly) {
        IAssemblyInstanceAbsolute assembly = new InstanceModelAssemblyInline(
            (InlineDefineAssembly) obj,
            objInstance,
            assemblyInlineDefinitionPosition.getAndIncrement(),
            parent,
            nodeItemFactory);
        addInstance(assembly, modelInstances, namedModelInstances, assemblyInstances);
      } else if (obj instanceof FieldReference) {
        IFieldInstanceAbsolute field = newInstance(
            (FieldReference) obj,
            objInstance,
            fieldReferencePosition.getAndIncrement(),
            parent);
        addInstance(field, modelInstances, namedModelInstances, fieldInstances);
      } else if (obj instanceof InlineDefineField) {
        IFieldInstanceAbsolute field = new InstanceModelFieldInline(
            (InlineDefineField) obj,
            objInstance,
            fieldInlineDefinitionPosition.getAndIncrement(),
            parent);
        addInstance(field, modelInstances, namedModelInstances, fieldInstances);
      } else if (obj instanceof AssemblyModel.Choice) {
        IChoiceInstance choice = new InstanceModelChoice(
            (Choice) obj,
            objInstance,
            choicePosition.getAndIncrement(),
            parent,
            nodeItemFactory);
        addInstance(choice, modelInstances, choiceInstances);
      } else if (obj instanceof AssemblyModel.ChoiceGroup) {
        IChoiceGroupInstance choiceGroup = new InstanceModelChoiceGroup(
            (ChoiceGroup) obj,
            objInstance,
            choiceGroupPosition.getAndIncrement(),
            parent,
            nodeItemFactory);
        addInstance(choiceGroup, modelInstances, choiceGroupInstances);
      } else {
        throw new UnsupportedOperationException(String.format("Unknown model instance class: %s", obj.getClass()));
      }
    });

    this.modelInstances = modelInstances.isEmpty()
        ? CollectionUtil.emptyList()
        : CollectionUtil.unmodifiableList(modelInstances);
    this.namedModelInstances = namedModelInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(namedModelInstances);
    this.fieldInstances = fieldInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(fieldInstances);
    this.assemblyInstances = assemblyInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(assemblyInstances);
    this.choiceInstances = choiceInstances.isEmpty()
        ? CollectionUtil.emptyList()
        : CollectionUtil.unmodifiableList(choiceInstances);
    this.choiceGroupInstances = choiceGroupInstances.isEmpty()
        ? CollectionUtil.emptyMap()
        : CollectionUtil.unmodifiableMap(choiceGroupInstances);
  }

  @Override
  public List<IModelInstanceAbsolute> getModelInstances() {
    return modelInstances;
  }

  @Override
  public Map<IEnhancedQName, INamedModelInstanceAbsolute> getNamedModelInstanceMap() {
    return namedModelInstances;
  }

  @Override
  public Map<IEnhancedQName, IFieldInstanceAbsolute> getFieldInstanceMap() {
    return fieldInstances;
  }

  @Override
  public Map<IEnhancedQName, IAssemblyInstanceAbsolute> getAssemblyInstanceMap() {
    return assemblyInstances;
  }

  @Override
  public List<IChoiceInstance> getChoiceInstances() {
    return choiceInstances;
  }

  @Override
  public Map<String, IChoiceGroupInstance> getChoiceGroupInstanceMap() {
    return choiceGroupInstances;
  }
}
