/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.IProblemHandler;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IJsonProblemHandler extends IProblemHandler {

  /**
   * Callback used to handle a JSON property that is unknown to the model being
   * parsed.
   *
   * @param definition
   *          the bound class currently describing the data being parsed
   * @param parentItem
   *          the Java object for the {@code parentDefinition}
   * @param fieldName
   *          the unknown JSON field name
   * @param parser
   *          the JSON reader context used for parsing
   * @return {@code true} if the attribute was handled by this method, or
   *         {@code false} otherwise
   * @throws IOException
   *           if an error occurred while handling the unrecognized data
   */
  boolean handleUnknownProperty(
      @NonNull IBoundDefinitionModelComplex definition,
      @Nullable IBoundObject parentItem,
      @NonNull String fieldName,
      @NonNull JsonParser parser) throws IOException;
}
