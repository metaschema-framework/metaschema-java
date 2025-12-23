/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * Contains pre-generated Metaschema binding classes for the binding
 * configuration model.
 * <p>
 * These classes are bootstrapped (pre-generated and committed to source
 * control) because the databind module cannot use the metaschema-maven-plugin
 * to generate them during build (circular dependency). When the binding
 * configuration schema changes, regenerate using:
 *
 * <pre>
 * java -jar metaschema-cli/target/metaschema-cli-*-metaschema-cli.jar \
 *     generate-java --output-dir /tmp/binding-classes \
 *     databind/src/main/metaschema/metaschema-bindings.yaml
 * </pre>
 *
 * Then copy the generated classes to this package.
 */

@gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaPackage(moduleClass = {
    gov.nist.secauto.metaschema.databind.config.binding.MetaschemaBindingsModule.class })
@gov.nist.secauto.metaschema.databind.model.annotations.XmlSchema(
    namespace = "https://csrc.nist.gov/ns/metaschema-binding/1.0",
    xmlns = { @gov.nist.secauto.metaschema.databind.model.annotations.XmlNs(prefix = "",
        namespace = "https://csrc.nist.gov/ns/metaschema-binding/1.0") },
    xmlElementFormDefault = gov.nist.secauto.metaschema.databind.model.annotations.XmlNsForm.QUALIFIED)
package gov.nist.secauto.metaschema.databind.config.binding;
