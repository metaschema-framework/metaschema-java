/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.mdm;

import dev.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;

/**
 * Represents a Metapath root assembly node item implementation that is backed
 * by a simple Metaschema module-based data model.
 */
public interface IDMRootAssemblyNodeItem
    extends IDMAssemblyNodeItem, IRootAssemblyNodeItem {
  // no additional methods
}
