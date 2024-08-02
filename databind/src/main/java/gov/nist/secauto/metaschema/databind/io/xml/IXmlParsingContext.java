/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.xml;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.IParsingContext;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;

import org.codehaus.stax2.XMLEventReader2;

import java.io.IOException;

import javax.xml.stream.XMLStreamConstants;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IXmlParsingContext extends IParsingContext<XMLEventReader2, IXmlProblemHandler> {

  /**
   * Parses XML into a bound object based on the provided {@code definition}.
   * <p>
   * Parses the {@link XMLStreamConstants#START_DOCUMENT}, any processing
   * instructions, and the element.
   *
   * @param <CLASS>
   *          the returned object type
   * @param definition
   *          the definition describing the element data to read
   * @return the parsed object
   * @throws IOException
   *           if an error occurred while parsing the input
   */
  <CLASS> CLASS read(@NonNull IBoundDefinitionModelComplex definition) throws IOException;

  /**
   * Read the data associated with the {@code instance} and apply it to the
   * provided {@code parentObject}.
   *
   * @param <T>
   *          the item Java type
   * @param instance
   *          the instance to parse data for
   * @param parentObject
   *          the Java object that data parsed by this method will be stored in
   * @param parseGrouping
   *          if {@code true} parse the instance's grouping element or
   *          {@code false} otherwise
   * @return {@code true} if the instance was parsed, or {@code false} if the data
   *         did not contain information for this instance
   * @throws IOException
   *           if an error occurred while parsing the input
   *
   */
  <T> boolean readItems(
      @NonNull IBoundInstanceModel<T> instance,
      @NonNull IBoundObject parentObject,
      boolean parseGrouping) throws IOException;
}
