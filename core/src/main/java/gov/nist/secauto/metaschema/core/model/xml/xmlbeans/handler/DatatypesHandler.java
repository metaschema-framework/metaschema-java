/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.datatype.DataTypeService;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.xmlbeans.SimpleValue;

// FIXME: remove this handler to ensure that prefixes can be handled
public final class DatatypesHandler {
  private DatatypesHandler() {
    // disable construction
  }

  /**
   * Given an XMLBeans value, return the {@link IDataTypeAdapter} instance with
   * the given name, as determined by matching
   * {@link IDataTypeAdapter#getNames()}.
   *
   * @param value
   *          the name of the data type
   * @return the data type instance
   */
  public static IDataTypeAdapter<?> decodeFieldDatatypesType(SimpleValue value) {
    return decode(value);
  }

  /**
   * Given a data type instance, set the name of the data type, as determined by
   * matching {@link IDataTypeAdapter#getNames()}, in the provided target XMLBeans
   * value.
   *
   * @param datatype
   *          the data type instance
   * @param target
   *          XMLBeans value to apply the name to
   */
  public static void encodeFieldDatatypesType(IDataTypeAdapter<?> datatype, SimpleValue target) {
    encode(datatype, target);
  }

  /**
   * Given an XMLBeans value, return the {@link IDataTypeAdapter} instance with
   * the given name, as determined by matching
   * {@link IDataTypeAdapter#getNames()}.
   *
   * @param value
   *          the name of the data type
   * @return the data type instance
   */
  public static IDataTypeAdapter<?> decodeSimpleDatatypesType(SimpleValue value) {
    return decode(value);
  }

  /**
   * Given a data type instance, set the name of the data type, as determined by
   * matching {@link IDataTypeAdapter#getNames()}, in the provided target XMLBeans
   * value.
   *
   * @param datatype
   *          the data type instance
   * @param target
   *          XMLBeans value to apply the name to
   */
  public static void encodeSimpleDatatypesType(IDataTypeAdapter<?> datatype, SimpleValue target) {
    encode(datatype, target);
  }

  private static IDataTypeAdapter<?> decode(SimpleValue target) {
    String name = ObjectUtils.requireNonNull(target.getStringValue());
    IAtomicOrUnionType<?> type = DataTypeService.instance().getAtomicTypeByQNameIndex(
        IEnhancedQName.of(MetapathConstants.NS_METAPATH, name).getIndexPosition());
    if (type == null) {
      throw new IllegalStateException("Unrecognized data type: " + name);
    }
    IDataTypeAdapter<?> retval = type.getAdapter();
    if (retval == null) {
      throw new IllegalStateException("No data type adpter found for name: " + name);
    }
    return retval;
  }

  private static void encode(IDataTypeAdapter<?> datatype, SimpleValue target) {
    if (datatype != null) {
      target.setStringValue(datatype.getItemType().getQName().getLocalName());
    }
  }

}
