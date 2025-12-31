/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.model.IModelElement;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a Metaschema model element loaded via data binding.
 * <p>
 * This interface is the base type for all model elements that are loaded from
 * Metaschema source files using data binding.
 */
public interface IBindingModelElement extends IModelElement {
  @Override
  IBindingMetaschemaModule getContainingModule();

  /**
   * Get the source node item representing this model element in the original
   * Metaschema module.
   * <p>
   * This provides access to the parsed Metaschema source data that was used to
   * create this model element.
   *
   * @return the source assembly node item
   */
  @NonNull
  IAssemblyNodeItem getSourceNodeItem();
}
