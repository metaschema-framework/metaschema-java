/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.datatype;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class AbstractDatatypeContent implements IDatatypeContent {
  @NonNull
  private final String typeName;
  @NonNull
  private final List<String> dependencies;

  public AbstractDatatypeContent(@NonNull String typeName, @NonNull List<String> dependencies) {
    this.typeName = typeName;
    this.dependencies = CollectionUtil.unmodifiableList(new ArrayList<>(dependencies));
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  @SuppressFBWarnings("EI_EXPOSE_REP")
  public List<String> getDependencies() {
    return dependencies;
  }
}
