/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.test;

import gov.nist.secauto.metaschema.core.datatype.adapter.UuidAdapter;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundAssembly;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundField;
import gov.nist.secauto.metaschema.databind.model.annotations.BoundFlag;
import gov.nist.secauto.metaschema.databind.model.annotations.GroupAs;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaAssembly;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@MetaschemaAssembly(name = "root", rootName = "root", moduleClass = TestMetaschema.class)
public class RootBoundAssembly implements IBoundObject {
  private final IMetaschemaData metaschemaData;

  @BoundFlag(name = "uuid", defaultValue = "374dd648-b247-483c-afd8-a66ba8876070", typeAdapter = UuidAdapter.class)
  private UUID uuid; // NOPMD - intentional

  /**
   * An optional singleton simple field.
   */
  @BoundField(useName = "simple-singleton-field")
  private String simpleSingletonField; // NOPMD - intentional

  /**
   * A required keyed assembly.
   */
  @BoundField(useName = "keyed-field",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "keyed-field-items",
          inJson = JsonGroupAsBehavior.KEYED))
  private Map<String, FlaggedBoundField> keyedField; // NOPMD - intentional

  /**
   * A required singleton or array assembly.
   */
  @BoundAssembly(useName = "singleton-or-array-assembly",
      minOccurs = 1,
      maxOccurs = -1,
      groupAs = @GroupAs(name = "singleton-or-array-assembly-items",
          inJson = JsonGroupAsBehavior.SINGLETON_OR_LIST))
  private List<OnlyModelBoundAssembly> singletonOrArrayAssembly; // NOPMD - intentional

  public RootBoundAssembly() {
    this(null);
  }

  public RootBoundAssembly(IMetaschemaData metaschemaData) {
    this.metaschemaData = metaschemaData;
  }

  @Override
  public IMetaschemaData getMetaschemaData() {
    return metaschemaData;
  }
}
