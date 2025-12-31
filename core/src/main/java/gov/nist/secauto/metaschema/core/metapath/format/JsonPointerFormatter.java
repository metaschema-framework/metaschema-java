/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.format;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyInstanceGroupedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.INamedModelInstance;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link IPathFormatter} that produces RFC 6901 compliant JSON Pointer
 * paths.
 * <p>
 * This formatter produces paths suitable for use with JSON tooling and
 * JSON-based error reporting. The format follows the JSON Pointer specification
 * (RFC 6901).
 * <p>
 * Example output: {@code /catalog/controls/0/id}
 * <p>
 * Key characteristics:
 * <ul>
 * <li>Uses JSON property names (not XML element names)</li>
 * <li>Uses 0-based array indices for LIST grouping</li>
 * <li>Uses key values for KEYED grouping</li>
 * <li>Handles SINGLETON_OR_LIST by checking sibling count</li>
 * <li>Escapes special characters per RFC 6901 (~ as ~0, / as ~1)</li>
 * <li>No @ prefix for flags (unlike XPath)</li>
 * </ul>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6901">RFC 6901 - JSON
 *      Pointer</a>
 */
public class JsonPointerFormatter implements IPathFormatter {
  private static final Logger LOGGER = LogManager.getLogger(JsonPointerFormatter.class);

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
    String jsonName = root.getDefinition().getJsonName();
    return escapeJsonPointer(jsonName);
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
    // JSON Pointer does not use @ prefix for attributes
    return escapeJsonPointer(flag.getQName().getLocalName());
  }

  /**
   * Format a model node item (assembly or field) based on its JSON grouping
   * behavior.
   *
   * @param item
   *          the model node item to format
   * @return the formatted path segment
   */
  @NonNull
  private static String formatModelItem(@NonNull IModelNodeItem<?, ?> item) {
    INamedModelInstance instance = item.getInstance();
    if (instance == null) {
      // No instance - use local name only
      return escapeJsonPointer(item.getQName().getLocalName());
    }

    String jsonName = escapeJsonPointer(instance.getJsonName());
    JsonGroupAsBehavior behavior = instance.getJsonGroupAsBehavior();

    switch (behavior) {
    case KEYED:
      String keyValue = getJsonKeyValue(item, instance);
      return jsonName + "/" + escapeJsonPointer(keyValue);
    case LIST:
      // 0-based index
      return jsonName + "/" + (item.getPosition() - 1);
    case SINGLETON_OR_LIST:
      int siblingCount = countSiblings(item);
      if (siblingCount > 1) {
        // Multiple siblings - use array notation
        return jsonName + "/" + (item.getPosition() - 1);
      }
      // Single sibling - no index
      return jsonName;
    case NONE:
    default:
      return jsonName;
    }
  }

  /**
   * Get the JSON key value for a KEYED collection item.
   *
   * @param item
   *          the model node item
   * @param instance
   *          the model instance
   * @return the key value, or falls back to 0-based index if not available
   */
  @NonNull
  private static String getJsonKeyValue(
      @NonNull IModelNodeItem<?, ?> item,
      @NonNull INamedModelInstance instance) {
    IFlagInstance keyFlag = instance.getEffectiveJsonKey();
    if (keyFlag != null) {
      IEnhancedQName keyFlagQName = keyFlag.getQName();
      IFlagNodeItem flagItem = item.getFlagByName(keyFlagQName);
      if (flagItem != null) {
        return flagItem.toAtomicItem().asString();
      }
    }
    // Fallback to 0-based index - this indicates a potential issue with the model
    // or data
    if (LOGGER.isWarnEnabled()) {
      LOGGER.warn("Unable to resolve JSON key for KEYED collection item '{}', falling back to numeric index",
          item.getQName().getLocalName());
    }
    return ObjectUtils.notNull(String.valueOf(item.getPosition() - 1));
  }

  /**
   * Count the number of siblings with the same name as the given item.
   *
   * @param item
   *          the model node item
   * @return the sibling count (including the item itself)
   */
  private static int countSiblings(@NonNull IModelNodeItem<?, ?> item) {
    IAssemblyNodeItem parent = item.getParentContentNodeItem();
    if (parent == null) {
      return 1;
    }
    List<? extends IModelNodeItem<?, ?>> siblings = parent.getModelItemsByName(item.getQName());
    return siblings.size();
  }

  /**
   * Escape a string value according to RFC 6901.
   * <p>
   * The order of escaping is important: ~ must be escaped first, then /.
   *
   * @param value
   *          the value to escape
   * @return the escaped value
   */
  @NonNull
  private static String escapeJsonPointer(@NonNull String value) {
    // Order matters: escape ~ first, then /
    return ObjectUtils.notNull(value.replace("~", "~0").replace("/", "~1"));
  }
}
