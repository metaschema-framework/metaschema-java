/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import org.w3c.dom.Element;

import java.util.Collections;
import java.util.List;

import dev.metaschema.core.model.IAnyContent;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * XML-specific implementation of {@link IAnyContent} that stores captured
 * unmodeled content as W3C DOM {@link Element} instances.
 */
public class XmlAnyContent implements IAnyContent {
  @NonNull
  private final List<Element> elements;

  /**
   * Construct a new instance with the provided captured elements.
   *
   * @param elements
   *          the captured DOM elements, must not be null
   */
  public XmlAnyContent(@NonNull List<Element> elements) {
    this.elements = Collections.unmodifiableList(List.copyOf(elements));
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * Get the captured DOM elements.
   *
   * @return an unmodifiable list of captured elements
   */
  @NonNull
  public List<Element> getElements() {
    return elements;
  }
}
