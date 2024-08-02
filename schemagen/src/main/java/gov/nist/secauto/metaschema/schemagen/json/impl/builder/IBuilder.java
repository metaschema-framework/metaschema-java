/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IBuilder<T extends IBuilder<T>> {
  void build(
      @NonNull ObjectNode object,
      @NonNull IJsonGenerationState state);
}
