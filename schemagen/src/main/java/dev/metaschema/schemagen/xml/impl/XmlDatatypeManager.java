/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.datatype.AbstractDatatypeManager;

import org.codehaus.stax2.XMLStreamWriter2;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Support for managing Module data type implementations aligned with the XML
 * schema format for use in schema generation.
 */
public class XmlDatatypeManager
    extends AbstractDatatypeManager {
  /** The namespace prefix for XML Schema elements. */
  @NonNull
  public static final String PREFIX_XML_SCHEMA = "xs";
  /** The XML Schema namespace URI. */
  @NonNull
  public static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  /** The XHTML namespace URI used for documentation content. */
  @NonNull
  public static final String NS_XHTML = "http://www.w3.org/1999/xhtml";

  @NonNull
  private static final Lazy<List<IDatatypeProvider>> DATATYPE_PROVIDERS = ObjectUtils.notNull(Lazy.of(() -> List.of(
      new XmlCoreDatatypeProvider(),
      new XmlProseCompositDatatypeProvider(
          ObjectUtils.notNull(List.of(
              new XmlMarkupMultilineDatatypeProvider(),
              new XmlMarkupLineDatatypeProvider()))))));

  /**
   * Generates XML Schema datatype definitions for all used types.
   * <p>
   * Iterates through registered datatype providers to generate definitions for
   * all required types. Throws an exception if any required types are not
   * provided.
   *
   * @param writer
   *          the XML stream writer to write datatype definitions to
   * @throws XMLStreamException
   *           if an error occurs while writing XML content
   * @throws IllegalStateException
   *           if any required datatypes are not provided by the registered
   *           providers
   */
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
