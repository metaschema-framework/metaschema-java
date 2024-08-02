

package gov.nist.secauto.metaschema.freemarker.support;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;

import gov.nist.secauto.metaschema.model.common.datatype.markup.IMarkupString;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.MergedNsContext;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class MarkupToHtmlMethod implements TemplateMethodModelEx {

  @Override
  public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
    if (arguments.isEmpty() || arguments.size() < 2 || arguments.size() > 3) {
      throw new TemplateModelException(String.format(
          "This method requires a %s typed object argument, a namspace string argument, and may optionally have a"
              + " prefix string argument.",
          IMarkupString.class.getName()));
    }

    String prefix = null;
    if (arguments.size() == 3) {
      prefix = DeepUnwrap.unwrap((TemplateModel) arguments.get(2)).toString();
    }

    Object markupObject = DeepUnwrap.unwrap((TemplateModel) arguments.get(0));

    if (!(markupObject instanceof IMarkupString)) {
      throw new TemplateModelException(String.format("The first argument must be of type %s. The type %s is invalid.",
          IMarkupString.class.getName(), markupObject.getClass().getName()));
    }

    IMarkupString<?> text = (IMarkupString<?>) markupObject;
    String namespace = DeepUnwrap.unwrap((TemplateModel) arguments.get(1)).toString();
    assert namespace != null;

    XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
    assert factory instanceof WstxOutputFactory;
    factory.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_STRUCTURE, false);
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      XMLStreamWriter2 xmlStreamWriter = (XMLStreamWriter2) factory.createXMLStreamWriter(os);
      NamespaceContext nsContext = MergedNsContext.construct(xmlStreamWriter.getNamespaceContext(),
          List.of(NamespaceEventImpl.constructNamespace(null, prefix != null ? prefix : "", namespace)));
      xmlStreamWriter.setNamespaceContext(nsContext);

      text.writeXHtml(namespace, xmlStreamWriter);
      
      xmlStreamWriter.flush();
      return os.toString(StandardCharsets.UTF_8);
    } catch (XMLStreamException | IOException ex) {
      throw new TemplateModelException(ex);
    }
  }

}
