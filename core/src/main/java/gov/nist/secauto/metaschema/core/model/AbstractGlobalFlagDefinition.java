/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.util.ModuleUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a flag definition defined globally within a Metaschema
 * module.
 *
 * @param <MODULE>
 *          the Java type of the containing module
 * @param <INSTANCE>
 *          the expected Java type of an instance of this definition
 */
public abstract class AbstractGlobalFlagDefinition<MODULE extends IModule, INSTANCE extends IFlagInstance>
    extends AbstractGlobalDefinition<MODULE, INSTANCE>
    implements IFlagDefinition {
  /**
   * Construct a new global flag definition.
   *
   * @param module
   *          the parent module containing this definition
   */
  protected AbstractGlobalFlagDefinition(@NonNull MODULE module) {
    super(module, name -> ModuleUtils.parseFlagName(module, name));
  }
}
