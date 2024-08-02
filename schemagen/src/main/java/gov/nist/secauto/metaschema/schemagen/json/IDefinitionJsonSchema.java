/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

import gov.nist.secauto.metaschema.core.model.IDefinition;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDefinitionJsonSchema<D extends IDefinition>
    extends IDefineableJsonSchema {
  @NonNull
  D getDefinition();

  void gatherDefinitions(
      @NonNull Map<IKey, IDefinitionJsonSchema<?>> gatheredDefinitions,
      @NonNull IJsonGenerationState state);

  @NonNull
  IKey getKey();
}
