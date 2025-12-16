/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.completion;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides shell completion code for a specific type.
 * <p>
 * Instances are registered with {@link CompletionTypeRegistry} and looked up by
 * the type class specified in {@link org.apache.commons.cli.Option#getType()}
 * or
 * {@link gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument#getType()}.
 */
public interface ICompletionType {
  /**
   * Generate Bash completion code for this type.
   *
   * @return bash completion snippet, or empty string for freeform input
   */
  @NonNull
  String getBashCompletion();

  /**
   * Generate Zsh completion code for this type.
   *
   * @return zsh completion snippet, or empty string for freeform input
   */
  @NonNull
  String getZshCompletion();
}
