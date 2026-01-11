/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Default implementation of {@link IMutableDefinitionBindingConfiguration} that
 * provides mutable binding configuration for a specific Metaschema definition.
 */
public class DefaultDefinitionBindingConfiguration implements IMutableDefinitionBindingConfiguration {
  @Nullable
  private String className;
  @Nullable
  private String baseClassName;
  @NonNull
  private final List<String> interfacesToImplement = new LinkedList<>();
  @NonNull
  private final Map<String, IChoiceGroupBindingConfiguration> choiceGroupBindings = new LinkedHashMap<>();

  /**
   * Create a new definition binding configuration.
   */
  public DefaultDefinitionBindingConfiguration() {
    // empty configuration
  }

  /**
   * Create a new definition binding configuration based on a previous
   * configuration.
   *
   * @param config
   *          the previous configuration
   */
  public DefaultDefinitionBindingConfiguration(@NonNull IDefinitionBindingConfiguration config) {
    this.className = config.getClassName();
    this.baseClassName = config.getQualifiedBaseClassName();
    this.interfacesToImplement.addAll(config.getInterfacesToImplement());
    this.choiceGroupBindings.putAll(config.getChoiceGroupBindings());
  }

  @Override
  public String getClassName() {
    return className;
  }

  @Override
  public void setClassName(String name) {
    this.className = name;
  }

  @Override
  public String getQualifiedBaseClassName() {
    return baseClassName;
  }

  @Override
  public void setQualifiedBaseClassName(String name) {
    this.baseClassName = name;
  }

  @Override
  public List<String> getInterfacesToImplement() {
    return interfacesToImplement;
  }

  @Override
  public void addInterfaceToImplement(String interfaceName) {
    this.interfacesToImplement.add(interfaceName);
  }

  @Override
  public Map<String, IChoiceGroupBindingConfiguration> getChoiceGroupBindings() {
    return CollectionUtil.unmodifiableMap(choiceGroupBindings);
  }

  /**
   * Add a choice group binding configuration.
   *
   * @param groupAsName
   *          the group-as name from the Metaschema module
   * @param config
   *          the choice group binding configuration
   */
  public void addChoiceGroupBinding(@NonNull String groupAsName, @NonNull IChoiceGroupBindingConfiguration config) {
    this.choiceGroupBindings.put(groupAsName, config);
  }
}
