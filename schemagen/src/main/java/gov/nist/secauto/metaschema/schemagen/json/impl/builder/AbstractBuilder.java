/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl.builder;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractBuilder<T extends AbstractBuilder<T>> implements IBuilder<T> {

  @NonNull
  protected abstract T thisBuilder();
}
