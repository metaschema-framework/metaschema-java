/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.AssemblyModel.Choice;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.AssemblyReference;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.FieldReference;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.InlineDefineAssembly;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.InlineDefineField;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class ChoiceModelContainerSupport
    extends AbstractBindingModelContainerSupport {
  @NonNull
  private final List<IModelInstanceAbsolute> modelInstances;
  @NonNull
  private final Map<QName, INamedModelInstanceAbsolute> namedModelInstances;
  @NonNull
  private final Map<QName, IFieldInstanceAbsolute> fieldInstances;
  @NonNull
  private final Map<QName, IAssemblyInstanceAbsolute> assemblyInstances;

  @SuppressWarnings("PMD.ShortMethodName")
  public static IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> of(
          @Nullable Choice binding,
          @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
          @NonNull IChoiceInstance parent,
          @NonNull INodeItemFactory nodeItemFactory) {
    List<Object> instances;
    return binding == null || (instances = binding.getChoices()) == null || instances.isEmpty()
        ? IContainerModelSupport.empty()
        : new ChoiceModelContainerSupport(
            binding,
            bindingInstance,
            parent,
            nodeItemFactory);
  }

  /**
   * Construct a new assembly model container.
   *
   * @param binding
   *          the choice model object bound to a Java class
   * @param bindingInstance
   *          the Metaschema module instance for the bound model object
   * @param parent
   *          the assembly definition containing this container
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   */
  @SuppressWarnings({ "PMD.AvoidInstantiatingObjectsInLoops", "PMD.UseConcurrentHashMap", "PMD.PrematureDeclaration" })
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public ChoiceModelContainerSupport(
      @NonNull Choice binding,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      @NonNull IChoiceInstance parent,
      @NonNull INodeItemFactory nodeItemFactory) {

    // create temporary collections to store the child binding objects
    final List<IModelInstanceAbsolute> modelInstances = new LinkedList<>();
    final Map<QName, INamedModelInstanceAbsolute> namedModelInstances = new LinkedHashMap<>();
    final Map<QName, IFieldInstanceAbsolute> fieldInstances = new LinkedHashMap<>();
    final Map<QName, IAssemblyInstanceAbsolute> assemblyInstances = new LinkedHashMap<>();

    // create counters to track child positions
    int assemblyReferencePosition = 0;
    int assemblyInlineDefinitionPosition = 0;
    int fieldReferencePosition = 0;
    int fieldInlineDefinitionPosition = 0;

    // TODO: make "instances" a constant
    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        bindingInstance.getDefinition().getChoiceGroupInstanceByName("choices"));
    for (Object obj : ObjectUtils.notNull(binding.getChoices())) {
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
}
