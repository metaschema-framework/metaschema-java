/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonGenerator;

import dev.metaschema.databind.io.IWritingContext;

/**
 * Provides the writing context for serializing Java objects to JSON format.
 * <p>
 * This interface extends {@link IWritingContext} with a JSON-specific writer
 * type.
 *
 * @see JsonGenerator
 */
public interface IJsonWritingContext extends IWritingContext<JsonGenerator> {
  // no additional methods
}
