/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class BooleanAdapterTest {
  private static final String TEST_JSON = "{ \"some-boolean\" : true }";

  private BooleanAdapter adapter;
  private JsonParser parser;

  @BeforeEach
  void initParser() throws JsonParseException, IOException { // NOPMD
    adapter = new BooleanAdapter();
    JsonFactory factory = new JsonFactory();
    parser = factory.createParser(TEST_JSON);
  }

  @Test
  void testParse() throws IOException {
    // skip to the value
    parser.nextToken();
    parser.nextToken();
    parser.nextToken();

    Boolean obj = adapter.parse(parser);
    assertAll(
        () -> assertTrue(obj, "object is not true"),
        () -> assertTrue(JsonToken.END_OBJECT.equals(parser.currentToken()), "token is not at end object"));
  }
}
