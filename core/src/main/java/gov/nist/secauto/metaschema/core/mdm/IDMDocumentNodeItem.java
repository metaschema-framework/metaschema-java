/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.DocumentImpl;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A document node item implementation that is backed by a simple Metaschema
 * module-based data model.
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
   * @param resourceLocation
   *          information about the (intended) location of the document resource
   * @param rootAssembly
   *          the assembly that is at the root of the node tree for this document
   * @param rootAssemblyLocation
   *          information about the (intended) location of the root assembly
   *          resource
   * @return the document node item
   */
  @NonNull
  static IDMDocumentNodeItem newInstance(
      @NonNull URI resource,
      @NonNull IResourceLocation resourceLocation,
      @NonNull IAssemblyDefinition rootAssembly,
      @NonNull IResourceLocation rootAssemblyLocation) {
    return new DocumentImpl(resource, resourceLocation, rootAssembly, rootAssemblyLocation);
  }
}
