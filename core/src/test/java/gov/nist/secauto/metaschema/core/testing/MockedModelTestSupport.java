/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.extension.RegisterExtension;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MockedModelTestSupport implements IModuleMockFactory {
  @RegisterExtension
  @NonNull
  Mockery context;

  /**
   * Construct a new model mock factory using the default JUnit-based mocking
   * context.
   */
  public MockedModelTestSupport() {
    this(new JUnit5Mockery());
  }

  /**
   * Construct a new model mock factory using the provided mocking context.
   */
  public MockedModelTestSupport(@NonNull Mockery context) {
    this.context = context;
  }

  @Override
  public Mockery getContext() {
    return context;
  }

  @Override
  public <T> T mock(Class<T> clazz, String name) {
    return new MockFactory(getContext()).mock(clazz, name);
  }
}
