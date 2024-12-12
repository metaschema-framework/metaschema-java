
package gov.nist.secauto.metaschema.core.metapath.item.node;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.model.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

class DefaultNodeItemFactoryTest
    extends MockedModelTestSupport {
  @NonNull
  private static final String NS = ObjectUtils.notNull("http://example.com/ns");

  @Test
  void testGenerateModelItems() {
    IAssemblyDefinition assembly = assembly()
        .namespace(NS)
        .name("assembly1")
        .source(ISource.externalSource("http://example.com/module"))
        .flags(List.of(
            flag().namespace(NS).name("flag1")))
        .modelInstances(List.of(
            field().namespace(NS).name("field1")))
        .toDefinition();

    // Setup the value calls
    StaticContext staticContext = StaticContext.instance();
    IDMAssemblyNodeItem parentItem = IDMAssemblyNodeItem.newInstance(assembly, staticContext);
    assembly.getFlagInstances()
        .forEach(flag -> parentItem.newFlag(flag, IStringItem.valueOf(flag.getName() + " value")));
    assembly.getFieldInstances()
        .forEach(field -> {
          parentItem.newField(field, IStringItem.valueOf(field.getName() + " value"));
        });

    Collection<? extends IFlagNodeItem> flagItems = parentItem.getFlags();
    Collection<? extends IModelNodeItem<?, ?>> modelItems = parentItem.modelItems()
        .collect(Collectors.toUnmodifiableList());
    assertAll(
        () -> assertThat(flagItems).extracting(IFlagNodeItem::getQName)
            .isEqualTo(List.of(IEnhancedQName.of(NS, "flag1"))),
        () -> assertThat(flagItems).extracting(flag -> flag.toAtomicItem().asString())
            .isEqualTo(List.of("flag1 value")),
        () -> assertThat(modelItems).extracting(IModelNodeItem::getQName)
            .isEqualTo(List.of(IEnhancedQName.of(NS, "field1"))),
        () -> assertThat(modelItems).extracting(field -> field.toAtomicItem().asString())
            .isEqualTo(List.of("field1 value")));
  }
}
