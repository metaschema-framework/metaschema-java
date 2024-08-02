/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IModelElement;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IBindingModelElement extends IModelElement {
  @Override
  IBindingMetaschemaModule getContainingModule();

  @NonNull
  IAssemblyNodeItem getSourceNodeItem();
}
