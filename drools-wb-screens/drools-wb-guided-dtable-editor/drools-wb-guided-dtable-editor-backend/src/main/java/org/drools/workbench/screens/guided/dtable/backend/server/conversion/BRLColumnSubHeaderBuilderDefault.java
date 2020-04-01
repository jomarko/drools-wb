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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTDRLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;

import static org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.Util.hasContent;

/**
 * Makes a single column out of the BRL column(s)
 */
public class BRLColumnSubHeaderBuilderDefault
        implements BRLColumnSubHeaderBuilder {

    private SubHeaderBuilder subHeaderBuilder;
    private ColumnContext columnContext;
    private final GuidedDecisionTable52 dtable;
    private final Map<String, String> varListInOrder = new HashMap<>();// original, new one

    public BRLColumnSubHeaderBuilderDefault(final SubHeaderBuilder subHeaderBuilder,
                                            final ColumnContext columnContext,
                                            final GuidedDecisionTable52 dtable) {
        this.subHeaderBuilder = subHeaderBuilder;
        this.columnContext = columnContext;
        this.dtable = dtable;
    }

    @Override
    public void brlActions(final BRLActionColumn brlColumn) {

        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.getAttributeCols().addAll(dtable.getAttributeCols());
        dt.getMetadataCols().addAll(dtable.getMetadataCols());
        dt.getConditions().addAll(dtable.getConditions());
        final ArrayList<DTCellValue52> list = new ArrayList<>();

        for (Object o : brlColumn.getDefinition()) {
            if (o instanceof ActionInsertFact) {
                ActionInsertFact insertFact = (ActionInsertFact) o;
                if (insertFact.getBoundName() == null) {
                    insertFact.setBoundName(getBoundName(insertFact));
                }
            }
        }

        for (int i = 0; i < dt.getExpandedColumns().size(); i++) {
            list.add(dtable.getData().get(0).get(i));
        }

        list.addAll(setUpVarNamesWithTemps(brlColumn.getChildColumns()));
        dt.getData().add(list);

        dt.getActionCols().add(brlColumn);

        subHeaderBuilder.addHeaderAndTitle(SubHeaderBuilder.ACTION,
                                           brlColumn.getHeader());
        subHeaderBuilder.getFieldRow().createCell(subHeaderBuilder.getTargetColumnIndex()).setCellValue(replaceTempVars(brlColumn.getChildColumns(),
                                                                                                                        subString(GuidedDTDRLPersistence.getInstance().marshal(dt), "then", "end")));
    }

    private String getBoundName(final ActionInsertFact insertFact) {
        if (hasContent(insertFact.getBoundName())) {
            return insertFact.getBoundName();
        } else {
            return columnContext.getNextFreeColumnFactName();
        }
    }

    @Override
    public void brlConditions(final BRLConditionColumn brlColumn) {

        for (IPattern iPattern : brlColumn.getDefinition()) {
            if (iPattern instanceof FactPattern) {
                columnContext.addBoundName(((FactPattern) iPattern).getBoundName());
            }
        }

        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.getConditions().add(brlColumn);
        final ArrayList<DTCellValue52> list = new ArrayList<>();
        list.add(new DTCellValue52(1));
        list.add(new DTCellValue52(""));

        list.addAll(setUpVarNamesWithTemps(brlColumn.getChildColumns()));
        dt.getData().add(list);

        subHeaderBuilder.addHeaderAndTitle(SubHeaderBuilder.CONDITION,
                                           brlColumn.getHeader());
        subHeaderBuilder.getFieldRow().createCell(subHeaderBuilder.getTargetColumnIndex()).setCellValue(replaceTempVars(brlColumn.getChildColumns(),
                                                                                                                        subString(GuidedDTDRLPersistence.getInstance().marshal(dt), "when", "then")));
    }

    private List<DTCellValue52> setUpVarNamesWithTemps(final List<? extends BRLVariableColumn> childColumns) {
        final List<DTCellValue52> result = new ArrayList<>();

        for (int i = 0; i < childColumns.size(); i++) {
            final BRLVariableColumn brlConditionVariableColumn = childColumns.get(i);
            columnContext.addBoundName(brlConditionVariableColumn.getVarName());
            if (brlConditionVariableColumn.getVarName().isEmpty()) {
                result.add(new DTCellValue52(true));
            } else {
                String varName = brlConditionVariableColumn.getVarName();
                String key = "";
                if (!varName.startsWith("$")) {
                    key = varName;
                    varName = "@{" + varName + "}";
                } else {
                    key = Integer.toString(i);
                    varName = "@{" + i + "}";
                }
                varListInOrder.put(brlConditionVariableColumn.getVarName(), key);
                result.add(new DTCellValue52(varName));
            }
        }

        return result;
    }

    private String replaceTempVars(final List<? extends BRLVariableColumn> childColumns,
                                   final String marshal) {
        int varIndex = 1;
        String result = marshal;
        for (BRLVariableColumn childColumn : childColumns) {

            final String var = varListInOrder.get(childColumn.getVarName());

            final Pattern pattern = Pattern.compile("@\\{(" + var + ")}");
            final Matcher matcher = pattern.matcher(result);
            result = matcher.replaceAll("\\$" + varIndex++);
        }
        return result;
    }

    private String subString(final String marshal,
                             final String from,
                             final String to) {

        Pattern regex = Pattern.compile("\\s*" + from + "\\s*(.*)" + to, Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(marshal);
        if (regexMatcher.find()) {
            return regexMatcher.group(1);
        } else {
            throw new IllegalStateException(String.format("No substring between '%s' and '%s' found",
                                                          from,
                                                          to));
        }
    }
}
