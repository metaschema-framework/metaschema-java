/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonParser;

import dev.metaschema.databind.io.IParsingContext;
import dev.metaschema.databind.model.info.IItemReadHandler;

/**
 * Provides the parsing context for reading JSON-based Metaschema module
 * instances.
 * <p>
 * This interface extends {@link IParsingContext} with JSON-specific reader and
 * problem handler types.
 *
 * @see JsonParser
 * @see IJsonProblemHandler
 */
public interface IJsonParsingContext extends IParsingContext<JsonParser, IJsonProblemHandler> {
  // no additional methods

  /**
   * A reader for processing JSON instances using the item read handler pattern.
   */
  interface IInstanceReader extends IItemReadHandler {
    // no additional methods
  }
}
