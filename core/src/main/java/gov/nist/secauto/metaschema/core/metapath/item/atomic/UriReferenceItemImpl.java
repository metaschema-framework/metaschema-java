/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.UriReferenceAdapter;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

class UriReferenceItemImpl
    extends AbstractUriItem
    implements IUriReferenceItem {

  public UriReferenceItemImpl(@NonNull URI value) {
    super(value);
  }

  @Override
  public UriReferenceAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.URI_REFERENCE;
  }
}
