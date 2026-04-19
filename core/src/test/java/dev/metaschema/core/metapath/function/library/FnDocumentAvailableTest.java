/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.item.atomic.IAnyUriItem;

class FnDocumentAvailableTest {

  /**
   * Tests for https://github.com/metaschema-framework/metaschema-java/issues/208.
   */
  @Test
  void issue208Test() {
    // URL pinned to a release tag of this project so the resource remains
    // available long-term. The previously-referenced fedramp-automation
    // content was removed upstream, causing this test to fail with HTTP 404.
    IAnyUriItem uri = IAnyUriItem.valueOf(
        "https://raw.githubusercontent.com/metaschema-framework/metaschema-java/v3.0.0.M3/README.md");

    assertTrue(FnDocumentAvailable.fnDocAvailable(uri, new DynamicContext()).toBoolean());
  }
}
