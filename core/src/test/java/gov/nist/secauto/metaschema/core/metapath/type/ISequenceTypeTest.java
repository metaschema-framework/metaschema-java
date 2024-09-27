
package gov.nist.secauto.metaschema.core.metapath.type;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ISequenceTypeTest {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            string("a"),
            Map.of(
                "meta:string", true,
                "sequence()", false,
                "item()", true,
                "meta:string?", true)));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testMatch(@NonNull IItem actual, @NonNull Map<String, Boolean> testToExpectedMap) {
    for (Map.Entry<String, Boolean> entry : testToExpectedMap.entrySet()) {
      String test = ". instanceof " + entry.getKey();
      MetapathExpression expression = MetapathExpression.compile(test);
      Boolean result = expression.evaluateAs(actual, MetapathExpression.ResultType.BOOLEAN);

      assertEquals(
          entry.getValue(),
          result,
          String.format("Expected `%s` to evaluate to '%s'",
              test,
              entry.getValue()));
    }
  }
}
