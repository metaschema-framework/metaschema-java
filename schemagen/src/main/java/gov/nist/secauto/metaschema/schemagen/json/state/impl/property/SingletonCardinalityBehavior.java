/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.state.impl.property;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public class SingletonCardinalityBehavior
    extends AbstractCardinalityBehavior
    implements ICardinalityBehavior {

  private static final SingletonCardinalityBehavior SINGLETON = new SingletonCardinalityBehavior();

  @NonNull
  public static SingletonCardinalityBehavior instance() {
    return SINGLETON;
  }

  @Override
  public void generate(
      ObjectNode node,
      IModelInstanceAbsolute instance,
      Collection<? extends IJsonSchemaDefinable> types,
      IJsonGenerationState state) {
    generateInternal(node, instance, types, state);
  }
}
