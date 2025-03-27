/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.state.impl.property;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ArrayCardinalityBehavior
    extends AbstractCardinalityBehavior {

  private static final ArrayCardinalityBehavior SINGLETON = new ArrayCardinalityBehavior();

  @NonNull
  public static ArrayCardinalityBehavior instance() {
    return SINGLETON;
  }

  @Override
  public void generate(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaDefinable> types,
      IJsonGenerationState state) {
    int minOccurs = instance.getMinOccurs();
    int maxOccurs = instance.getMaxOccurs();

    assert minOccurs >= 0;
    assert maxOccurs == -1 || maxOccurs >= 1;

    node.put("type", "array");

    ObjectNode items = ObjectUtils.notNull(node.putObject("items"));
    generateInternal(items, instance, types, state);
    assert !items.isEmpty();

    // always use minItems since the "required" entry will dictate if the array is
    // required or not. If not required, this will ensure the array has at least one
    // entry when used
    node.put("minItems", Math.max(1, minOccurs));

    if (maxOccurs != -1) {
      node.put("maxItems", maxOccurs);
    }
  }
}
