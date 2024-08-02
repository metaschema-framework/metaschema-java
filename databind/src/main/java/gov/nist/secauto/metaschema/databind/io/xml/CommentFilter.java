/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.xml;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;

public class CommentFilter implements EventFilter {

  @Override
  public boolean accept(XMLEvent event) {
    return event.getEventType() != XMLStreamConstants.COMMENT;
  }

}
