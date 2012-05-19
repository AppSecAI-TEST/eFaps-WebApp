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

package org.efaps.ui.wicket.components.menutree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.ui.wicket.EFapsSession;
import org.efaps.ui.wicket.models.objects.UIMenuItem;
import org.efaps.ui.wicket.pages.content.form.FormPage;
import org.efaps.ui.wicket.pages.content.table.TablePage;
import org.efaps.ui.wicket.pages.contentcontainer.ContentContainerPage;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.util.EFapsException;

/**
 * @author The eFaps Team
 * @version $Id:AjaxGoIntoLink.java 1510 2007-10-18 14:35:40Z jmox $
 */
public class AjaxGoIntoLink
    extends AbstractAjaxLink
{

    /**
     * Needed foer serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construtor setting the ID and the Node of this Component.
     *
     * @param _wicketId wicketid for this component
     * @param _node node for his component
     */
    public AjaxGoIntoLink(final String _wicketId,
                          final DefaultMutableTreeNode _node)
    {
        super(_wicketId, _node);
    }

    /**
     * @see org.apache.wicket.ajax.markup.html.AjaxLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
     * @param _target AjaxRequestTarget
     */
    @Override
    public void onClick(final AjaxRequestTarget _target)
    {
        // update the Content
        final UIMenuItem model = (UIMenuItem) getNode().getUserObject();

        final AbstractCommand cmd = model.getCommand();

        InlineFrame frame;
        try {
            if (cmd.getTargetTable() != null) {
                final TablePage page = new TablePage(model.getCommandUUID(), model.getInstanceKey(), true);
                frame = new InlineFrame(ContentContainerPage.IFRAME_WICKETID, page);
            } else {
                final FormPage page = new FormPage(model.getCommandUUID(), model.getInstanceKey(), true);
                frame = new InlineFrame(ContentContainerPage.IFRAME_WICKETID, page);
            }
        } catch (final EFapsException e) {

            frame = new InlineFrame(ContentContainerPage.IFRAME_WICKETID, new ErrorPage(e));
        }
        final InlineFrame component = (InlineFrame) getPage().get(((ContentContainerPage) getPage()).getInlinePath());
        frame.setOutputMarkupId(true);

        component.replaceWith(frame);
        _target.add(frame.getParent());

        // update MenuTree
        final MenuTree menutree = findParent(MenuTree.class);

        final MenuTree newMenuTree = new MenuTree(menutree.getId(), new DefaultTreeModel(getNode()), menutree
                        .getMenuKey());
        ((EFapsSession) getSession()).putIntoCache(menutree.getMenuKey(), newMenuTree);

        model.setStepInto(true);
        model.setAncestor((DefaultMutableTreeNode) ((DefaultTreeModel) menutree.getDefaultModelObject()).getRoot());
        menutree.replaceWith(newMenuTree);
        newMenuTree.getTreeState().selectNode(getNode(), true);
        newMenuTree.updateTree(_target);
    }
}
