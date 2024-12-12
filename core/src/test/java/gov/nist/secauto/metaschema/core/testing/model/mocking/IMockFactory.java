/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model.mocking;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IMockFactory {
  /**
   * Create a mock for the given class.
   * <p>
   * Automatically generates a name for the mocked object.
   *
   * @param <T>
   *          the Java type to mock
   * @param clazz
   *          the class of the Java type to mock
   * @return the mocked object
   */
  @NonNull
  default <T> T mock(@NonNull Class<T> clazz) {
    return mock(clazz, null);
  }

  /**
   * Create a mock for the given class.
   * <p>
   * Uses the provided name, if not {@code null}, or otherwise automatically
   * generates a name for the mocked object.
   *
   * @param <T>
   *          the Java type to mock
   * @param clazz
   *          the class of the Java type to mock
   * @param name
   *          the name to use for the mocked object if not {@code null}
   * @return the mocked object
   */
  @NonNull
  <T> T mock(@NonNull Class<T> clazz, @Nullable String name);
}
