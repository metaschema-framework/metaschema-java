/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.ConstraintLetType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.RemarksType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.net.URI;

class ModelFactoryTest {

  @Test
  void letTest() throws XmlException {
    ISource source = ISource.externalSource(ObjectUtils.notNull(URI.create("https://example.com/")));

    String variable = "var1";
    String expression = "1 + 1";
    RemarksType remarks = RemarksType.Factory.parse("<p>Test</p>");

    ConstraintLetType letObj = ConstraintLetType.Factory.newInstance();
    letObj.setExpression(expression);
    letObj.setVar(variable);
    letObj.setRemarks(remarks);

    ILet let = ModelFactory.newLet(letObj, source);
    MarkupMultiline letRemarks = let.getRemarks();
    assertAll(
        () -> assertEquals(IEnhancedQName.of(variable), let.getName()),
        () -> assertEquals(expression, let.getValueExpression().getPath()),
        () -> assertEquals(source, let.getSource()),
        () -> assertEquals("Test", letRemarks == null ? null : letRemarks.toMarkdown()));
  }
}
