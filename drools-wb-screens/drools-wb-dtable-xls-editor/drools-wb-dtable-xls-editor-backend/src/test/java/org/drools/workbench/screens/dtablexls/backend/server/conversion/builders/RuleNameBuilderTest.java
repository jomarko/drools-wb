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
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import org.drools.decisiontable.parser.ActionType;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RuleNameBuilderTest {

    private RuleNameBuilder ruleNameBuilder;

    @Before
    public void setUp() throws Exception {
        ruleNameBuilder = new RuleNameBuilder();
    }

    @Test
    public void populateDecisionTable() {
        ruleNameBuilder.addCellValue(0, 0, "");
        ruleNameBuilder.populateDecisionTable(new GuidedDecisionTable52(),
                                              2);
    }

    @Test
    public void getActionTypeCode() {
        assertEquals(ActionType.Code.NAME, ruleNameBuilder.getActionTypeCode());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getResult() {
        ruleNameBuilder.getResult();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addTemplate() {
        ruleNameBuilder.addTemplate(0, 0, "");
    }

    @Test
    public void getColumn() {
        assertEquals(GuidedDecisionTable52.RULE_NAME_COLUMN_INDEX, ruleNameBuilder.getColumn());
    }
}