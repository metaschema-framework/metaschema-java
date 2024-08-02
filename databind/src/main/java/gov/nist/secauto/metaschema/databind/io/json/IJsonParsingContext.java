/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.databind.io.IParsingContext;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;

public interface IJsonParsingContext extends IParsingContext<JsonParser, IJsonProblemHandler> {
  // no additional methods

  interface IInstanceReader extends IItemReadHandler {
    // no additional methods
  }
}
