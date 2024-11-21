/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.util.ModuleUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for an assembly definition defined globally within a Metaschema
 * module.
 *
 * @param <MODULE>
 *          the Java type of the containing module
 * @param <INSTANCE>
 *          the expected Java type of an instance of this definition
 * @param <FLAG>
 *          the expected Java type of flag children
 * @param <MODEL>
 *          the expected Java type of model children
 * @param <NAMED_MODEL>
 *          the expected Java type of named model children
 * @param <FIELD>
 *          the expected Java type of field children
 * @param <ASSEMBLY>
 *          the expected Java type of assembly children
 * @param <CHOICE>
 *          the expected Java type of choice children
 * @param <CHOICE_GROUP>
 *          the expected Java type of choice group children
 */
public abstract class AbstractGlobalAssemblyDefinition<
    MODULE extends IModule,
    INSTANCE extends IAssemblyInstance,
    FLAG extends IFlagInstance,
    MODEL extends IModelInstanceAbsolute,
    NAMED_MODEL extends INamedModelInstanceAbsolute,
    FIELD extends IFieldInstanceAbsolute,
    ASSEMBLY extends IAssemblyInstanceAbsolute,
    CHOICE extends IChoiceInstance,
    CHOICE_GROUP extends IChoiceGroupInstance>
    extends AbstractGlobalDefinition<MODULE, INSTANCE>
    implements IAssemblyDefinition, IFeatureContainerFlag<FLAG>, IFeatureContainerModelAssembly<
        MODEL,
        NAMED_MODEL,
        FIELD,
        ASSEMBLY,
        CHOICE,
        CHOICE_GROUP> {

  /**
   * Construct a new global assembly definition.
   *
   * @param module
   *          the parent module containing this definition
   */
  protected AbstractGlobalAssemblyDefinition(@NonNull MODULE module) {
    super(module, name -> ModuleUtils.parseModelName(module, name));
  }
}
