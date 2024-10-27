/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.model.IMetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.METASCHEMA;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metaschema module represented as binding to Java classes and fields.
 */
public interface IBindingMetaschemaModule
    extends IMetaschemaModule<IBindingMetaschemaModule> {

  /**
   * Get the underlying module data as a bound object.
   *
   * @return the bound class
   */
  @NonNull
  METASCHEMA getBinding();

  /**
   * Get the underling bound objects as a {@link IDocumentNodeItem}.
   *
   * @return the document node item
   */
  @NonNull
  IDocumentNodeItem getSourceNodeItem();

  /**
   * Get a node item that can be used to query the module's model.
   *
   * @return the module node item
   */
  @NonNull
  IModuleNodeItem getModuleNodeItem();
}
