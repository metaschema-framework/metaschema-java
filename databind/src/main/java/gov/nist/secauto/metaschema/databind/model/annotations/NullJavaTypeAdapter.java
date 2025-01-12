/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.databind.model.annotations.NullJavaTypeAdapter.VoidItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Used to mark a Java type that has no configured adapter.
 */
public final class NullJavaTypeAdapter
    extends AbstractDataTypeAdapter<Void, VoidItem> {

  private static final String NOT_VALID = "not a valid type";

  /**
   * Construct a new adapter.
   */
  @SuppressWarnings("synthetic-access")
  public NullJavaTypeAdapter() {
    super(Void.class, VoidItem.class, VoidItem::cast);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.NULL;
  }

  @Override
  public Void copy(@NonNull Object obj) {
    throw new UnsupportedOperationException(NOT_VALID);
  }

  @Override
  public Void parse(String value) {
    throw new UnsupportedOperationException(NOT_VALID);
  }

  @SuppressWarnings("exports")
  @Override
  public VoidItem newItem(Object value) {
    throw new UnsupportedOperationException(NOT_VALID);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    throw new UnsupportedOperationException(NOT_VALID);
  }

  protected static class VoidItem implements IAnyAtomicItem {

    private static VoidItem cast(@SuppressWarnings("unused") @NonNull IAnyAtomicItem item) {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public Void getValue() {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public @NonNull
    IAnyAtomicItem toAtomicItem() {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public @NonNull
    String asString() {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public IDataTypeAdapter<?> getJavaTypeAdapter() {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public IAnyAtomicItem castAsType(IAnyAtomicItem item) {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public int compareTo(IAnyAtomicItem item) {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public IMapKey asMapKey() {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public IItemType getType() {
      throw new UnsupportedOperationException(NOT_VALID);
    }

    @Override
    public String toSignature() {
      throw new UnsupportedOperationException(NOT_VALID);
    }
  }
}
