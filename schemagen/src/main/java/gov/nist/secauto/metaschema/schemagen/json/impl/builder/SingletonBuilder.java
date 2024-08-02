/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

public class SingletonBuilder
    extends AbstractCollectionBuilder<SingletonBuilder> {
  private int minOccurrence = IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS;

  @Override
  protected SingletonBuilder thisBuilder() {
    return this;
  }

  @Override
  public int getMinOccurrence() {
    return minOccurrence;
  }

  @Override
  public int getMaxOccurrence() {
    return 1;
  }

  @Override
  public SingletonBuilder minItems(int min) {
    if (min < 0 || min > 1) {
      throw new IllegalArgumentException(
          String.format("The minimum value '%d' must be 0 or 1.", min));
    }
    minOccurrence = min;
    return this;
  }

  @Override
  public SingletonBuilder maxItems(int max) {
    if (max != 1) {
      throw new IllegalArgumentException(
          String.format("The maximum value '%d' must be 1.", max));
    }
    return this;
  }

  @Override
  public void build(
      ObjectNode object,
      IJsonGenerationState state) {
    if (!getTypes().isEmpty()) {
      buildInternal(object, state);
    }
  }
}
