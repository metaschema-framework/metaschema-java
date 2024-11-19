/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.datatype.DataTypeService;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;

import org.junit.jupiter.api.Test;

class MetaschemaDataTypeProviderTest {

  @Test
  void test() {
    assertNotNull(DataTypeService.instance().getJavaTypeAdapterByQNameIndex(
        EQNameFactory.of(MetapathConstants.NS_METAPATH, "uuid").getIndexPosition()));
  }

}
