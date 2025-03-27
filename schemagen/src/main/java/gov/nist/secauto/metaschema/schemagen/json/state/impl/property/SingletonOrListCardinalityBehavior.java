/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.state.impl.property;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SingletonOrListCardinalityBehavior
    extends AbstractCardinalityBehavior {
  private static final SingletonOrListCardinalityBehavior SINGLETON = new SingletonOrListCardinalityBehavior();

  @NonNull
  public static SingletonOrListCardinalityBehavior instance() {
    return SINGLETON;
  }

  @Override
  public void generate(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaDefinable> types,
      IJsonGenerationState state) {
    ArrayNode oneOf = node.putArray("oneOf");

    generateInternal(ObjectUtils.notNull(oneOf.addObject()), instance, types, state);

    generateArray(ObjectUtils.notNull(oneOf.addObject()), instance, types, state);
  }

  private void generateArray(
      @NonNull ObjectNode node,
      @NonNull IModelInstanceAbsolute instance,
      @NonNull Collection<? extends IJsonSchemaDefinable> types,
      @NonNull IJsonGenerationState state) {
    int minOccurs = instance.getMinOccurs();
    int maxOccurs = instance.getMaxOccurs();

    assert minOccurs >= 0;
    assert maxOccurs == -1 || maxOccurs >= 1;

    node.put("type", "array");

    ObjectNode items = ObjectUtils.notNull(node.putObject("items"));
    generateInternal(items, instance, types, state);
    assert !items.isEmpty();

    node.put("minItems", Math.max(2, minOccurs));

    if (maxOccurs != -1) {
      node.put("maxItems", maxOccurs);
    }
  }
}
