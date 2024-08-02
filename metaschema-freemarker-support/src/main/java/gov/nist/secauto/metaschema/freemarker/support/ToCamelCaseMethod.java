

package gov.nist.secauto.metaschema.freemarker.support;

import java.util.List;
import java.util.Locale;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class ToCamelCaseMethod implements TemplateMethodModelEx {
  @Override
  public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {

    if (arguments.isEmpty() || arguments.size() != 1) {
      throw new TemplateModelException("This method requires a single string argument.");
    }

    String text = ((TemplateScalarModel) arguments.get(0)).getAsString();
    StringBuilder builder = new StringBuilder();
    for (String segment : text.split("\\p{Punct}")) {
      if (segment.length() > 0) {
        builder.append(segment.substring(0, 1).toUpperCase(Locale.ROOT));
      }
      if (segment.length() > 1) {
        builder.append(segment.substring(1).toLowerCase(Locale.ROOT));
      }
    }
    return builder.toString();
  }
}
