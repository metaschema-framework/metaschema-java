/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.model.constraint.ILet;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.ConstraintLetType;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.RemarksType;

import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Test;

import java.net.URI;

import javax.xml.namespace.QName;

class ModelFactoryTest {

  @SuppressWarnings("null")
  @Test
  void letTest() throws XmlException {
    ISource source = ISource.externalSource(StaticContext.builder()
        .baseUri(URI.create("https://example.com/"))
        .build());

    String variable = "var1";
    String expression = "1 + 1";
    RemarksType remarks = RemarksType.Factory.parse("<p>Test</p>");

    ConstraintLetType letObj = ConstraintLetType.Factory.newInstance();
    letObj.setExpression(expression);
    letObj.setVar(variable);
    letObj.setRemarks(remarks);

    ILet let = ModelFactory.newLet(letObj, source);
    assertAll(
        () -> assertEquals(new QName(variable), let.getName()),
        () -> assertEquals(expression, let.getValueExpression().getPath()),
        () -> assertEquals(source, let.getSource()),
        () -> assertEquals("Test", let.getRemarks().toMarkdown()));
  }
}
