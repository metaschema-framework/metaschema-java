/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.jmock.Mockery;

import java.util.UUID;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class MockFactory implements IMockFactory {

  @NonNull
  private final Mockery context;

  /**
   * Construct a new mock factory.
   *
   * @param ctx
   *          the mocking context
   */
  public MockFactory(@NonNull Mockery ctx) {
    this.context = ctx;
  }

  @Override
  public Mockery getContext() {
    return context;
  }

  @Override
  public <T> T mock(@NonNull Class<T> clazz, @Nullable String name) {
    StringBuilder builder = new StringBuilder()
        .append(clazz.getSimpleName());
    if (name != null) {
      builder
          .append('-')
          .append(name);
    }
    builder
        .append('-')
        .append(UUID.randomUUID().toString());

    String mockName = builder.toString();
    return ObjectUtils.notNull(getContext().mock(clazz, mockName));
  }
}
