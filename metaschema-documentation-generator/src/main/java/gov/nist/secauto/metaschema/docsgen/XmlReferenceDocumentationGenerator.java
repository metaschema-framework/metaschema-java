

package gov.nist.secauto.metaschema.docsgen;

import gov.nist.secauto.metaschema.model.common.IMetaschema;

import java.io.IOException;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class XmlReferenceDocumentationGenerator
    extends AbstractExplodedModelFreemarkerGenerator {

  @Override
  protected Template getTemplate(Configuration cfg)
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
    return cfg.getTemplate("xml-reference.ftlx");
  }

  @Override
  protected void buildModel(@NonNull Configuration cfg, @NonNull Map<String, Object> root,
      @NonNull IMetaschema metaschema) throws IOException, TemplateException {
    super.buildModel(cfg, root, metaschema);
    root.put("test", "test string");
  }
}
