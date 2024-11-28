/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnPath;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.model.constraint.ExternalConstraintsModulePostProcessor;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

public class MetaConstraintLoaderTest {

  @Test
  void test() throws MetaschemaException, IOException {

    List<IConstraintSet> constraintSet = new XmlMetaConstraintLoader()
        .load(ObjectUtils.requireNonNull(Paths.get("src/test/resources/computer-metaschema-meta-constraints.xml")));

    ExternalConstraintsModulePostProcessor postProcessor
        = new ExternalConstraintsModulePostProcessor(constraintSet);
    ModuleLoader loader = new ModuleLoader(CollectionUtil.singletonList(postProcessor));
    URI moduleUri = ObjectUtils.notNull(
        Paths.get("metaschema/examples/computer-example.xml").toUri());
    IXmlMetaschemaModule module = loader.load(moduleUri);

    IMetapathExpression expression = IMetapathExpression.compile("//@id", module.getModuleStaticContext());
    IModuleNodeItem moduleItem = INodeItemFactory.instance().newModuleNodeItem(module);
    for (IItem item : expression.evaluate(moduleItem)) {
      IDefinitionNodeItem<?, ?> nodeItem = (IDefinitionNodeItem<?, ?>) item;
      System.out.print(FnPath.fnPath(nodeItem));
      System.out.print(": ");
      System.out.println(Long.toString(nodeItem.getDefinition().getMatchesConstraints().stream()
          .filter(matches -> MetaschemaDataTypeProvider.UUID.equals(matches.getDataType()))
          .count()));
    }

    expression.evaluate(moduleItem).stream()
        .map(item -> (IDefinitionNodeItem<?, ?>) item)
        .forEach(item -> assertEquals(1, item.getDefinition().getMatchesConstraints().stream()
            .filter(matches -> MetaschemaDataTypeProvider.UUID.equals(matches.getDataType()))
            .count()));
  }

}
