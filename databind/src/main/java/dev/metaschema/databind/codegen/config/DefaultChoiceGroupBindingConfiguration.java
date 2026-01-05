/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import dev.metaschema.databind.config.binding.MetaschemaBindings;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Default implementation of {@link IChoiceGroupBindingConfiguration}.
 *
 * <p>
 * This implementation wraps a
 * {@link MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding}
 * instance from the binding configuration and provides the configuration
 * interface methods.
 *
 * <p>
 * The class stores:
 * <ul>
 * <li>The required group-as name (guaranteed non-null from the schema)</li>
 * <li>The optional fully-qualified Java type name for collection items</li>
 * <li>A flag indicating whether to use wildcard bounded types (defaults to
 * {@code true})</li>
 * </ul>
 */
public class DefaultChoiceGroupBindingConfiguration implements IChoiceGroupBindingConfiguration {
  @NonNull
  private final String groupAsName;
  @Nullable
  private final String itemTypeName;
  private final boolean useWildcard;

  /**
   * Constructs a new choice group binding configuration from a binding
   * configuration object.
   *
   * @param binding
   *          the binding configuration object from the parsed binding
   *          configuration file
   */
  public DefaultChoiceGroupBindingConfiguration(
      @NonNull MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding binding) {
    this.groupAsName = binding.getName();

    MetaschemaBindings.MetaschemaBinding.DefineAssemblyBinding.ChoiceGroupBinding.ItemType itemType
        = binding.getItemType();
    if (itemType != null) {
      this.itemTypeName = itemType.getValue();
      // Default to true if not explicitly set
      Boolean useWildcardFlag = itemType.getUseWildcard();
      this.useWildcard = useWildcardFlag == null || useWildcardFlag;
    } else {
      this.itemTypeName = null;
      this.useWildcard = true; // default value
    }
  }

  @Override
  @NonNull
  public String getGroupAsName() {
    return groupAsName;
  }

  @Override
  @Nullable
  public String getItemTypeName() {
    return itemTypeName;
  }

  @Override
  public boolean isUseWildcard() {
    return useWildcard;
  }
}
