/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

/**
 * An XML event filter that excludes comment events from the event stream.
 * <p>
 * This filter is used during XML parsing to skip over comment nodes, allowing
 * the parser to focus only on meaningful content.
 */
public class CommentFilter implements EventFilter {

  @Override
  public boolean accept(XMLEvent event) {
    return event.getEventType() != XMLStreamConstants.COMMENT;
  }

}
