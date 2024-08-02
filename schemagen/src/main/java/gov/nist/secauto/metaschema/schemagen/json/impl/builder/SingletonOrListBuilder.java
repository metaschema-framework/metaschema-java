/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.INamedModelInstanceGrouped;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import java.util.List;

public class SingletonOrListBuilder implements IModelInstanceBuilder<SingletonOrListBuilder> {
  private int minOccurrence = IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS;
  private final ArrayBuilder arrayBuilder;
  private final SingletonBuilder singletonBuilder;

  public SingletonOrListBuilder() {
    this.arrayBuilder = new ArrayBuilder();
    // the array must minimally have 2 items or else the singleton will be used
    this.arrayBuilder.minItems(2);
    this.singletonBuilder = new SingletonBuilder();
  }

  @Override
  public List<IType> getTypes() {
    return arrayBuilder.getTypes();
  }

  @Override
  public SingletonOrListBuilder addItemType(INamedModelInstanceGrouped itemType) {
    arrayBuilder.addItemType(itemType);
    singletonBuilder.addItemType(itemType);
    return this;
  }

  @Override
  public SingletonOrListBuilder addItemType(INamedModelInstanceAbsolute itemType) {
    arrayBuilder.addItemType(itemType);
    singletonBuilder.addItemType(itemType);
    return this;
  }

  @Override
  public void build(
      ObjectNode object,
      IJsonGenerationState state) {
    ArrayNode oneOf = object.putArray("oneOf");
    singletonBuilder.build(ObjectUtils.notNull(oneOf.addObject()), state);
    arrayBuilder.build(ObjectUtils.notNull(oneOf.addObject()), state);
  }

  @Override
  public SingletonOrListBuilder minItems(int min) {
    this.minOccurrence = min;
    arrayBuilder.minItems(Integer.max(2, min));
    if (min > 0) {
      singletonBuilder.minItems(1);
    }
    return this;
  }

  @Override
  public SingletonOrListBuilder maxItems(int max) {
    arrayBuilder.maxItems(max);
    return this;
  }

  @Override
  public int getMinOccurrence() {
    return minOccurrence;
  }

  @Override
  public int getMaxOccurrence() {
    return arrayBuilder.getMaxOccurrence();
  }

}
