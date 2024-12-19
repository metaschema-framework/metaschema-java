/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.mdm;

import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.INamedInstance;

public interface IDMDefinitionNodeItem<D extends IDefinition, I extends INamedInstance>
    extends IDMNodeItem, IDefinitionNodeItem<D, I> {

}
