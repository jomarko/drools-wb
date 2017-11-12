/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FromAccumulateCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromCompositeFactPattern;
import org.drools.workbench.models.datamodel.rule.FromEntryPointFactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

public class FromAccumulateCompositeFactPatternWidget extends FromCompositeFactPatternWidget {

    public FromAccumulateCompositeFactPatternWidget(RuleModeller modeller,
                                                    EventBus eventBus,
                                                    FromAccumulateCompositeFactPattern pattern,
                                                    Boolean readOnly) {
        super(modeller,
              eventBus,
              pattern,
              readOnly);
    }

    public FromAccumulateCompositeFactPatternWidget(RuleModeller modeller,
                                                    EventBus eventBus,
                                                    FromAccumulateCompositeFactPattern pattern) {
        super(modeller,
              eventBus,
              pattern);
    }

    @Override
    protected Widget getCompositeLabel() {
        ClickHandler leftPatternclick = new ClickHandler() {

            public void onClick(ClickEvent event) {
                showFactTypeSelector();
            }
        };
        ClickHandler sourcePatternClick = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget w = (Widget) event.getSource();
                showSourcePatternSelector(w);
            }
        };

        String lbl = "<div class='form-field'>" + HumanReadable.getCEDisplayName("from accumulate") + "</div>";

        FlexTable panel = new FlexTable();

        int r = 0;

        if (pattern.getFactPattern() == null) {
            panel.setWidget(r++,
                            0,
                            new ClickableLabel("<br> <font color='red'>" + GuidedRuleEditorResources.CONSTANTS.clickToAddPattern() + "</font>",
                                               leftPatternclick,
                                               !this.readOnly));
        }

        panel.setWidget(r++,
                        0,
                        new HTML(lbl));

        if (this.getFromAccumulatePattern().getSourcePattern() == null) {
            panel.setWidget(r++,
                            0,
                            new ClickableLabel("<br> <font color='red'>" + GuidedRuleEditorResources.CONSTANTS.clickToAddPattern() + "</font>",
                                               sourcePatternClick,
                                               !this.readOnly));
        } else {
            IPattern rPattern = this.getFromAccumulatePattern().getSourcePattern();

            RuleModellerWidget sourcePatternWidget;
            if (rPattern instanceof FactPattern) {
                sourcePatternWidget = new FactPatternWidget(this.getModeller(),
                                                            getEventBus(),
                                                            rPattern,
                                                            true,
                                                            true,
                                                            this.readOnly);
            } else if (rPattern instanceof FromAccumulateCompositeFactPattern) {
                sourcePatternWidget = new FromAccumulateCompositeFactPatternWidget(this.getModeller(),
                                                                                   this.getEventBus(),
                                                                                   (FromAccumulateCompositeFactPattern) rPattern,
                                                                                   this.readOnly);
            } else if (rPattern instanceof FromCollectCompositeFactPattern) {
                sourcePatternWidget = new FromCollectCompositeFactPatternWidget(this.getModeller(),
                                                                                this.getEventBus(),
                                                                                (FromCollectCompositeFactPattern) rPattern,
                                                                                this.readOnly);
            } else if (rPattern instanceof FromEntryPointFactPattern) {
                sourcePatternWidget = new FromEntryPointFactPatternWidget(this.getModeller(),
                                                                          this.getEventBus(),
                                                                          (FromEntryPointFactPattern) rPattern,
                                                                          this.readOnly);
            } else if (rPattern instanceof FromCompositeFactPattern) {
                sourcePatternWidget = new FromCompositeFactPatternWidget(this.getModeller(),
                                                                         this.getEventBus(),
                                                                         (FromCompositeFactPattern) rPattern,
                                                                         this.readOnly);
            } else {
                throw new IllegalArgumentException("Unsupported pattern "
                                                           + rPattern + " for right side of FROM ACCUMULATE");
            }

            sourcePatternWidget.addOnModifiedCommand(new Command() {
                public void execute() {
                    setModified(true);
                }
            });

            panel.setWidget(r++,
                            0,
                            addRemoveButton(sourcePatternWidget,
                                            new ClickHandler() {

                                                public void onClick(ClickEvent event) {
                                                    if (Window.confirm(GuidedRuleEditorResources.CONSTANTS.RemoveThisBlockOfData())) {
                                                        setModified(true);
                                                        getFromAccumulatePattern().setSourcePattern(null);
                                                        getModeller().refreshWidget();
                                                    }
                                                }
                                            }));
        }

        final TabPanel tPanel = new TabPanel() {{
            addStyleName("uf-tabbar-panel");
        }};
        final NavTabs navTabs = new NavTabs() {{
            addStyleName("uf-tabbar-panel-nav-tabs");
            addStyleName("nav-tabs-pf");
        }};
        final TabContent content = new TabContent();
        tPanel.add(navTabs);
        tPanel.add(content);

        FlexTable codeTable = new FlexTable();
        int codeTableRow = 0;
        int codeTableCol = 0;

        codeTable.setWidget(codeTableRow,
                            codeTableCol++,
                            new HTML("<div class='form-field'>" + GuidedRuleEditorResources.CONSTANTS.Init() + ":</div>"));

        final TextBox initField = new TextBox();
        initField.setTitle(GuidedRuleEditorResources.CONSTANTS.InitCode());
        initField.setText(getFromAccumulatePattern().getInitCode());
        initField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++,
                            codeTableCol--,
                            initField);

        codeTable.setWidget(codeTableRow,
                            codeTableCol++,
                            new HTML("<div class='form-field'>" + GuidedRuleEditorResources.CONSTANTS.Action() + ":</div>"));
        final TextBox actionField = new TextBox();
        actionField.setTitle(GuidedRuleEditorResources.CONSTANTS.ActionCode());
        actionField.setText(getFromAccumulatePattern().getActionCode());
        actionField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++,
                            codeTableCol--,
                            actionField);

        codeTable.setWidget(codeTableRow,
                            codeTableCol++,
                            new HTML("<div class='form-field'>" + GuidedRuleEditorResources.CONSTANTS.Reverse() + ":</div>"));
        final TextBox reverseField = new TextBox();
        reverseField.setTitle(GuidedRuleEditorResources.CONSTANTS.ReverseCode());
        reverseField.setText(getFromAccumulatePattern().getReverseCode());
        reverseField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++,
                            codeTableCol--,
                            reverseField);

        codeTable.setWidget(codeTableRow,
                            codeTableCol++,
                            new HTML("<div class='form-field'>" + GuidedRuleEditorResources.CONSTANTS.Result() + ":</div>"));
        final TextBox resultField = new TextBox();
        resultField.setTitle(GuidedRuleEditorResources.CONSTANTS.ResultCode());
        resultField.setText(getFromAccumulatePattern().getResultCode());
        resultField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++,
                            codeTableCol--,
                            resultField);

        //panel.setWidget(r++, 0, codeTable);
        final ScrollPanel codePanel = new ScrollPanel();
        codePanel.add(codeTable);

        final TabPane codeTabPane = new TabPane() {{
            add(codePanel);
        }};

        final TabListItem codeTabListItem = new TabListItem(GuidedRuleEditorResources.CONSTANTS.CustomCode()) {{
            addStyleName("uf-dropdown-tab-list-item");
            setDataTargetWidget(codeTabPane);
            getElement().getStyle().setFontSize(12,
                                                Style.Unit.PX);
        }};

        navTabs.add(codeTabListItem);
        content.add(codeTabPane);

        FlexTable functionTable = new FlexTable();

        functionTable.setWidget(0,
                                0,
                                new HTML("<div class='form-field'>" + GuidedRuleEditorResources.CONSTANTS.Function() + ":</div>"));
        final TextBox functionField = new TextBox();
        functionField.setTitle(GuidedRuleEditorResources.CONSTANTS.FunctionCode());
        functionField.setText(getFromAccumulatePattern().getFunction());
        functionField.setEnabled(!this.readOnly);
        functionTable.setWidget(0,
                                1,
                                functionField);

        //        panel.setWidget(r++, 0, functionTable);

        final ScrollPanel functionPanel = new ScrollPanel();
        functionPanel.add(functionTable);

        final TabPane functionTabPane = new TabPane() {{
            add(functionPanel);
        }};

        final TabListItem functionTabListItem = new TabListItem(GuidedRuleEditorResources.CONSTANTS.Function()) {{
            addStyleName("uf-dropdown-tab-list-item");
            setDataTargetWidget(functionTabPane);
            getElement().getStyle().setFontSize(12,
                                                Style.Unit.PX);
        }};

        navTabs.add(functionTabListItem);
        content.add(functionTabPane);

        ChangeHandler changehandler = new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                Widget sender = (Widget) event.getSource();
                TextBox senderTB = (TextBox) event.getSource();
                String code = senderTB.getText();
                setModified(true);
                if (sender == initField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setInitCode(code);
                } else if (sender == actionField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setActionCode(code);
                } else if (sender == reverseField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setReverseCode(code);
                } else if (sender == resultField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setResultCode(code);
                } else if (sender == functionField) {
                    getFromAccumulatePattern().clearCodeFields();
                    initField.setText("");
                    actionField.setText("");
                    reverseField.setText("");
                    resultField.setText("");
                    getFromAccumulatePattern().setFunction(code);
                }
            }
        };

        initField.addChangeHandler(changehandler);
        actionField.addChangeHandler(changehandler);
        reverseField.addChangeHandler(changehandler);
        resultField.addChangeHandler(changehandler);
        functionField.addChangeHandler(changehandler);

        boolean useFunction = getFromAccumulatePattern().useFunctionOrCode().equals(FromAccumulateCompositeFactPattern.USE_FUNCTION);

        if (useFunction) {
            functionTabListItem.setActive(true);
            functionTabPane.setActive(true);
        } else {
            codeTabListItem.setActive(true);
            codeTabPane.setActive(true);
        }

        panel.setWidget(r++,
                        0,
                        tPanel);

        return panel;
    }

    /**
     * Pops up the fact selector.
     */
    @Override
    protected void showFactTypeSelector() {
        final ListBox box = GWT.create(ListBox.class);
        AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();
        String[] facts = oracle.getFactTypes();

        box.addItem(GuidedRuleEditorResources.CONSTANTS.Choose());

        for (int i = 0; i < facts.length; i++) {
            box.addItem(facts[i]);
        }
        box.setSelectedIndex(0);

        final FormStylePopup popup = GWT.create(FormStylePopup.class);
        popup.setTitle(GuidedRuleEditorResources.CONSTANTS.NewFactPattern());
        popup.addAttribute(GuidedRuleEditorResources.CONSTANTS.chooseFactType(),
                           box);
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                pattern.setFactPattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        });
        popup.show();
    }

    /**
     * Pops up the fact selector.
     */
    protected void showSourcePatternSelector(final Widget w) {
        final ListBox box = new ListBox();
        AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();
        String[] facts = oracle.getFactTypes();

        box.addItem(GuidedRuleEditorResources.CONSTANTS.Choose());
        for (int i = 0; i < facts.length; i++) {
            box.addItem(facts[i]);
        }
        box.setSelectedIndex(0);

        final FormStylePopup popup = new FormStylePopup(GuidedRuleEditorResources.CONSTANTS.NewFactPattern());
        popup.addAttribute(GuidedRuleEditorResources.CONSTANTS.chooseFactType(),
                           box);
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                getFromAccumulatePattern().setSourcePattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        });

        final Button fromBtn = new Button(HumanReadableConstants.INSTANCE.From());
        final Button fromAccumulateBtn = new Button(HumanReadableConstants.INSTANCE.FromAccumulate());
        final Button fromCollectBtn = new Button(HumanReadableConstants.INSTANCE.FromCollect());
        final Button fromEntryPointBtn = new Button(HumanReadableConstants.INSTANCE.FromEntryPoint());
        ClickHandler btnsClickHandler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget sender = (Widget) event.getSource();
                if (sender == fromBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromCompositeFactPattern());
                } else if (sender == fromAccumulateBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromAccumulateCompositeFactPattern());
                } else if (sender == fromCollectBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromCollectCompositeFactPattern());
                } else if (sender == fromEntryPointBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromEntryPointFactPattern());
                } else {
                    throw new IllegalArgumentException("Unknown sender: "
                                                               + sender);
                }

                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        };

        fromBtn.addClickHandler(btnsClickHandler);
        fromAccumulateBtn.addClickHandler(btnsClickHandler);
        fromCollectBtn.addClickHandler(btnsClickHandler);
        fromEntryPointBtn.addClickHandler(btnsClickHandler);
        popup.addAttribute("",
                           fromBtn);
        popup.addAttribute("",
                           fromAccumulateBtn);
        popup.addAttribute("",
                           fromCollectBtn);
        popup.addAttribute("",
                           fromEntryPointBtn);

        popup.show();
    }

    private FromAccumulateCompositeFactPattern getFromAccumulatePattern() {
        return (FromAccumulateCompositeFactPattern) this.pattern;
    }
}
