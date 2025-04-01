
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

  @Test
  void testSameResultCurrentDateTime() {
    StaticContext staticContext = StaticContext.builder()
        .build();

    IFunction currentDateTimeFunction = staticContext.lookupFunction("current-dateTime", 0);

    DynamicContext dynamicContext = new DynamicContext(staticContext);

    ISequence<?> result1 = currentDateTimeFunction.execute(
        CollectionUtil.emptyList(),
        dynamicContext,
        ISequence.empty());
    ISequence<?> result2 = currentDateTimeFunction.execute(
        CollectionUtil.emptyList(),
        dynamicContext,
        ISequence.empty());

    assertNotNull(currentDateTimeFunction);
    assertSame(result1, result2);
  }

  @Test
  void testSameResultBoolean() {
    StaticContext staticContext = StaticContext.builder()
        .build();

    IFunction currentDateTimeFunction = staticContext.lookupFunction("boolean", 1);

    DynamicContext dynamicContext = new DynamicContext(staticContext);

    ISequence<?> result1 = currentDateTimeFunction.execute(
        CollectionUtil.singletonList(ISequence.of(IBooleanItem.valueOf(true))),
        dynamicContext,
        ISequence.empty());
    ISequence<?> result2 = currentDateTimeFunction.execute(
        CollectionUtil.singletonList(ISequence.of(IBooleanItem.valueOf(true))),
        dynamicContext,
        ISequence.empty());

    assertNotNull(currentDateTimeFunction);
    assertSame(result1, result2);
  }
}
