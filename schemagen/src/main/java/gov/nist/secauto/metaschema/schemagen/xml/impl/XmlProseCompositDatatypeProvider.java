/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import org.codehaus.stax2.XMLStreamWriter2;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlProseCompositDatatypeProvider
    extends CompositeDatatypeProvider {

  private final XmlProseBaseDatatypeProvider proseBaseProvider = new XmlProseBaseDatatypeProvider();

  public XmlProseCompositDatatypeProvider(@NonNull List<IDatatypeProvider> proxiedProviders) {
    super(proxiedProviders);
  }

  @Override
  public @NonNull Set<String> generateDatatypes(Set<String> requiredTypes,
      @NonNull XMLStreamWriter2 writer) throws XMLStreamException {
    Set<String> result = super.generateDatatypes(requiredTypes, writer);

    if (!result.isEmpty()) {
      // apply core markup types
      Collection<IDatatypeContent> datatypes = proseBaseProvider.getDatatypes().values();
      Set<String> proseBaseTypes = datatypes.stream().map(IDatatypeContent::getTypeName).collect(Collectors.toSet());
      proseBaseProvider.generateDatatypes(proseBaseTypes, writer);
    }
    return result;
  }

}
