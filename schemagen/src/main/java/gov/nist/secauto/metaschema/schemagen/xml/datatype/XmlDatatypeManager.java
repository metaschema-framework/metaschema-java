/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.datatype;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.datatype.AbstractDatatypeManager;
import gov.nist.secauto.metaschema.schemagen.datatype.IDatatypeProvider;

import org.codehaus.stax2.XMLStreamWriter2;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public class XmlDatatypeManager
    extends AbstractDatatypeManager {
  public static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  @NonNull
  private static final Lazy<List<IDatatypeProvider>> DATATYPE_PROVIDERS = ObjectUtils.notNull(Lazy.lazy(() -> List.of(
      new XmlCoreDatatypeProvider(),
      new XmlProseCompositDatatypeProvider(
          ObjectUtils.notNull(List.of(
              new XmlMarkupMultilineDatatypeProvider(),
              new XmlMarkupLineDatatypeProvider()))))));

  public void generateDatatypes(@NonNull XMLStreamWriter2 writer) throws XMLStreamException {
    // resolve dependencies
    Set<String> used = getUsedTypes();

    Set<String> requiredTypes = getDatatypeTranslationMap().values().stream()
        .filter(used::contains)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    for (IDatatypeProvider provider : DATATYPE_PROVIDERS.get()) {
      Set<String> providedDatatypes = provider.generateDatatypes(requiredTypes, writer);
      requiredTypes.removeAll(providedDatatypes);
    }

    if (!requiredTypes.isEmpty()) {
      throw new IllegalStateException(
          String.format("The following datatypes were not provided: %s",
              requiredTypes.stream().collect(Collectors.joining(","))));
    }
  }
}
