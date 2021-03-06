/*
 * Copyright 2003 - 2014 The eFaps Team
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

package org.efaps.ui.wicket.components.split;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;
import org.efaps.ui.wicket.pages.contentcontainer.ContentContainerPage;
import org.efaps.ui.wicket.util.Configuration;
import org.efaps.ui.wicket.util.Configuration.ConfigAttribute;
import org.efaps.ui.wicket.util.DojoClasses;
import org.efaps.ui.wicket.util.DojoWrapper;

/**
 * Class renders an ajax post link which is used to store the position of the
 * horizontal splitter.
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class AjaxStorePositionBehavior
    extends AbstractDefaultAjaxBehavior
{

    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * String used as the parameter name.
     */
    private static final String PARAMETER_VERTICALPOSITION = "eFapsPosV";

    /**
     * String used as the parameter name.
     */
    private static final String PARAMETER_HORIZONTALPOSITION = "eFapsPosH";

    /**
     * Store the vertical.
     */
    private final boolean vertical;

    /**
     * @param _vertical store vertical to
     */
    public AjaxStorePositionBehavior(final boolean _vertical)
    {
        this.vertical = _vertical;
    }

    /**
     * On request the values are stored.
     *
     * @param _target AjaxRequestTarget
     */
    @Override
    protected void respond(final AjaxRequestTarget _target)
    {
        final StringValue horizontal = getComponent().getRequest().getRequestParameters().getParameterValue(
                        AjaxStorePositionBehavior.PARAMETER_HORIZONTALPOSITION);
        final StringValue verticalTmp = getComponent().getRequest().getRequestParameters().getParameterValue(
                        AjaxStorePositionBehavior.PARAMETER_VERTICALPOSITION);
        if (!horizontal.isNull()) {
            Configuration.setAttribute(ConfigAttribute.SPLITTERPOSHORIZONTAL, horizontal.toString());
        }
        if (!verticalTmp.isNull()) {
            Configuration.setAttribute(ConfigAttribute.SPLITTERPOSVERTICAL, verticalTmp.toString());
        }
    }

    @Override
    public void renderHead(final Component _component,
                           final IHeaderResponse _response)
    {
        super.renderHead(_component, _response);
        _response.render(JavaScriptHeaderItem.forScript(getCallbackScript(),
                        AjaxStorePositionBehavior.class.getName()));
    }

    /**
     * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#getCallbackScript()
     * @param _component Component the script belongs to
     * @return script
     */
    @Override
    public CharSequence getCallbackScript(final Component _component)
    {
        final String borderPanelId = ((ContentContainerPage) _component.getPage()).getBorderPanelId();
        final String leftPanelId = _component.getMarkupId(true);
        final String topPanelId = ((SidePanel) _component).getTopPanelId();

        final StringBuilder js = new StringBuilder();
        if (this.vertical) {
            js.append("var storePosV = ")
                .append(getCallbackFunction(
                            CallbackParameter.explicit(AjaxStorePositionBehavior.PARAMETER_VERTICALPOSITION)));
        }
        js.append("var storePosH = ").
                append(getCallbackFunction(
                            CallbackParameter.explicit(AjaxStorePositionBehavior.PARAMETER_HORIZONTALPOSITION)))
            .append("dojo.ready(function() {\n")
            .append("var bp = dijit.registry.byId(\"").append(borderPanelId).append("\");\n")
            .append("var lp = dijit.registry.byId(\"").append(leftPanelId).append("\");\n")
            .append("var hs = bp.getSplitter(\"leading\");\n");

        if (this.vertical) {
            js.append("var tp = dijit.registry.byId(\"").append(topPanelId).append("\");\n")
                .append("var vs = lp.getSplitter(\"top\");\n");
        }

        js.append(" dojo.connect(hs, \"onOpen\",function(pane){\n")
            .append("storePosH(pane.domNode.clientWidth);")
            .append("});\n")
            .append(" dojo.connect(hs, \"onClosed\",function(pane){\n")
            .append("storePosH(pane.domNode.clientWidth);")
            .append("});\n")
            .append(" dojo.connect(hs, \"_stopDrag\",function(e){\n")
            .append("storePosH(lp.domNode.clientWidth);")
            .append("});\n");

        if (this.vertical) {
            js.append(" dojo.connect(vs, \"onOpen\",function(pane){\n")
                .append("storePosV(pane.domNode.clientHeight);")
                .append("});\n")
                .append(" dojo.connect(vs, \"onClosed\",function(pane){\n")
                .append("storePosV(pane.domNode.clientHeight);")
                .append("});\n")
                .append(" dojo.connect(vs, \"_stopDrag\",function(e){\n")
                .append("storePosV(tp.domNode.clientHeight);")
                .append("});\n");
        }
        js.append("});");
        return js.toString();
    }

    @Override
    protected void updateAjaxAttributes(final AjaxRequestAttributes _attributes)
    {
        super.updateAjaxAttributes(_attributes);
        _attributes.setThrottlingSettings(new ThrottlingSettings("storeThrottel", Duration.seconds(2)));
        _attributes.setMethod(Method.POST);
    }
}
