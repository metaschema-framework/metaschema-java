/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.XmlBookmark;
import org.apache.xmlbeans.XmlLineNumber;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports parsing Metaschema assembly and field XMLBeans objects that contain
 * other Metaschema objects.
 *
 * @param <T>
 *          the Java type of the state that is passed to the element parsing
 *          handlers
 */
public class XmlObjectParser<T> {
  private static final XmlOptions XML_OPTIONS = new XmlOptions().setXPathUseSaxon(false).setXPathUseXmlBeans(true);
  private final Map<IEnhancedQName, Handler<T>> elementNameToHandlerMap;
  private final String xpath;

  private static String generatePath(@NonNull Collection<IEnhancedQName> nodes) {
    // build a mapping of namespace prefix to namespace
    AtomicInteger count = new AtomicInteger();
    Map<String, String> namespaceToPrefixMap = nodes.stream()
        .map(IEnhancedQName::getNamespace)
        .distinct()
        .map(ns -> Pair.of(ns, "m" + count.getAndIncrement()))
        .collect(Collectors.toMap(
            Pair::getKey,
            Pair::getValue,
            (k1, k2) -> k1,
            LinkedHashMap::new));

    // generate namespace declarations using prefix and namespace
    StringBuilder builder = new StringBuilder(24);
    namespaceToPrefixMap.entrySet().forEach(entry -> {
      builder.append("declare namespace ")
          .append(entry.getValue())
          .append("='")
          .append(entry.getKey())
          .append("';");
    });

    // generate child path
    builder.append(nodes.stream()
        .map(qname -> new StringBuilder()
            .append("$this/")
            .append(namespaceToPrefixMap.get(qname.getNamespace()))
            .append(':')
            .append(qname.getLocalName())
            .toString())
        .collect(Collectors.joining("|")));

    return builder.toString();
  }

  /**
   * Construct a new XmlObject parser.
   *
   * @param elementNameToHandlerMap
   *          the mapping of element names to associated handlers
   */
  public XmlObjectParser(@NonNull Map<IEnhancedQName, Handler<T>> elementNameToHandlerMap) {
    this.elementNameToHandlerMap = elementNameToHandlerMap;
    this.xpath = generatePath(ObjectUtils.notNull(elementNameToHandlerMap.keySet()));
  }

  private Map<IEnhancedQName, Handler<T>> getElementNameToHandlerMap() {
    return elementNameToHandlerMap;
  }

  private String getXpath() {
    return xpath;
  }

  /**
   * Get the resource location of the provided object.
   *
   * @param obj
   *          the XMLBeans object to get the location for
   * @return the resource location or an empty string if the location is not known
   */
  @SuppressWarnings({ "resource" })
  @NonNull
  public static String toLocation(@NonNull XmlObject obj) {
    return toLocation(ObjectUtils.notNull(obj.newCursor()));
  }

  /**
   * Get the resource location of the provided cursor.
   *
   * @param cursor
   *          the XMLBeans cursor to get the location for
   * @return the resource location or {@code null} if the location is not known
   */
  @NonNull
  public static String toLocation(@NonNull XmlCursor cursor) {
    String retval = "";
    XmlBookmark bookmark = cursor.getBookmark(XmlLineNumber.class);
    if (bookmark != null) {
      StringBuilder locationBuilder = new StringBuilder();
      XmlLineNumber lineNumber = (XmlLineNumber) bookmark;

      String source = cursor.documentProperties().getSourceName();
      if (source != null) {
        locationBuilder.append(source)
            .append(':');
      }

      locationBuilder.append(lineNumber.getLine())
          .append(':')
          .append(lineNumber.getColumn());

      retval = ObjectUtils.notNull(locationBuilder.toString());
    }
    return retval;
  }

  /**
   * Used to determine which parser {@link Handler} implementation to use to parse
   * the object.
   * <p>
   * Subclasses can override this method to implement a more efficient or advanced
   * detection method.
   *
   * @param cursor
   *          the current XmlCursor location
   * @param obj
   *          the strongly typed XmlObject at the current location
   * @return the identified handler
   * @throws IllegalStateException
   *           if a suitable handler cannot be identified
   */
  @NonNull
  protected Handler<T> identifyHandler(@NonNull XmlCursor cursor, @NonNull XmlObject obj) {
    IEnhancedQName qname = IEnhancedQName.of(ObjectUtils.notNull(cursor.getName()));
    Handler<T> retval = getElementNameToHandlerMap().get(qname);
    if (retval == null) {
      String location = toLocation(cursor);
      if (!location.isEmpty()) {
        location = new StringBuilder()
            .append(" at location '")
            .append(location)
            .append('\'')
            .toString();
      }
      throw new IllegalStateException(String.format("Unhandled node '%s'%s.", qname, location));
    }
    return retval;
  }

  /**
   * Parse an XmlObject element tree using the configured child element handlers.
   *
   * @param source
   *          information about the parsed resource
   * @param xmlObject
   *          the XmlObject container to parse
   * @param state
   *          parsing state to pass to the handlers
   * @return the state
   */
  public T parse(@NonNull ISource source, @NonNull XmlObject xmlObject, T state) {
    try (XmlCursor cursor = xmlObject.newCursor()) {
      assert cursor != null;
      cursor.selectPath(getXpath(), XML_OPTIONS);
      while (cursor.toNextSelection()) {
        XmlObject obj = cursor.getObject();
        assert obj != null;
        Handler<T> handler = identifyHandler(cursor, obj);
        handler.handle(source, obj, state);
      }
    }
    return state;
  }

  /**
   * Provides a common interface for element parsing handlers.
   *
   * @param <T>
   *          the Java type of the state that is passed to the element parsing
   *          handlers
   */
  @FunctionalInterface
  public interface Handler<T> {
    /**
     * Parse the provided {@code obj} using the provided {@code state}.
     *
     * @param source
     *          information about the parsed resource
     * @param xmlObject
     *          the object to parse
     * @param state
     *          the state to use for parsing
     */
    void handle(@NonNull ISource source, @NonNull XmlObject xmlObject, T state);
  }
}
