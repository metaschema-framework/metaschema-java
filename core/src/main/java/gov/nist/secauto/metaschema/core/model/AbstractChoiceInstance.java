/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a choice that is a member of a containing model.
 *
 * @param <PARENT>
 *          the Java type of the containing assembly definition
 * @param <MODEL>
 *          the Java type of child model instances supported by this choice
 * @param <NAMED_MODEL>
 *          the Java type of child named model instances supported by this
 *          choice
 * @param <FIELD>
 *          the Java type of child field instances supported by this choice
 * @param <ASSEMBLY>
 *          the Java type of child assembly instances supported by this choice
 */
public abstract class AbstractChoiceInstance<
    PARENT extends IAssemblyDefinition,
    MODEL extends IModelInstanceAbsolute,
    NAMED_MODEL extends INamedModelInstanceAbsolute,
    FIELD extends IFieldInstanceAbsolute,
    ASSEMBLY extends IAssemblyInstanceAbsolute>
    extends AbstractInstance<PARENT>
    implements IChoiceInstance, IFeatureContainerModelAbsolute<MODEL, NAMED_MODEL, FIELD, ASSEMBLY> {

  /**
   * Construct a new choice instance that is contained with the provided parent
   * assembly definition.
   *
   * @param parent
   *          the parent assembly definition container for this instance
   */
  protected AbstractChoiceInstance(@NonNull PARENT parent) {
    super(parent);
  }

  @Override
  public String getGroupAsName() {
    // a choice does not have a groups-as name
    return null;
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
