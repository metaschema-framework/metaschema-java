/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import edu.umd.cs.findbugs.annotations.NonNull;

interface IMutableDefinitionBindingConfiguration extends IDefinitionBindingConfiguration {
  void setClassName(@NonNull String name);

  void setQualifiedBaseClassName(@NonNull String name);

  void addInterfaceToImplement(@NonNull String name);
}
