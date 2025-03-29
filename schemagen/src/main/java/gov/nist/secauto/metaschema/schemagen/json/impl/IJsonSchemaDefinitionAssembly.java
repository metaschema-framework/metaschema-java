/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IChoiceInstance;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema model assembly
 * definition, which can be generated inline or as a JSON schema definition.
 */
public interface IJsonSchemaDefinitionAssembly extends IJsonSchemaModelDefinition {
  @Override
  IAssemblyDefinition getDefinition();

  /**
   * Get the property combinations, which are exploded based on the presence of an
   * {@link IChoiceInstance} in the assembly definition's model.
   *
   * @return the sequence of possible property combinations or an empty list if
   *         the resulting JSON schema has no properties
   */
  @NonNull
  List<JsonSchemaHelper.Choice> getChoices();
}
