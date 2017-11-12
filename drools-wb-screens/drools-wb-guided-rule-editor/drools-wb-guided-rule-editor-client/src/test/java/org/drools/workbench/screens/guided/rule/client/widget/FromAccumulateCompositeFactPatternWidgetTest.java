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

package org.drools.workbench.screens.guided.rule.client.widget;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.base.AbstractAnchorListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@WithClassesToStub({TabListItem.class, AnchorListItem.class, AbstractAnchorListItem.class})
@RunWith(GwtMockitoTestRunner.class)
public class FromAccumulateCompositeFactPatternWidgetTest {

    String[] factTypes = {Number.class.getSimpleName()};

    @Mock
    private ListBox choices;

    @Mock
    private FormStylePopup formStylePopup;

    @Mock
    private EventBus eventBus;

    private FromAccumulateCompositeFactPattern fromAccumulatePattern;

    @Mock
    private RuleModeller modeller;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    private FromAccumulateCompositeFactPatternWidget fromAccumulateWidget;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(ListBox.class, aClass -> choices);
        GwtMockito.useProviderForType(FormStylePopup.class, aClass -> formStylePopup);

        fromAccumulatePattern = new FromAccumulateCompositeFactPattern();
        fromAccumulateWidget = new FromAccumulateCompositeFactPatternWidget(modeller,
                                                                            eventBus,
                                                                            fromAccumulatePattern);

        doReturn(oracle).when(modeller).getDataModelOracle();

        doReturn(factTypes).when(oracle).getFactTypes();
    }

    @Test
    public void testShowFactTypeSelector() throws Exception {
        fromAccumulateWidget.showFactTypeSelector();

        verify(choices).addItem(Number.class.getSimpleName());
        verify(formStylePopup).addAttribute(GuidedRuleEditorResources.CONSTANTS.chooseFactType(), choices);
    }
}
