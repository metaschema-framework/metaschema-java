/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import dev.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a named model instance with absolute positioning and JSON
 * serialization support.
 * <p>
 * Provides JSON name resolution based on cardinality and grouping behavior,
 * with support for JSON key configuration.
 */
public interface INamedModelInstanceAbsolute extends INamedModelInstance, IModelInstanceAbsolute, IJsonInstance {
  @Override
  default String getJsonName() {
    @NonNull
    String retval;
    if (getMaxOccurs() == -1 || getMaxOccurs() > 1) {
      @NonNull
      String groupAsName = ObjectUtils.requireNonNull(getGroupAsName(),
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
    return getJsonGroupAsBehavior() == JsonGroupAsBehavior.KEYED
        ? getJsonKey()
        : null;
  }

  @Override
  @Nullable
  default IFlagInstance getJsonKey() {
    return getDefinition().getJsonKey();
  }
}
