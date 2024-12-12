/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.testing.model.mocking;

import static org.mockito.Mockito.withSettings;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.mockito.Answers;
import org.mockito.Mockito;

import java.util.UUID;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class AbstractMockitoFactory
    implements IMockFactory {

  protected AbstractMockitoFactory() {
    // allow construction by extending classes
  }

  @Override
  @NonNull
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
        .append(UUID.randomUUID().toString())
        .toString();
    return ObjectUtils.notNull(Mockito.mock(clazz, withSettings()
        .name(builder.toString())
        .defaultAnswer(Answers.CALLS_REAL_METHODS)));
  }
}
