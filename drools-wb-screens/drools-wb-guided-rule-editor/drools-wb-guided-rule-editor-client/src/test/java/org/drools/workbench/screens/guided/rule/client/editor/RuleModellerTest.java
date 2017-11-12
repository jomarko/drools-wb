/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.drools.workbench.screens.guided.rule.client.widget.FromAccumulateCompositeFactPatternWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.base.AbstractAnchorListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@WithClassesToStub(
        {TabListItem.class, AnchorListItem.class, AbstractAnchorListItem.class, GuidedRuleEditorImages508.class})
@RunWith(GwtMockitoTestRunner.class)
public class RuleModellerTest {

    private RuleModeller ruleModeller;

    private RuleModellerWidgetFactory ruleModellerFactory;

    private RuleModel ruleModel;

    private FromAccumulateCompositeFactPattern fromAccumulatePattern;

    @Mock
    private EventBus eventBus;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private HTMLTable.ColumnFormatter columnFormatter;

    @Mock
    private FlexTable.FlexCellFormatter flexCellFormatter;

    @Mock
    private FlexTable.CellFormatter cellFormatter;

    @Mock
    private HTMLTable.RowFormatter rowFormatter;

    private FlexTable layout;

    @Before
    public void setUp() throws Exception {
        layout = new FlexTable();

        doReturn(columnFormatter).when(layout).getColumnFormatter();
        doReturn(flexCellFormatter).when(layout).getFlexCellFormatter();
        doReturn(cellFormatter).when(layout).getCellFormatter();
        doReturn(rowFormatter).when(layout).getRowFormatter();
        GwtMockito.useProviderForType(FlexTable.class, aClass -> layout);

        fromAccumulatePattern = new FromAccumulateCompositeFactPattern();
        ruleModel = new RuleModel();
        ruleModel.addLhsItem(fromAccumulatePattern);

        ruleModellerFactory = new RuleModellerWidgetFactory();

        ruleModeller = new RuleModeller(ruleModel,
                                        oracle,
                                        ruleModellerFactory,
                                        eventBus,
                                        false);
    }

    @Test
    public void testRenderFromAccumulatePattern() throws Exception {

    }
}
