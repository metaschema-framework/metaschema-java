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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class LexicalQName implements IEQName {
  private static final Pattern PREFIXED_NAME = Pattern.compile("^([^:]+):(.+)$");

  @NonNull
  private final String prefix;
  @NonNull
  private final String localName;

  public LexicalQName(@NonNull String name) {
    Matcher matcher = PREFIXED_NAME.matcher(name);
    if (matcher.matches()) {
      this.prefix = IEQName.checkValidNCName(ObjectUtils.notNull(matcher.group(1)));
      this.localName = IEQName.checkValidNCName(ObjectUtils.notNull(matcher.group(2)));
    } else {
      this.prefix = XMLConstants.DEFAULT_NS_PREFIX;
      this.localName = IEQName.checkValidNCName(ObjectUtils.notNull(name));
    }
  }

  public LexicalQName(@NonNull String prefix, @NonNull String localName) {
    this.prefix = IEQName.checkValidNCName(prefix);
    this.localName = IEQName.checkValidNCName(localName);
  }

  @Nullable
  public URI getNamespace(StaticContext context, @Nullable Supplier<URI> defaultNamespaceSupplier) {
    URI retval = context.lookupNamespaceURIForPrefix(getPrefix());
    if (retval == null && defaultNamespaceSupplier != null) {
      retval = defaultNamespaceSupplier.get();
    }
    return retval;
  }

  @NonNull
  public String getPrefix() {
    return prefix;
  }

  @Override
  public String getLocalName() {
    return localName;
  }

  @Override
  public boolean isLexical() {
    // always lexical
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (!prefix.isEmpty()) {
      builder.append(prefix);
      builder.append(":");
    }
    builder.append(localName);
    return builder.toString();
  }

  @Override
  public QName toQName(StaticContext staticContext, Supplier<URI> defaultNamespaceSupplier) {
    URI namespace = getNamespace(staticContext, defaultNamespaceSupplier);
    String ns = namespace == null ? null : namespace.toASCIIString();
    return new QName(ns, getLocalName(), getPrefix());
  }
}
