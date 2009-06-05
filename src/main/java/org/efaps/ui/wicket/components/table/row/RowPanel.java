/*
 * Copyright 2003 - 2009 The eFaps Team
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
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.ui.wicket.components.table.row;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;

import org.efaps.ui.wicket.components.table.AjaxAddRemoveRowPanel;
import org.efaps.ui.wicket.components.table.TablePanel;
import org.efaps.ui.wicket.components.table.cell.CellPanel;
import org.efaps.ui.wicket.models.TableModel;
import org.efaps.ui.wicket.models.cell.TableCellModel;
import org.efaps.ui.wicket.models.cell.UITableCell;
import org.efaps.ui.wicket.models.objects.UIRow;
import org.efaps.ui.wicket.models.objects.UITable;

/**
 * @author The eFaps Team
 * @version $Id$
 */
public class RowPanel extends Panel
{

    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param _wicketId         wicket id for this component
     * @param _model            model for this component
     * @param _tablePanel       tablepanel this row is in
     * @param _updateListMenu   must the listmenu be updated
     * @param _rowNumber        number of this row
     */
    public RowPanel(final String _wicketId, final IModel<UIRow> _model, final TablePanel _tablePanel,
                    final boolean _updateListMenu, final int _rowNumber)
    {
        super(_wicketId, _model);
        final UIRow uirow = (UIRow) super.getDefaultModelObject();

        final UITable uiTable = (UITable) _tablePanel.getDefaultModelObject();
        int i = uiTable.getTableId();

        final RepeatingView cellRepeater = new RepeatingView("cellRepeater");
        add(cellRepeater);

        if (uiTable.isShowCheckBoxes()) {
            final CellPanel cellpanel = new CellPanel(cellRepeater.newChildId(), uirow.getInstanceKeys());
            cellpanel.setOutputMarkupId(true);
            cellpanel.add(new SimpleAttributeModifier("class", "eFapsTableCheckBoxCell"));
            cellRepeater.add(cellpanel);
            i++;
        }
        if (uiTable.isCreateMode()) {
            final AjaxAddRemoveRowPanel remove = new AjaxAddRemoveRowPanel(cellRepeater.newChildId(),
                                                                           new TableModel(uiTable), null, false);
            remove.setOutputMarkupId(true);
            remove.add(new SimpleAttributeModifier("class", "eFapsTableRemoveRowCell"));
            cellRepeater.add(remove);
            i++;
        }
        int cellNumber = 0;
        for (final UITableCell cellmodel : uirow.getValues()) {
            final CellPanel   cell = new CellPanel(cellRepeater.newChildId(), new TableCellModel(cellmodel),
                                                      _updateListMenu, uiTable, _rowNumber, cellNumber++);
            cell.setOutputMarkupId(true);
            if (cellmodel.isFixedWidth()) {
                cell.add(new SimpleAttributeModifier("class", "eFapsTableCell eFapsCellFixedWidth" + i));
            } else {
                cell.add(new SimpleAttributeModifier("class", "eFapsTableCell eFapsCellWidth" + i));
            }
            cellRepeater.add(cell);
            i++;
        }
    }
}
