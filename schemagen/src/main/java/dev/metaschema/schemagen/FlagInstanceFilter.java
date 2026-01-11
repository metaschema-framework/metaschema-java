/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A utility class for filtering flag instances during schema generation.
 * <p>
 * This class provides methods to exclude specific flag instances (such as JSON
 * key flags or JSON value key flags) from collections before processing.
 */
public final class FlagInstanceFilter {
  private FlagInstanceFilter() {
    // disable construction
  }

  /**
   * Filters flag instances by excluding the specified JSON key flag.
   *
   * @param flags
   *          the collection of flag instances to filter
   * @param jsonKeyFlag
   *          the flag instance used as a JSON key to exclude, or {@code null} if
   *          no filtering is needed
   * @return a collection containing all flags except the JSON key flag
   */
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

  /**
   * Filters flag instances by excluding both the JSON key flag and JSON value key
   * flag.
   *
   * @param flags
   *          the collection of flag instances to filter
   * @param jsonKeyFlag
   *          the flag instance used as a JSON key to exclude, or {@code null} if
   *          no JSON key filtering is needed
   * @param jsonValueKeyFlag
   *          the flag instance used as a JSON value key to exclude, or
   *          {@code null} if no JSON value key filtering is needed
   * @return a collection containing all flags except the excluded ones
   */
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
