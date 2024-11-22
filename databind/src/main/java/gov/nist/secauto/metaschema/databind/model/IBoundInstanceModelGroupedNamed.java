/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.util.ModuleUtils;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a model instance that is a member of a choice group instance.
 */
public interface IBoundInstanceModelGroupedNamed
    extends INamedModelInstanceGrouped, IFeatureComplexItemValueHandler {
  @Override
  IBoundInstanceModelChoiceGroup getParentContainer();

  @Override
  IBoundDefinitionModelComplex getDefinition();

  @Override
  @Nullable
  default IBoundInstanceFlag getEffectiveJsonKey() {
    return JsonGroupAsBehavior.KEYED.equals(getParentContainer().getJsonGroupAsBehavior())
        ? getJsonKey()
        : null;
  }

  @Override
  default IBoundInstanceFlag getJsonKey() {
    String name = getParentContainer().getJsonKeyFlagInstanceName();
    return name == null
        ? null
        : ObjectUtils.requireNonNull(
            getDefinition().getFlagInstanceByName(
                ModuleUtils.parseFlagName(getContainingModule(), name).getIndexPosition()));
  }

  @Override
  default IBoundDefinitionModelAssembly getContainingDefinition() {
    return getParentContainer().getContainingDefinition();
  }

  @Override
  default String getName() {
    return getDefinition().getName();
  }

  @Override
  default IBoundObject deepCopyItem(IBoundObject item, IBoundObject parentInstance) throws BindingException {
    return getDefinition().deepCopyItem(item, parentInstance);
  }

  @Override
  default void callBeforeDeserialize(IBoundObject targetObject, IBoundObject parentObject) throws BindingException {
    getDefinition().callBeforeDeserialize(targetObject, parentObject);
  }

  @Override
  default void callAfterDeserialize(IBoundObject targetObject, IBoundObject parentObject) throws BindingException {
    getDefinition().callAfterDeserialize(targetObject, parentObject);
  }
}
