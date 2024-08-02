/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.test;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;

import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD")
@MetaschemaAssembly(name = "only-model", moduleClass = TestMetaschema.class)
public class OnlyModelBoundAssembly implements IBoundObject { // NOPMD - intentional
  private final IMetaschemaData metaschemaData;

  /*
   * ================ = simple field = ================
   */
  /**
   * An optional singleton simple field.
   */
  @BoundField(useName = "simple-singleton-field")
  private String simpleSingletonField;

  /**
   * A required singleton simple field.
   */
  @BoundField(useName = "simple-required-singleton-field",
      minOccurs = 1)
  private String simpleRequiredSingletonField;

  /**
   * An optional array field.
   */
  @BoundField(useName = "simple-array-field",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "simple-array-field-items",
          inJson = JsonGroupAsBehavior.LIST))
  private List<String> simpleArrayField;

  /**
   * An required array field.
   */
  @BoundField(useName = "simple-required-array-field",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "simple-required-array-field-items",
          inJson = JsonGroupAsBehavior.LIST))
  private List<String> simpleRequiredArrayField;

  /**
   * An optional singleton or array field.
   */
  @BoundField(useName = "simple-singleton-or-array-field",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "simple-singleton-or-array-field-items",
          inJson = JsonGroupAsBehavior.SINGLETON_OR_LIST))
  private List<String> simpleSingletonOrArrayField;

  /*
   * ================= = flagged field = =================
   */
  /**
   * An optional singleton flagged field.
   */
  @BoundField(useName = "flagged-singleton-field")
  private FlaggedBoundField flaggedSingletonField;

  /**
   * A required singleton flagged field.
   */
  @BoundField(useName = "flagged-required-singleton-field",
      minOccurs = 1)
  private FlaggedBoundField flaggedRequiredSingletonField;

  /**
   * An optional array flagged field.
   */
  @BoundField(useName = "flagged-array-field",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "flagged-array-field-items",
          inJson = JsonGroupAsBehavior.LIST))
  private List<FlaggedBoundField> flaggedArrayField;

  /**
   * An required array flagged field.
   */
  @BoundField(useName = "flagged-required-array-field",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "flagged-required-array-field-items",
          inJson = JsonGroupAsBehavior.LIST))
  private List<FlaggedBoundField> flaggedRequiredArrayField;

  /**
   * An optional singleton or array flagged field.
   */
  @BoundField(useName = "flagged-singleton-or-array-field",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "flagged-singleton-or-array-field-items",
          inJson = JsonGroupAsBehavior.SINGLETON_OR_LIST))
  private List<FlaggedBoundField> flaggedSingletonOrArrayField;

  /*
   * ============== = assemblies = ==============
   */
  /**
   * An optional singleton assembly.
   */
  @BoundAssembly(useName = "singleton-assembly")
  private EmptyBoundAssembly singletonAssembly;

  /**
   * An optional array assembly.
   */
  @BoundAssembly(useName = "array-assembly",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "array-assembly-items",
          inJson = JsonGroupAsBehavior.LIST))
  private List<OnlyModelBoundAssembly> arrayAssembly;

  /**
   * An optional singleton or array assembly.
   */
  @BoundAssembly(useName = "singleton-or-array-assembly",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "singleton-or-array-assembly-items",
          inJson = JsonGroupAsBehavior.SINGLETON_OR_LIST))
  private List<OnlyModelBoundAssembly> singletonOrArrayAssembly;
  /**
   * An optional keyed assembly.
   */
  @BoundAssembly(useName = "keyed-assembly",
      maxOccurs = -1,
      groupAs = @GroupAs(name = "keyed-assembly-items",
          inJson = JsonGroupAsBehavior.KEYED))
  private Map<String, FlaggedBoundAssembly> keyedAssembly;

  public OnlyModelBoundAssembly() {
    this(null);
  }

  public OnlyModelBoundAssembly(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }
}
