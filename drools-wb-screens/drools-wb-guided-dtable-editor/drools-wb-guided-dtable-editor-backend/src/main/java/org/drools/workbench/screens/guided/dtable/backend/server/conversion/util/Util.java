/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.backend.server.conversion.util;

import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;

public class Util {

    public static boolean hasContent(final String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean canSplit(final BRLActionColumn brlColumn) {

        for (IAction iAction : brlColumn.getDefinition()) {
            if (iAction instanceof ActionFieldList) {
                for (ActionFieldValue fieldValue : ((ActionFieldList) iAction).getFieldValues()) {
                    if (fieldValue.getNature() != FieldNatureType.TYPE_TEMPLATE) {
                        return false;
                    }
                }
            } else if (!(iAction instanceof ActionFieldList)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canSplit(final BRLConditionColumn brlColumn) {
        for (IPattern iPattern : brlColumn.getDefinition()) {
            if (iPattern instanceof FactPattern) {

                for (FieldConstraint constraint : ((FactPattern) iPattern).getConstraintList().getConstraints()) {
                    if (constraint instanceof SingleFieldConstraint) {
                        final SingleFieldConstraint fieldConstraint = (SingleFieldConstraint) constraint;
                        if (!fieldConstraint.getExpressionValue().isEmpty()) {
                            return false;
                        }
                        if (fieldConstraint.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_TEMPLATE) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
