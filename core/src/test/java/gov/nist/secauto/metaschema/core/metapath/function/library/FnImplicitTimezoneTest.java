
package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

class FnImplicitTimezoneTest {

  @Test
  void test() {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime utc = now.withZoneSameInstant(ZoneId.of("UTC"));

    DynamicContext context = new DynamicContext();

    assertEquals(
        context.getCurrentDateTime().getOffset().get(ChronoField.OFFSET_SECONDS),
        FnImplicitTimezone.fnImplicitTimezone(context).asDuration().get(ChronoUnit.SECONDS),
        "The offset in seconds must be equal.");
  }

}
