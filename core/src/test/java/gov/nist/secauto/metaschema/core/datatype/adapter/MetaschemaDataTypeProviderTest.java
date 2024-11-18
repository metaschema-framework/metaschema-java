/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.datatype.DataTypeService;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;

import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

class MetaschemaDataTypeProviderTest {

  @Test
  void test() {
    assertNotNull(DataTypeService.instance()
        .getJavaTypeAdapterByQName(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "uuid")));
  }

}
