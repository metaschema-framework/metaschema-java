/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IModelDefinitionJsonSchema<D extends IModelDefinition> extends IDefinitionJsonSchema<D> {
  void registerJsonKey(@NonNull IFlagInstance jsonKey, @NonNull String suffix);
}
