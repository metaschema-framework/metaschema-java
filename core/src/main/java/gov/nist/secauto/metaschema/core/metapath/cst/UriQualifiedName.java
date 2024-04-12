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
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class UriQualifiedName implements IEQName {
  private static final Pattern URI_QUALIFIED_NAME = Pattern.compile("^Q\\{([^{}]*)\\}(.+)$");

  @NonNull
  private final URI namespace;
  @NonNull
  private final String localName;

  public UriQualifiedName(@NonNull String name) {
    Matcher matcher = URI_QUALIFIED_NAME.matcher(name);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          String.format("The name '%s' is not a valid BracedURILiteral of the form: Q{URI}local-name", name));
    }
    this.namespace = ObjectUtils.notNull(URI.create(matcher.group(1)));
    this.localName = IEQName.checkValidNCName(ObjectUtils.notNull(matcher.group(2)));
  }

  public UriQualifiedName(@NonNull URI namespace, @NonNull String localName) {
    this.namespace = namespace;
    this.localName = localName;
  }

  public URI getNamespace() {
    return namespace;
  }

  @Override
  public String getLocalName() {
    return localName;
  }

  @Override
  public boolean isLexical() {
    // never lexical
    return false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Q{");
    builder.append(namespace);
    builder.append("}");
    builder.append(localName);
    return builder.toString();
  }

  @Override
  public QName toQName(StaticContext staticContext, Supplier<URI> defaultNamespaceSupplier) {
    return new QName(getNamespace().toASCIIString(), getLocalName());
  }
}
