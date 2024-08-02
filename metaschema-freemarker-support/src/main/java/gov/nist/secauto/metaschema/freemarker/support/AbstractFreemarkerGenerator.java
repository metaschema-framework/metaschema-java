

package gov.nist.secauto.metaschema.freemarker.support;

import gov.nist.secauto.metaschema.model.common.IMetaschema;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;
import freemarker.cache.ClassTemplateLoader;
import freemarker.core.ParseException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.Version;

public abstract class AbstractFreemarkerGenerator implements IFreemarkerGenerator {
  private static final Version CONFIG_VERSION = Configuration.VERSION_2_3_30;
  private static final boolean DEBUG = false;

  protected Configuration newConfiguration() {
    // Create your Configuration instance, and specify if up to what FreeMarker
    // version (here 2.3.29) do you want to apply the fixes that are not 100%
    // backward-compatible. See the Configuration JavaDoc for details.
    Configuration cfg = new Configuration(CONFIG_VERSION);

    // // Specify the source where the template files come from. Here I set a
    // // plain directory for it, but non-file-system sources are possible too:
    // cfg.setDirectoryForTemplateLoading(new File("/where/you/store/templates"));
    ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/templates");
    cfg.setTemplateLoader(ctl);

    if (DEBUG) {
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
    } else {
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    cfg.setLogTemplateExceptions(false);

    // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
    cfg.setWrapUncheckedExceptions(true);

    // Do not fall back to higher scopes when reading a null loop variable:
    cfg.setFallbackOnNullLoopVariable(false);

    return cfg;
  }

  @Override
  public void generateFromMetaschema(@NonNull IMetaschema metaschema, Writer out)
      throws TemplateNotFoundException, MalformedTemplateNameException, TemplateException, ParseException, IOException {

    Configuration cfg = newConfiguration();

    // add directives
    cfg.setSharedVariable("toCamelCase", new ToCamelCaseMethod());
    cfg.setSharedVariable("markupToHTML", new MarkupToHtmlMethod());
    cfg.setSharedVariable("markupToMarkdown", new MarkupToMarkdownMethod());

    // add constants
    BeansWrapper wrapper = new BeansWrapperBuilder(CONFIG_VERSION).build();
    TemplateHashModel staticModels = wrapper.getStaticModels();

    // add static values
    cfg.setSharedVariable("statics", staticModels);

    // Create the root hash. We use a Map here, but it could be a JavaBean too.
    Map<String, Object> root = new HashMap<>(); // NOPMD - Freemarker templates run in a single thread

    Template template = getTemplate(cfg);

    // add metaschema model
    buildModel(cfg, root, metaschema);

    template.process(root, out);
  }

  protected abstract Template getTemplate(Configuration cfg)
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException;

  protected abstract void buildModel(@NonNull Configuration cfg, @NonNull Map<String, Object> root,
      @NonNull IMetaschema metaschema) throws IOException, TemplateException;
}
