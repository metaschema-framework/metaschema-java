/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.datatype.DataTypeService;

import org.junit.jupiter.api.Test;

class MetaschemaDataTypeProviderTest {

  @Test
  void test() {
    assertNotNull(DataTypeService.getInstance().getJavaTypeAdapterByName("uuid"));
  }

}
