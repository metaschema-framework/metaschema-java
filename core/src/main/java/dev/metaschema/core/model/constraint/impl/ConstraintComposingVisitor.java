/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint.impl;

import dev.metaschema.core.model.AbstractModelElementVisitor;
import dev.metaschema.core.model.IAnyInstance;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IAssemblyInstanceAbsolute;
import dev.metaschema.core.model.IAssemblyInstanceGrouped;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelElement;
import dev.metaschema.core.model.INamedModelElement;
import dev.metaschema.core.model.constraint.ConstraintInitializationException;
import dev.metaschema.core.model.constraint.ITargetedConstraints;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Applies targeted constraints to their intended targets.
 */
public class ConstraintComposingVisitor
    extends AbstractModelElementVisitor<ITargetedConstraints, Void> {

  @Override
  public Void visitChoiceInstance(IChoiceInstance instance, ITargetedConstraints context) {
    illegalTargetError(instance, context);
    return null;
  }

  @Override
  public Void visitChoiceGroupInstance(IChoiceGroupInstance instance, ITargetedConstraints context) {
    illegalTargetError(instance, context);
    return null;
  }

  @Override
  public Void visitAny(IAnyInstance instance, ITargetedConstraints context) {
    illegalTargetError(instance, context);
    return null;
  }

  @Override
  public Void visitFlagInstance(IFlagInstance instance, ITargetedConstraints context) {
    if (instance.isInlineDefinition()) {
      visitFlagDefinition(instance.getDefinition(), context);
    } else {
      illegalTargetError(instance, context);
    }
    return null;
  }

  @Override
  public Void visitFieldInstance(IFieldInstanceAbsolute instance, ITargetedConstraints context) {
    if (instance.isInlineDefinition()) {
      visitFieldDefinition(instance.getDefinition(), context);
    } else {
      illegalTargetError(instance, context);
    }
    return null;
  }

  @Override
  public Void visitFieldInstance(IFieldInstanceGrouped instance, ITargetedConstraints context) {
    if (instance.isInlineDefinition()) {
      visitFieldDefinition(instance.getDefinition(), context);
    } else {
      illegalTargetError(instance, context);
    }
    return null;
  }

  @Override
  public Void visitAssemblyInstance(IAssemblyInstanceAbsolute instance, ITargetedConstraints context) {
    if (instance.isInlineDefinition()) {
      visitAssemblyDefinition(instance.getDefinition(), context);
    } else {
      illegalTargetError(instance, context);
    }
    return null;
  }

  @Override
  public Void visitAssemblyInstance(IAssemblyInstanceGrouped instance, ITargetedConstraints context) {
    if (instance.isInlineDefinition()) {
      visitAssemblyDefinition(instance.getDefinition(), context);
    } else {
      illegalTargetError(instance, context);
    }
    return null;
  }

  @Override
  public Void visitFlagDefinition(IFlagDefinition definition, ITargetedConstraints context) {
    context.target(definition);
    return null;
  }

  @Override
  public Void visitFieldDefinition(IFieldDefinition definition, ITargetedConstraints context) {
    context.target(definition);
    return null;
  }

  @Override
  public Void visitAssemblyDefinition(IAssemblyDefinition definition, ITargetedConstraints context) {
    context.target(definition);
    return null;
  }

  private static void illegalTargetError(
      @NonNull IModelElement element,
      ITargetedConstraints context) {
    throw new ConstraintInitializationException(
        String.format(
            "Invalid %s target%s for constraints targeting '%s' in '%s'. A document node is an" +
                " invalid constraint target. Constraints can only apply to an assembly, field, or flag definition.",
            element.getModelType(),
            element instanceof INamedModelElement ? " '" + ((INamedModelElement) element).getQName() + "'" : "",
            context.getTargets(),
            context.getSource().getLocationHint()));

  }

  @Override
  protected Void defaultResult(IModelElement element, ITargetedConstraints context) {
    return null;
  }
}
