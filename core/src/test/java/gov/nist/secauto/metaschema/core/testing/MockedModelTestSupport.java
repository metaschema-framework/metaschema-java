/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.testing.mocking.AbstractMockitoFactory;

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
