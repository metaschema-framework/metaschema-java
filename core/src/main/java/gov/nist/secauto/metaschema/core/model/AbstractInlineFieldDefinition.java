/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for an assembly instance defined inline.
 *
 * @param <PARENT>
 *          the Java type of the parent model container for this instance
 * @param <DEFINITION>
 *          the Java type of the related assembly definition
 * @param <INSTANCE>
 *          the expected Java type of an instance of this definition
 * @param <PARENT_DEFINITION>
 *          the Java type of the containing assembly definition
 * @param <FLAG>
 *          the expected Java type of flag children
 */
public abstract class AbstractInlineFieldDefinition<
    PARENT extends IContainerModel,
    DEFINITION extends IFieldDefinition,
    INSTANCE extends IFieldInstance,
    PARENT_DEFINITION extends IAssemblyDefinition,
    FLAG extends IFlagInstance>
    extends AbstractNamedModelInstance<PARENT, PARENT_DEFINITION>
    implements IFieldInstance, IFieldDefinition,
    IFeatureContainerFlag<FLAG>,
    IFeatureDefinitionInstanceInlined<DEFINITION, INSTANCE> {

  /**
   * Construct a new inline assembly definition.
   *
   * @param parent
   *          the parent model containing this instance
   */
  protected AbstractInlineFieldDefinition(@NonNull PARENT parent) {
    super(parent);
  }

  @Override
  public final DEFINITION getDefinition() {
    return ObjectUtils.asType(this);
  }

  @Override
  @NonNull
  public final INSTANCE getInlineInstance() {
    return ObjectUtils.asType(this);
  }

  @Override
  public final FLAG getJsonKey() {
    return IFeatureContainerFlag.super.getJsonKey();
  }
}
