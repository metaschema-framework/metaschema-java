/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IInstance;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractJsonProperty<I extends IInstance>
    implements IJsonProperty<I> {
  @NonNull
  private final I instance;

  protected AbstractJsonProperty(@NonNull I instance) {
    this.instance = instance;
  }

  @Override
  @NonNull
  public I getInstance() {
    return instance;
  }

  protected void generateMetadata(@NonNull ObjectNode obj) {
    // do nothing by default
  }

  // REFACTOR: rename to generate schema
  protected abstract void generateBody(
      @NonNull ObjectNode obj,
      @NonNull IJsonGenerationState state);

  @Override
  public void generateProperty(
      PropertyCollection properties,
      IJsonGenerationState state) {

    ObjectNode contextObj = ObjectUtils.notNull(state.getJsonNodeFactory().objectNode());

    generateMetadata(contextObj);

    generateBody(contextObj, state);

    String name = getName();
    properties.addProperty(name, contextObj);
    if (isRequired()) {
      properties.addRequired(name);
    }
  }
}
