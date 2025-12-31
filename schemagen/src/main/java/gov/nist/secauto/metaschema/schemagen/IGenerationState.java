/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents the state information used during schema generation.
 *
 * @param <WRITER>
 *          the type of writer used for schema output
 */
public interface IGenerationState<WRITER> {
  /**
   * Get the Metaschema module being processed for schema generation.
   *
   * @return the module
   */
  @NonNull
  IModule getModule();

  /**
   * Get the writer used for schema output.
   * <p>
   * The caller does not own this writer and must not close it.
   *
   * @return the writer instance
   */
  @NonNull
  WRITER getWriter();

  /**
   * Get the collection of root assembly definitions exported by the module.
   *
   * @return the root assembly definitions
   */
  @NonNull
  default Collection<? extends IAssemblyDefinition> getRootDefinitions() {
    return getModule().getExportedRootAssemblyDefinitions();
  }

  /**
   * Determine if the provided definition should be inlined in the generated
   * schema.
   *
   * @param definition
   *          the definition to check
   * @return {@code true} if the definition should be inlined, {@code false}
   *         otherwise
   */
  boolean isInline(@NonNull IDefinition definition);

  /**
   * Flush any buffered content to the underlying writer.
   *
   * @throws IOException
   *           if an I/O error occurs while flushing
   */
  void flushWriter() throws IOException;

  /**
   * Generate a type name for the provided definition with an optional suffix.
   *
   * @param definition
   *          the definition to generate a type name for
   * @param suffix
   *          an optional suffix to append to the type name, or {@code null} if no
   *          suffix is needed
   * @return the generated type name
   */
  @NonNull
  String getTypeNameForDefinition(@NonNull IDefinition definition, @Nullable String suffix);

  /**
   * Convert a text string to camel case by splitting on punctuation and
   * capitalizing each segment.
   *
   * @param text
   *          the text to convert
   * @return the camel case representation of the text
   */
  @NonNull
  static CharSequence toCamelCase(String text) {
    StringBuilder builder = new StringBuilder();
    for (String segment : text.split("\\p{Punct}")) {
      if (segment.length() > 0) {
        builder.append(segment.substring(0, 1).toUpperCase(Locale.ROOT));
      }
      if (segment.length() > 1) {
        builder.append(segment.substring(1).toLowerCase(Locale.ROOT));
      }
    }
    return builder;
  }
}
