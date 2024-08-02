/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

// TODO: remove
public interface IMetaschemaModule<SELF extends IMetaschemaModule<SELF>>
    extends IModuleExtended<
        SELF,
        IModelDefinition,
        IFlagDefinition,
        IFieldDefinition,
        IAssemblyDefinition> {
  // No additional methods
}
