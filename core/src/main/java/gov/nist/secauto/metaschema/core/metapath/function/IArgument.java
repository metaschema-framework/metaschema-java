/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a single function argument signature.
 */
public interface IArgument {
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IArgument of(@NonNull IEnhancedQName name, @NonNull ISequenceType sequenceType) {
    return new ArgumentImpl(name, sequenceType);
  }

  /**
   * Get the argument's name.
   *
   * @return the argument's name
   */
  @NonNull
  IEnhancedQName getName();

  /**
   * Get information about the type of sequence supported by the argument.
   *
   * @return the sequence information
   */
  @NonNull
  ISequenceType getSequenceType();

  /**
   * Get the signature of the argument.
   *
   * @return the argument's signature
   */
  @NonNull
  String toSignature();

  /**
   * Get a new argument builder.
   *
   * @return the new argument builder
   */
  @NonNull
  static Builder builder() {
    return new Builder();
  }

  @NonNull
  static String resolveArgumentName(@NonNull String prefix) {
    if (!"".equals(prefix)) {
      throw new UnsupportedOperationException("Lexical qualified names are not allowed.");
    }
    return "";
  }

  /**
   * Used to create an argument's signature using a builder pattern.
   */
  final class Builder {
    private IEnhancedQName name;
    @NonNull
    private IItemType type;
    private Occurrence occurrence;

    private Builder() {
      // construct a new non-initialized builder
      this.type = IItem.type();
    }

    /**
     * Define the name of the function argument.
     *
     * @param name
     *          the argument's name
     * @return this builder
     */
    @NonNull
    public Builder name(@NonNull String name) {
      if (Objects.requireNonNull(name, "name").isBlank()) {
        throw new IllegalArgumentException("the name must be non-blank");
      }
      this.name = EQNameFactory.instance().parseName(name, IArgument::resolveArgumentName);
      return this;
    }

    /**
     * Define the type of the function argument.
     * <p>
     * By default an argument has the type {@link IItem}.
     *
     * @param name
     *          the qualified name of the argument's type
     * @return this builder
     */
    @NonNull
    public Builder type(@NonNull IEnhancedQName name) {
      try {
        this.type = StaticContext.lookupAtomicType(name);
      } catch (StaticMetapathException ex) {
        throw new IllegalArgumentException(
            String.format("No data type with the name '%s'.", name), ex);
      }
      return this;
    }

    /**
     * Define the type of the function argument.
     * <p>
     * By default an argument has the type {@link IItem}.
     *
     * @param type
     *          the argument's type
     * @return this builder
     */
    @NonNull
    public Builder type(@NonNull IItemType type) {
      this.type = type;
      return this;
    }

    /**
     * Identifies the argument's cardinality as a single, optional item (zero or
     * one).
     *
     * @return this builder
     */
    @NonNull
    public Builder zeroOrOne() {
      return occurrence(Occurrence.ZERO_OR_ONE);
    }

    /**
     * Identifies the argument's cardinality as a single, required item (one).
     *
     * @return this builder
     */
    @NonNull
    public Builder one() {
      return occurrence(Occurrence.ONE);
    }

    /**
     * Identifies the argument's cardinality as an optional series of items (zero or
     * more).
     *
     * @return this builder
     */
    @NonNull
    public Builder zeroOrMore() {
      return occurrence(Occurrence.ZERO_OR_MORE);
    }

    /**
     * Identifies the argument's cardinality as a required series of items (one or
     * more).
     *
     * @return this builder
     */
    @NonNull
    public Builder oneOrMore() {
      return occurrence(Occurrence.ONE_OR_MORE);
    }

    @NonNull
    private Builder occurrence(@NonNull Occurrence occurrence) {
      Objects.requireNonNull(occurrence, "occurrence");
      this.occurrence = occurrence;
      return this;
    }

    /**
     * Builds the argument's signature.
     *
     * @return the argument's signature
     */
    @NonNull
    public IArgument build() {
      return new ArgumentImpl(
          ObjectUtils.requireNonNull(name, "the argument name must not be null"),
          ISequenceType.of(type, ObjectUtils.requireNonNull(occurrence, "occurrence")));
    }
  }
}
