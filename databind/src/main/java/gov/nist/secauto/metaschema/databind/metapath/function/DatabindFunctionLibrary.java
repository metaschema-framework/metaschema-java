/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.function.FunctionLibrary;

/**
 * Function library providing databind-specific Metapath functions.
 * <p>
 * This library registers functions that are specific to the data binding layer,
 * such as the model() function for accessing module information.
 */
public class DatabindFunctionLibrary
    extends FunctionLibrary {

  public DatabindFunctionLibrary() {
    registerFunction(Model.SIGNATURE);
  }
}
