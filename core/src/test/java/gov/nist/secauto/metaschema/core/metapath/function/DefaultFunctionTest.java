
package gov.nist.secauto.metaschema.core.metapath.function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;

class DefaultFunctionTest {

  /**
   * Ensure that the same call to the fn:boolean function produces the exact same
   * result instances.
   */
  @Test
  void testSameResultCurrentDateTime() {
    StaticContext staticContext = StaticContext.builder()
        .build();

    IFunction function = staticContext.lookupFunction("current-dateTime", 0);
    assertNotNull(function);

    DynamicContext dynamicContext = new DynamicContext(staticContext);

    ISequence<?> result1 = function.execute(
        CollectionUtil.emptyList(),
        dynamicContext,
        ISequence.empty());
    ISequence<?> result2 = function.execute(
        CollectionUtil.emptyList(),
        dynamicContext,
        ISequence.empty());

    assertSame(result1, result2);
  }

  /**
   * Ensure that the same call to the fn:boolean function with the same argument
   * produces the exact same result instances.
   */
  @Test
  void testSameResultBoolean() {
    StaticContext staticContext = StaticContext.builder()
        .build();

    IFunction function = staticContext.lookupFunction("boolean", 1);
    assertNotNull(function);

    DynamicContext dynamicContext = new DynamicContext(staticContext);

    ISequence<?> result1 = function.execute(
        CollectionUtil.singletonList(ISequence.of(IBooleanItem.valueOf(true))),
        dynamicContext,
        ISequence.empty());
    ISequence<?> result2 = function.execute(
        CollectionUtil.singletonList(ISequence.of(IBooleanItem.valueOf(true))),
        dynamicContext,
        ISequence.empty());

    assertSame(result1, result2);
  }
}
