/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class DefaultDefinitionBindingConfiguration implements IMutableDefinitionBindingConfiguration {
  @Nullable
  private String className;
  @Nullable
  private String baseClassName;
  @NonNull
  private final List<String> interfacesToImplement = new LinkedList<>();

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
}
