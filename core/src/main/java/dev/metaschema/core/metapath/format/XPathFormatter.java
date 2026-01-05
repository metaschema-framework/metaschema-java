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
import dev.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import dev.metaschema.core.model.INamedModelInstance;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link IPathFormatter} that produces XPath 3.1 expressions with
 * namespace-qualified names using the EQName format (e.g.,
 * {@code Q{namespace}localname}).
 * <p>
 * This formatter produces paths suitable for use with XML tooling that requires
 * namespace qualification. The format follows the XPath 3.1 specification for
 * EQNames.
 * <p>
 * Example output:
 * {@code /Q{http://example.com}catalog/Q{http://example.com}control[1]/@Q{http://example.com}id}
 * <p>
 * For elements with XML grouping ({@link XmlGroupAsBehavior#GROUPED}), the
 * wrapper element is included in the path with position [1], followed by the
 * actual element with its position.
 *
 * @see IEnhancedQName#toEQName()
 */
public class XPathFormatter implements IPathFormatter {

  @Override
  @NonNull
  public String formatMetaschema(IModuleNodeItem metaschema) {
    // Returns empty string to produce leading "/" via join in format method
    return "";
  }

  @Override
  @NonNull
  public String formatDocument(IDocumentNodeItem document) {
    // Returns empty string to produce leading "/" via join in format method
    return "";
  }

  @Override
  @NonNull
  public String formatRootAssembly(IRootAssemblyNodeItem root) {
    return ObjectUtils.notNull(root.getQName().toEQName());
  }

  @Override
  @NonNull
  public String formatAssembly(IAssemblyNodeItem assembly) {
    return formatModelItem(assembly);
  }

  @Override
  @NonNull
  public String formatAssembly(IAssemblyInstanceGroupedNodeItem assembly) {
    return formatModelItem(assembly);
  }

  @Override
  @NonNull
  public String formatField(IFieldNodeItem field) {
    return formatModelItem(field);
  }

  @Override
  @NonNull
  public String formatFlag(IFlagNodeItem flag) {
    return ObjectUtils.notNull("@" + flag.getQName().toEQName());
  }

  /**
   * Format a model node item (assembly or field) with optional XML grouping
   * wrapper element.
   * <p>
   * When the instance has {@link XmlGroupAsBehavior#GROUPED}, the wrapper element
   * is prepended to the path with position [1], since the wrapper element appears
   * exactly once containing all grouped items.
   *
   * @param item
   *          the model node item to format
   * @return the formatted path segment
   */
  @NonNull
  private static String formatModelItem(@NonNull IModelNodeItem<?, ?> item) {
    StringBuilder builder = new StringBuilder();

    // Check for XML grouping wrapper element
    INamedModelInstance instance = item.getInstance();
    if (instance != null && instance.getXmlGroupAsBehavior() == XmlGroupAsBehavior.GROUPED) {
      IEnhancedQName wrapperQName = instance.getEffectiveXmlGroupAsQName();
      if (wrapperQName != null) {
        builder.append(wrapperQName.toEQName())
            .append("[1]/");
      }
    }

    // Append element name with position predicate
    builder.append(item.getQName().toEQName())
        .append('[')
        .append(item.getPosition())
        .append(']');

    return ObjectUtils.notNull(builder.toString());
  }
}
