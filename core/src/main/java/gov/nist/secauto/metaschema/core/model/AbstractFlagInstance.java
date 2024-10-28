/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a flag that is a member of a containing model.
 *
 * @param <PARENT>
 *          the Java type of the parent model (i.e., assembly, field).
 * @param <DEFINITION>
 *          the Java type of the definition for this member flag
 * @param <INSTANCE>
 *          the Java type of the instance for this member flag
 */
public abstract class AbstractFlagInstance<
    PARENT extends IModelDefinition,
    DEFINITION extends IFlagDefinition,
    INSTANCE extends IFlagInstance>
    extends AbstractNamedInstance<PARENT>
    implements IFlagInstance, IFeatureDefinitionReferenceInstance<DEFINITION, INSTANCE> {

  /**
   * Construct a new flag instance.
   *
   * @param parent
   *          the parent model containing this instance
   */
  protected AbstractFlagInstance(@NonNull PARENT parent) {
    super(parent, name -> parent.getContainingModule().toFlagQName(name));
  }

  @Override
  public DEFINITION getDefinition() {
    QName qname = getReferencedDefinitionQName();
    // this should always be not null
    IFlagDefinition definition = getContainingModule().getScopedFlagDefinitionByName(qname);
    if (definition == null) {
      throw new IllegalStateException(
          String.format("Unable to resolve field reference '%s' in definition '%s' in module '%s'",
              qname,
              getContainingDefinition().getName(),
              getContainingModule().getShortName()));
    }
    return ObjectUtils.asType(definition);
  }

  @Override
  public final PARENT getContainingDefinition() {
    return getParentContainer();
  }

  /**
   * Generates a "coordinate" string for the assembly instance.
   *
   * @return the coordinate
   */
  @SuppressWarnings("null")
  @Override
  public String toCoordinates() {
    IDefinition definition = getDefinition();
    return String.format("flag instance %s -> %s in module %s (@%d(%d)",
        getXmlQName(),
        definition.getDefinitionQName(),
        getContainingDefinition().getContainingModule().getShortName(),
        hashCode(),
        definition.hashCode());
  }

  @Override
  public IModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }
}
