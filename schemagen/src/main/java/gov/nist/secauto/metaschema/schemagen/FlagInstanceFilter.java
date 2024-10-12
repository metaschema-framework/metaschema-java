/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class FlagInstanceFilter {
  private FlagInstanceFilter() {
    // disable construction
  }

  @NonNull
  public static Collection<? extends IFlagInstance> filterFlags(
      @NonNull Collection<? extends IFlagInstance> flags,
      IFlagInstance jsonKeyFlag) {
    Predicate<IFlagInstance> filter = null;

    // determine if we need to filter a JSON key
    if (jsonKeyFlag != null) {
      filter = filterFlag(jsonKeyFlag);
    }
    return applyFilter(flags, filter);
  }

  @NonNull
  public static Collection<? extends IFlagInstance> filterFlags(
      @NonNull Collection<? extends IFlagInstance> flags,
      IFlagInstance jsonKeyFlag,
      IFlagInstance jsonValueKeyFlag) {
    Predicate<IFlagInstance> filter = null;

    // determine if we need to filter a JSON key
    if (jsonKeyFlag != null) {
      filter = filterFlag(jsonKeyFlag);
    }

    // determine if we need to filter a JSON value key
    if (jsonValueKeyFlag != null) {
      Predicate<IFlagInstance> jsonValueKeyFilter
          = filterFlag(jsonValueKeyFlag);
      if (filter == null) {
        filter = jsonValueKeyFilter;
      } else {
        filter = filter.and(jsonValueKeyFilter);
      }
    }

    return applyFilter(flags, filter);
  }

  @NonNull
  private static Predicate<IFlagInstance>
      filterFlag(@NonNull IFlagInstance flagToFilter) {
    return flag -> !flagToFilter.equals(flag);
  }

  @NonNull
  private static Collection<? extends IFlagInstance> applyFilter(
      @NonNull Collection<? extends IFlagInstance> flags,
      Predicate<IFlagInstance> filter) {
    Collection<? extends IFlagInstance> retval;
    if (filter == null) {
      retval = flags;
    } else {
      retval = ObjectUtils.notNull(flags.stream()
          .filter(filter)
          .collect(Collectors.toList()));
    }
    return retval;
  }
}
