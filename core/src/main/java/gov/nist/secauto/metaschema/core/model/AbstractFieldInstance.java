/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for a field that is a member of a containing model.
 *
 * @param <PARENT>
 *          the Java type of the parent model (i.e., assembly, choice,
 *          choiceGroup).
 * @param <DEFINITION>
 *          the Java type of the definition for this member field
 * @param <INSTANCE>
 *          the Java type of the instance for this member field
 * @param <PARENT_DEFINITION>
 *          the Java type of the containing assembly definition
 */
public abstract class AbstractFieldInstance<
    PARENT extends IContainerModel,
    DEFINITION extends IFieldDefinition,
    INSTANCE extends IFieldInstance,
    PARENT_DEFINITION extends IAssemblyDefinition>
    extends AbstractNamedModelInstance<PARENT, PARENT_DEFINITION>
    implements IFieldInstance, IFeatureDefinitionReferenceInstance<DEFINITION, INSTANCE> {

  /**
   * Construct a new field instance.
   *
   * @param parent
   *          the parent model containing this instance
   */
  protected AbstractFieldInstance(@NonNull PARENT parent) {
    super(parent);
  }

  @Override
  public DEFINITION getDefinition() {
    // this should always be not null
    return ObjectUtils.asType(ObjectUtils.requireNonNull(
        getContainingModule()
            .getScopedFieldDefinitionByName(getReferencedDefinitionQName())));
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
    return String.format("field instance %s -> %s in module %s (@%d(%d)",
        getXmlQName(),
        definition.getDefinitionQName(),
        getContainingDefinition().getContainingModule().getShortName(),
        hashCode(),
        definition.hashCode());
  }
}
