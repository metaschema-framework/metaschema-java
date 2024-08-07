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
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.AssemblyModel;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.AssemblyModel.Choice;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.AssemblyModel.ChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.AssemblyReference;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.FieldReference;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.InlineDefineAssembly;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.InlineDefineField;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingDefinitionModelAssembly;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

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
  private final Map<QName, INamedModelInstanceAbsolute> namedModelInstances;
  @NonNull
  private final Map<QName, IFieldInstanceAbsolute> fieldInstances;
  @NonNull
  private final Map<QName, IAssemblyInstanceAbsolute> assemblyInstances;
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
  @SuppressWarnings({ "PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseConcurrentHashMap", "PMD.PrematureDeclaration" })
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  protected AssemblyModelContainerSupport(
      @NonNull AssemblyModel model,
      @NonNull IBoundInstanceModelAssembly modelInstance,
      @NonNull IBindingDefinitionModelAssembly parent,
      @NonNull INodeItemFactory nodeItemFactory) {

    // create temporary collections to store the child binding objects
    final List<IModelInstanceAbsolute> modelInstances = new LinkedList<>();
    final Map<QName, INamedModelInstanceAbsolute> namedModelInstances = new LinkedHashMap<>();
    final Map<QName, IFieldInstanceAbsolute> fieldInstances = new LinkedHashMap<>();
    final Map<QName, IAssemblyInstanceAbsolute> assemblyInstances = new LinkedHashMap<>();
    final List<IChoiceInstance> choiceInstances = new LinkedList<>();
    final Map<String, IChoiceGroupInstance> choiceGroupInstances = new LinkedHashMap<>();

    // create counters to track child positions
    int assemblyReferencePosition = 0;
    int assemblyInlineDefinitionPosition = 0;
    int fieldReferencePosition = 0;
    int fieldInlineDefinitionPosition = 0;
    int choicePosition = 0;
    int choiceGroupPosition = 0;

    // TODO: make "instances" a constant
    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        modelInstance.getDefinition().getChoiceGroupInstanceByName("instances"));
    for (Object obj : ObjectUtils.notNull(model.getInstances())) {
      IBoundInstanceModelGroupedAssembly objInstance
          = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

      if (obj instanceof AssemblyReference) {
        IAssemblyInstanceAbsolute assembly = newInstance(
            (AssemblyReference) obj,
            objInstance,
            assemblyReferencePosition++,
            parent);
        addInstance(assembly, modelInstances, namedModelInstances, assemblyInstances);
      } else if (obj instanceof InlineDefineAssembly) {
        IAssemblyInstanceAbsolute assembly = new InstanceModelAssemblyInline(
            (InlineDefineAssembly) obj,
            objInstance,
            assemblyInlineDefinitionPosition++,
            parent,
            nodeItemFactory);
        addInstance(assembly, modelInstances, namedModelInstances, assemblyInstances);
      } else if (obj instanceof FieldReference) {
        IFieldInstanceAbsolute field = newInstance(
            (FieldReference) obj,
            objInstance,
            fieldReferencePosition++,
            parent);
        addInstance(field, modelInstances, namedModelInstances, fieldInstances);
      } else if (obj instanceof InlineDefineField) {
        IFieldInstanceAbsolute field = new InstanceModelFieldInline(
            (InlineDefineField) obj,
            objInstance,
            fieldInlineDefinitionPosition++,
            parent);
        addInstance(field, modelInstances, namedModelInstances, fieldInstances);
      } else if (obj instanceof AssemblyModel.Choice) {
        IChoiceInstance choice = new InstanceModelChoice(
            (Choice) obj,
            objInstance,
            choicePosition++,
            parent,
            nodeItemFactory);
        modelInstances.add(choice);
        choiceInstances.add(choice);
      } else if (obj instanceof AssemblyModel.ChoiceGroup) {
        IChoiceGroupInstance choiceGroup = new InstanceModelChoiceGroup(
            (ChoiceGroup) obj,
            objInstance,
            choiceGroupPosition++,
            parent,
            nodeItemFactory);
        modelInstances.add(choiceGroup);
        choiceGroupInstances.put(choiceGroup.getGroupAsName(), choiceGroup);
      } else {
        throw new UnsupportedOperationException(String.format("Unknown model instance class: %s", obj.getClass()));
      }
    }

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
  public Map<QName, INamedModelInstanceAbsolute> getNamedModelInstanceMap() {
    return namedModelInstances;
  }

  @Override
  public Map<QName, IFieldInstanceAbsolute> getFieldInstanceMap() {
    return fieldInstances;
  }

  @Override
  public Map<QName, IAssemblyInstanceAbsolute> getAssemblyInstanceMap() {
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
