/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

/**
 * Tests for {@link TimingCollector} and {@link TimingRecord}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
class TimingCollectorTest {

  @Test
  void testPhaseTimingAccumulation() {
    TimingCollector collector = new TimingCollector();

    collector.beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
    // simulate some work
    collector.afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);

    TimingRecord record = collector.getPhaseTiming(ValidationPhase.CONSTRAINT_VALIDATION);
    assertNotNull(record, "phase timing record should exist");
    assertEquals(1, record.getCount());
    assertTrue(record.getTotalTimeNs() >= 0);
    assertNotNull(record.getStartTimestampUtc());
    assertNotNull(record.getEndTimestampUtc());
  }

  @Test
  void testConstraintTimingAccumulation() {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .identifier("test-constraint")
        .test(IMetapathExpression.compile("true()"))
        .build();

    INodeItem target = mock(INodeItem.class);

    collector.beforeConstraintEvaluation(constraint, target);
    collector.afterConstraintEvaluation(constraint, target);

    TimingRecord record = collector.getConstraintTiming("test-constraint");
    assertNotNull(record, "constraint timing record should exist");
    assertEquals(1, record.getCount());
    assertTrue(record.getTotalTimeNs() >= 0);
  }

  @Test
  void testConstraintTimingMultipleInvocations() {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .identifier("multi-constraint")
        .test(IMetapathExpression.compile("true()"))
        .build();

    INodeItem target = mock(INodeItem.class);

    for (int i = 0; i < 5; i++) {
      collector.beforeConstraintEvaluation(constraint, target);
      collector.afterConstraintEvaluation(constraint, target);
    }

    TimingRecord record = collector.getConstraintTiming("multi-constraint");
    assertNotNull(record);
    assertEquals(5, record.getCount());
    assertTrue(record.getMinTimeNs() <= record.getMaxTimeNs());
  }

  @Test
  void testLetTimingAccumulation() {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");
    IEnhancedQName name = IEnhancedQName.of("my-var");

    ILet let = ILet.of(
        name,
        IMetapathExpression.compile("count(//item)"),
        source,
        null);

    collector.beforeLetEvaluation(let);
    collector.afterLetEvaluation(let);

    TimingRecord record = collector.getLetTiming(let);
    assertNotNull(record, "let timing record should exist");
    assertEquals(1, record.getCount());
  }

  @Test
  void testOverallValidationTiming() {
    TimingCollector collector = new TimingCollector();
    URI document = URI.create("https://example.com/document.xml");

    collector.beforeValidation(document);
    collector.afterValidation(document);

    TimingRecord record = collector.getValidationTiming();
    assertNotNull(record, "validation timing should exist");
    assertEquals(1, record.getCount());
    assertNotNull(record.getStartTimestampUtc());
    assertNotNull(record.getEndTimestampUtc());
  }

  @Test
  void testNoTimingWhenNotRecorded() {
    TimingCollector collector = new TimingCollector();

    ILet unrecordedLet = ILet.of(
        IEnhancedQName.of("unrecorded"),
        IMetapathExpression.compile("1"),
        ISource.externalSource("https://example.com/test"),
        null);

    assertNull(collector.getPhaseTiming(ValidationPhase.SCHEMA_VALIDATION));
    assertNull(collector.getConstraintTiming("nonexistent"));
    assertNull(collector.getLetTiming(unrecordedLet));
    assertNull(collector.getValidationTiming());
  }

  @Test
  void testMinMaxTracking() {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .identifier("minmax-constraint")
        .test(IMetapathExpression.compile("true()"))
        .build();

    INodeItem target = mock(INodeItem.class);

    // Run multiple times to get varying durations
    for (int i = 0; i < 10; i++) {
      collector.beforeConstraintEvaluation(constraint, target);
      // No actual delay needed; min/max will reflect real nanoTime deltas
      collector.afterConstraintEvaluation(constraint, target);
    }

    TimingRecord record = collector.getConstraintTiming("minmax-constraint");
    assertNotNull(record);
    assertEquals(10, record.getCount());
    assertTrue(record.getMinTimeNs() >= 0, "minTimeNs should be non-negative");
    assertTrue(record.getMaxTimeNs() >= record.getMinTimeNs(),
        "maxTimeNs should be >= minTimeNs");
    assertTrue(record.getTotalTimeNs() >= record.getMinTimeNs(),
        "totalTimeNs should be >= minTimeNs");
  }

  @Test
  void testThreadSafety() throws Exception {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");

    int threadCount = 8;
    int iterationsPerThread = 100;
    ExecutorService executor = ObjectUtils.notNull(Executors.newFixedThreadPool(threadCount));
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    try {
      for (int t = 0; t < threadCount; t++) {
        final int threadId = t;
        executor.submit(() -> {
          try {
            startLatch.await();
            // Each thread uses its own constraint but they share the collector
            IExpectConstraint constraint = IExpectConstraint.builder()
                .source(source)
                .identifier("thread-constraint-" + threadId)
                .test(IMetapathExpression.compile("true()"))
                .build();

            INodeItem target = mock(INodeItem.class);

            for (int i = 0; i < iterationsPerThread; i++) {
              collector.beforeConstraintEvaluation(constraint, target);
              collector.afterConstraintEvaluation(constraint, target);
            }
          } catch (Exception e) {
            // fail silently; assertions below will catch issues
          } finally {
            doneLatch.countDown();
          }
        });
      }

      startLatch.countDown();
      assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "threads should complete");

      // Verify each thread's constraint timing is correct
      for (int t = 0; t < threadCount; t++) {
        TimingRecord record = collector.getConstraintTiming("thread-constraint-" + t);
        assertNotNull(record, "timing for thread " + t + " should exist");
        assertEquals(iterationsPerThread, record.getCount(),
            "thread " + t + " should have correct count");
      }
    } finally {
      executor.shutdown();
    }
  }

  @Test
  void testNestedConstraintAndLetEvents() {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");

    IExpectConstraint constraint = IExpectConstraint.builder()
        .source(source)
        .identifier("outer-constraint")
        .test(IMetapathExpression.compile("true()"))
        .build();

    ILet let = ILet.of(
        IEnhancedQName.of("nested-var"),
        IMetapathExpression.compile("count(//item)"),
        source,
        null);

    INodeItem target = mock(INodeItem.class);

    // Simulate constraint evaluation that triggers let evaluation
    collector.beforeConstraintEvaluation(constraint, target);
    collector.beforeLetEvaluation(let);
    collector.afterLetEvaluation(let);
    collector.afterConstraintEvaluation(constraint, target);

    // Both should be recorded correctly
    TimingRecord constraintRecord = collector.getConstraintTiming("outer-constraint");
    assertNotNull(constraintRecord);
    assertEquals(1, constraintRecord.getCount());

    TimingRecord letRecord = collector.getLetTiming(let);
    assertNotNull(letRecord);
    assertEquals(1, letRecord.getCount());

    // Constraint time should be >= let time (it contains the let)
    assertTrue(constraintRecord.getTotalTimeNs() >= letRecord.getTotalTimeNs(),
        "constraint time should include let time");
  }

  @Test
  void testGetAllConstraintTimings() {
    TimingCollector collector = new TimingCollector();
    ISource source = ISource.externalSource("https://example.com/module");
    INodeItem target = mock(INodeItem.class);

    IExpectConstraint c1 = IExpectConstraint.builder()
        .source(source)
        .identifier("c1")
        .test(IMetapathExpression.compile("true()"))
        .build();
    IExpectConstraint c2 = IExpectConstraint.builder()
        .source(source)
        .identifier("c2")
        .test(IMetapathExpression.compile("true()"))
        .build();

    collector.beforeConstraintEvaluation(c1, target);
    collector.afterConstraintEvaluation(c1, target);
    collector.beforeConstraintEvaluation(c2, target);
    collector.afterConstraintEvaluation(c2, target);

    Map<String, TimingRecord> all = collector.getConstraintTimings();
    assertEquals(2, all.size());
    assertTrue(all.containsKey("c1"));
    assertTrue(all.containsKey("c2"));
  }
}
