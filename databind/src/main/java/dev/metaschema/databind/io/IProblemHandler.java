/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.databind.model.IBoundDefinitionModelComplex;
import dev.metaschema.databind.model.IBoundProperty;

import java.io.IOException;
import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implementations support handling common parsing issues.
 */
@FunctionalInterface
public interface IProblemHandler {
  /**
   * A callback used to handle bound properties for which no data was found when
   * the content was parsed.
   * <p>
   * This can be used to supply default or prescribed values based on application
   * logic.
   *
   * @param parentDefinition
   *          the bound class on which the missing properties are found
   * @param targetObject
   *          the Java object for the {@code parentDefinition}
   * @param unhandledInstances
   *          the set of instances that had no data to parse
   * @throws IOException
   *           if an error occurred while handling the missing instances
   */
  void handleMissingInstances(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @NonNull IBoundObject targetObject,
      @NonNull Collection<? extends IBoundProperty<?>> unhandledInstances)
      throws IOException;

  /**
   * A callback used to handle bound properties for which no data was found when
   * the content was parsed, with additional validation context.
   * <p>
   * This method provides richer context information for error messages including
   * source location, document path, and format-specific details.
   * <p>
   * The default implementation delegates to
   * {@link #handleMissingInstances(IBoundDefinitionModelComplex, IBoundObject, Collection)}
   * for backward compatibility.
   *
   * @param parentDefinition
   *          the bound class on which the missing properties are found
   * @param targetObject
   *          the Java object for the {@code parentDefinition}
   * @param unhandledInstances
   *          the set of instances that had no data to parse
   * @param context
   *          the validation context with location and path information, may be
   *          null for backward compatibility
   * @throws IOException
   *           if an error occurred while handling the missing instances
   */
  default void handleMissingInstances(
      @NonNull IBoundDefinitionModelComplex parentDefinition,
      @NonNull IBoundObject targetObject,
      @NonNull Collection<? extends IBoundProperty<?>> unhandledInstances,
      @Nullable ValidationContext context)
      throws IOException {
    // Default implementation ignores context for backward compatibility
    handleMissingInstances(parentDefinition, targetObject, unhandledInstances);
  }
}
