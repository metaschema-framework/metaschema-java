/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.util.Locale;

import dev.metaschema.core.qname.IEnhancedQName;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A marker interface for an {@code any} instance in a Metaschema model.
 * <p>
 * An {@code any} instance represents unmodeled content that may appear within
 * an assembly's model. Unlike other model instances, an {@code any} instance
 * does not have a definition or name; it acts as a wildcard that captures
 * content not explicitly declared by the model.
 * <p>
 * An {@code any} instance is always optional ({@link #getMinOccurs()} returns
 * {@code 0}) and unbounded ({@link #getMaxOccurs()} returns {@code -1}).
 */
public interface IAnyInstance extends IModelInstanceAbsolute {

  /**
   * Provides the Metaschema model type of "ANY".
   *
   * @return the model type
   */
  @Override
  default ModelType getModelType() {
    return ModelType.ANY;
  }

  @Override
  default IAssemblyDefinition getContainingDefinition() {
    return getParentContainer().getOwningDefinition();
  }

  @Override
  default int getMinOccurs() {
    return 0;
  }

  @Override
  default int getMaxOccurs() {
    return -1;
  }

  @Override
  default IEnhancedQName getEffectiveXmlGroupAsQName() {
    // never grouped
    return null;
  }

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    // any content is never wrapped
    return false;
  }

  @SuppressWarnings("null")
  @Override
  default String toCoordinates() {
    return String.format("%s-instance:%s:%s@%d",
        getModelType().toString().toLowerCase(Locale.ROOT),
        getContainingDefinition().getContainingModule().getShortName(),
        getContainingDefinition().getName(),
        hashCode());
  }

  /**
   * A visitor callback.
   *
   * @param <CONTEXT>
   *          the type of the context parameter
   * @param <RESULT>
   *          the type of the visitor result
   * @param visitor
   *          the calling visitor
   * @param context
   *          a parameter used to pass contextual information between visitors
   * @return the visitor result
   */
  @Override
  default <CONTEXT, RESULT> RESULT accept(@NonNull IModelElementVisitor<CONTEXT, RESULT> visitor, CONTEXT context) {
    return visitor.visitAny(this, context);
  }
}
