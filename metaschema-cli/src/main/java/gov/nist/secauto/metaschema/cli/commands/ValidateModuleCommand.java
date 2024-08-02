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
import gov.nist.secauto.metaschema.databind.DefaultBindingContext;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.METASCHEMA;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.MetaschemaModelModule;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.ISchemaGenerator.SchemaFormat;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Source;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ValidateModuleCommand
    extends AbstractValidateContentCommand {
  private static final Logger LOGGER = LogManager.getLogger(ValidateModuleCommand.class);
  @NonNull
  private static final String COMMAND = "validate";

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Validate that the specified Module is well-formed and valid to the Module model";
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {
    return new ValidateModuleCommandExecutor(callingContext, commandLine);
  }

  private final class ValidateModuleCommandExecutor
      extends AbstractValidationCommandExecutor {
    private Path tempDir;

    private ValidateModuleCommandExecutor(
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

    @Override
    protected IBindingContext getBindingContext(Set<IConstraintSet> constraintSets)
        throws MetaschemaException, IOException {
      IBindingContext retval;
      if (constraintSets.isEmpty()) {
        retval = IBindingContext.instance();
      } else {
        ExternalConstraintsModulePostProcessor postProcessor
            = new ExternalConstraintsModulePostProcessor(constraintSets);
        retval = new DefaultBindingContext(CollectionUtil.singletonList(postProcessor));
      }
      retval.getBoundDefinitionForClass(METASCHEMA.class);
      return retval;
    }

    @Override
    public List<Source> getXmlSchemas(@NonNull URL targetResource) throws IOException {
      List<Source> retval = new LinkedList<>();
      retval.add(XmlUtil.getStreamSource(
          ObjectUtils.requireNonNull(
              ModuleLoader.class.getResource("/schema/xml/metaschema.xsd"),
              "Unable to load '/schema/xml/metaschema.xsd' on the classpath")));
      return CollectionUtil.unmodifiableList(retval);
    }

    @Override
    public JSONObject getJsonSchema(@NonNull JSONObject json) throws IOException {
      IModule module = IBindingContext.instance().registerModule(MetaschemaModelModule.class);

      Path schemaFile = Files.createTempFile(getTempDir(), "schema-", ".json");
      assert schemaFile != null;
      IMutableConfiguration<SchemaGenerationFeature<?>> configuration = new DefaultConfiguration<>();
      ISchemaGenerator.generateSchema(module, schemaFile, SchemaFormat.JSON, configuration);
      try (BufferedReader reader = ObjectUtils.notNull(Files.newBufferedReader(schemaFile, StandardCharsets.UTF_8))) {
        return JsonUtil.toJsonObject(reader);
      }
    }
  }
}
