/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.util;

import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.StaticMetapathException;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.ModelInitializationException;
import dev.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides utility methods for parsing qualified names within Metaschema
 * modules.
 */
public final class ModuleUtils {
  /**
   * Parse a flag name.
   * <p>
   * The namespace for the name will be determined according to
   * {@link StaticContext#parseFlagName(String)}.
   *
   * @param module
   *          the containing module
   * @param name
   *          the name
   * @return the parsed qualified name
   */
  @NonNull
  public static IEnhancedQName parseFlagName(
      @NonNull IModule module,
      @NonNull String name) {
    try {
      return module.getModuleStaticContext().parseFlagName(name);
    } catch (StaticMetapathException ex) {
      throw new ModelInitializationException(ex);
    }
  }

  /**
   * Parse the name of a field or assembly.
   * <p>
   * The namespace for the name will be determined according to
   * {@link StaticContext#parseModelName(String)}.
   *
   * @param module
   *          the containing module
   * @param name
   *          the name
   * @return the parsed qualified name
   */
  @NonNull
  public static IEnhancedQName parseModelName(
      @NonNull IModule module,
      @NonNull String name) {
    try {
      return module.getModuleStaticContext().parseModelName(name);
    } catch (StaticMetapathException ex) {
      throw new ModelInitializationException(ex);
    }
  }

  private ModuleUtils() {
    // disable construction
  }

}
