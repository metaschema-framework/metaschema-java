/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

@XmlSchema(
    namespace = "http://example.org/ns/test",
    xmlns = { @XmlNs(prefix = "",
        namespace = "http://example.org/ns/test") },
    xmlElementFormDefault = XmlNsForm.QUALIFIED)
package dev.metaschema.databind.model.test;

import dev.metaschema.databind.model.annotations.XmlNs;
import dev.metaschema.databind.model.annotations.XmlNsForm;
import dev.metaschema.databind.model.annotations.XmlSchema;
