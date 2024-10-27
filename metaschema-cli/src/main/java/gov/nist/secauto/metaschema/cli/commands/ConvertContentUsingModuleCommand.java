/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ConvertContentUsingModuleCommand
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
    retval.add(MetaschemaCommands.METASCHEMA_OPTION);

    return CollectionUtil.unmodifiableCollection(retval);
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine commandLine) {

    return new OscalCommandExecutor(callingContext, commandLine);
  }

  private final class OscalCommandExecutor
      extends AbstractConversionCommandExecutor {

    private OscalCommandExecutor(
        @NonNull CallingContext callingContext,
        @NonNull CommandLine commandLine) {
      super(callingContext, commandLine);
    }

    @Override
    protected IBindingContext getBindingContext() throws IOException, MetaschemaException {
      IBindingContext retval = MetaschemaCommands.newBindingContextWithDynamicCompilation();

      URI cwd = ObjectUtils.notNull(Paths.get("").toAbsolutePath().toUri());

      IModule module;
      try {
        module = MetaschemaCommands.handleModule(getCommandLine(), cwd, retval);
      } catch (URISyntaxException ex) {
        throw new IOException(String.format("Cannot load module as '%s' is not a valid file or URL.", ex.getInput()),
            ex);
      }
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
