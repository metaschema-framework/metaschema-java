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
public abstract class AbstractInlineAssemblyDefinition<
    PARENT extends IContainerModel,
    DEFINITION extends IAssemblyDefinition,
    INSTANCE extends IAssemblyInstance,
    PARENT_DEFINITION extends IAssemblyDefinition,
    FLAG extends IFlagInstance,
    MODEL extends IModelInstanceAbsolute,
    NAMED_MODEL extends INamedModelInstanceAbsolute,
    FIELD extends IFieldInstanceAbsolute,
    ASSEMBLY extends IAssemblyInstanceAbsolute,
    CHOICE extends IChoiceInstance,
    CHOICE_GROUP extends IChoiceGroupInstance>
    extends AbstractNamedModelInstance<PARENT, PARENT_DEFINITION>
    implements IAssemblyInstance, IAssemblyDefinition, IFeatureContainerFlag<FLAG>,
    IFeatureContainerModelAssembly<
        MODEL,
        NAMED_MODEL,
        FIELD,
        ASSEMBLY,
        CHOICE,
        CHOICE_GROUP>,
    IFeatureDefinitionInstanceInlined<DEFINITION, INSTANCE> {

  /**
   * Construct a new inline assembly definition.
   *
   * @param parent
   *          the parent model containing this instance
   */
  protected AbstractInlineAssemblyDefinition(@NonNull PARENT parent) {
    super(parent);
  }

  @Override
  public final DEFINITION getDefinition() {
    return ObjectUtils.asType(this);
  }

  @Override
  public boolean isInline() {
    return true;
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
