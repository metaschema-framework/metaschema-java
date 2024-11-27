
package gov.nist.secauto.metaschema.core.metapath.cst;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.api.Test;

class AnonymousFunctionCallTest {
  private static final String NS = "http://example.com/ns";

  @Test
  void test() {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.bindVariableValue(IEnhancedQName.of(NS, "var1"), ISequence.of(string("fn:empty")));

    String metapath = "let $function := function($str) as meta:string { fn:concat('extra ',$str) } "
        + "return $function('cool')";

    assertEquals(
        "extra cool",
        MetapathExpression.compile(metapath, staticContext).evaluateAs(
            null,
            MetapathExpression.ResultType.STRING,
            dynamicContext));
  }

}
