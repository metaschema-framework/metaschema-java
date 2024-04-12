/*
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;

import java.net.URI;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an
 * <a href="https://www.w3.org/TR/xpath-31/#dt-expanded-qname">extended
 * qualified name</a>.
 */
public interface IEQName {
  Pattern NCNAME = Pattern.compile(String.format("^(\\p{L}|_)(\\p{L}|\\p{N}|[.\\-_])*$"));

  static boolean isValidNCName(@NonNull String name) {
    Matcher matcher = NCNAME.matcher(name);
    return matcher.matches();
  }

  @NonNull
  static String checkValidNCName(@NonNull String name) {
    if (!isValidNCName(name)) {
      throw new IllegalArgumentException(String.format("The name '%s' is not a valid NCName.", name));
    }
    return name;
  }

  @NonNull
  static IEQName of(@NonNull String prefix, @NonNull String localName) {
    return new LexicalQName(prefix, localName);
  }

  @NonNull
  static IEQName of(@NonNull URI namespace, @NonNull String localName) {
    return new UriQualifiedName(namespace, localName);
  }

  @NonNull
  static IEQName of(@NonNull String name) {
    return name.startsWith("Q{")
        ? new UriQualifiedName(name)
        : new LexicalQName(name);
  }

  @NonNull
  String getLocalName();

  boolean isLexical();

  @NonNull
  QName toQName(@NonNull StaticContext staticContext, @Nullable Supplier<URI> defaultNamespaceSupplier);
}
