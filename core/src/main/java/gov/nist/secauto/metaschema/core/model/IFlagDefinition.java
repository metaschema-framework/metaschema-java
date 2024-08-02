/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public interface IFlagDefinition extends IValuedDefinition, IFlag {
  @Override
  IFlagInstance getInlineInstance();
}
