/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io.xml;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.util.XmlEventUtil;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.AbstractProblemHandler;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.events.Attribute;

/**
 * Handles problems identified in the parsed XML.
 * <p>
 * The default problem handler will report unknown attributes, and provide empty collections for
 * multi-valued model items and default values for flags and single valued fields.
 */
public class DefaultXmlProblemHandler
    extends AbstractProblemHandler
    implements IXmlProblemHandler {
  private static final Logger LOGGER = LogManager.getLogger(DefaultXmlProblemHandler.class);

  private static final IEnhancedQName XSI_SCHEMA_LOCATION
      = IEnhancedQName.of("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
  private static final Set<IEnhancedQName> IGNORED_QNAMES;

  static {
    IGNORED_QNAMES = new HashSet<>();
    IGNORED_QNAMES.add(XSI_SCHEMA_LOCATION);
  }

  @Override
  public boolean handleUnknownAttribute(
      IBoundDefinitionModelComplex parentDefinition,
      IBoundObject targetObject,
      Attribute attribute,
      IXmlParsingContext parsingContext) {
    IEnhancedQName qname = IEnhancedQName.of(ObjectUtils.requireNonNull(attribute.getName()));
    // check if warning is needed
    if (LOGGER.isWarnEnabled() && !IGNORED_QNAMES.contains(qname)) {
      LOGGER.atWarn().log("Skipping unrecognized attribute '{}'{}.",
          qname,
          XmlEventUtil.generateLocationMessage(
              ObjectUtils.notNull(attribute.getLocation()),
              parsingContext.getSource()));
    }
    // always ignore
    return true;
  }
}
