/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.model.IContainerFlagSupport;
import gov.nist.secauto.metaschema.core.model.IFlagContainerBuilder;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.FlagReference;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.InlineDefineFlag;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingDefinitionModel;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD.OnlyOneReturn")
public final class FlagContainerSupport {
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  @NonNull
  public static IContainerFlagSupport<IFlagInstance> newFlagContainer(
      @Nullable List<Object> flags,
      @NonNull IBoundInstanceModelGroupedAssembly bindingInstance,
      @NonNull IBindingDefinitionModel parent,
      @Nullable String jsonKeyName) {
    if (flags == null || flags.isEmpty()) {
      return IContainerFlagSupport.empty();
    }

    // create temporary collections to store the child binding objects
    IFlagContainerBuilder<IFlagInstance> builder = jsonKeyName == null
        ? IContainerFlagSupport.builder()
        : IContainerFlagSupport.builder(parent.getContainingModule().toFlagQName(jsonKeyName));

    // create counter to track child positions
    int flagReferencePosition = 0;
    int flagInlineDefinitionPosition = 0;

    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        bindingInstance.getDefinition().getChoiceGroupInstanceByName("flags"));
    for (Object obj : flags) {
      IBoundInstanceModelGroupedAssembly objInstance
          = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

      IFlagInstance flag;
      if (obj instanceof InlineDefineFlag) {
        flag = new InstanceFlagInline(
            (InlineDefineFlag) obj,
            objInstance,
            flagInlineDefinitionPosition++,
            parent);
      } else if (obj instanceof FlagReference) {
        flag = newFlagInstance(
            (FlagReference) obj,
            objInstance,
            flagReferencePosition++,
            parent);
      } else {
        throw new UnsupportedOperationException(String.format("Unknown flag instance class: %s", obj.getClass()));
      }

      builder.flag(flag);
    }

    return builder.build();
  }

  @NonNull
  private static IFlagInstance newFlagInstance(
      @NonNull FlagReference obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance,
      int position,
      @NonNull IBindingDefinitionModel parent) {
    IModule module = parent.getContainingModule();

    QName qname = module.toFlagQName(ObjectUtils.requireNonNull(obj.getRef()));
    IFlagDefinition definition = module.getScopedFlagDefinitionByName(qname);
    if (definition == null) {
      throw new IllegalStateException(
          String.format("Unable to resolve flag reference '%s' in definition '%s' in module '%s'",
              qname,
              parent.getName(),
              module.getShortName()));
    }
    return new InstanceFlagReference(obj, objInstance, position, definition, parent);
  }

  private FlagContainerSupport() {
    // disable construction
  }

}
