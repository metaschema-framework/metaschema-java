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
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemVisitor;
import gov.nist.secauto.metaschema.core.model.constraint.ITargetedConstraints;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ConstraintComposingVisitor
    implements INodeItemVisitor<ITargetedConstraints, Void> {

  @Override
  public Void visitDocument(@NonNull IDocumentNodeItem item, ITargetedConstraints context) {
    throw new UnsupportedOperationException("constraints can only apply to an assembly, field, or flag definition");
  }

  @Override
  public Void visitFlag(@NonNull IFlagNodeItem item, ITargetedConstraints context) {
    context.target(item.getDefinition());
    return null;
  }

  @Override
  public Void visitField(@NonNull IFieldNodeItem item, ITargetedConstraints context) {
    context.target(item.getDefinition());
    return null;
  }

  @Override
  public Void visitAssembly(@NonNull IAssemblyNodeItem item, ITargetedConstraints context) {
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
    throw new UnsupportedOperationException("constraints can only apply to an assembly, field, or flag definition");
  }

}
