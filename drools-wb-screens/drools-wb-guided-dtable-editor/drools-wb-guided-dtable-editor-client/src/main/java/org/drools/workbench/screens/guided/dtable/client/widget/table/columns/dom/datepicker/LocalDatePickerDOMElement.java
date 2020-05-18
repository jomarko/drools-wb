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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker;

import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueDOMElement;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement for DatePicker.
 * This class is a clone of 'DatePickerDOMElement'
 * This class needs to be used for 'LocalDate' cells as 'LocalDate' has no GWT support in client modules
 */
public class LocalDatePickerDOMElement extends SingleValueDOMElement<String, DatePicker> {

    public LocalDatePickerDOMElement(final DatePicker widget,
                                     final GridLayer gridLayer,
                                     final GridWidget gridWidget) {
        super(widget,
              gridLayer,
              gridWidget);
    }
}
