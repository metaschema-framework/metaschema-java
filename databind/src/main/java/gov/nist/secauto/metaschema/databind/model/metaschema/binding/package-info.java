/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

/**
 * A Metaschema module represented as a set of Metaschema module bindings.
 */

@gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaPackage(moduleClass = { MetaschemaModelModule.class })
@gov.nist.secauto.metaschema.databind.model.annotations.XmlSchema(
    namespace = gov.nist.secauto.metaschema.core.MetaschemaConstants.METASCHEMA_NAMESPACE,
    xmlns = { @gov.nist.secauto.metaschema.databind.model.annotations.XmlNs(prefix = "",
        namespace = gov.nist.secauto.metaschema.core.MetaschemaConstants.METASCHEMA_NAMESPACE) },
    xmlElementFormDefault = gov.nist.secauto.metaschema.databind.model.annotations.XmlNsForm.QUALIFIED)
package gov.nist.secauto.metaschema.databind.model.metaschema.binding;
