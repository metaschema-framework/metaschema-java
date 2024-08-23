
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class AbstractRecursionPreventingNodeItemVisitorTest {

  @Test
  void testRecursion() throws MetaschemaException, IOException {
    AbstractRecursionPreventingNodeItemVisitor<Void, Void> visitor
        = new AbstractRecursionPreventingNodeItemVisitor<>() {
          @Override
          protected Void defaultResult() {
            return null;
          }
        };

    IModule module = new ModuleLoader().load(ObjectUtils.notNull(
        Paths.get("metaschema/schema/metaschema/metaschema-module-metaschema.xml")));
    visitor.visitMetaschema(INodeItemFactory.instance().newModuleNodeItem(module), null);
  }

}
