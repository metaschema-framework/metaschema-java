/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides objects used for parsing data associated with a specific format.
 *
 * @param <READER>
 *          the format specific data reader
 * @param <PROBLEM_HANDLER>
 *          the format specific problem handler
 */
public interface IParsingContext<READER, PROBLEM_HANDLER extends IProblemHandler> {
  /**
   * The parser used for reading data associated with the supported format.
   *
   * @return the parser
   */
  @NonNull
  READER getReader();

  /**
   * A handler that provides callbacks used to resolve parsing issues.
   *
   * @return the configured handler
   */
  @NonNull
  PROBLEM_HANDLER getProblemHandler();
}
