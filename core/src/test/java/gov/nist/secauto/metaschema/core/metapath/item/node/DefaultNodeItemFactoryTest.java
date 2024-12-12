
package gov.nist.secauto.metaschema.core.metapath.item.node;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.MockedModelTestSupport;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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
        () -> assertThat(flagItems, containsInAnyOrder(
            allOf(
                match("name", IFlagNodeItem::getQName, equalTo(IEnhancedQName.of(NS, "flag1"))),
                match("value", node -> node.toAtomicItem().asString(), equalTo("flag1 value"))))),
        () -> assertThat(modelItems, containsInAnyOrder(
            allOf(
                match("name", IModelNodeItem::getQName,
                    equalTo(IEnhancedQName.of(NS, "field1"))),
                match("value", node -> node.toAtomicItem().asString(), equalTo("field1 value"))))));
  }

  private static <T, R> FeatureMatcher<T, R> match(
      @NonNull String label,
      @NonNull Function<T, R> lambda,
      Matcher<R> matcher) {
    return new FeatureMatcher<>(matcher, label, label) {
      @Override
      protected R featureValueOf(T actual) {
        return lambda.apply(actual);
      }
    };
  }
}
