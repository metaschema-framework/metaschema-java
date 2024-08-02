/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

class BindingConfigurationLoaderTest {

  @Test
  void testDefault() {
    DefaultBindingConfiguration config = new DefaultBindingConfiguration();

    // test namespaces
    Map<String, String> namespaceToPackageName = new HashMap<>();
    namespaceToPackageName.put("http://csrc.nist.gov/ns/metaschema/testing/assembly",
        "gov.nist.csrc.ns.metaschema.testing.assembly");

    for (Map.Entry<String, String> entry : namespaceToPackageName.entrySet()) {
      assertEquals(entry.getValue(), config.getPackageNameForNamespace(ObjectUtils.notNull(entry.getKey())));
    }
  }

  @Test
  void testConfiguredNamespace() {
    DefaultBindingConfiguration config = new DefaultBindingConfiguration();

    // test namespaces
    Map<String, String> namespaceToPackageName = new HashMap<>();
    namespaceToPackageName.put("http://csrc.nist.gov/ns/metaschema/testing/assembly",
        "gov.nist.secauto.metaschema.testing.assembly");

    for (Map.Entry<String, String> entry : namespaceToPackageName.entrySet()) {
      config.addModelBindingConfig(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, String> entry : namespaceToPackageName.entrySet()) {
      assertEquals(entry.getValue(), config.getPackageNameForNamespace(ObjectUtils.notNull(entry.getKey())));
    }
  }

  @Test
  void test() throws MalformedURLException, IOException {
    File configFile = new File("src/main/metaschema-bindings/oscal-metaschema-bindings.xml");
    DefaultBindingConfiguration config = new DefaultBindingConfiguration();
    config.load(configFile);
    assertNotNull(config);
  }

}
