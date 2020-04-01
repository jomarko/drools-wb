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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Row;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

import static org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.Util.hasContent;

public class BRLColumnDataBuilderDefault
        implements BRLColumnDataBuilder {

    private final DataBuilder.DataRowBuilder dataRowBuilder;

    public BRLColumnDataBuilderDefault(final DataBuilder.DataRowBuilder dataRowBuilder) {
        this.dataRowBuilder = dataRowBuilder;
    }

    @Override
    public void build(final BRLActionColumn brlColumn,
                      final List<DTCellValue52> row,
                      final Row xlsRow) {

        add(row, xlsRow, brlColumn, false);
    }

    @Override
    public void build(final BRLConditionColumn brlColumn,
                      final List<DTCellValue52> row,
                      final Row xlsRow) {

        add(row, xlsRow, brlColumn, true);
    }

    private void add(final List<DTCellValue52> row,
                     final Row xlsRow,
                     final BRLColumn brlColumn,
                     final boolean trimQuotesOff) {
        final boolean missingVariable = isMissingVariable(brlColumn);
        final int amountOfChildColumns = brlColumn.getChildColumns().size();

        final StringBuilder result = new StringBuilder();
        boolean hasContent = false;
        for (int i = 0; i < amountOfChildColumns; i++) {

            final String value = dataRowBuilder.getValue(row,
                                                         dataRowBuilder.getSourceColumnIndex());
            if (value != null && missingVariable) {
                if (Objects.equals("true", value.toLowerCase())) {
                    result.append("X");
                    hasContent = true;
                }
                break;
            } else if (hasContent(value)) {
                result.append(trimQuotesOff(trimQuotesOff, value));
                hasContent = true;
            }
            if (i < amountOfChildColumns - 1) {
                result.append(", ");
                dataRowBuilder.upSourceColumnIndex();
            }
        }

        if (hasContent) {
            xlsRow.createCell(dataRowBuilder.getTargetColumnIndex())
                    .setCellValue(result.toString());
        }
    }

    private String trimQuotesOff(final boolean trimQuotesOff,
                                 final String value) {
        if (trimQuotesOff && isSurroundedByQuotes(value)) {
            return value.substring(1, value.length() - 1);
        } else {
            return value;
        }
    }

    private boolean isSurroundedByQuotes(final String value) {
        return hasContent(value) && value.length() >= 2
                && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"';
    }

    private boolean isMissingVariable(final BRLColumn brlColumn) {
        for (final Object childColumn : brlColumn.getChildColumns()) {
            if (!getVariableName(childColumn).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getVariableName(final Object childColumn) {
        if (childColumn instanceof BRLActionVariableColumn) {
            return ((BRLActionVariableColumn) childColumn).getVarName();
        } else if (childColumn instanceof BRLConditionVariableColumn) {
            return ((BRLConditionVariableColumn) childColumn).getVarName();
        } else {
            return "";
        }
    }
}
