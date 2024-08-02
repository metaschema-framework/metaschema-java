/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.xml;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.AbstractSerializer;
import gov.nist.secauto.metaschema.databind.io.SerializationFeature;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultXmlSerializer<CLASS extends IBoundObject>
    extends AbstractSerializer<CLASS> {
  private XMLOutputFactory2 xmlOutputFactory;

  /**
   * Construct a new XML serializer based on the top-level assembly indicated by
   * the provided {@code classBinding}.
   *
   * @param definition
   *          the bound Module assembly definition that describes the data to
   *          serialize
   */
  public DefaultXmlSerializer(@NonNull IBoundDefinitionModelAssembly definition) {
    super(definition);
  }

  /**
   * Get the configured XML output factory used to create {@link XMLStreamWriter2}
   * instances.
   *
   * @return the factory
   */
  @NonNull
  protected final XMLOutputFactory2 getXMLOutputFactory() {
    synchronized (this) {
      if (xmlOutputFactory == null) {
        xmlOutputFactory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
        assert xmlOutputFactory instanceof WstxOutputFactory;
        xmlOutputFactory.configureForSpeed();
        xmlOutputFactory.setProperty(WstxOutputProperties.P_USE_DOUBLE_QUOTES_IN_XML_DECL, true);
        xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
      }
      assert xmlOutputFactory != null;
      return xmlOutputFactory;
    }
  }

  /**
   * Override the default {@link XMLOutputFactory2} instance with a custom
   * factory.
   *
   * @param xmlOutputFactory
   *          the new factory
   */
  protected void setXMLOutputFactory(@NonNull XMLOutputFactory2 xmlOutputFactory) {
    synchronized (this) {
      this.xmlOutputFactory = xmlOutputFactory;
    }
  }

  /**
   * Create a new stream writer using the provided writer.
   *
   * @param writer
   *          the writer to use for output
   * @return the stream writer created by the output factory
   * @throws IOException
   *           if an error occurred while creating the writer
   */
  @NonNull
  protected final XMLStreamWriter2 newXMLStreamWriter(@NonNull Writer writer) throws IOException {
    try {
      return ObjectUtils.notNull((XMLStreamWriter2) getXMLOutputFactory().createXMLStreamWriter(writer));
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }

  @Override
  public void serialize(IBoundObject data, Writer writer) throws IOException {
    XMLStreamWriter2 streamWriter = newXMLStreamWriter(writer);
    IOException caughtException = null;
    IBoundDefinitionModelAssembly definition = getDefinition();

    MetaschemaXmlWriter xmlGenerator = new MetaschemaXmlWriter(streamWriter);

    boolean serializeRoot = get(SerializationFeature.SERIALIZE_ROOT);
    try {
      if (serializeRoot) {
        streamWriter.writeStartDocument("UTF-8", "1.0");
        xmlGenerator.writeRoot(definition, data);
      } else {
        xmlGenerator.write(definition, data);
      }

      streamWriter.flush();

      if (serializeRoot) {
        streamWriter.writeEndDocument();
      }
    } catch (XMLStreamException ex) {
      caughtException = new IOException(ex);
      throw caughtException;
    } finally { // NOPMD - exception handling is needed
      try {
        streamWriter.close();
      } catch (XMLStreamException ex) {
        if (caughtException == null) {
          throw new IOException(ex);
        }
        caughtException.addSuppressed(ex);
        throw caughtException; // NOPMD - intentional
      }
    }
  }
}
