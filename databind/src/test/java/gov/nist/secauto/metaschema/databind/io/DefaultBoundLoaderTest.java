/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.codegen.AbstractMetaschemaTest;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class DefaultBoundLoaderTest
    extends AbstractMetaschemaTest {

  @Test
  void testIssue187() throws IOException, MetaschemaException {

    IBindingContext bindingContext = newBindingContext();

    bindingContext.loadMetaschema(ObjectUtils.notNull(
        Paths.get("src/test/resources/content/issue187-metaschema.xml")));

    IBoundLoader loader = new DefaultBoundLoader(bindingContext);

    IDocumentNodeItem docItem = loader.loadAsNodeItem(ObjectUtils.notNull(
        Paths.get("src/test/resources/content/issue187-instance.xml")));

    assertEquals(
        8,
        IMetapathExpression.compile("//a//b", docItem.getStaticContext())
            .evaluate(docItem)
            .size());
  }
}
