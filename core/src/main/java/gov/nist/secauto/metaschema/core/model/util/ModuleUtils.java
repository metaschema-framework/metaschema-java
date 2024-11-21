/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.util;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.ModelInitializationException;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

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
   * Parse the name of a field or assemvly.
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
