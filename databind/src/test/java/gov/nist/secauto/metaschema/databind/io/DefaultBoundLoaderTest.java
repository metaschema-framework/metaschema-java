/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class DefaultBoundLoaderTest {

  @Test
  void testIssue187() throws IOException, MetaschemaException {

    IBindingContext bindingContext = IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(Paths.get("target"), "modules-")))
        .build();

    IBindingMetaschemaModule module = bindingContext.loadMetaschema(ObjectUtils.notNull(
        Paths.get("src/test/resources/content/issue187-metaschema.xml")));

    IBoundLoader loader = new DefaultBoundLoader(bindingContext);

    IDocumentNodeItem docItem = loader.loadAsNodeItem(ObjectUtils.notNull(
        Paths.get("src/test/resources/content/issue187-instance.xml")));

    MetapathExpression metapath = MetapathExpression.compile("//a//b", docItem.getStaticContext());

    ISequence<?> result = metapath.evaluate(docItem);

    assertEquals(8, result.size());
  }
}
