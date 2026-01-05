
package dev.metaschema.docsgen;

import dev.metaschema.docsgen.explode.ExplosionVisitor;
import dev.metaschema.docsgen.explode.IAssemblyModelElement;
import dev.metaschema.freemarker.support.AbstractFreemarkerGenerator;
import dev.metaschema.model.common.IMetaschema;
import dev.metaschema.model.common.metapath.DynamicContext;
import dev.metaschema.model.common.metapath.StaticContext;
import dev.metaschema.model.common.metapath.item.INodeItemFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public abstract class AbstractExplodedModelFreemarkerGenerator
    extends AbstractFreemarkerGenerator {

  @Override
  protected void buildModel(@NonNull Configuration cfg, @NonNull Map<String, Object> root,
      @NonNull IMetaschema metaschema) throws IOException, TemplateException {

    ExplosionVisitor visitor = new ExplosionVisitor();

    INodeItemFactory factory = INodeItemFactory.instance();

    DynamicContext dynamicContext = new StaticContext()
        .newDynamicContext()
        .disablePredicateEvaluation();

    List<? extends IAssemblyModelElement> rootAssemblies = metaschema.getExportedAssemblyDefinitions().stream()
        .filter(modelItem -> modelItem.isRoot())
        .map(rootDefinition -> factory.newAssemblyNodeItem(rootDefinition, metaschema.getLocation()))
        .map(rootItem -> (IAssemblyModelElement) visitor.visit(rootItem, dynamicContext))
        .collect(Collectors.toUnmodifiableList());

    // cfg.setObjectWrapper(new DefaultObjectWrapperBuilder()...build());

    root.put("roots", rootAssemblies);
  }

}
