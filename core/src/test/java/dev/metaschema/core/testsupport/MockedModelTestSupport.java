/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.testsupport;

import dev.metaschema.core.testsupport.builder.IModuleMockFactory;
import dev.metaschema.core.testsupport.mocking.AbstractMockitoFactory;

/**
 * Provides the ability to generate mocked Metaschema module definitions and
 * instances, along with other mocked data.
 */
public class MockedModelTestSupport
    extends AbstractMockitoFactory
    implements IModuleMockFactory {
  /**
   * Construct a new model mock factory using the default JUnit-based mocking
   * context.
   */
  public MockedModelTestSupport() {
    // do nothing
  }
}
