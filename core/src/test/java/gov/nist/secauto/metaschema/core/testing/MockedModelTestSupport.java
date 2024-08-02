/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.extension.RegisterExtension;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MockedModelTestSupport implements IMockFactory {
  @RegisterExtension
  @NonNull
  Mockery context = new JUnit5Mockery();

  /**
   * Get a new flag builder.
   *
   * @return the builder
   */
  @NonNull
  protected FlagBuilder flag() {
    return FlagBuilder.builder(context);
  }

  /**
   * Get a new field builder.
   *
   * @return the builder
   */
  @NonNull
  protected FieldBuilder field() {
    return FieldBuilder.builder(context);
  }

  /**
   * Get a new assembly builder.
   *
   * @return the builder
   */
  @NonNull
  protected AssemblyBuilder assembly() {
    return AssemblyBuilder.builder(context);
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
