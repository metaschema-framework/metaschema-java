/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.IConstraintSet;
import dev.metaschema.core.model.constraint.MetaConstraintSet;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implementation of {@link IConstraintSetBuilder} for creating constraint sets
 * programmatically.
 */
public class ConstraintSetBuilder implements IConstraintSetBuilder {
  private ISource source;
  @NonNull
  private final List<IConstraintSet> imports = new ArrayList<>();
  @NonNull
  private final List<ContextBuilder> contexts = new ArrayList<>();

  /**
   * Construct a new builder.
   */
  public ConstraintSetBuilder() {
    // default constructor
  }

  @Override
  @NonNull
  public IConstraintSetBuilder source(@NonNull ISource source) {
    this.source = source;
    return this;
  }

  @Override
  @NonNull
  public IConstraintSetBuilder imports(@NonNull IConstraintSet... imports) {
    this.imports.addAll(Arrays.asList(imports));
    return this;
  }

  @Override
  @NonNull
  public IConstraintSetBuilder context(@NonNull Consumer<IContextBuilder> contextConfigurer) {
    ContextBuilder contextBuilder = new ContextBuilder(
        ObjectUtils.requireNonNull(source, "source must be set before adding contexts"));
    contextConfigurer.accept(contextBuilder);
    this.contexts.add(contextBuilder);
    return this;
  }

  @Override
  @NonNull
  public IConstraintSet build() {
    ISource constraintSource = ObjectUtils.requireNonNull(source, "source must be set");

    // Build contexts
    List<MetaConstraintSet.Context> builtContexts = new ArrayList<>();
    for (ContextBuilder contextBuilder : contexts) {
      builtContexts.add(contextBuilder.build(null));
    }

    return new MetaConstraintSet(constraintSource, imports, builtContexts);
  }
}
