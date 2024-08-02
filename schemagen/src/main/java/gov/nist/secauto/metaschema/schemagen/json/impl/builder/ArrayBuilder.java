/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

public class ArrayBuilder
    extends AbstractCollectionBuilder<ArrayBuilder> {

  @Override
  public void build(
      ObjectNode object,
      IJsonGenerationState state) {
    object.put("type", "array");

    if (!getTypes().isEmpty()) {
      ObjectNode items = ObjectUtils.notNull(object.putObject("items"));
      buildInternal(items, state);
    }

    if (getMinOccurrence() > 1) {
      object.put("minItems", getMinOccurrence());
    } else {
      object.put("minItems", 1);
    }

    if (getMaxOccurrence() != -1) {
      object.put("maxItems", getMaxOccurrence());
    }
  }

  @Override
  protected ArrayBuilder thisBuilder() {
    return this;
  }
}
