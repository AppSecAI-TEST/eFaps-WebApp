/*
 * Copyright 2003 - 2013 The eFaps Team
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

package org.efaps.ui.wicket.components.bpm.process;

import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.iterator.ComponentHierarchyIterator;
import org.efaps.ui.wicket.components.bpm.AbstractSortableProvider;
import org.efaps.ui.wicket.models.objects.UIProcessInstance;
import org.efaps.ui.wicket.resources.AbstractEFapsHeaderItem;
import org.efaps.ui.wicket.resources.EFapsContentReference;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id: ProcessAdminPanel.java 9946 2013-08-02 22:04:49Z jan@moxter.net
 *          $
 */
public class ProcessAdminPanel
    extends Panel
{

    /**
     * Reference to the style sheet.
     */
    public static final EFapsContentReference CSS = new EFapsContentReference(AbstractSortableProvider.class,
                    "BPM.css");

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    /**
     * @param _wicketId WicketId
     * @param _pageReference    Refernce to the page
     * @throws EFapsException on error
     */
    public ProcessAdminPanel(final String _wicketId,
                             final PageReference _pageReference)
        throws EFapsException
    {
        super(_wicketId);
        final Form<UIProcessInstance> form = new Form<UIProcessInstance>("form");
        add(form);
        final DropDownChoice<String> dropDown = new DropDownChoice<String>("processIds", Model.<String>of(),
                        UIProcessInstance.getProcessIds());
        dropDown.setNullValid(true);
        dropDown.add(new AjaxFormComponentUpdatingBehavior("onchange")
        {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(final AjaxRequestTarget _target)
            {
                final ComponentHierarchyIterator iter = getPage().visitChildren(AjaxFallbackDefaultDataTable.class);
                while (iter.hasNext()) {
                    final AjaxFallbackDefaultDataTable<?, ?> table = (AjaxFallbackDefaultDataTable<?, ?>) iter.next();
                    final IDataProvider<?> provider = table.getDataProvider();
                    if (provider instanceof ProcessInstanceProvider) {
                        ((ProcessInstanceProvider) provider).setProcessId(getComponent()
                                        .getDefaultModelObjectAsString());
                        ((ProcessInstanceProvider) provider).requery();
                        _target.add(table);
                        break;
                    }
                }
            }
        });
        form.add(dropDown);

        final ProcessTablePanel taskTable = new ProcessTablePanel("processTable", _pageReference,
                        new ProcessInstanceProvider());
        form.add(taskTable);
    }

    @Override
    public void renderHead(final IHeaderResponse _response)
    {
        super.renderHead(_response);
        _response.render(AbstractEFapsHeaderItem.forCss(ProcessAdminPanel.CSS));
    }

}
