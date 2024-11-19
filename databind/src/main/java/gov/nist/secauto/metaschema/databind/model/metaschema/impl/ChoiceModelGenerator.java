/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.DefaultChoiceModelBuilder;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IContainerModelAbsolute;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyModel;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.AssemblyReference;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.FieldReference;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.InlineDefineAssembly;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.InlineDefineField;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Supports building the model contents of a Metaschema choice instance.
 * <p>
 * This class is not thread safe.
 */
public final class ChoiceModelGenerator
    extends AbstractAbsoluteModelGenerator<
        IContainerModelAbsolute,
        DefaultChoiceModelBuilder<
            IModelInstanceAbsolute,
            INamedModelInstanceAbsolute,
            IFieldInstanceAbsolute,
            IAssemblyInstanceAbsolute>> {

  /**
   * Construct a new choice model container.
   *
   * @param binding
   *          the choice model object bound to a Java class
   * @param bindingInstance
   *          the Metaschema module instance for the bound model object
   * @param parent
   *          the assembly definition containing this container
   * @param nodeItemFactory
   *          the node item factory used to generate child nodes
   * @return the container
   */
  @SuppressWarnings("PMD.ShortMethodName")
  public static IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> of(
          @Nullable AssemblyModel.Choice binding,
          @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
          @NonNull IChoiceInstance parent,
          @NonNull INodeItemFactory nodeItemFactory) {
    List<Object> instances;
    return binding == null || (instances = binding.getChoices()) == null || instances.isEmpty()
        ? IContainerModelSupport.empty()
        : newInstance(
            binding,
            bindingInstance,
            parent,
            nodeItemFactory);
  }

  private static IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> newInstance(
          @NonNull AssemblyModel.Choice binding,
          @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
          @NonNull IChoiceInstance parent,
          @NonNull INodeItemFactory nodeItemFactory) {
    ChoiceModelGenerator generator = new ChoiceModelGenerator(parent, nodeItemFactory);

    // TODO: make "instances" a constant
    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        bindingInstance.getDefinition().getChoiceGroupInstanceByName("choices"));

    ObjectUtils.notNull(binding.getChoices()).forEach(obj -> {
      assert obj != null;
      IBoundInstanceModelGroupedAssembly objInstance
          = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

      if (obj instanceof AssemblyReference) {
        generator.addAssemblyInstance((AssemblyReference) obj, objInstance);
      } else if (obj instanceof InlineDefineAssembly) {
        generator.addAssemblyInstance((InlineDefineAssembly) obj, objInstance);
      } else if (obj instanceof FieldReference) {
        generator.addFieldInstance((FieldReference) obj, objInstance);
      } else if (obj instanceof InlineDefineField) {
        generator.addFieldInstance((InlineDefineField) obj, objInstance);
      } else {
        throw new UnsupportedOperationException(String.format("Unknown model instance class: %s", obj.getClass()));
      }
    });

    return generator.getBuilder().buildChoice();
  }

  private ChoiceModelGenerator(
      @NonNull IContainerModelAbsolute parent,
      @NonNull INodeItemFactory nodeItemFactory) {
    super(
        parent,
        nodeItemFactory,
        new DefaultChoiceModelBuilder<>());
  }
}
