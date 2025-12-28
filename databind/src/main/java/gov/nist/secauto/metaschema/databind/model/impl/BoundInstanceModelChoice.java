/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.AbstractChoiceInstance;
import gov.nist.secauto.metaschema.core.model.DefaultChoiceModelBuilder;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IContainerModelSupport;
import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelField;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModelNamed;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a choice instance for annotation-based bound definitions.
 * <p>
 * A choice contains mutually exclusive model instance alternatives. This class
 * is used for annotation-based bindings (classes with {@code @BoundChoice}
 * annotations).
 */
public final class BoundInstanceModelChoice
    extends AbstractChoiceInstance<
        IBoundDefinitionModelAssembly,
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute> {

  @NonNull
  private final String choiceId;
  @NonNull
  private final IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> modelContainer;

  /**
   * Construct a new choice instance from a list of named model instances.
   *
   * @param choiceId
   *          the identifier for this choice
   * @param parent
   *          the containing assembly definition
   * @param instances
   *          the list of named model instances that are alternatives in this
   *          choice
   */
  public BoundInstanceModelChoice(
      @NonNull String choiceId,
      @NonNull IBoundDefinitionModelAssembly parent,
      @NonNull List<IBoundInstanceModelNamed<?>> instances) {
    super(parent);
    this.choiceId = choiceId;
    this.modelContainer = buildModelContainer(instances);
  }

  @NonNull
  private static IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> buildModelContainer(
          @NonNull List<IBoundInstanceModelNamed<?>> instances) {
    if (instances.isEmpty()) {
      return IContainerModelSupport.empty();
    }

    DefaultChoiceModelBuilder<
        IModelInstanceAbsolute,
        INamedModelInstanceAbsolute,
        IFieldInstanceAbsolute,
        IAssemblyInstanceAbsolute> builder = new DefaultChoiceModelBuilder<>();

    for (IBoundInstanceModelNamed<?> instance : instances) {
      if (instance instanceof IBoundInstanceModelField) {
        builder.append((IFieldInstanceAbsolute) instance);
      } else if (instance instanceof IBoundInstanceModelAssembly) {
        builder.append((IAssemblyInstanceAbsolute) instance);
      }
    }

    return builder.buildChoice();
  }

  /**
   * Get the choice identifier for this choice instance.
   *
   * @return the choice identifier
   */
  @NonNull
  public String getChoiceId() {
    return choiceId;
  }

  @Override
  public IContainerModelSupport<
      IModelInstanceAbsolute,
      INamedModelInstanceAbsolute,
      IFieldInstanceAbsolute,
      IAssemblyInstanceAbsolute> getModelContainer() {
    return modelContainer;
  }

  /**
   * {@inheritDoc}
   * <p>
   * For annotation-based bindings, choices are optional by default (minOccurs =
   * 0). The individual alternatives have their own minOccurs constraints that
   * apply when that alternative is selected.
   */
  @Override
  public int getMinOccurs() {
    return 0;
  }

  @Override
  public IModule getContainingModule() {
    return getContainingDefinition().getContainingModule();
  }

  @Override
  public MarkupMultiline getRemarks() {
    // no remarks for annotation-based bindings
    return null;
  }
}
