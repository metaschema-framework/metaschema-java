/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import org.codehaus.stax2.XMLStreamWriter2;
import org.eclipse.jdt.annotation.Owning;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides a common base implementation for XML schema datatype providers.
 * <p>
 * This class loads datatype definitions from an XML schema resource and
 * provides them for use during schema generation. The schema is lazily loaded
 * on first access.
 */
public abstract class AbstractXmlDatatypeProvider implements IDatatypeProvider {
  private Map<String, IDatatypeContent> datatypes;

  /**
   * Get the input stream for the schema resource containing datatype definitions.
   * <p>
   * The caller owns the returned stream and is responsible for closing it.
   *
   * @return the schema resource input stream
   */
  @Owning
  @NonNull
  protected abstract InputStream getSchemaResource();

  private void initSchema() {
    synchronized (this) {
      if (datatypes == null) {
        try (InputStream is = getSchemaResource()) {
          assert is != null;
          JDom2XmlSchemaLoader loader = new JDom2XmlSchemaLoader(is);

          List<Element> elements = queryElements(loader);

          datatypes = Collections.unmodifiableMap(handleResults(elements));
        } catch (JDOMException | IOException ex) {
          throw new IllegalStateException(ex);
        }
      }
    }
  }

  /**
   * Query the schema loader for elements to be processed as datatype definitions.
   *
   * @param loader
   *          the schema loader to query
   * @return the list of elements representing datatype definitions
   */
  @NonNull
  protected abstract List<Element> queryElements(JDom2XmlSchemaLoader loader);

  /**
   * Process the queried elements and create datatype content mappings.
   *
   * @param items
   *          the elements to process
   * @return a map of datatype names to their content definitions
   */
  @NonNull
  protected abstract Map<String, IDatatypeContent> handleResults(@NonNull List<Element> items);

  @Override
  @SuppressFBWarnings({ "IS2_INCONSISTENT_SYNC", "MT_CORRECTNESS", "EI_EXPOSE_REP" })
  public Map<String, IDatatypeContent> getDatatypes() {
    initSchema();
    assert datatypes != null;
    return datatypes;
  }

  @Override
  public Set<String> generateDatatypes(Set<String> requiredTypes, @NonNull XMLStreamWriter2 writer)
      throws XMLStreamException {
    Map<String, IDatatypeContent> datatypes = getDatatypes();

    Set<String> providedDatatypes = new LinkedHashSet<>();
    for (IDatatypeContent datatype : datatypes.values()) {
      String type = datatype.getTypeName();
      if (requiredTypes.contains(type)) {
        providedDatatypes.add(type);
        providedDatatypes.addAll(datatype.getDependencies());
      }
    }

    for (IDatatypeContent datatype : datatypes.values()) {
      String type = datatype.getTypeName();
      if (providedDatatypes.contains(type)) {
        datatype.write(writer);
      }
    }
    return providedDatatypes;
  }

}
