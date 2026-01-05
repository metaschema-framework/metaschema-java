/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import dev.metaschema.core.metapath.item.node.AbstractNodeItemVisitor;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a set of callbacks used when iterating over model elements in a
 * directed graph.
 * <p>
 * The {@link AbstractNodeItemVisitor} provides an abstract implementation of
 * this visitor pattern.
 *
 * @param <CONTEXT>
 *          the type of contextual data to pass to each visited node
 * @param <RESULT>
 *          the type of result produced by visitation
 * @see AbstractModelElementVisitor
 */
public abstract class AbstractModelElementVisitor<CONTEXT, RESULT> implements IModelElementVisitor<CONTEXT, RESULT> {
  /**
   * Produce a default result for the provided element and contextual information.
   *
   * @param element
   *          the element the result is for
   * @param context
   *          the contextual information provided by the visitor.
   * @return the result
   */
  protected abstract RESULT defaultResult(@NonNull IModelElement element, CONTEXT context);

  @Override
  public RESULT visitFlagInstance(IFlagInstance item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitFieldInstance(IFieldInstanceAbsolute item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitFieldInstance(IFieldInstanceGrouped item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitAssemblyInstance(IAssemblyInstanceAbsolute item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitAssemblyInstance(IAssemblyInstanceGrouped item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitChoiceInstance(IChoiceInstance item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitChoiceGroupInstance(IChoiceGroupInstance item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitFlagDefinition(IFlagDefinition item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitFieldDefinition(IFieldDefinition item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }

  @Override
  public RESULT visitAssemblyDefinition(IAssemblyDefinition item, CONTEXT context) {
    // do nothing
    return defaultResult(item, context);
  }
}
