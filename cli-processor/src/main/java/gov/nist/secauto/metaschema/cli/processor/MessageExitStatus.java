/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link ExitStatus} implementation with an associated message.
 * <p>
 * The message arguments are stored in an unmodifiable list to ensure
 * thread-safety and immutability.
 */
public class MessageExitStatus
    extends AbstractExitStatus {
  private final List<Object> messageArguments;

  /**
   * Construct a new {@link ExitStatus} based on an array of message arguments.
   *
   * @param code
   *          the exit code to use.
   * @param messageArguments
   *          the arguments that can be passed to a formatted string to generate
   *          the message
   */
  public MessageExitStatus(@NonNull ExitCode code, @NonNull Object... messageArguments) {
    super(code);
    this.messageArguments = CollectionUtil.unmodifiableList(
        ObjectUtils.notNull(Arrays.asList(messageArguments)));
  }

  @Override
  public String getMessage() {
    String format = lookupMessageForCode(getExitCode());
    return String.format(format, messageArguments.toArray());
  }

  private String lookupMessageForCode(@SuppressWarnings("unused") ExitCode ignoredExitCode) {
    // TODO: add message bundle support
    StringBuilder builder = new StringBuilder();
    // builder.append(getExitCode()).append(":");
    for (int index = 1; index <= messageArguments.size(); index++) {
      if (index > 1) {
        builder.append(' ');
      }
      builder.append("%s");
      // builder.append(index);
    }
    return builder.toString();
  }
}
