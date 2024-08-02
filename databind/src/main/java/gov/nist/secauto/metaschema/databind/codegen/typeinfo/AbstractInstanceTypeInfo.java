/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IInstance;
import gov.nist.secauto.metaschema.databind.codegen.typeinfo.def.IDefinitionTypeInfo;

import edu.umd.cs.findbugs.annotations.NonNull;

abstract class AbstractInstanceTypeInfo<INSTANCE extends IInstance, PARENT extends IDefinitionTypeInfo>
    extends AbstractPropertyTypeInfo<PARENT>
    implements IInstanceTypeInfo {
  @NonNull
  private final INSTANCE instance;

  protected AbstractInstanceTypeInfo(@NonNull INSTANCE instance, @NonNull PARENT parentDefinition) {
    super(parentDefinition);
    this.instance = instance;
  }

  @Override
  public INSTANCE getInstance() {
    return instance;
  }

}
