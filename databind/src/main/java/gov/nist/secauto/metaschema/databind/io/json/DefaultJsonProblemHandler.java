/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.util.JsonUtil;
import gov.nist.secauto.metaschema.databind.io.AbstractProblemHandler;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This problem handler implementation handles common issues when parsing
 * JSON-based Metaschema module instances.
 */
public class DefaultJsonProblemHandler
    extends AbstractProblemHandler
    implements IJsonProblemHandler {
  private static final String JSON_SCHEMA_FIELD_NAME = "$schema";
  private static final Set<String> IGNORED_FIELD_NAMES;

  static {
    IGNORED_FIELD_NAMES = new HashSet<>();
    IGNORED_FIELD_NAMES.add(JSON_SCHEMA_FIELD_NAME);
  }

  @Override
  public boolean handleUnknownProperty(
      IBoundDefinitionModelComplex classBinding,
      IBoundObject targetObject,
      String fieldName,
      IJsonParsingContext parsingContext) throws IOException {
    boolean retval = false;
    if (IGNORED_FIELD_NAMES.contains(fieldName)) {
      JsonUtil.skipNextValue(parsingContext.getReader(), parsingContext.getSource());
      retval = true;
    }
    return retval;
  }
}
