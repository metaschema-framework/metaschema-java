/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAbsolute;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyReference;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FieldReference;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractBindingModelContainerSupport
    implements IContainerModelSupport<
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute> {

  protected static void addInstance(
      @NonNull IAssemblyInstanceAbsolute assembly,
      @NonNull List<IModelInstanceAbsolute> modelInstances,
      @NonNull Map<QName, INamedModelInstanceAbsolute> namedModelInstances,
      @NonNull Map<QName, IAssemblyInstanceAbsolute> assemblyInstances) {
    QName effectiveName = assembly.getXmlQName();
    modelInstances.add(assembly);
    namedModelInstances.put(effectiveName, assembly);
    assemblyInstances.put(effectiveName, assembly);
  }

  protected static void addInstance(
      @NonNull IFieldInstanceAbsolute field,
      @NonNull List<IModelInstanceAbsolute> modelInstances,
      @NonNull Map<QName, INamedModelInstanceAbsolute> namedModelInstances,
      @NonNull Map<QName, IFieldInstanceAbsolute> fieldInstances) {
    QName effectiveName = field.getXmlQName();
    modelInstances.add(field);
    namedModelInstances.put(effectiveName, field);
    fieldInstances.put(effectiveName, field);
  }

  protected static void addInstance(
      @NonNull IChoiceInstance choice,
      @NonNull List<IModelInstanceAbsolute> modelInstances,
      @NonNull List<IChoiceInstance> choiceInstances) {
    modelInstances.add(choice);
    choiceInstances.add(choice);
  }

  protected static void addInstance(
      @NonNull IChoiceGroupInstance choiceGroup,
      @NonNull List<IModelInstanceAbsolute> modelInstances,
      @NonNull Map<String, IChoiceGroupInstance> choiceGroupInstances) {
    modelInstances.add(choiceGroup);
    choiceGroupInstances.put(choiceGroup.getGroupAsName(), choiceGroup);
  }

  @NonNull
  protected static IAssemblyInstanceAbsolute newInstance(
      @NonNull AssemblyReference obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance,
      int position,
      @NonNull IContainerModelAbsolute parent) {
    IAssemblyDefinition owningDefinition = parent.getOwningDefinition();
    IModule module = owningDefinition.getContainingModule();

    String name = ObjectUtils.requireNonNull(obj.getRef());
    IAssemblyDefinition definition = module.getScopedAssemblyDefinitionByName(
        module.toModelQName(name));

    if (definition == null) {
      throw new IllegalStateException(
          String.format("Unable to resolve assembly reference '%s' in definition '%s' in module '%s'",
              name,
              owningDefinition.getName(),
              module.getShortName()));
    }
    return new InstanceModelAssemblyReference(obj, objInstance, position, definition, parent);
  }

  @NonNull
  protected static IFieldInstanceAbsolute newInstance(
      @NonNull FieldReference obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance,
      int position,
      @NonNull IContainerModelAbsolute parent) {
    IAssemblyDefinition owningDefinition = parent.getOwningDefinition();
    IModule module = owningDefinition.getContainingModule();

    String name = ObjectUtils.requireNonNull(obj.getRef());
    IFieldDefinition definition = module.getScopedFieldDefinitionByName(
        module.toModelQName(name));
    if (definition == null) {
      throw new IllegalStateException(
          String.format("Unable to resolve field reference '%s' in definition '%s' in module '%s'",
              name,
              owningDefinition.getName(),
              module.getShortName()));
    }
    return new InstanceModelFieldReference(obj, objInstance, position, definition, parent);
  }
}
