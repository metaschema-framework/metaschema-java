/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.DocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metapath document node item that is backed by a simple
 * Metaschema module-based data model.
 * <p>
 * The {@link #newInstance(URI, IAssemblyDefinition)} method can be used to
 * create a new document-based data model.
 */
public interface IDMDocumentNodeItem
    extends IDocumentNodeItem {
  @Override
  IDMRootAssemblyNodeItem getRootAssemblyNodeItem();

  /**
   * Create a new Metaschema document-based data model.
   *
   * @param resource
   *          the base URI of the document resource
   * @param rootAssembly
   *          the assembly that is at the root of the node tree for this document
   * @return the document node item
   */
  @NonNull
  static IDMDocumentNodeItem newInstance(
      @NonNull URI resource,
      @NonNull IAssemblyDefinition rootAssembly) {
    return new DocumentNodeItem(resource, rootAssembly);
  }
}
