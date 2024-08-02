/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriAdapter;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

class AnyUriItemImpl
    extends AbstractUriItem {

  public AnyUriItemImpl(@NonNull URI value) {
    super(value);
  }

  @Override
  public UriAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.URI;
  }
}
