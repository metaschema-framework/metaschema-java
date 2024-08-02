# Metaschema Java Tools

This project provides a Java implementation of the [Metaschema](https://metaschema.dev/) framework that supports format-agnostic information modeling and processing.

This project supports the following features:

- **Allows developers to generate Java classes from Metaschema definitions.** Using a given Metaschema module, a developer can quickly [generate Java classes](metaschema-databind/) for a Metaschema-based model, which can be used to create, parse, modify, and write XML, JSON, and YAML representations of that model using the generated bound Java objects. This allows a developer to quickly start programming business logic instead of spending hours writing parsing code. This approach is similar to the binding frameworks provided by [Jakarta XML Binding (JAXB)](https://eclipse-ee4j.github.io/jaxb-ri/), [XMLBeans](https://xmlbeans.apache.org/), [Java API for JSON Binding (JSON-B)](https://javaee.github.io/jsonb-spec/), and other class-to-object binding approaches. The [OSCAL Java Library](https://github.com/metaschema-framework/liboscal-java/) is an example of applying this approach to generate Java programming APIs for a set of Metaschema-defined models.
- **Supports generating Metaschema-based Java code during Maven builds.** An [Apache Maven](https://maven.apache.org/) [Metaschema code generation plugin](metaschema-maven-plugin/) is provided that supports Java class generation for a Metaschema definition during Maven builds.
- **Enables validation of data aligned with a set of Metaschema definitions using constraints defined within the Metaschema definitions.** Using this framework, format-agnostic validation rules, defined in a Metaschema, can be enforced over data loaded into bound objects.
- **Allows execution of Metapath queries against data aligned with a Metaschema-based model.** Metapath is an XPath-like expression language that can be used to query XML, JSON, or YAML data aligned with a Metaschema definition. This allows data to be queried irrespective of the format it is stored in.

This project contains the following sub-modules:

- [Metaschema Java API](metaschema-core/): Provides a [Java API](metaschema-core/apidocs/index.html) for interacting with Metaschema modules and executing Metapath expressions in Java programs. Supports loading XML-based Metaschema modules.
- [Metaschema Java Binding](metaschema-databind/): Supports the generation and use of annotated plain old Java objects (POJOs) to create and store Metaschema module-based data. Supports code generation of POJO classes based on a Metaschema module and reading and writing Metaschema module-based data. Can read and write XML, JSON, and YAML content that is valid to the associated Metaschema model.
- [Metaschema Data Bindings](metaschema-databind-modules/): Provides a variety of different Metaschema modules for different types of data, including support for the Static Analysis Results Interchange Format (SARIF).
- [Metaschema Maven Plugin](metaschema-maven-plugin/): A Maven build plugin that automates generation on Java classes and XML and JSON schemas based on a Metaschema module as part of a Maven build.
- [Metaschema Schema Generator](metaschema-schema-generator): An API for generating XML and JSON schemas based on a Metaschema module.

Please refer to each sub-module for usage instructions.
