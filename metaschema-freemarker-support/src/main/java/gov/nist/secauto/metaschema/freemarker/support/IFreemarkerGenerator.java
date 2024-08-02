

package gov.nist.secauto.metaschema.freemarker.support;

import gov.nist.secauto.metaschema.model.common.IMetaschema;

import java.io.IOException;
import java.io.Writer;

import edu.umd.cs.findbugs.annotations.NonNull;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public interface IFreemarkerGenerator {
  void generateFromMetaschema(@NonNull IMetaschema metaschema, @NonNull Writer out)
      throws TemplateNotFoundException, MalformedTemplateNameException, TemplateException, ParseException, IOException;
}
