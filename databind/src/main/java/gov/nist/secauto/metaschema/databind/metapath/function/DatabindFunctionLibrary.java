/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.metapath.function;

import com.google.auto.service.AutoService;

import gov.nist.secauto.metaschema.core.metapath.function.FunctionLibrary;
import gov.nist.secauto.metaschema.core.metapath.function.IFunctionLibrary;

@AutoService(IFunctionLibrary.class)
public class DatabindFunctionLibrary
    extends FunctionLibrary {

  public DatabindFunctionLibrary() {
    registerFunction(Model.SIGNATURE);
  }
}
