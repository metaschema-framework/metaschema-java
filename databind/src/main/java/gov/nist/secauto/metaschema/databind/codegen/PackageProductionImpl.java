/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.databind.codegen.typeinfo.IMetaschemaClassFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

class PackageProductionImpl implements IPackageProduction {
  @NonNull
  private final URI xmlNamespace;
  @NonNull
  private final IGeneratedClass packageInfoClass;

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public PackageProductionImpl(
      @NonNull PackageMetadata metadata,
      @NonNull IMetaschemaClassFactory classFactory,
      @NonNull Path targetDirectory)
      throws IOException {
    this.xmlNamespace = metadata.getXmlNamespace();
    this.packageInfoClass = classFactory.generatePackageInfoClass(
        metadata.getPackageName(),
        this.xmlNamespace,
        metadata.getModuleProductions(),
        targetDirectory);
  }
  //
  // public static String formatMarkdown(@NonNull IFlexmarkMarkupString<?> markup)
  // {
  // // TODO: seemingly unused method. Remove?
  // Formatter.Builder builder =
  // Formatter.builder(FlexmarkConfiguration.FLEXMARK_CONFIG);
  // builder.set(ObjectUtils.notNull(Formatter.FORMAT_FLAGS),
  // LineAppendable.F_WHITESPACE_REMOVAL);
  // // builder.set(Formatter.ESCAPE_SPECIAL_CHARS, false);
  // Formatter formatter = builder.build();
  // String markdown = markup.toMarkdown(formatter).trim();
  // markdown = markdown.replace("\\&", "&");
  // markdown = markdown.replace("\"", "\\\"");
  // return markdown;
  // }

  @Override
  public URI getXmlNamespace() {
    return xmlNamespace;
  }

  /**
   * Get the generated package-info class associated with this package.
   *
   * @return the package-info class
   */
  @Override
  public IGeneratedClass getGeneratedClass() {
    return packageInfoClass;
  }
}
