/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Supports generation of a JSON schema based on a Metaschema model definition,
 * which can be generated inline or as a JSON schema definition.
 */
public interface IJsonSchemaModelDefinition extends IJsonSchemaDefinition {
  /**
   * Get the name of the JSON key flag.
   * 
   * @return the name or {@code null} if a JSON property key is not used
   */
  @Nullable
  default IEnhancedQName getJsonKeyFlagName() {
    IFlagInstance jsonKey = getJsonKeyFlag();
    return jsonKey == null ? null : jsonKey.getQName();
  }

  /**
   * Get the JSON key flag.
   * 
   * @return the flag or {@code null} if a JSON property key is not used
   */
  @Nullable
  IFlagInstance getJsonKeyFlag();

  /**
   * Get the list of flags to use as properties.
   * <p>
   * This list will not include the JSON key flag.
   * 
   * @return the list of flag JSON schema properties
   */
  @NonNull
  List<IJsonSchemaPropertyFlag> getFlagProperties();
}
