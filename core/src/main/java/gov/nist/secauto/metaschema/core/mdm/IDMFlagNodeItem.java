/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import edu.umd.cs.findbugs.annotations.NonNull;
import gov.nist.secauto.metaschema.core.mdm.impl.DefinitionFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAtomicValuedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;

/**
 * Represents a Metapath flag node item that is backed by a simple Metaschema
 * module-based data model.
 */
public interface IDMFlagNodeItem extends IFlagNodeItem, IDMNodeItem, IAtomicValuedNodeItem {
	  /**
	   * Create new flag node item that is detached from a parent node item.
	   *
	   * @param definition
	   *          the Metaschema flag definition describing the flag
	   * @param value
	   *          the flag's initial value
	   * @param staticContext
	   *          the atomic flag value
	   * @return the new flag node item
	   */
	  @NonNull
	  static IDMFlagNodeItem newInstance(
	      @NonNull IFlagDefinition definition,
	      @NonNull IAnyAtomicItem value,
	      @NonNull StaticContext staticContext) {
	    return new DefinitionFlagNodeItem(definition, value, staticContext);
	  }
}
