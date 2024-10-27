/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.IAttributable;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides builder methods for the core data elements of an
 * {@link IConstraint}.
 * <p>
 * The base class of all constraint builders.
 *
 * @param <T>
 *          the Java type of the implementing builder
 * @param <R>
 *          the Java type of the resulting built object
 */
public abstract class AbstractConstraintBuilder<
    T extends AbstractConstraintBuilder<T, R>,
    R extends IConstraint> {
  private String id;
  private String formalName;
  private MarkupLine description;
  private ISource source;
  @NonNull
  private Level level = IConstraint.DEFAULT_LEVEL;
  @NonNull
  private String target = IConstraint.DEFAULT_TARGET_METAPATH;
  @NonNull
  private Map<IAttributable.Key, Set<String>> properties = new LinkedHashMap<>(); // NOPMD not thread safe
  private String message;
  private MarkupMultiline remarks;

  /**
   * Get the builder.
   * <p>
   * Implementations of this method must return {@code this}.
   *
   * @return the builder instance
   */
  @NonNull
  protected abstract T getThis();

  /**
   * Set an identifier for the constraint.
   *
   * @param id
   *          the identifier to set
   * @return this builder
   */
  @NonNull
  public T identifier(@NonNull String id) {
    this.id = id;
    return getThis();
  }

  /**
   * Set a formal name for the constraint.
   *
   * @param name
   *          the formal name to set
   * @return this builder
   */
  @NonNull
  public T formalName(@NonNull String name) {
    this.formalName = name;
    return getThis();
  }

  /**
   * Set a description for the constraint.
   *
   * @param description
   *          the description to set
   * @return this builder
   */
  @NonNull
  public T description(@NonNull MarkupLine description) {
    this.description = description;
    return getThis();
  }

  /**
   * Set the source the constraint was parsed from.
   *
   * @param source
   *          the source to set
   * @return this builder
   */
  @NonNull
  public T source(@NonNull ISource source) {
    this.source = source;
    return getThis();
  }

  /**
   * Set the severity level for when the constraint is violated.
   *
   * @param level
   *          the level to set
   * @return this builder
   */
  @NonNull
  public T level(@NonNull Level level) {
    this.level = level;
    return getThis();
  }

  /**
   * Set the Metapath expression used to get the target(s) of the constraint.
   *
   * @param target
   *          a Metapath expression, which will be evaluated relative to the
   *          definition it is declared on
   * @return this builder
   */
  @NonNull
  public T target(@NonNull String target) {
    this.target = target;
    return getThis();
  }

  /**
   * Set the collection of properties associated with the constraint.
   *
   * @param properties
   *          the properties to set
   * @return this builder
   */
  @NonNull
  public T properties(@NonNull Map<IAttributable.Key, Set<String>> properties) {
    this.properties = properties;
    return getThis();
  }

  /**
   * Set the values of the property with the provided {@code name} to the provided
   * {@code value}.
   *
   * @param key
   *          the property's name
   * @param value
   *          the value to set
   * @return this builder
   */
  @NonNull
  public T property(@NonNull IAttributable.Key key, @NonNull String value) {
    return property(key, CollectionUtil.singleton(value));
  }

  /**
   * Set the values of the property with the provided {@code name} to the provided
   * {@code values}.
   *
   * @param key
   *          the property's name
   * @param values
   *          the values to set
   * @return this builder
   */
  @NonNull
  public T property(@NonNull IAttributable.Key key, @NonNull Set<String> values) {
    properties.put(key, new LinkedHashSet<>(values));
    return getThis();
  }

  /**
   * A message to emit when the constraint is violated. Allows embedded Metapath
   * expressions using the syntax {@code \{ metapath \}}.
   *
   * @param message
   *          the message if defined or {@code null} otherwise
   * @return this builder
   */
  @NonNull
  public T message(@NonNull String message) {
    this.message = message;
    return getThis();
  }

  /**
   * Set the provided {@code remarks}.
   *
   * @param remarks
   *          the remarks to set
   * @return this builder
   */
  @NonNull
  public T remarks(@NonNull MarkupMultiline remarks) {
    this.remarks = remarks;
    return getThis();
  }

  /**
   * Validate the values provided to the builder.
   *
   * @throws NullPointerException
   *           if a required value is {@code null}
   * @throws IllegalStateException
   *           in other cases where the combination of values is inappropriate
   */
  protected void validate() {
    ObjectUtils.requireNonNull(getSource());
  }

  /**
   * Get a new instance of the built object.
   *
   * @return the built instance
   */
  @NonNull
  protected abstract R newInstance();

  /**
   * Generate the built instance after validating the provided data.
   *
   * @return the built instance
   */
  @NonNull
  public R build() {
    validate();
    return newInstance();
  }

  /**
   * Get the constraint identifier provided to the builder.
   *
   * @return the identifier or {@code null} if no identifier has been set
   */
  @Nullable
  protected String getId() {
    return id;
  }

  /**
   * Get the constraint formal name provided to the builder.
   *
   * @return the formal name or {@code null} if no formal name has been set
   */
  @Nullable
  protected String getFormalName() {
    return formalName;
  }

  /**
   * Get the constraint description provided to the builder.
   *
   * @return the description or {@code null} if no description has been set
   */
  @Nullable
  protected MarkupLine getDescription() {
    return description;
  }

  /**
   * Get the constraint source provided to the builder.
   *
   * @return the source or {@code null} if no source has been set
   */
  @Nullable
  protected ISource getSource() {
    return source;
  }

  /**
   * Get the constraint severity level provided to the builder.
   *
   * @return the severity level
   */
  @NonNull
  protected Level getLevel() {
    return level;
  }

  /**
   * Get the Metapath expression, provided to the builder, used to get the
   * target(s) of the constraint.
   *
   * @return the target Metapath expression
   */
  @NonNull
  protected String getTarget() {
    return target;
  }

  /**
   * Get the constraint properties provided to the builder.
   *
   * @return the properties or an empty Map if no properties are set
   */
  @NonNull
  protected Map<IAttributable.Key, Set<String>> getProperties() {
    return properties;
  }

  /**
   * Get the constraint message provided to the builder.
   *
   * @return the message or {@code null} if no message is set
   */
  @Nullable
  protected String getMessage() {
    return message;
  }

  /**
   * Get the remarks provided to the builder.
   *
   * @return the remarks
   */
  @Nullable
  protected MarkupMultiline getRemarks() {
    return remarks;
  }
}
