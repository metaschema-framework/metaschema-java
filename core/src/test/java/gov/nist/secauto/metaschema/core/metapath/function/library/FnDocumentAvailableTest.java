/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;

import org.junit.jupiter.api.Test;

class FnDocumentAvailableTest {

  /**
   * Tests for https://github.com/metaschema-framework/metaschema-java/issues/208.
   */
  @Test
  void issue208Test() {
    IAnyUriItem uri = IAnyUriItem.valueOf(
        "https://raw.githubusercontent.com/GSA/fedramp-automation/8301e380c88532ebbb22aca55521701750eb0b83/src/content/awesome-cloud/xml/AwesomeCloudSSP1.xml");

    assertTrue(FnDocumentAvailable.fnDocAvailable(uri, new DynamicContext()).toBoolean());
  }
}
