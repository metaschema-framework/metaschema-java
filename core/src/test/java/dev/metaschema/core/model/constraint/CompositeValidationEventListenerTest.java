/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.model.ISource;
import dev.metaschema.core.qname.IEnhancedQName;

@SuppressWarnings("PMD.TooManyStaticImports")
class CompositeValidationEventListenerTest {

  @Test
  void testDelegatesToAllListeners() {
    ValidationEventListener listener1 = mock(ValidationEventListener.class);
    ValidationEventListener listener2 = mock(ValidationEventListener.class);

    CompositeValidationEventListener composite
        = new CompositeValidationEventListener(List.of(listener1, listener2));

    URI doc = URI.create("https://example.com/doc.xml");
    INodeItem target = mock(INodeItem.class);
    IConstraint constraint = mock(IConstraint.class);
    ILet let = ILet.of(
        IEnhancedQName.of("test-var"),
        IMetapathExpression.compile("1"),
        ISource.externalSource("https://example.com/module"),
        null);

    // Fire all event types
    composite.beforeValidation(doc);
    composite.afterValidation(doc);
    composite.beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
    composite.afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);
    composite.beforeConstraintEvaluation(constraint, target);
    composite.afterConstraintEvaluation(constraint, target);
    composite.beforeLetEvaluation(let);
    composite.afterLetEvaluation(let);

    // Verify both listeners received all events
    verify(listener1).beforeValidation(doc);
    verify(listener1).afterValidation(doc);
    verify(listener1).beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
    verify(listener1).afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);
    verify(listener1).beforeConstraintEvaluation(constraint, target);
    verify(listener1).afterConstraintEvaluation(constraint, target);
    verify(listener1).beforeLetEvaluation(let);
    verify(listener1).afterLetEvaluation(let);

    verify(listener2).beforeValidation(doc);
    verify(listener2).afterValidation(doc);
    verify(listener2).beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
    verify(listener2).afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);
    verify(listener2).beforeConstraintEvaluation(constraint, target);
    verify(listener2).afterConstraintEvaluation(constraint, target);
    verify(listener2).beforeLetEvaluation(let);
    verify(listener2).afterLetEvaluation(let);
  }

  @Test
  void testEmptyListenerList() {
    CompositeValidationEventListener composite
        = new CompositeValidationEventListener(Collections.emptyList());

    URI doc = URI.create("https://example.com/doc.xml");
    INodeItem target = mock(INodeItem.class);
    IConstraint constraint = mock(IConstraint.class);

    // Should not throw with empty listener list
    assertDoesNotThrow(() -> {
      composite.beforeValidation(doc);
      composite.afterValidation(doc);
      composite.beforePhase(ValidationPhase.CONSTRAINT_VALIDATION);
      composite.afterPhase(ValidationPhase.CONSTRAINT_VALIDATION);
      composite.beforeConstraintEvaluation(constraint, target);
      composite.afterConstraintEvaluation(constraint, target);
    });
  }

  @Test
  void testSingleListener() {
    ValidationEventListener listener = mock(ValidationEventListener.class);

    CompositeValidationEventListener composite
        = new CompositeValidationEventListener(List.of(listener));

    URI doc = URI.create("https://example.com/doc.xml");
    composite.beforeValidation(doc);

    verify(listener).beforeValidation(doc);
  }
}
