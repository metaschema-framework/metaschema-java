/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.metapath.function;

import dev.metaschema.core.metapath.function.FunctionLibrary;

/**
 * Function library providing databind-specific Metapath functions.
 * <p>
 * This library registers functions that are specific to the data binding layer,
 * such as the model() function for accessing module information.
 */
public class DatabindFunctionLibrary
    extends FunctionLibrary {

  /**
   * Construct a new databind function library, registering all databind-specific
   * Metapath functions.
   */
  public DatabindFunctionLibrary() {
    registerFunction(Model.SIGNATURE);
  }
}
