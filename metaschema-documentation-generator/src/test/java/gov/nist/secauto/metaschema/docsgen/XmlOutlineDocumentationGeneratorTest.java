

package gov.nist.secauto.metaschema.docsgen;

import gov.nist.secauto.metaschema.model.MetaschemaLoader;
import gov.nist.secauto.metaschema.model.common.IMetaschema;
import gov.nist.secauto.metaschema.model.common.MetaschemaException;

import org.apache.commons.io.output.TeeOutputStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.umd.cs.findbugs.annotations.NonNull;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

class XmlOutlineDocumentationGeneratorTest {

  @Test
  void test() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
      MetaschemaException, TemplateException {
    XmlOutlineDocumentationGenerator generator = new XmlOutlineDocumentationGenerator();

    MetaschemaLoader loader = new MetaschemaLoader();

    @NonNull
    IMetaschema metaschema = loader.load(new URL(
        "https://raw.githubusercontent.com/usnistgov/OSCAL/v1.0.0/src/metaschema/oscal_complete_metaschema.xml"));

    try (OutputStream fos = Files.newOutputStream(Paths.get("xml-outline.html"))) {
      TeeOutputStream out = new TeeOutputStream(System.out, fos);
      generator.generateFromMetaschema(metaschema, new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }
  }
}
