/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.model.constraint.AbstractConstraintBuilder;
import dev.metaschema.core.model.constraint.AssemblyConstraintSet;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.ICardinalityConstraint;
import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.IExpectConstraint;
import dev.metaschema.core.model.constraint.IIndexConstraint;
import dev.metaschema.core.model.constraint.IIndexHasKeyConstraint;
import dev.metaschema.core.model.constraint.IMatchesConstraint;
import dev.metaschema.core.model.constraint.IModelConstrained;
import dev.metaschema.core.model.constraint.IUniqueConstraint;
import dev.metaschema.core.model.constraint.MetaConstraintSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implementation of {@link IContextBuilder} for creating constraint contexts.
 * <p>
 * This builder supports all constraint types defined in the Metaschema
 * constraint model:
 * <ul>
 * <li>{@link IAllowedValuesConstraint}</li>
 * <li>{@link IMatchesConstraint}</li>
 * <li>{@link IExpectConstraint}</li>
 * <li>{@link IIndexHasKeyConstraint}</li>
 * <li>{@link ICardinalityConstraint}</li>
 * <li>{@link IIndexConstraint}</li>
 * <li>{@link IUniqueConstraint}</li>
 * </ul>
 */
public class ContextBuilder implements IContextBuilder {
  @NonNull
  private final ISource source;
  @NonNull
  private final List<IMetapathExpression> metapaths = new ArrayList<>();
  @NonNull
  private final List<IConstraint> constraints = new ArrayList<>();
  @NonNull
  private final List<ContextBuilder> childContexts = new ArrayList<>();

  /**
   * Construct a new context builder.
   *
   * @param source
   *          the source for constraints in this context
   */
  public ContextBuilder(@NonNull ISource source) {
    this.source = source;
  }

  @Override
  @NonNull
  public IContextBuilder metapath(@NonNull String target) {
    this.metapaths.add(IMetapathExpression.lazyCompile(target, source.getStaticContext()));
    return this;
  }

  @Override
  @NonNull
  public IContextBuilder constraint(
      @NonNull AbstractConstraintBuilder<?, ? extends IConstraint> constraintBuilder) {
    this.constraints.add(constraintBuilder.build());
    return this;
  }

  @Override
  @NonNull
  public IContextBuilder childContext(@NonNull Consumer<IContextBuilder> childConfigurer) {
    ContextBuilder childBuilder = new ContextBuilder(source);
    childConfigurer.accept(childBuilder);
    this.childContexts.add(childBuilder);
    return this;
  }

  /**
   * Build the context.
   *
   * @param parent
   *          the parent context, or null if this is a top-level context
   * @return the built context
   */
  @NonNull
  MetaConstraintSet.Context build(@Nullable MetaConstraintSet.Context parent) {
    // Create the constraint set for this context
    IModelConstrained modelConstrained = new AssemblyConstraintSet(source);

    // Add constraints to the model
    for (IConstraint constraint : constraints) {
      addConstraint(modelConstrained, constraint);
    }

    // Create the context with a defensive copy of metapaths
    MetaConstraintSet.Context context = new MetaConstraintSet.Context(
        parent,
        source,
        List.copyOf(metapaths),
        modelConstrained);

    // Build and add child contexts
    List<MetaConstraintSet.Context> builtChildren = new ArrayList<>();
    for (ContextBuilder childBuilder : childContexts) {
      builtChildren.add(childBuilder.build(context));
    }
    context.addAll(builtChildren);

    return context;
  }

  /**
   * Add a constraint to the model constrained object.
   * <p>
   * This method dispatches the constraint to the appropriate typed add method
   * based on the constraint's runtime type.
   * <p>
   * <b>Note:</b> When new constraint types are added to the Metaschema constraint
   * model, this method and the class-level Javadoc must be updated to include
   * support for the new type.
   *
   * @param modelConstrained
   *          the model constrained object to add the constraint to
   * @param constraint
   *          the constraint to add
   * @throws UnsupportedOperationException
   *           if the constraint type is not supported
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  private static void addConstraint(@NonNull IModelConstrained modelConstrained, @NonNull IConstraint constraint) {
    if (constraint instanceof IAllowedValuesConstraint) {
      modelConstrained.addConstraint((IAllowedValuesConstraint) constraint);
    } else if (constraint instanceof IMatchesConstraint) {
      modelConstrained.addConstraint((IMatchesConstraint) constraint);
    } else if (constraint instanceof IExpectConstraint) {
      modelConstrained.addConstraint((IExpectConstraint) constraint);
    } else if (constraint instanceof IIndexHasKeyConstraint) {
      modelConstrained.addConstraint((IIndexHasKeyConstraint) constraint);
    } else if (constraint instanceof ICardinalityConstraint) {
      modelConstrained.addConstraint((ICardinalityConstraint) constraint);
    } else if (constraint instanceof IIndexConstraint) {
      modelConstrained.addConstraint((IIndexConstraint) constraint);
    } else if (constraint instanceof IUniqueConstraint) {
      modelConstrained.addConstraint((IUniqueConstraint) constraint);
    } else {
      throw new UnsupportedOperationException(
          "Unsupported constraint type: " + constraint.getClass().getName());
    }
  }
}
