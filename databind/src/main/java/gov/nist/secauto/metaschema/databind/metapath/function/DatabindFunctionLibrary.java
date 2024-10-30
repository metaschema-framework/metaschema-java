/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.function.FunctionLibrary;

public class DatabindFunctionLibrary
    extends FunctionLibrary {

  public DatabindFunctionLibrary() {
    registerFunction(Model.SIGNATURE);
  }
}
