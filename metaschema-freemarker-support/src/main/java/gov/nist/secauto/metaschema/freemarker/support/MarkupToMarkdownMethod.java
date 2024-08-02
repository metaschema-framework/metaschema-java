

package gov.nist.secauto.metaschema.freemarker.support;

import gov.nist.secauto.metaschema.model.common.datatype.markup.IMarkupString;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class MarkupToMarkdownMethod implements TemplateMethodModelEx {

  @Override
  public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {

    if (arguments.isEmpty() || arguments.size() != 1) {
      throw new TemplateModelException(String.format(
          "This method requires a %s typed object argument.",
          IMarkupString.class.getName()));
    }

    Object markupObject = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));

    if (!(markupObject instanceof IMarkupString)) {
      throw new TemplateModelException(String.format("The first argument must be of type %s. The type %s is invalid.",
          IMarkupString.class.getName(), markupObject.getClass().getName()));
    }

    IMarkupString<?> text = (IMarkupString<?>) markupObject;

    return text.toMarkdown();
  }

}
