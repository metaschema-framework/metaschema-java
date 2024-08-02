/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a choice group that is a member of a containing model.
 *
 * @param <PARENT>
 *          the Java type of the containing assembly definition
 * @param <NAMED_MODEL>
 *          the Java type of child named model instances supported by this
 *          choice group
 * @param <FIELD>
 *          the Java type of child field instances supported by this choice
 *          group
 * @param <ASSEMBLY>
 *          the Java type of child assembly instances supported by this choice
 *          group
 */
public abstract class AbstractChoiceGroupInstance<
    PARENT extends IAssemblyDefinition,
    NAMED_MODEL extends INamedModelInstanceGrouped,
    FIELD extends IFieldInstanceGrouped,
    ASSEMBLY extends IAssemblyInstanceGrouped>
    extends AbstractInstance<PARENT>
    implements IChoiceGroupInstance, IFeatureContainerModelGrouped<NAMED_MODEL, FIELD, ASSEMBLY> {

  /**
   * Construct a new choice group instance that is contained with the provided
   * parent assembly definition container.
   *
   * @param parent
   *          the parent assembly definition container for this instance
   */
  protected AbstractChoiceGroupInstance(@NonNull PARENT parent) {
    super(parent);
  }

  /**
   * Retrieve the Metaschema assembly definition on which this instance is
   * declared.
   *
   * @return the parent Metaschema assembly definition
   */
  @Override
  public PARENT getContainingDefinition() {
    return getParentContainer();
  }

  @Override
  public IModule getContainingModule() {
    return getParentContainer().getContainingModule();
  }
}
