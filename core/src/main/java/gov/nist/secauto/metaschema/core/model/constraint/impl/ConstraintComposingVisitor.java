/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyInstanceGroupedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemVisitor;
import gov.nist.secauto.metaschema.core.model.constraint.ConstraintInitializationException;
import gov.nist.secauto.metaschema.core.model.constraint.ITargetedConstraints;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Applies targeted constraints to their intended targets.
 */
public class ConstraintComposingVisitor
    implements INodeItemVisitor<ITargetedConstraints, Void> {

  @Override
  public Void visitDocument(IDocumentNodeItem item, ITargetedConstraints context) {
    illegalTargetError(item, context);
    return null;
  }

  @Override
  public Void visitFlag(IFlagNodeItem item, ITargetedConstraints context) {
    context.target(item.getDefinition());
    return null;
  }

  @Override
  public Void visitField(IFieldNodeItem item, ITargetedConstraints context) {
    context.target(item.getDefinition());
    return null;
  }

  @Override
  public Void visitAssembly(IAssemblyNodeItem item, ITargetedConstraints context) {
    context.target(item.getDefinition());
    return null;
  }

  @Override
  public Void visitAssembly(IAssemblyInstanceGroupedNodeItem item, ITargetedConstraints context) {
    context.target(item.getDefinition());
    return null;
  }

  @Override
  public Void visitMetaschema(@NonNull IModuleNodeItem item, ITargetedConstraints context) {
    illegalTargetError(item, context);
    return null;
  }

  private static void illegalTargetError(
      @NonNull INodeItem item,
      ITargetedConstraints context) {
    throw new ConstraintInitializationException(
        String.format(
            "Invalid target '%s' for constraints targeting '%s' in '%s'. A document node is an" +
                " invalid constraint target. Constraints can only apply to an assembly, field, or flag definition.",
            item.getMetapath(),
            context.getTarget(),
            context.getSource().getLocationHint()));

  }
}
