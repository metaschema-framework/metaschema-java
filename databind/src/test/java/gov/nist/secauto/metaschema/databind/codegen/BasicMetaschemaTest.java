/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.model.IConstraintLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.metaschema.IBindingMetaschemaModule;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

class BasicMetaschemaTest
    extends AbstractMetaschemaTest {

  @Test
  void testSimpleMetaschema() throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests("simple", "gov.nist.csrc.ns.metaschema.testing.simple.TopLevel", ObjectUtils.notNull(generationDir));
    // runTests("simple", "gov.nist.csrc.ns.metaschema.testing.simple.TopLevel",
    // generationDir, (obj) ->
    // {
    // try {
    // Assertions.assertEquals("test", reflectMethod(obj, "getId"));
    // } catch (NoSuchMethodException | SecurityException e) {
    // Assertions.fail(e);
    // }
    // });
  }

  @Test
  void testSimpleUuidMetaschema()
      throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests(
        "simple_with_uuid",
        "gov.nist.csrc.ns.metaschema.testing.simple.with.uuid.TopLevel",
        ObjectUtils.notNull(generationDir),
        obj -> {
          try {
            assertEquals("5de455cf-2f8d-4da2-9182-323d433e1065", reflectMethod(obj, "getUuid").toString());
          } catch (NoSuchMethodException | SecurityException e) {
            fail(e);
          }
        });
  }

  @Test
  void testSimpleWithFieldMetaschema()
      throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests(
        "simple_with_field",
        "gov.nist.csrc.ns.metaschema.testing.simple.with.field.TopLevel",
        ObjectUtils.notNull(generationDir));
  }

  private static Object reflectMethod(Object obj, String name) throws NoSuchMethodException {
    return ReflectionUtils.invokeMethod(obj.getClass().getMethod(name), obj);
  }

  @Test
  void testFieldsWithFlagMetaschema()
      throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests(
        "fields_with_flags",
        "gov.nist.csrc.ns.metaschema.testing.fields.with.flags.TopLevel",
        ObjectUtils.notNull(generationDir),
        obj -> {
          try {
            assertEquals("test", reflectMethod(obj, "getId"));
            Object field1 = ReflectionUtils.invokeMethod(obj.getClass().getMethod("getComplexField1"), obj);
            assertNotNull(field1);
            assertEquals("complex-field1", reflectMethod(field1, "getId"));
            assertEquals("test-string", reflectMethod(field1, "getValue"));

            @SuppressWarnings("unchecked") List<Object> field2s
                = (List<Object>) ReflectionUtils.invokeMethod(obj.getClass().getMethod("getComplexFields2"),
                    obj);
            assertNotNull(field2s);
            assertEquals(1, field2s.size());
            Object field2 = field2s.get(0);
            assertEquals("complex-field2-1", reflectMethod(field2, "getId"));
            assertEquals("test-string2", reflectMethod(field2, "getValue"));

            @SuppressWarnings("unchecked") List<Object> field3s
                = (List<Object>) ReflectionUtils.invokeMethod(obj.getClass().getMethod("getComplexFields3"),
                    obj);
            assertEquals(2, field3s.size());
            assertAll("ComplexFields4 item", () -> {
              Object item = field3s.get(0);
              assertEquals("complex-field3-1", reflectMethod(item, "getId2"));
              assertEquals("test-string3", reflectMethod(item, "getValue"));
            });
            assertAll("ComplexFields4 item", () -> {
              Object item = field3s.get(1);
              assertEquals("complex-field3-2", reflectMethod(item, "getId2"));
              assertEquals("test-string4", reflectMethod(item, "getValue"));
            });

            assertAll("ComplexFields4", () -> {
              @SuppressWarnings("unchecked") Map<String, Object> collection
                  = (Map<String, Object>) ReflectionUtils.invokeMethod(obj.getClass().getMethod("getComplexFields4"),
                      obj);
              assertNotNull(collection, "ComplexFields4 collection is null");
              assertEquals(2, collection.size(), "ComplexFields4 collection is not size 2");
              Set<Map.Entry<String, Object>> entries = collection.entrySet();
              Iterator<Map.Entry<String, Object>> iter = entries.iterator();

              assertAll("ComplexFields4 item", () -> {
                Map.Entry<String, Object> entry = iter.next();
                assertEquals("complex-field4-1", entry.getKey());
                assertEquals("complex-field4-1", reflectMethod(entry.getValue(), "getId2"));
                assertEquals("test-string5", reflectMethod(entry.getValue(), "getValue"));
              });

              assertAll("ComplexFields4 item", () -> {
                Map.Entry<String, Object> entry = iter.next();
                assertEquals("complex-field4-2", entry.getKey());
                assertEquals("complex-field4-2", reflectMethod(entry.getValue(), "getId2"));
                assertEquals("test-string6", reflectMethod(entry.getValue(), "getValue"));
              });
            });
          } catch (NoSuchMethodException | SecurityException e) {
            fail(e);
          }
        });
  }

  @Test
  void testAssemblyMetaschema()
      throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests(
        "assembly",
        "gov.nist.itl.metaschema.codegen.xml.example.assembly.TopLevel",
        ObjectUtils.notNull(generationDir),
        obj -> {
          try {
            assertEquals("test", reflectMethod(obj, "getId"));
          } catch (NoSuchMethodException | SecurityException e) {
            fail(e);
          }
        });
  }

  @Test
  void testLocalDefinitionsMetaschema()
      throws MetaschemaException, IOException, ClassNotFoundException, BindingException {
    runTests(
        "local-definitions",
        "gov.nist.csrc.ns.metaschema.testing.local.definitions.TopLevel",
        ObjectUtils.notNull(generationDir));
  }

  @Test
  void testExistsWithVariable() throws IOException, URISyntaxException, MetaschemaException {
    IBindingContext bindingContext = getBindingContext();

    IBindingMetaschemaModule module = bindingContext.loadMetaschema(
        new URL("https://raw.githubusercontent.com/usnistgov/OSCAL/main/src/metaschema/oscal_complete_metaschema.xml"));

    IDocumentNodeItem moduleItem = ObjectUtils.requireNonNull(module.getSourceNodeItem());
    // METASCHEMA moduleData =
    moduleItem.getValue();
    assert moduleItem != null;

    StaticContext staticContext = moduleItem.getStaticContext();

    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.setDocumentLoader(bindingContext.newBoundLoader());

    // MetapathExpression importsMetapath = MetapathExpression.compile(
    // "for $import in /METASCHEMA/import return
    // doc(resolve-uri($import/@href))/METASCHEMA",
    // staticContext);

    // ISequence<?> imports = importsMetapath.evaluate(moduleItem, dynamicContext);

    MetapathExpression allImportsExpression = MetapathExpression.compile(
        "recurse-depth(/METASCHEMA,'for $import in ./import return doc(resolve-uri($import/@href))/METASCHEMA')",
        staticContext);

    ISequence<?> allImports = allImportsExpression.evaluate(moduleItem, dynamicContext);
    allImports.getValue();

    MetapathExpression path = MetapathExpression.compile("exists($all-imports/define-assembly/root-name)",
        staticContext);

    boolean result = ObjectUtils.requireNonNull(path.evaluateAs(
        moduleItem,
        MetapathExpression.ResultType.BOOLEAN,
        dynamicContext.subContext().bindVariableValue(new QName("all-imports"), allImports)));

    assertTrue(result, "no root");
  }

  @Test
  void codegenTest() throws MetaschemaException, IOException {

    List<IConstraintSet> constraints;
    {
      IConstraintLoader constraintLoader = IBindingContext.getConstraintLoader();

      constraints = constraintLoader.load(ObjectUtils.notNull(
          Paths.get("../core/metaschema/schema/metaschema/metaschema-module-constraints.xml")));
    }

    IBindingContext bindingContext = IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(Paths.get("target"), "modules-")))
        .constraintSet(constraints)
        .build();

    IBindingMetaschemaModule module = bindingContext.loadMetaschema(ObjectUtils.notNull(
        Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml")));

    assertFalse(module.getRootAssemblyDefinitions().isEmpty());
  }
}
