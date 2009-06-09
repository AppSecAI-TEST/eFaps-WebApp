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

package org.efaps.ui.wicket.behaviors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.model.IModel;

import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.ui.wicket.models.cell.UITableCell;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class AjaxFieldUpdateBehavior extends AjaxFormSubmitBehavior
{
    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Key used for the javascript in the map.
     */
    private static String JS = "eFapsFieldUpdateJS";


    /**
     * Model that will be used on submit (if not null).
     */
    private final IModel<?> model;

    /**
     * @param _event event that this behavior should be executed on;
     */
    public AjaxFieldUpdateBehavior(final String _event)
    {
        this (_event, null);
    }

    /**
     * @param _event    event that this behavior should be executed on;
     * @param _model    model that willbe used on submit
     */
    public AjaxFieldUpdateBehavior(final String _event, final IModel<?> _model)
    {
        super(_event);
        this.model = _model;
    }

    /**
     * Default means nothing is done on error.
     * @see org.apache.wicket.ajax.form.AjaxFormSubmitBehavior#onError(org.apache.wicket.ajax.AjaxRequestTarget)
     * @param _target AjaxRequestTarget
     */
    @Override
    protected void onError(final AjaxRequestTarget _target)
    {
    }

    /**
     * @see org.apache.wicket.ajax.form.AjaxFormSubmitBehavior#onSubmit(org.apache.wicket.ajax.AjaxRequestTarget)
     * @param _target AjaxRequestTarget
     */
    @Override
    protected void onSubmit(final AjaxRequestTarget _target)
    {
        final UITableCell uiObject;
        if (this.model == null) {
            uiObject = (UITableCell) getComponent().getDefaultModelObject();
        } else {
            uiObject = (UITableCell) this.model.getObject();
        }
        final Map<String, String> map = new HashMap<String, String>();
        try {
            final List<Return> returns = uiObject.getFieldUpdate(getComponent().getMarkupId());
            for (final Return aReturn : returns) {
                final Object ob = aReturn.get(ReturnValues.VALUES);
                if (ob instanceof List) {
                    final List<Map<String, String>> list = (List<Map<String, String>>) ob;
                    for (final Map<String, String> mapObj : list) {
                        map.putAll(mapObj);
                    }
                }
            }
        } catch (final EFapsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final StringBuilder js = new StringBuilder();
        if (map.size() > 0) {
            js.append("var pos = 0; var eFapsFields = document.getElementsByName('")
                .append(uiObject.getName()).append("');")
                .append(" for (var i = 0; i < eFapsFields.length; i++) {")
                .append(" if (eFapsFields[i].id=='").append(getComponentMarkupId()).append("'){")
                .append(" pos = i; break;}}");
            for (final String keyString : map.keySet()) {
                // if the map contains a key that is not defined in this class it is assumed to be the name of a field
                if (!(AjaxFieldUpdateBehavior.JS.equals(keyString))) {
                    js.append("var ").append(keyString).append(" = document.getElementsByName('").append(keyString)
                        .append("');")
                        .append("if (").append(keyString).append(".length > 1) {")
                        .append(keyString).append("[pos].value ='").append(map.get(keyString)).append("';")
                        .append("} else {")
                        .append(keyString).append("[0].value ='").append(map.get(keyString)).append("';")
                        .append("}");
                }
            }
        }
        if (map.containsKey(AjaxFieldUpdateBehavior.JS)) {
            js.append(map.get(AjaxFieldUpdateBehavior.JS));
        }

        _target.appendJavascript(js.toString());
    }


    /**
     * Method to get the ComponentMarkupId.
     * @return markup id of the component.
     */
    protected String getComponentMarkupId()
    {
        return getComponent().getMarkupId();
    }

    /**
     * Overwritten to deactivate the visit of all other components
     * and the setting of model objects. This would lead to an
     * error, because this component does not have a model.
     *
     * @see org.apache.wicket.ajax.form.AjaxFormSubmitBehavior#onEvent(org.apache.wicket.ajax.AjaxRequestTarget)
     * @param _target AjaxRequestTarget
     */
    @Override
    protected void onEvent(final AjaxRequestTarget _target)
    {
        onSubmit(_target);
    }
}