/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testsupport.builder;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.AbstractConstraintBuilder;
import gov.nist.secauto.metaschema.core.model.constraint.AssemblyConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IExpectConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IIndexHasKeyConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IMatchesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IModelConstrained;
import gov.nist.secauto.metaschema.core.model.constraint.MetaConstraintSet;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implementation of {@link IContextBuilder} for creating constraint contexts.
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
  public <B extends AbstractConstraintBuilder<B, C>, C extends IConstraint> IContextBuilder constraint(
      @NonNull AbstractConstraintBuilder<B, C> constraintBuilder) {
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

    // Create the context
    MetaConstraintSet.Context context = new MetaConstraintSet.Context(
        parent,
        source,
        ObjectUtils.notNull(metapaths),
        modelConstrained);

    // Build and add child contexts
    List<MetaConstraintSet.Context> builtChildren = new ArrayList<>();
    for (ContextBuilder childBuilder : childContexts) {
      builtChildren.add(childBuilder.build(context));
    }
    context.addAll(builtChildren);

    return context;
  }

  private static void addConstraint(@NonNull IModelConstrained modelConstrained, @NonNull IConstraint constraint) {
    if (constraint instanceof IAllowedValuesConstraint) {
      modelConstrained.addConstraint((IAllowedValuesConstraint) constraint);
    } else if (constraint instanceof IMatchesConstraint) {
      modelConstrained.addConstraint((IMatchesConstraint) constraint);
    } else if (constraint instanceof IExpectConstraint) {
      modelConstrained.addConstraint((IExpectConstraint) constraint);
    } else if (constraint instanceof IIndexHasKeyConstraint) {
      modelConstrained.addConstraint((IIndexHasKeyConstraint) constraint);
    } else {
      throw new UnsupportedOperationException(
          "Unsupported constraint type: " + constraint.getClass().getName());
    }
  }
}
