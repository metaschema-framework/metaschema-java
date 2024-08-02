/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.model.IChoiceInstance;
import gov.nist.secauto.metaschema.core.model.IDefinition;

public class ChoiceNotInlineStrategy implements IInlineStrategy {
  @Override
  public boolean isInline(
      IDefinition definition,
      ModuleIndex metaschemaIndex) {
    // allow inline if the definition is inline and not part of definition with a
    // choice
    return definition.isInline() && !(definition.getInlineInstance().getParentContainer() instanceof IChoiceInstance);
  }
}
