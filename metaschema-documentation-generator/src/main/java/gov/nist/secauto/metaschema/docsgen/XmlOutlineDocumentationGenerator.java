

package gov.nist.secauto.metaschema.docsgen;

import java.io.IOException;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

public class XmlOutlineDocumentationGenerator
    extends AbstractDocumentationGenerator {

  @Override
  protected Template getTemplate(Configuration cfg)
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
    return cfg.getTemplate("xml-outline.ftlx");
  }
}
