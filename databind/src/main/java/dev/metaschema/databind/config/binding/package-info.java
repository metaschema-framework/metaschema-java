/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */
// Generated from: ../../../../../../metaschema/metaschema-bindings.yaml
// Do not edit - changes will be lost when regenerated.
/**
 * Provides generated Metaschema binding classes for module(s): Metaschema
 * Binding Configuration.
 * <p>
 * version 1.0.0
 * <p>
 * This module defines the binding configuration format used to customize Java
 * code generation from Metaschema modules. It allows specifying package names,
 * class names, interface implementations, base classes, and collection types
 * for generated binding classes.
 * </p>
 */

@dev.metaschema.databind.model.annotations.MetaschemaPackage(moduleClass = {
    dev.metaschema.databind.config.binding.MetaschemaBindingsModule.class })
@dev.metaschema.databind.model.annotations.XmlSchema(namespace = "https://csrc.nist.gov/ns/metaschema-binding/1.0",
    xmlns = { @dev.metaschema.databind.model.annotations.XmlNs(prefix = "",
        namespace = "https://csrc.nist.gov/ns/metaschema-binding/1.0") },
    xmlElementFormDefault = dev.metaschema.databind.model.annotations.XmlNsForm.QUALIFIED)
package dev.metaschema.databind.config.binding;
