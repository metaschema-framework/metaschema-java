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

public interface IGenerationState<WRITER> {
  @NonNull
  IModule getModule();

  @NonNull
  WRITER getWriter();

  @NonNull
  default Collection<? extends IAssemblyDefinition> getRootDefinitions() {
    return getModule().getExportedRootAssemblyDefinitions();
  }

  boolean isInline(@NonNull IDefinition definition);

  void flushWriter() throws IOException;

  @NonNull
  String getTypeNameForDefinition(@NonNull IDefinition definition, @Nullable String suffix);

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
