/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface INamedModelInstanceAbsolute extends INamedModelInstance, IModelInstanceAbsolute {
  @Override
  default String getJsonName() {
    @NonNull String retval;
    if (getMaxOccurs() == -1 || getMaxOccurs() > 1) {
      @NonNull String groupAsName = ObjectUtils.requireNonNull(getGroupAsName(),
          ObjectUtils.notNull(String.format("null group-as name in instance '%s' on definition '%s' in '%s'",
              this.getName(),
              this.getContainingDefinition().getName(),
              this.getContainingModule().getLocation())));
      retval = groupAsName;
    } else {
      retval = getEffectiveName();
    }
    return retval;
  }

  @Override
  @Nullable
  default IFlagInstance getEffectiveJsonKey() {
    return JsonGroupAsBehavior.KEYED.equals(getJsonGroupAsBehavior())
        ? getJsonKey()
        : null;
  }

  @Override
  @Nullable
  default IFlagInstance getJsonKey() {
    return getDefinition().getJsonKey();
  }
}
