
package gov.nist.secauto.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.databind.IBindingContext;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class DefaultBoundLoaderTest {

  @Test
  void testIssue187() throws IOException, MetaschemaException {

    IModule module = new ModuleLoader().load(Paths.get("src/test/resources/content/issue187-metaschema.xml"));

    IBindingContext bindingContext = IBindingContext.instance();

    bindingContext.registerModule(module, Files.createTempDirectory(Paths.get("target"), "modules-"));

    IBoundLoader loader = bindingContext.newBoundLoader();

    IDocumentNodeItem docItem = loader.loadAsNodeItem(Paths.get("src/test/resources/content/issue187-instance.xml"));

    MetapathExpression metapath = MetapathExpression.compile("//a//b", docItem.getStaticContext());

    ISequence<?> result = metapath.evaluate(docItem);

    assertEquals(8, result.size());
  }
}
