/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IMetaschemaData;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.annotations.BoundChoice;
import dev.metaschema.databind.model.annotations.BoundField;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import dev.metaschema.databind.testing.model.TestModule;

/**
 * Tests for the {@link BoundChoice} annotation and
 * {@link BoundInstanceModelChoice} class.
 */
class BoundChoiceTest {

  /**
   * Test assembly with valid adjacent choice fields.
   */
  @MetaschemaAssembly(
      name = "valid-choice-assembly",
      rootName = "valid-choice-assembly",
      moduleClass = TestModule.class)
  public static class ValidChoiceAssembly implements IBoundObject {
    private IMetaschemaData metaschemaData;

    @BoundField(useName = "option-a")
    @BoundChoice(choiceId = "choice-1")
    private String optionA;

    @BoundField(useName = "option-b")
    @BoundChoice(choiceId = "choice-1")
    private String optionB;

    @BoundField(useName = "other-field")
    private String otherField;

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }

    public String getOptionA() {
      return optionA;
    }

    public void setOptionA(String optionA) {
      this.optionA = optionA;
    }

    public String getOptionB() {
      return optionB;
    }

    public void setOptionB(String optionB) {
      this.optionB = optionB;
    }

    public String getOtherField() {
      return otherField;
    }

    public void setOtherField(String otherField) {
      this.otherField = otherField;
    }
  }

  /**
   * Test assembly with non-adjacent choice fields (invalid).
   */
  @MetaschemaAssembly(
      name = "invalid-choice-assembly",
      rootName = "invalid-choice-assembly",
      moduleClass = TestModule.class)
  public static class InvalidChoiceAssembly implements IBoundObject {
    private IMetaschemaData metaschemaData;

    @BoundField(useName = "option-a")
    @BoundChoice(choiceId = "choice-1")
    private String optionA;

    @BoundField(useName = "interrupting-field")
    private String interruptingField;

    @BoundField(useName = "option-b")
    @BoundChoice(choiceId = "choice-1")
    private String optionB;

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }
  }

  /**
   * Test assembly with multiple choice groups.
   */
  @MetaschemaAssembly(
      name = "multi-choice-assembly",
      rootName = "multi-choice-assembly",
      moduleClass = TestModule.class)
  public static class MultiChoiceAssembly implements IBoundObject {
    private IMetaschemaData metaschemaData;

    @BoundField(useName = "choice1-a")
    @BoundChoice(choiceId = "choice-1")
    private String choice1A;

    @BoundField(useName = "choice1-b")
    @BoundChoice(choiceId = "choice-1")
    private String choice1B;

    @BoundField(useName = "choice2-a")
    @BoundChoice(choiceId = "choice-2")
    private String choice2A;

    @BoundField(useName = "choice2-b")
    @BoundChoice(choiceId = "choice-2")
    private String choice2B;

    @Override
    public IMetaschemaData getMetaschemaData() {
      return metaschemaData;
    }
  }

  @Test
  void testValidChoiceInstancesCreated() {
    IBindingContext context = IBindingContext.newInstance();
    IBoundDefinitionModelAssembly definition = (IBoundDefinitionModelAssembly) context
        .getBoundDefinitionForClass(ValidChoiceAssembly.class);

    assertNotNull(definition, "Definition should not be null");

    List<IChoiceInstance> choices = definition.getChoiceInstances();
    assertNotNull(choices, "Choice instances should not be null");
    assertEquals(1, choices.size(), "Should have exactly one choice instance");

    IChoiceInstance choice = choices.get(0);
    assertNotNull(choice, "Choice instance should not be null");
    assertEquals(2, choice.getNamedModelInstances().size(),
        "Choice should have 2 alternatives");
  }

  @Test
  void testInvalidNonAdjacentChoiceThrowsException() {
    IBindingContext context = IBindingContext.newInstance();

    // Attempting to get definition for class with non-adjacent choice fields
    // should throw IllegalStateException when model is accessed
    IBoundDefinitionModelAssembly definition = (IBoundDefinitionModelAssembly) context
        .getBoundDefinitionForClass(InvalidChoiceAssembly.class);
    assertNotNull(definition, "Definition should not be null before accessing model");

    assertThrows(IllegalStateException.class, () -> {
      // Force model initialization by accessing the choice instances
      definition.getChoiceInstances();
    }, "Should throw exception for non-adjacent choice fields");
  }

  @Test
  void testMultipleChoiceGroups() {
    IBindingContext context = IBindingContext.newInstance();
    IBoundDefinitionModelAssembly definition = (IBoundDefinitionModelAssembly) context
        .getBoundDefinitionForClass(MultiChoiceAssembly.class);

    assertNotNull(definition, "Definition should not be null");

    List<IChoiceInstance> choices = definition.getChoiceInstances();
    assertNotNull(choices, "Choice instances should not be null");
    assertEquals(2, choices.size(), "Should have exactly two choice instances");

    // Each choice should have 2 alternatives
    for (IChoiceInstance choice : choices) {
      assertEquals(2, choice.getNamedModelInstances().size(),
          "Each choice should have 2 alternatives");
    }
  }

  @Test
  void testChoiceInstanceProperties() {
    IBindingContext context = IBindingContext.newInstance();
    IBoundDefinitionModelAssembly definition = (IBoundDefinitionModelAssembly) context
        .getBoundDefinitionForClass(ValidChoiceAssembly.class);
    assertNotNull(definition, "Definition should not be null");

    List<IChoiceInstance> choices = definition.getChoiceInstances();
    IChoiceInstance choice = choices.get(0);

    // Verify IChoiceInstance properties
    // Annotation-based bindings default to optional choices (minOccurs = 0)
    assertEquals(0, choice.getMinOccurs(), "minOccurs should be 0 for optional choice");
    assertEquals(1, choice.getMaxOccurs(), "maxOccurs should be 1");
    assertNotNull(choice.getContainingDefinition(), "Containing definition should not be null");
    assertEquals(definition, choice.getContainingDefinition(),
        "Containing definition should be the parent assembly");

    // Verify the choice contains the expected fields
    assertTrue(choice.getFieldInstances().size() > 0 || choice.getAssemblyInstances().size() > 0,
        "Choice should contain field or assembly instances");
  }

  @Test
  void testBoundInstanceModelChoiceId() {
    IBindingContext context = IBindingContext.newInstance();
    IBoundDefinitionModelAssembly definition = (IBoundDefinitionModelAssembly) context
        .getBoundDefinitionForClass(ValidChoiceAssembly.class);
    assertNotNull(definition, "Definition should not be null");

    List<IChoiceInstance> choices = definition.getChoiceInstances();
    BoundInstanceModelChoice choice = (BoundInstanceModelChoice) choices.get(0);

    assertEquals("choice-1", choice.getChoiceId(), "Choice ID should match annotation");
  }
}
