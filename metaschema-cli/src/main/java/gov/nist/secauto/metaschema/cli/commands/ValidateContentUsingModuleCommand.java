/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.configuration.DefaultConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.util.JsonUtil;
import gov.nist.secauto.metaschema.core.model.util.XmlUtil;
import gov.nist.secauto.metaschema.core.model.xml.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.core.util.UriUtils;
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator.SchemaFormat;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ValidateContentUsingModuleCommand
    extends AbstractValidateContentCommand {
  @NonNull
  private static final String COMMAND = "validate-content";

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Verify that the provided resource is well-formed and valid to the provided Module-based model.";
  }

  @Override
  public Collection<? extends Option> gatherOptions() {
    Collection<? extends Option> orig = super.gatherOptions();

    List<Option> retval = new ArrayList<>(orig.size() + 1);
    retval.addAll(orig);
    retval.add(MetaschemaCommandSupport.METASCHEMA_OPTION);

    return CollectionUtil.unmodifiableCollection(retval);
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {
    return new OscalCommandExecutor(callingContext, commandLine);
  }

  private final class OscalCommandExecutor
      extends AbstractValidationCommandExecutor {

    private Path tempDir;
    private IModule module;

    private OscalCommandExecutor(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) {
      super(callingContext, commandLine);
    }

    private Path getTempDir() throws IOException {
      if (tempDir == null) {
        tempDir = Files.createTempDirectory("validation-");
        tempDir.toFile().deleteOnExit();
      }
      return tempDir;
    }

    @NonNull
    private IModule getModule(@NonNull Set<IConstraintSet> constraintSets)
        throws MetaschemaException, IOException {
      URI cwd = Paths.get("").toAbsolutePath().toUri();

      if (module == null) {
        String moduleName
            = ObjectUtils.requireNonNull(getCommandLine().getOptionValue(MetaschemaCommandSupport.METASCHEMA_OPTION));
        URI moduleUri;

        try {
          moduleUri = UriUtils.toUri(moduleName, cwd);
        } catch (URISyntaxException ex) {
          IOException newEx = new IOException( // NOPMD - intentional
              String.format("Cannot load module as '%s' is not a valid file or URL.", moduleName));
          newEx.addSuppressed(ex);
          throw newEx;
        }

        assert moduleUri != null;

        ExternalConstraintsModulePostProcessor postProcessor
            = new ExternalConstraintsModulePostProcessor(constraintSets);

        ModuleLoader loader = new ModuleLoader(CollectionUtil.singletonList(postProcessor));

        // BindingModuleLoader loader
        // = new BindingModuleLoader(new DefaultBindingContext(),
        // CollectionUtil.singletonList(postProcessor));

        loader.allowEntityResolution();
        module = loader.load(moduleUri);
      }
      assert module != null;
      return module;
    }

    @NonNull
    private IModule getModule() {
      // should be initialized already
      return ObjectUtils.requireNonNull(module);
    }

    @Override
    protected IBindingContext getBindingContext(@NonNull Set<IConstraintSet> constraintSets)
        throws MetaschemaException, IOException {

      IBindingContext retval = new DefaultBindingContext();
      retval.registerModule(getModule(constraintSets), getTempDir());
      return retval;
    }

    @Override
    public List<Source> getXmlSchemas(@NonNull URL targetResource) throws IOException {
      Path schemaFile = Files.createTempFile(getTempDir(), "schema-", ".xml");
      assert schemaFile != null;
      IMutableConfiguration<SchemaGenerationFeature<?>> configuration = new DefaultConfiguration<>();
      ISchemaGenerator.generateSchema(getModule(), schemaFile, SchemaFormat.XML, configuration);
      return ObjectUtils.requireNonNull(List.of(
          XmlUtil.getStreamSource(schemaFile.toUri().toURL())));
    }

    @Override
    public JSONObject getJsonSchema(@NonNull JSONObject json) throws IOException {
      Path schemaFile = Files.createTempFile(getTempDir(), "schema-", ".json");
      assert schemaFile != null;
      IMutableConfiguration<SchemaGenerationFeature<?>> configuration = new DefaultConfiguration<>();
      ISchemaGenerator.generateSchema(getModule(), schemaFile, SchemaFormat.JSON, configuration);
      try (BufferedReader reader = ObjectUtils.notNull(Files.newBufferedReader(schemaFile, StandardCharsets.UTF_8))) {
        return JsonUtil.toJsonObject(reader);
      }
    }
  }

}
