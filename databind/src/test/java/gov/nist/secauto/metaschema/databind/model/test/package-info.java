/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

@XmlSchema(
    namespace = "http://example.org/ns/test",
    xmlns = { @XmlNs(prefix = "",
        namespace = "http://example.org/ns/test") },
    xmlElementFormDefault = XmlNsForm.QUALIFIED)
package gov.nist.secauto.metaschema.databind.model.test;

import gov.nist.secauto.metaschema.databind.model.annotations.XmlNs;
import gov.nist.secauto.metaschema.databind.model.annotations.XmlNsForm;
import gov.nist.secauto.metaschema.databind.model.annotations.XmlSchema;
