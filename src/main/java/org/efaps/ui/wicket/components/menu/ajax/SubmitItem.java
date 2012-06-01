/*
 * Copyright 2003 - 2012 The eFaps Team
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
 * Revision:        $Rev:1510 $
 * Last Changed:    $Date:2007-10-18 09:35:40 -0500 (Thu, 18 Oct 2007) $
 * Last Changed By: $Author:jmox $
 */

package org.efaps.ui.wicket.components.menu.ajax;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.string.StringValue;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.ui.wicket.components.modalwindow.ModalWindowContainer;
import org.efaps.ui.wicket.models.FormModel;
import org.efaps.ui.wicket.models.TableModel;
import org.efaps.ui.wicket.models.UIModel;
import org.efaps.ui.wicket.models.objects.AbstractUIObject;
import org.efaps.ui.wicket.models.objects.UIForm;
import org.efaps.ui.wicket.models.objects.UIMenuItem;
import org.efaps.ui.wicket.models.objects.UITable;
import org.efaps.ui.wicket.pages.content.AbstractContentPage;
import org.efaps.ui.wicket.pages.content.form.FormPage;
import org.efaps.ui.wicket.pages.content.table.TablePage;
import org.efaps.ui.wicket.pages.dialog.DialogPage;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.ui.wicket.pages.main.MainPage;
import org.efaps.ui.wicket.util.ParameterUtil;
import org.efaps.util.EFapsException;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class SubmitItem
    extends AbstractItem
{
    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param _wicketId     wicketid
     * @param _menuItem     menuitem
     */
    public SubmitItem(final String _wicketId,
                      final IModel<UIMenuItem> _menuItem)
    {
        super(_wicketId, _menuItem);
        add(new SubmitAndUpdateBehavior());
    }

    /**
     * Behavior called on submit.
     */
    public class SubmitAndUpdateBehavior
        extends AbstractSubmitBehavior
    {
        /**
         * Needed for serialization.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Constructor.
         */
        public SubmitAndUpdateBehavior()
        {
            super("onClick");
        }

        /**
         * On submit a page is returned or the action directly executed.
         * @param _target AjaxRequestTarget
         */
        @Override
        protected void onSubmit(final AjaxRequestTarget _target)
        {
            final UIMenuItem uiMenuItem = (UIMenuItem) super.getComponent().getDefaultModelObject();

            final IRequestParameters para = getRequest().getRequestParameters();
            final List<StringValue> oidValues = para.getParameterValues("selectedRow");
            final String[] oids = ParameterUtil.parameter2Array(para, "selectedRow");
            boolean check = false;
            if (uiMenuItem.getSubmitSelectedRows() > -1) {
                if (uiMenuItem.getSubmitSelectedRows() > 0) {
                    check = oidValues == null ? false : oidValues.size() == uiMenuItem.getSubmitSelectedRows();
                } else {
                    check = oidValues == null ? false : !oidValues.isEmpty();
                }
            } else {
                check = true;
            }

            if (check) {
                if (uiMenuItem.isAskUser()) {
                    final ModalWindowContainer modal;
                    if (super.getComponent().getPage() instanceof MainPage) {
                        modal = ((MainPage) super.getComponent().getPage()).getModal();
                    } else {
                        modal = ((AbstractContentPage) super.getComponent().getPage()).getModal();
                    }
                    modal.setPageCreator(new ModalWindow.PageCreator() {

                        private static final long serialVersionUID = 1L;

                        public Page createPage()
                        {
                            return new DialogPage(getPage().getPageReference(), new UIModel<UIMenuItem>(uiMenuItem),
                                            oids, SubmitItem.this);
                        }
                    });
                    modal.setInitialHeight(150);
                    modal.setInitialWidth(350);
                    modal.show(_target);
                } else {
                    final AbstractCommand command = ((UIMenuItem) super.getComponent().getDefaultModelObject())
                                    .getCommand();

                    if (command.hasEvents(EventType.UI_COMMAND_EXECUTE)) {
                        try {
                            if (oidValues != null) {
                                command.executeEvents(EventType.UI_COMMAND_EXECUTE, ParameterValues.OTHERS, oids);
                            } else {
                                command.executeEvents(EventType.UI_COMMAND_EXECUTE);
                            }
                        } catch (final EFapsException e) {
                            throw new RestartResponseException(new ErrorPage(e));
                        }
                    }
                    final AbstractUIObject uiObject = (AbstractUIObject) getPage().getDefaultModelObject();
                    uiObject.resetModel();

                    Page page = null;
                    try {
                        if (uiObject instanceof UITable) {
                            page = new TablePage(new TableModel((UITable) uiObject),
                                            ((AbstractContentPage) getPage()).getCalledByPageReference());
                        } else if (uiObject instanceof UIForm) {
                            page = new FormPage(new FormModel((UIForm) uiObject),
                                            ((AbstractContentPage) getPage()).getCalledByPageReference());
                        }
                    } catch (final EFapsException e) {
                        page = new ErrorPage(e);
                    }
                    setResponsePage(page);
                }
            } else {
                final ModalWindowContainer modal;
                if (super.getComponent().getPage() instanceof MainPage) {
                    modal = ((MainPage) super.getComponent().getPage()).getModal();
                } else {
                    modal = ((AbstractContentPage) super.getComponent().getPage()).getModal();
                }
                modal.setPageCreator(new ModalWindow.PageCreator() {

                    private static final long serialVersionUID = 1L;

                    public Page createPage()
                    {
                        return new DialogPage(getPage().getPageReference(), "SubmitSelectedRows.fail"
                                        + uiMenuItem.getSubmitSelectedRows(),
                                        false, null);
                    }
                });
                modal.setInitialHeight(150);
                modal.setInitialWidth(350);
                modal.show(_target);
            }
        }
    }
}