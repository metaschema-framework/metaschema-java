/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import java.util.Objects;

import dev.metaschema.core.metapath.type.ISequenceType;
import dev.metaschema.core.qname.IEnhancedQName;
import edu.umd.cs.findbugs.annotations.NonNull;

class ArgumentImpl implements IArgument {
  @NonNull
  private final IEnhancedQName name;
  @NonNull
  private final ISequenceType sequenceType;

  protected ArgumentImpl(@NonNull IEnhancedQName name, @NonNull ISequenceType sequenceType) {
    this.name = name;
    this.sequenceType = sequenceType;
  }

  @Override
  public IEnhancedQName getName() {
    return name;
  }

  @Override
  public ISequenceType getSequenceType() {
    return sequenceType;
  }

  @Override
  public String toSignature() {
    StringBuilder builder = new StringBuilder();

    // name
    builder.append(getName().toEQName())
        .append(" as ")
        .append(getSequenceType().toSignature());

    return builder.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, sequenceType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ArgumentImpl other = (ArgumentImpl) obj;
    return Objects.equals(name, other.name) && Objects.equals(sequenceType, other.sequenceType);
  }
}
