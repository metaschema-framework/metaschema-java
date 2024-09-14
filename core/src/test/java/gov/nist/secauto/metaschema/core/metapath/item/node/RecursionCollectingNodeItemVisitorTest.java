
package gov.nist.secauto.metaschema.core.metapath.item.node;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

class RecursionCollectingNodeItemVisitorTest {

  @Test
  void testAssemblyRecursion() throws MetaschemaException, IOException {
    IModule module = new ModuleLoader().load(ObjectUtils.notNull(
        Paths.get("metaschema/schema/metaschema/metaschema-module-metaschema.xml")));

    RecursionCollectingNodeItemVisitor walker = new RecursionCollectingNodeItemVisitor();
    walker.visit(module);
    Set<RecursionCollectingNodeItemVisitor.AssemblyRecord> recursiveAssemblies
        = walker.getRecursiveAssemblyDefinitions();

    System.out.println("Recursive Assemblies");
    System.out.println("--------------------");
    recursiveAssemblies.forEach(record -> {
      System.out.println(record.getDefinition().getFormalName());
      record.getLocations().forEach(location -> {
        assert location != null;
        System.out.println("- " + metapath(location));
      });
    });
  }

  private static String metapath(@NonNull IDefinitionNodeItem<?, ?> item) {
    return metapath(item.getMetapath());
  }

  private static String metapath(@NonNull String path) {
    return path.replaceAll("\\[1\\]", "");
  }

}
