/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.databind.test.util.CloseDetectingInputStream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

class FormatDetectorTest {

  @Test
  void testDetectXml() throws IOException {
    try (InputStream is = Files.newInputStream(Paths.get("src/test/resources/test-content/bound-class-simple.xml"))) {
      assert is != null;

      try (CloseDetectingInputStream cis = new CloseDetectingInputStream(is)) {
        FormatDetector detector = new FormatDetector();
        FormatDetector.Result result = detector.detect(cis);

        assertAll(
            () -> assertNotNull(is),
            () -> assertEquals(Format.XML, result.getFormat()),
            () -> assertFalse(cis.isClosed(), "primary closed"),
            () -> {
              result.getDataStream().close();
              assertTrue(cis.isClosed(), "secondary closed");
            });
      }
    }
  }

  @Test
  void testDetectJson() throws IOException {
    try (InputStream is = Files.newInputStream(Paths.get("src/test/resources/test-content/bound-class-simple.json"))) {
      assert is != null;

      try (CloseDetectingInputStream cis = new CloseDetectingInputStream(is)) {
        FormatDetector detector = new FormatDetector();
        FormatDetector.Result result = detector.detect(cis);

        assertAll(
            () -> assertNotNull(is),
            () -> assertEquals(Format.JSON, result.getFormat()),
            () -> assertFalse(cis.isClosed(), "primary closed"),
            () -> {
              result.getDataStream().close();
              assertTrue(cis.isClosed(), "secondary closed");
            });
      }
    }
  }

}
