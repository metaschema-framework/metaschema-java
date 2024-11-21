/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.model.util.ModuleUtils;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for name members of a containing model.
 *
 * @param <PARENT>
 *          the Java type of the parent model container for this instance
 * @param <PARENT_DEFINITION>
 *          the Java type of the containing assembly definition
 */
public abstract class AbstractNamedModelInstance<
    PARENT extends IContainerModel,
    PARENT_DEFINITION extends IAssemblyDefinition>
    extends AbstractNamedInstance<PARENT>
    implements INamedModelInstance {

  /**
   * Construct a new instance.
   *
   * @param parent
   *          the parent containing the instance
   */
  protected AbstractNamedModelInstance(@NonNull PARENT parent) {
    super(parent, name -> ModuleUtils.parseModelName(parent.getOwningDefinition().getContainingModule(), name));
  }

  @Override
  public final PARENT_DEFINITION getContainingDefinition() {
    // TODO: look for ways to avoid this cast. The problem is that IContainerModel
    // is not easily generalized, since this interface is extended by core model
    // interfaces. Perhaps moving default implementation into abstract or concrete
    // implementation is a possible path?
    return ObjectUtils.asType(getParentContainer().getOwningDefinition());
  }

  @Override
  public IModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }
}
