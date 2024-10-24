/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import com.ctc.wstx.stax.WstxInputFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.MergedStream;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.util.JsonUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.json.JsonFactoryFactory;
import gov.nist.secauto.metaschema.databind.io.yaml.impl.YamlFactoryFactory;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.eclipse.jdt.annotation.NotOwning;
import org.eclipse.jdt.annotation.Owning;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides a means to analyze content to determine what type of bound data it
 * contains.
 */
public class ModelDetector {
  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final IConfiguration<DeserializationFeature<?>> configuration;

  /**
   * Construct a new format detector using the default configuration.
   *
   * @param bindingContext
   *          information about how Java classes are bound to Module definitions
   */
  public ModelDetector(
      @NonNull IBindingContext bindingContext) {
    this(bindingContext, new DefaultConfiguration<>());
  }

  /**
   * Construct a new format detector using the provided {@code configuration}.
   *
   * @param bindingContext
   *          information about how Java classes are bound to Module definitions
   * @param configuration
   *          the deserialization configuration
   */
  public ModelDetector(
      @NonNull IBindingContext bindingContext,
      @NonNull IConfiguration<DeserializationFeature<?>> configuration) {
    this.bindingContext = bindingContext;
    this.configuration = configuration;
  }

  private int getLookaheadLimit() {
    return configuration.get(DeserializationFeature.FORMAT_DETECTION_LOOKAHEAD_LIMIT);
  }

  @NonNull
  private IBindingContext getBindingContext() {
    return bindingContext;
  }

  @NonNull
  private IConfiguration<DeserializationFeature<?>> getConfiguration() {
    return configuration;
  }

  /**
   * Analyzes the data from the provided {@code inputStream} to determine it's
   * model.
   *
   * @param inputStream
   *          the resource stream to analyze
   * @param format
   *          the expected format of the data to read
   * @return the analysis result
   * @throws IOException
   *           if an error occurred while reading the resource
   */
  @NonNull
  @Owning
  public Result detect(@NonNull @NotOwning InputStream inputStream, @NonNull Format format)
      throws IOException {
    byte[] buf = ObjectUtils.notNull(inputStream.readNBytes(getLookaheadLimit()));

    Class<? extends IBoundObject> clazz;
    try (InputStream bis = new ByteArrayInputStream(buf)) {
      assert bis != null;
      switch (format) {
      case JSON:
        try (JsonParser parser = JsonFactoryFactory.instance().createParser(bis)) {
          assert parser != null;
          clazz = detectModelJsonClass(parser);
        }
        break;
      case YAML:
        YAMLFactory factory = YamlFactoryFactory.newParserFactoryInstance(getConfiguration());
        try (JsonParser parser = factory.createParser(bis)) {
          assert parser != null;
          clazz = detectModelJsonClass(parser);
        }
        break;
      case XML:
        clazz = detectModelXmlClass(bis);
        break;
      default:
        throw new UnsupportedOperationException(
            String.format("The format '%s' dataStream not supported", format));
      }
    }

    if (clazz == null) {
      throw new IllegalStateException(
          String.format("Detected format '%s', but unable to detect the bound data type", format.name()));
    }

    return new Result(clazz, inputStream, buf);
  }

  @NonNull
  private Class<? extends IBoundObject> detectModelXmlClass(@NonNull InputStream is) throws IOException {
    QName startElementQName;
    try {
      XMLInputFactory2 xmlInputFactory = (XMLInputFactory2) XMLInputFactory.newInstance();
      assert xmlInputFactory instanceof WstxInputFactory;
      xmlInputFactory.configureForXmlConformance();
      xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, false);

      Reader reader = new InputStreamReader(is, Charset.forName("UTF8"));
      XMLEventReader2 eventReader = (XMLEventReader2) xmlInputFactory.createXMLEventReader(reader);
      while (eventReader.hasNext() && !eventReader.peek().isStartElement()) {
        eventReader.nextEvent();
      }

      if (!eventReader.peek().isStartElement()) {
        throw new IOException("Unable to detect a start element");
      }

      StartElement start = eventReader.nextEvent().asStartElement();
      startElementQName = ObjectUtils.notNull(start.getName());
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }

    Class<? extends IBoundObject> clazz = getBindingContext().getBoundClassForRootXmlQName(startElementQName);
    if (clazz == null) {
      throw new IOException("Unrecognized element name: " + startElementQName.toString());
    }
    return clazz;
  }

  @Nullable
  private Class<? extends IBoundObject> detectModelJsonClass(@NonNull JsonParser parser) throws IOException {
    Class<? extends IBoundObject> retval = null;
    JsonUtil.advanceAndAssert(parser, JsonToken.START_OBJECT);
    outer: while (JsonToken.FIELD_NAME.equals(parser.nextToken())) {
      String name = ObjectUtils.notNull(parser.currentName());
      if (!"$schema".equals(name)) {
        IBindingContext bindingContext = getBindingContext();
        retval = bindingContext.getBoundClassForRootJsonName(name);
        if (retval == null) {
          throw new IOException("Unrecognized JSON field name: " + name);
        }
        break outer;
      }
      // do nothing
      parser.nextToken();
      // JsonUtil.skipNextValue(parser);
    }
    return retval;
  }

  public static final class Result implements Closeable {
    @NonNull
    private final Class<? extends IBoundObject> boundClass;
    @Owning
    private InputStream dataStream;

    private Result(
        @NonNull Class<? extends IBoundObject> clazz,
        @NonNull InputStream is,
        @NonNull byte[] buf) {
      this.boundClass = clazz;
      this.dataStream = new MergedStream(null, is, buf, 0, buf.length);
    }

    /**
     * Get the Java class representing the detected bound object.
     *
     * @return the Java class
     */
    @NonNull
    public Class<? extends IBoundObject> getBoundClass() {
      return boundClass;
    }

    /**
     * Get an {@link InputStream} that can be used to read the analyzed data from
     * the start.
     *
     * @return the stream
     */
    @NonNull
    @Owning
    public InputStream getDataStream() {
      return ObjectUtils.requireNonNull(dataStream, "data stream already closed");
    }

    @SuppressWarnings("PMD.NullAssignment")
    @Override
    public void close() throws IOException {
      this.dataStream.close();
      this.dataStream = null;
    }
  }
}
