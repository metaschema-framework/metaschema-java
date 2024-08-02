/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.json;

import com.fasterxml.jackson.core.JsonParser;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.util.JsonUtil;
import gov.nist.secauto.metaschema.databind.io.AbstractProblemHandler;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
      JsonParser parser) throws IOException {
    boolean retval = false;
    if (IGNORED_FIELD_NAMES.contains(fieldName)) {
      JsonUtil.skipNextValue(parser);
      retval = true;
    }
    return retval;
  }
}
