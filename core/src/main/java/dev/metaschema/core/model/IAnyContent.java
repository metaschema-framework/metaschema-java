/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

/**
 * A format-neutral representation of unmodeled content captured from an
 * assembly instance that declares {@code <any/>} in its model.
 *
 * <p>
 * Implementations hold native content representations specific to each
 * serialization format (e.g., W3C DOM for XML, Jackson ObjectNode for JSON).
 * Consumers needing format-specific access should use {@code instanceof} checks
 * on the implementation class.
 */
@FunctionalInterface
public interface IAnyContent {
  /**
   * Determine if this content container has no captured content.
   *
   * @return {@code true} if no unmodeled content was captured, {@code false}
   *         otherwise
   */
  boolean isEmpty();
}
