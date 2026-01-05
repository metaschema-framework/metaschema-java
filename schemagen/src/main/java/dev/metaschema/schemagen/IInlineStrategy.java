/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen;

import dev.metaschema.core.configuration.IConfiguration;
import dev.metaschema.core.model.IDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A strategy for determining whether a definition should be inlined in the
 * generated schema or referenced as a separate type definition.
 */
@FunctionalInterface
public interface IInlineStrategy {
  /**
   * A strategy that never inlines any definition.
   */
  @NonNull
  IInlineStrategy NONE_INLINE = new IInlineStrategy() {
    @Override
    public boolean isInline(
        @NonNull IDefinition definition,
        @NonNull ModuleIndex metaschemaIndex) {
      return false;
    }
  };

  /**
   * A strategy that inlines definitions based on their
   * {@link IDefinition#isInline()} property.
   */
  @NonNull
  IInlineStrategy DEFINED_AS_INLINE = new IInlineStrategy() {
    @Override
    public boolean isInline(
        @NonNull IDefinition definition,
        @NonNull ModuleIndex metaschemaIndex) {
      return definition.isInline();
    }
  };

  /**
   * A strategy that inlines definitions unless they are used in a choice group.
   */
  @NonNull
  IInlineStrategy CHOICE_NOT_INLINE = new ChoiceNotInlineStrategy();

  /**
   * Create a new inline strategy based on the provided configuration.
   *
   * @param configuration
   *          the schema generation configuration
   * @return the appropriate inline strategy based on the configuration settings
   */
  @NonNull
  static IInlineStrategy newInlineStrategy(@NonNull IConfiguration<SchemaGenerationFeature<?>> configuration) {
    IInlineStrategy retval;
    if (configuration.isFeatureEnabled(SchemaGenerationFeature.INLINE_DEFINITIONS)) {
      if (configuration.isFeatureEnabled(SchemaGenerationFeature.INLINE_CHOICE_DEFINITIONS)) {
        retval = DEFINED_AS_INLINE;
      } else {
        retval = CHOICE_NOT_INLINE;
      }
    } else {
      retval = NONE_INLINE;
    }
    return retval;
  }

  /**
   * Determine if the provided definition should be inlined in the generated
   * schema.
   *
   * @param definition
   *          the definition to check
   * @param metaschemaIndex
   *          the module index containing definition usage information
   * @return {@code true} if the definition should be inlined, {@code false} if it
   *         should be referenced as a separate type
   */
  boolean isInline(
      @NonNull IDefinition definition,
      @NonNull ModuleIndex metaschemaIndex);
}
