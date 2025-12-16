Metaschema Command line Tool v${project.version}

Overview:
---------

This open-source, tool offers a convenient way to manipulate Metaschema based
content supporting the following operations:

- Validating a Metaschema model definition to ensure it is well-formed and valid.
- Generating XML and JSON Schemas from a Metaschema model definition.

More information can be found at: https://github.com/metaschema-framework/metaschema-java

Requirements:
-------------

Requires installation of a Java runtime environment version 11 or newer

Use:
----

The tool has an integrated help feature that explains the various command line options and commands.

The tool can be run as follows:

metaschema-cli --help

Shell Completion:
-----------------

The tool supports tab-completion for Bash and Zsh shells. To enable completion:

For Bash:

  # Generate and source completion script
  source <(metaschema-cli shell-completion bash)

  # Or save to a file for persistent use
  metaschema-cli shell-completion bash --to ~/.metaschema-cli-completion.bash
  echo 'source ~/.metaschema-cli-completion.bash' >> ~/.bashrc

For Zsh:

  # Generate and save to completions directory
  metaschema-cli shell-completion zsh > ~/.zsh/completions/_metaschema-cli

  # Ensure completions directory is in fpath (add to ~/.zshrc if needed)
  fpath=(~/.zsh/completions $fpath)
  autoload -Uz compinit && compinit

Completion provides intelligent suggestions for commands, options, file paths,
and format arguments when you press Tab.

Feedback:
---------

Please post issues about tool defects, enhancement requests, and any other related
comments in the tool's GitHub repository at https://github.com/metaschema-framework/metaschema-java/issues.

Change Log:
----------

For change logs, please goto https://github.com/metaschema-framework/metaschema-java/releases.
