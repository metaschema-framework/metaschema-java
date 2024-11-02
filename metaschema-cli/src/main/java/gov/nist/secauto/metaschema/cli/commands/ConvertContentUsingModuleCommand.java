/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.command.CommandExecutionException;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.Format;
import gov.nist.secauto.metaschema.databind.io.FormatDetector;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.io.IDeserializer;
import gov.nist.secauto.metaschema.databind.io.ISerializer;
import gov.nist.secauto.metaschema.databind.io.ModelDetector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This command implementation supports the conversion of a content instance
 * between supported formats based on a provided Metaschema module.
 */
class ConvertContentUsingModuleCommand
    extends AbstractConvertSubcommand {
  @NonNull
  private static final String COMMAND = "convert";

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Convert the provided resource aligned to the provided Metaschema module to the specified format.";
  }

  @Override
  public Collection<? extends Option> gatherOptions() {
    Collection<? extends Option> orig = super.gatherOptions();

    List<Option> retval = new ArrayList<>(orig.size() + 1);
    retval.addAll(orig);
    retval.add(MetaschemaCommands.METASCHEMA_REQUIRED_OPTION);

    return CollectionUtil.unmodifiableCollection(retval);
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {

    return new CommandExecutor(callingContext, commandLine);
  }

  private final class CommandExecutor
      extends AbstractConversionCommandExecutor {

    private CommandExecutor(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) {
      super(callingContext, commandLine);
    }

    @Override
    protected IBindingContext getBindingContext() throws CommandExecutionException {
      IBindingContext retval = MetaschemaCommands.newBindingContextWithDynamicCompilation();

      IModule module = MetaschemaCommands.loadModule(
          getCommandLine(),
          MetaschemaCommands.METASCHEMA_REQUIRED_OPTION,
          ObjectUtils.notNull(getCurrentWorkingDirectory().toUri()),
          retval);
      retval.registerModule(module);
      return retval;
    }

    @Override
    protected void handleConversion(URI source, Format toFormat, Writer writer, IBoundLoader loader)
        throws FileNotFoundException, IOException {
      URI resourceUri = loader.resolve(source);
      URL resource = resourceUri.toURL();

      try (InputStream is = resource.openStream()) {
        assert is != null;

        FormatDetector.Result formatMatch = loader.detectFormat(is);
        Format format = formatMatch.getFormat();

        try (InputStream formatStream = formatMatch.getDataStream()) {
          try (ModelDetector.Result modelMatch = loader.detectModel(formatStream, format)) {

            IBindingContext bindingContext = loader.getBindingContext();

            IDeserializer<?> deserializer = bindingContext.newDeserializer(format, modelMatch.getBoundClass());
            deserializer.applyConfiguration(loader);
            try (InputStream modelStream = modelMatch.getDataStream()) {
              IBoundObject obj = deserializer.deserialize(modelStream, resourceUri);

              ISerializer<?> serializer = bindingContext.newSerializer(toFormat, modelMatch.getBoundClass());
              serializer.serialize(obj, writer);
            }

          }
        }
      }
    }

  }

}
