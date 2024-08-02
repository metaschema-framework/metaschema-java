/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFlagInstanceTypeInfo extends INamedInstanceTypeInfo {
  /**
   * Construct a new type information object for the provided {@code instance}.
   *
   * @param instance
   *          the instance to provide type information for
   * @param parentDefinition
   *          the definition containing the instance
   * @return the type information
   */
  @NonNull
  static IFlagInstanceTypeInfo newTypeInfo(
      @NonNull IFlagInstance instance,
      @NonNull IDefinitionTypeInfo parentDefinition) {
    return new FlagInstanceTypeInfoImpl(instance, parentDefinition);
  }

  @Override
  IFlagInstance getInstance();
}
