/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IDefineableJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractDefineableJsonSchema implements IDefineableJsonSchema {
  @Nullable
  private String name;

  protected abstract String generateDefinitionName(@NonNull IJsonGenerationState state);

  @Override
  public String getDefinitionName(IJsonGenerationState state) {
    synchronized (this) {
      if (this.name == null) {
        this.name = generateDefinitionName(state);
      }
      assert this.name != null;
      return this.name;
    }
  }

  @Override
  public String getDefinitionRef(IJsonGenerationState state) {
    return ObjectUtils.notNull(new StringBuilder()
        .append("#/definitions/")
        .append(getDefinitionName(state))
        .toString());
  }
}
