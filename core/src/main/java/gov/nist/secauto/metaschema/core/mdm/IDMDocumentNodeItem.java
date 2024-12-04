/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.mdm.impl.DocumentImpl;
import gov.nist.secauto.metaschema.core.metapath.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDMDocumentNodeItem
    extends IDocumentNodeItem, IDMNodeItem {
  @NonNull
  static IDMDocumentNodeItem newInstance(
      @NonNull IDMAssemblyNodeItem rootAssembly,
      @NonNull URI resource,
      @NonNull IResourceLocation resourceLocation) {
    return new DocumentImpl(rootAssembly, resource, resourceLocation);
  }
}
