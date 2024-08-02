

package gov.nist.secauto.metaschema.docsgen;

import gov.nist.secauto.metaschema.freemarker.support.AbstractFreemarkerGenerator;
import gov.nist.secauto.metaschema.model.common.IAssemblyDefinition;
import gov.nist.secauto.metaschema.model.common.IDefinition;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.UsedDefinitionModelWalker;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@SuppressWarnings("PMD")
public abstract class AbstractDocumentationGenerator
    extends AbstractFreemarkerGenerator
    implements DocumentationGenerator {

  @Override
  protected void buildModel(
      @NonNull Configuration cfg,
      @NonNull Map<String, Object> root,
      @NonNull IMetaschema metaschema)
      throws IOException, TemplateException {

    Collection<? extends IDefinition> definitions
        = UsedDefinitionModelWalker.collectUsedDefinitionsFromMetaschema(metaschema);
    Objects.requireNonNull(definitions, "definitions");
    Set<IMetaschema> metaschemas = new LinkedHashSet<>();
    Set<IAssemblyDefinition> rootAssemblies = new LinkedHashSet<>();
    for (IDefinition definition : definitions) {
      IMetaschema containingMetaschema = definition.getContainingMetaschema();
      if (!metaschemas.contains(containingMetaschema)) {
        metaschemas.add(containingMetaschema);
      }

      if (definition instanceof IAssemblyDefinition) {
        IAssemblyDefinition assemblyDefinition = (IAssemblyDefinition) definition;
        if (assemblyDefinition.isRoot()) {
          rootAssemblies.add(assemblyDefinition);
        }
      }
    }

    root.put("metaschema", metaschema);
    root.put("metaschemas", metaschemas);
    root.put("definitions", definitions);
    root.put("root-definitions", rootAssemblies);
  }

}
