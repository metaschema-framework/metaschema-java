/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.databind.io.IWritingContext;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides the writing context for serializing Java objects to XML format.
 * <p>
 * This interface extends {@link IWritingContext} with an XML-specific writer
 * type and adds a method for writing root elements.
 *
 * @see XMLStreamWriter2
 */
public interface IXmlWritingContext extends IWritingContext<XMLStreamWriter2> {
  /**
   * Write the root element for the provided definition and bound object.
   *
   * @param definition
   *          the assembly definition describing the root element
   * @param item
   *          the bound object to serialize as the root element
   * @throws IOException
   *           if an error occurs during writing
   */
  void writeRoot(
      @NonNull IBoundDefinitionModelAssembly definition,
      @NonNull IBoundObject item) throws IOException;
}
