/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.commands.metapath;

import gov.nist.secauto.metaschema.cli.processor.CLIProcessor.CallingContext;
import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;
import gov.nist.secauto.metaschema.cli.processor.command.AbstractTerminalCommand;
import gov.nist.secauto.metaschema.cli.processor.command.ICommandExecutor;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionService;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ListFunctionsSubcommand
    extends AbstractTerminalCommand {
  private static final Logger LOGGER = LogManager.getLogger(ListFunctionsSubcommand.class);

  @NonNull
  private static final String COMMAND = "list-functions";

  @Override
  public String getName() {
    return COMMAND;
  }

  @Override
  public String getDescription() {
    return "Get a listing of supported Metapath functions";
  }

  @Override
  public ICommandExecutor newExecutor(CallingContext callingContext, CommandLine cmdLine) {
    return ICommandExecutor.using(callingContext, cmdLine, this::executeCommand);
  }

  /**
   * Execute the list functions command.
   *
   * @param callingContext
   *          the context of the command execution
   * @param cmdLine
   *          the parsed command line details
   * @return the execution result
   */
  @SuppressWarnings({
      "PMD.OnlyOneReturn", // readability
      "PMD.AvoidInstantiatingObjectsInLoops"
  })
  protected ExitStatus executeCommand(
      @NonNull CallingContext callingContext,
      @NonNull CommandLine cmdLine) {

    Map<String, Map<String, List<IFunction>>> namespaceToNameToFunctionMap = FunctionService.getInstance().stream()
        .collect(Collectors.groupingBy(
            function -> function.getQName().getNamespaceURI(),
            Collectors.groupingBy(
                IFunction::getName,
                Collectors.toList())));

    Map<String, String> namespaceToPrefixMap = StaticContext.getWellKnownNamespacesMap().entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getValue().toASCIIString(), Map.Entry::getKey));

    List<String> namespaces = new ArrayList<>(namespaceToNameToFunctionMap.keySet());

    Collections.sort(namespaces);

    for (String namespace : namespaces) {
      String prefix = namespaceToPrefixMap.get(namespace);

      if (prefix == null) {
        LOGGER.atInfo().log("In namespace '{}':", namespace);
      } else {
        LOGGER.atInfo().log("In namespace '{}' as '{}':", namespace, prefix);
      }

      Map<String, List<IFunction>> namespacedFunctions = namespaceToNameToFunctionMap.get(namespace);

      List<String> names = new ArrayList<>(namespacedFunctions.keySet());
      Collections.sort(names);

      for (String name : names) {
        List<IFunction> functions = namespacedFunctions.get(name);
        Collections.sort(functions, Comparator.comparing(IFunction::arity));

        for (IFunction function : functions) {
          String functionRef = prefix == null
              ? String.format("Q{%s}%s", function.getQName().getNamespaceURI(), function.getName())
              : String.format("%s:%s", prefix, function.getName());

          LOGGER.atInfo().log(String.format("%s(%s) as %s",
              functionRef,
              function.getArguments().isEmpty()
                  ? ""
                  : function.getArguments().stream().map(IArgument::toSignature)
                      .collect(Collectors.joining(","))
                      + (function.isArityUnbounded() ? ", ..." : ""),
              function.getResult().toSignature()));
        }
      }
    }
    return ExitCode.OK.exit();
  }
}
