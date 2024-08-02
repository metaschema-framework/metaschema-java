/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedInstance;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import org.apache.commons.lang3.tuple.Pair;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractNamedInstanceJsonProperty<I extends IInstanceAbsolute & INamedInstance>
    extends AbstractJsonProperty<I> {

  protected AbstractNamedInstanceJsonProperty(@NonNull I instance) {
    super(instance);
  }

  @Override
  public String getName() {
    return getInstance().getJsonName();
  }

  @Nullable
  protected String getJsonKeyFlagName() {
    return null;
  }

  @Nullable
  protected Pair<String, String> getDiscriminator() {
    return null;
  }

  @Override
  protected abstract void generateBody(
      ObjectNode obj,
      IJsonGenerationState state);
}
