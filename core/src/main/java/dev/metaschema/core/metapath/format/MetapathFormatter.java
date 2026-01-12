/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.format;

import dev.metaschema.core.metapath.item.node.IAssemblyInstanceGroupedNodeItem;
import dev.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import dev.metaschema.core.metapath.item.node.IDocumentNodeItem;
import dev.metaschema.core.metapath.item.node.IFieldNodeItem;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.metapath.item.node.IModelNodeItem;
import dev.metaschema.core.metapath.item.node.IModuleNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link IPathFormatter} that produces a Metapath expression for the path to
 * a given {@link INodeItem}.
 */
public class MetapathFormatter implements IPathFormatter {

  @Override
  @NonNull
  public String formatMetaschema(IModuleNodeItem metaschema) {
    // this will result in a slash being generated using the join in the format
    // method
    return "";
  }

  @Override
  public String formatDocument(IDocumentNodeItem document) {
    // this will result in a slash being generated using the join in the format
    // method
    return "";
  }

  @Override
  public String formatRootAssembly(IRootAssemblyNodeItem root) {
    return ObjectUtils.notNull(root.getQName().getLocalName());
  }

  @Override
  public String formatAssembly(IAssemblyNodeItem assembly) {
    // TODO: does it make sense to use this for an intermediate that has no parent?
    return formatModelPathSegment(assembly);
  }

  @Override
  public String formatAssembly(IAssemblyInstanceGroupedNodeItem assembly) {
    // TODO: does it make sense to use this for an intermediate that has no parent?
    return formatModelPathSegment(assembly);
  }

  @Override
  public String formatField(IFieldNodeItem field) {
    return formatModelPathSegment(field);
  }

  @Override
  public String formatFlag(IFlagNodeItem flag) {
    return "@" + flag.getQName();
  }

  @SuppressWarnings("null")
  @NonNull
  private static String formatModelPathSegment(@NonNull IModelNodeItem<?, ?> item) {
    StringBuilder builder = new StringBuilder(item.getQName().getLocalName())
        .append('[')
        .append(item.getPosition())
        .append(']');
    return builder.toString();
  }
}
