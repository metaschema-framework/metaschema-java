/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.util;

import gov.nist.secauto.metaschema.core.model.xml.impl.XmlObjectParser;
import gov.nist.secauto.metaschema.core.model.xml.impl.XmlObjectParser.Handler;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

class XmlObjectParserTest {
  private static final String TEST_NS = "https://example.com/ns/test";

  @Test
  void test() {
    XmlObject obj = XmlObject.Factory.newInstance();
    try (XmlCursor cursor = obj.newCursor()) {

      cursor.toNextToken();

      cursor.beginElement(new QName(TEST_NS, "A"));
      cursor.toEndToken();
      cursor.toNextToken();

      cursor.beginElement(new QName(TEST_NS, "B"));
      cursor.toEndToken();
      cursor.toNextToken();

      cursor.beginElement(new QName(TEST_NS, "A"));
      cursor.toEndToken();
      cursor.toNextToken();

      cursor.beginElement(new QName(TEST_NS, "C"));
      cursor.toEndToken();
      cursor.toNextToken();
    }

    obj.dump();

    Procesor processor = new Procesor();
    Map<QName, Handler<Void>> objMapping = ObjectUtils.notNull(Map.ofEntries(
        Map.entry(new QName(TEST_NS, "A"), processor::handleA),
        Map.entry(new QName(TEST_NS, "B"), processor::handleB),
        Map.entry(new QName(TEST_NS, "C"), processor::handleC)));

    new XmlObjectParser<Void>(objMapping).parse(obj, null);
  }

  @SuppressWarnings("unused")
  private static class Procesor {
    void handleA(@NonNull XmlObject obj, Void state) {
      try {
        obj.save(System.out);
        System.out.println();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    void handleB(@NonNull XmlObject obj, Void state) {
      try {
        obj.save(System.out);
        System.out.println();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    void handleC(@NonNull XmlObject obj, Void state) {
      try {
        obj.save(System.out);
        System.out.println();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
