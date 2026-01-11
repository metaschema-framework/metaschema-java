/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema.impl;

import dev.metaschema.core.model.IContainerFlagSupport;
import dev.metaschema.core.model.IFlagContainerBuilder;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.util.ModuleUtils;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundInstanceModelChoiceGroup;
import dev.metaschema.databind.model.IBoundInstanceModelGroupedAssembly;
import dev.metaschema.databind.model.metaschema.IBindingDefinitionModel;
import dev.metaschema.databind.model.metaschema.binding.FlagReference;
import dev.metaschema.databind.model.metaschema.binding.InlineDefineFlag;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Support class for building flag containers from binding data.
 * <p>
 * This class provides utility methods for discovering and organizing flag
 * instances from Metaschema module bindings.
 */
public final class FlagContainerSupport {
  /**
   * Creates a new flag container from a list of flag binding objects.
   *
   * @param flags
   *          the list of flag binding objects, may be {@code null} or empty
   * @param bindingInstance
   *          the parent binding instance
   * @param parent
   *          the parent definition model containing the flags
   * @param jsonKeyName
   *          the JSON key flag name if applicable, or {@code null}
   * @return the flag container, or an empty container if no flags
   */
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
        : IContainerFlagSupport.builder(
            ModuleUtils.parseFlagName(parent.getContainingModule(), jsonKeyName).getIndexPosition());

    // create counter to track child positions
    AtomicInteger flagReferencePosition = new AtomicInteger();
    AtomicInteger flagInlineDefinitionPosition = new AtomicInteger();

    IBoundInstanceModelChoiceGroup instance = ObjectUtils.requireNonNull(
        bindingInstance.getDefinition().getChoiceGroupInstanceByName("flags"));

    flags.stream()
        .map(obj -> {
          assert obj != null;
          IBoundInstanceModelGroupedAssembly objInstance
              = (IBoundInstanceModelGroupedAssembly) instance.getItemInstance(obj);

          IFlagInstance flag;
          if (obj instanceof InlineDefineFlag) {
            flag = new InstanceFlagInline(
                (InlineDefineFlag) obj,
                objInstance,
                flagInlineDefinitionPosition.incrementAndGet(),
                parent);
          } else if (obj instanceof FlagReference) {
            flag = newFlagInstance(
                (FlagReference) obj,
                objInstance,
                flagReferencePosition.incrementAndGet(),
                parent);
          } else {
            throw new UnsupportedOperationException(String.format("Unknown flag instance class: %s", obj.getClass()));
          }
          return flag;
        }).forEachOrdered(builder::flag);

    return builder.build();
  }

  @NonNull
  private static IFlagInstance newFlagInstance(
      @NonNull FlagReference obj,
      @NonNull IBoundInstanceModelGroupedAssembly objInstance,
      int position,
      @NonNull IBindingDefinitionModel parent) {
    IModule module = parent.getContainingModule();

    IEnhancedQName qname = ModuleUtils.parseFlagName(module, ObjectUtils.requireNonNull(obj.getRef()));
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
