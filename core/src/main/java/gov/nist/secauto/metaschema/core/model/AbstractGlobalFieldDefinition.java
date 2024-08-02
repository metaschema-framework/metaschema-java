/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a field definition defined globally within a Metaschema
 * module.
 *
 * @param <MODULE>
 *          the Java type of the containing module
 * @param <INSTANCE>
 *          the expected Java type of an instance of this definition
 * @param <FLAG>
 *          the expected Java type of flag children
 */
public abstract class AbstractGlobalFieldDefinition<
    MODULE extends IModule,
    INSTANCE extends IFieldInstance,
    FLAG extends IFlagInstance>
    extends AbstractGlobalDefinition<MODULE, INSTANCE>
    implements IFieldDefinition, IFeatureContainerFlag<FLAG> {

  /**
   * Construct a new global field definition.
   *
   * @param module
   *          the parent module containing this definition
   */
  protected AbstractGlobalFieldDefinition(@NonNull MODULE module) {
    super(module, module::toModelQName);
  }
}
