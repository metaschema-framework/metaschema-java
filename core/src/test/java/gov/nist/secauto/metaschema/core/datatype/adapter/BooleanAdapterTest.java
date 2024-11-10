/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

class BooleanAdapterTest {
  private static final String TEST_JSON = "{ \"some-boolean\" : true }";

  @Test
  void testParse() throws IOException {
    JsonFactory factory = new JsonFactory();
    try (JsonParser parser = factory.createParser(TEST_JSON)) {
      // skip to the value
      parser.nextToken();
      parser.nextToken();
      parser.nextToken();

      Boolean obj = new BooleanAdapter().parse(
          parser,
          ObjectUtils.notNull(URI.create("https://example.com/not-a-resource")));
      assertAll(
          () -> assertTrue(obj, "object is not true"),
          () -> assertTrue(JsonToken.END_OBJECT.equals(parser.currentToken()), "token is not at end object"));
    }
  }
}
