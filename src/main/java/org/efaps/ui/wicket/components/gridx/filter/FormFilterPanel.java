/*
 * Copyright 2003 - 2016 The eFaps Team
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
 */

package org.efaps.ui.wicket.components.gridx.filter;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.efaps.api.ui.IMapFilter;
import org.efaps.ui.wicket.behaviors.dojo.AbstractDojoBehavior;
import org.efaps.ui.wicket.behaviors.dojo.ContentPaneBehavior;
import org.efaps.ui.wicket.components.LazyIframe;
import org.efaps.ui.wicket.components.LazyIframe.IFrameProvider;
import org.efaps.ui.wicket.models.objects.UIGrid;
import org.efaps.ui.wicket.pages.content.grid.filter.FormFilterPage;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.ui.wicket.util.DojoClasses;
import org.efaps.ui.wicket.util.DojoWrapper;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
public class FormFilterPanel
    extends GenericPanel<IMapFilter>
{

    /** The Constant CSS. */
    public static final ResourceReference CSS = new CssResourceReference(AbstractDojoBehavior.class,
                    "dojox/layout/resources/ResizeHandle.css");

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new form filter panel.
     *
     * @param _wicketId the wicket id
     * @param _model the model
     * @param _uiGrid the ui grid
     */
    public FormFilterPanel(final String _wicketId,
                           final IModel<IMapFilter> _model,
                           final UIGrid _uiGrid)
    {
        super(_wicketId);
        final LazyIframe frame = new LazyIframe("content", new IFrameProvider()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Page getPage(final Component _component)
            {
                Page error = null;
                WebPage page = null;
                try {
                    page = new FormFilterPage(_model, _uiGrid, _component.getPage().getPageReference());
                } catch (final EFapsException e) {
                    error = new ErrorPage(e);
                }
                return error == null ? page : error;
            }
        }, null, false);

        final String id = RandomStringUtils.randomAlphabetic(8);
        frame.setMarkupId(id);
        frame.setOutputMarkupId(true);
        frame.add(new ContentPaneBehavior(null, false).setJsExecuteable(true));
        frame.add(new LoadFormBehavior());
        frame.setDefaultModel(_model);
        this.add(frame);
    }

    @Override
    public void renderHead(final IHeaderResponse _response)
    {
        super.renderHead(_response);
        _response.render(CssHeaderItem.forReference(FormFilterPanel.CSS));
    }

    /**
     * The Class LoadFormBehavior.
     *
     */
    public static class LoadFormBehavior
        extends AbstractDojoBehavior
    {
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        @Override
        public void renderHead(final Component _component,
                               final IHeaderResponse _response)
        {
            super.renderHead(_component, _response);
            final String fttd = "fttd_" + ((IMapFilter) _component.getDefaultModelObject()).getFieldId();

            final StringBuilder js = new StringBuilder()
                .append("ready(function() {\n")
                .append("var pd = registry.byId(\"").append(fttd).append("\");\n")
                .append("aspect.before(pd, 'onOpen', function() {\n")
                .append("registry.byId(\"").append(_component.getMarkupId()).append("\").set(\"href\",\"")
                .append(_component.urlFor(ILinkListener.INTERFACE, new PageParameters())).append("\");\n")
                .append("});\n")
                .append("});");
            _response.render(JavaScriptHeaderItem.forScript(DojoWrapper.require(js, DojoClasses.ready,
                            DojoClasses.registry, DojoClasses.aspect, DojoClasses.domConstruct),
                            _component.getMarkupId() + "-Script"));
        }
    }
}
