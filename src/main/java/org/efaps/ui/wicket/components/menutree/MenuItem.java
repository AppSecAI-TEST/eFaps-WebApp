/*
 * Copyright 2003 - 2011 The eFaps Team
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

package org.efaps.ui.wicket.components.menutree;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.efaps.ui.wicket.components.efapscontent.StaticImageComponent;
import org.efaps.ui.wicket.models.objects.UIMenuItem;
import org.efaps.ui.wicket.pages.content.form.FormPage;
import org.efaps.ui.wicket.pages.content.structurbrowser.StructurBrowserPage;
import org.efaps.ui.wicket.pages.content.table.TablePage;
import org.efaps.ui.wicket.pages.contentcontainer.ContentContainerPage;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.ui.wicket.resources.EFapsContentReference;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class MenuItem
    extends Panel
{
    /**
     * Reference to icon for remove button.
     */
    public static final EFapsContentReference ICON_REMOVE = new EFapsContentReference(MenuItem.class, "Remove.gif");

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MenuItem.class);

    private final MenuTree tree;

    /**
     * @param _id
     * @param _tree
     * @param _model
     */
    public MenuItem(final String _id,
                    final MenuTree _tree,
                    final IModel<UIMenuItem> _model)
    {
        super(_id, _model);
        this.tree = _tree;
        setOutputMarkupId(true);
        add(new SelectedAttributeModifier());

        final MarkupContainer link = new Item("link", _model);
        add(link);

        final Label label = new Label("label", _model.getObject().getLabel());
        link.add(label);

        if (_model.getObject().isHeader()) {
            label.add(AttributeModifier.append("class", "eFapsMenuTreeHeader"));

            String imageUrl = _model.getObject().getImage();
            if (imageUrl == null) {
                try {
                    imageUrl = _model.getObject().getTypeImage();
                } catch (final EFapsException e) {
                    MenuItem.LOG.error("Error on retrieving the image for a image: {}",
                                    _model.getObject().getImage());
                }
            }
            if (imageUrl == null) {
                link.add(new WebMarkupContainer("icon").setVisible(false));
            } else {
                link.add(new StaticImageComponent("icon", imageUrl));
            }

        } else {
            label.add(AttributeModifier.append("class", "eFapsMenuTreeItem"));
            link.add(new WebMarkupContainer("icon").setVisible(false));
            // _item.add(new
            // WebMarkupContainer("goIntolink").setVisible(false));
            // _item.add(new
            // WebMarkupContainer("removelink").setVisible(false));
            // _item.add(new WebMarkupContainer("goUplink").setVisible(false));
        }

        if (_model.getObject().getAncestor() == null) {
            add(new WebMarkupContainer("removeLink").setVisible(false));
        } else {
            final AjaxRemoveLink removelink = new AjaxRemoveLink("removeLink", _model);
            add(removelink);
            removelink.add(new StaticImageComponent("removeIcon", MenuItem.ICON_REMOVE));
        }

    }

    public class Item
        extends WebMarkupContainer
        implements ILinkListener
    {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /**
         * @param _id
         * @param _model
         */
        public Item(final String _id,
                    final IModel<UIMenuItem> _model)
        {
            super(_id, _model);
            add(new ItemBehavior());
        }

        /*
         * (non-Javadoc)
         * @see org.apache.wicket.markup.html.link.ILinkListener#onLinkClicked()
         */
        @Override
        public void onLinkClicked()
        {
            final UIMenuItem menuItem = (UIMenuItem) getDefaultModelObject();
            Page page;
            try {
                if (menuItem.getCommand().getTargetTable() != null) {
                    if (menuItem.getCommand().getTargetStructurBrowserField() != null) {
                        page = new StructurBrowserPage(menuItem.getCommandUUID(),
                                        menuItem.getInstanceKey(), getPage()
                                        .getPageReference());
                    } else {
                        page = new TablePage(menuItem.getCommandUUID(), menuItem.getInstanceKey(), getPage()
                                        .getPageReference());
                    }
                } else {
                    page = new FormPage(menuItem.getCommandUUID(), menuItem.getInstanceKey(), getPage()
                                    .getPageReference());
                }
            } catch (final EFapsException e) {
                page = new ErrorPage(e);
            }
            setResponsePage(page);
        }
    }

    public static class SelectedAttributeModifier extends Behavior
    {

        /**
         *
         */
        private static final long serialVersionUID = 1L;


        /* (non-Javadoc)
         * @see org.apache.wicket.behavior.Behavior#onComponentTag(org.apache.wicket.Component, org.apache.wicket.markup.ComponentTag)
         */
        @Override
        public void onComponentTag(final Component _component,
                                   final ComponentTag _tag)
        {
            super.onComponentTag(_component, _tag);
            final UIMenuItem menuItem = (UIMenuItem) _component.getDefaultModelObject();
            if (menuItem.isSelected()) {
                _tag.put("class", "eFapsMenuTreeItemSelected");
            } else {
                _tag.put("class", "eFapsMenuTreeItem");
            }
        }
    }

    private final class ItemBehavior
        extends AjaxEventBehavior
    {

        private static final long serialVersionUID = 1L;

        public ItemBehavior()
        {
            super("onclick");
        }

        @Override
        protected void onEvent(final AjaxRequestTarget _target)
        {
            if (MenuItem.this.tree.getSelected() != null) {
                _target.add(MenuItem.this.tree.getSelected());
                final UIMenuItem menuItem = (UIMenuItem) MenuItem.this.tree.getSelected().getDefaultModelObject();
                menuItem.setSelected(true);
            }
            _target.add(getComponent());
            MenuItem.this.tree.setSelected(getComponent());
            final UIMenuItem menuItem = (UIMenuItem) MenuItem.this.tree.getSelected().getDefaultModelObject();
            menuItem.setSelected(true);
        }


        /*
         * (non-Javadoc)
         * @see
         * org.apache.wicket.ajax.AjaxEventBehavior#updateAjaxAttributes(org
         * .apache.wicket.ajax.attributes.AjaxRequestAttributes)
         */
        @Override
        protected void updateAjaxAttributes(final AjaxRequestAttributes _attributes)
        {
            super.updateAjaxAttributes(_attributes);
            final AjaxCallListener listener = new AjaxCallListener();
            final StringBuilder js = new StringBuilder();
            js.append("dijit.byId(\"").append(((ContentContainerPage) getPage()).getCenterPanelId())
                .append("\").set(\"content\", dojo.create(\"iframe\", {")
                .append("\"src\": \"")
                .append(getComponent().urlFor(ILinkListener.INTERFACE, new PageParameters()))
                .append("\",\"style\": \"border: 0; width: 100%; height: 100%\"")
                .append("})); ");
            listener.onAfter(js);
            _attributes.getAjaxCallListeners().add(listener);
        }
    }
}