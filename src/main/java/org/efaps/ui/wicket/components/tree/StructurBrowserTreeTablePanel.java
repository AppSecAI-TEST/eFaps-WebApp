/*
 * Copyright 2003 - 2010 The eFaps Team
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

package org.efaps.ui.wicket.components.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Response;
import org.apache.wicket.extensions.markup.html.tree.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.tree.table.AbstractRenderableColumn;
import org.apache.wicket.extensions.markup.html.tree.table.AbstractTreeColumn;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.IRenderable;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.efaps.admin.ui.field.Field.Display;
import org.efaps.ui.wicket.components.table.cell.ContentContainerLink;
import org.efaps.ui.wicket.models.cell.StructurBrowserTableCellModel;
import org.efaps.ui.wicket.models.cell.UIStructurBrowserTableCell;
import org.efaps.ui.wicket.models.objects.UIStructurBrowser;
import org.efaps.ui.wicket.models.objects.UITableHeader;

/**
 * @author The eFaps Team
 * @version $Id$
 */
public class StructurBrowserTreeTablePanel
    extends Panel
{

    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param _wicketId wicket id of this component
     * @param _model model for this component
     * @param _parentLink must the link be done using the parent
     */
    public StructurBrowserTreeTablePanel(final String _wicketId,
                                         final IModel<UIStructurBrowser> _model,
                                         final boolean _parentLink)
    {
        super(_wicketId, _model);

        final UIStructurBrowser model = (UIStructurBrowser) super.getDefaultModelObject();
        if (!model.isInitialized()) {
            model.execute();
        }

        final IColumn[] columns = new IColumn[model.getHeaders().size() + 1];
        if (model.isCreateMode() || model.isEditMode()) {
            columns[0] = new EditColumn(new ColumnLocation(Alignment.LEFT, 16, Unit.PX), "", _model);
        } else {
            columns[0] = new SelectColumn(new ColumnLocation(Alignment.LEFT, 16, Unit.PX), "");
        }
        int i = 0;
        for (final UITableHeader header : model.getHeaders()) {
            if (header.getFieldName().equals(model.getBrowserFieldName())) {
                columns[i + 1] = new TreeColumn(new ColumnLocation(Alignment.MIDDLE, 2, Unit.PROPORTIONAL),
                                header.getLabel(), _model);
            } else {
                columns[i + 1] = new SimpleColumn(new ColumnLocation(Alignment.MIDDLE, 1, Unit.PROPORTIONAL),
                                header.getLabel(), i);
            }
            i++;
        }

        final StructurBrowserTreeTable tree = new StructurBrowserTreeTable("treeTable", model.getTreeModel(), columns,
                        _parentLink);
        add(tree);
    }

    /**
     * Class for the column that contains the tree.
     */
    public class TreeColumn
        extends AbstractTreeColumn
    {

        /**
         * Needed for serialization.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Header for this column.
         */
        private final String header;

        /**
         * Model for this column.
         */
        private final IModel<UIStructurBrowser> model;

        /**
         * @param _location column location
         * @param _header   header
         * @param _model    model
         */
        public TreeColumn(final ColumnLocation _location,
                          final String _header,
                          final IModel<UIStructurBrowser> _model)
        {
            super(_location, _header);
            this.header = _header;
            this.model = _model;
        }

        /**
         * Render value for the node.
         *
         * @param _node node to render
         * @return String with the value for the node
         */
        @Override
        public String renderNode(final TreeNode _node)
        {
            return _node.toString();
        }

        /**
         * Add the sortlink as a the header.
         *
         * @param _parent parent
         * @param _wicketId wicket id for the sortlink
         * @return Component to be used as the header
         *
         */
        @Override
        public Component newHeader(final MarkupContainer _parent,
                                   final String _wicketId)
        {
            return new SortHeaderColumnLink(_wicketId, this.header, this.model);
        }

    }

    /**
     * Class for the standard column.
     */
    public class SimpleColumn
        extends AbstractRenderableColumn
    {

        /**
         * Needed for serialization.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Index of this column.
         */
        private final int index;

        /**
         * Counter index for the Link element.
         */
        private int idx = 0;

        /**
         * @param _location Location
         * @param _header header
         * @param _index index
         */
        public SimpleColumn(final ColumnLocation _location,
                            final String _header,
                            final int _index)
        {
            super(_location, _header);
            this.index = _index;
            setContentAsTooltip(true);
        }


        /**
         * This method is used to populate the cell for given node in case when
         * {@link IColumn#newCell(TreeNode, int)} returned null.
         *
         * @param _parent   The parent to which the cell must be added.
         * @param _wicketId The component id
         * @param _node     TreeNode for the cell
         * @param _level    Convenience parameter that indicates how deep the node is in hierarchy
         * @return The populated cell component
         */
        @Override
        public Component newCell(final MarkupContainer _parent,
                                 final String _wicketId,
                                 final TreeNode _node,
                                 final int _level)
        {
            return new TreeCellPanel(_wicketId, _node, this.index);
        }


        /**
         * For edit or create Mode null is returned and therefore
         * method {@link #newCell(MarkupContainer, String, TreeNode, int)} will
         * be called. (as specified from the Wicket API). For other Modes in
         * case of a link a ContentContainerlink is rendered else a standard
         * cell.
         *
         * @param _node Node the cell is rendered for
         * @param _level level of the node
         * @return new Cell
         */
        @Override
        public IRenderable newCell(final TreeNode _node,
                                   final int _level)
        {
            IRenderable ret;
            final UIStructurBrowser uiStru = (UIStructurBrowser) ((DefaultMutableTreeNode) _node)
                            .getUserObject();
            final UIStructurBrowserTableCell uiObject = uiStru.getColumnValue(getIndex());
            if ((uiStru.isEditMode() || uiStru.isCreateMode()) &&  uiObject.getDisplay().equals(Display.EDITABLE)) {
                ret = null;
            } else {

                if (uiObject.getReference() == null) {
                    ret = super.newCell(_node, _level);
                } else {
                    this.idx++;
                    final StructurBrowserTableCellModel model = new StructurBrowserTableCellModel(uiObject);
                    final ContentContainerLink<UIStructurBrowserTableCell> celllink
                        = new ContentContainerLink<UIStructurBrowserTableCell>(
                                    "link" + uiObject.getName() + this.idx, model);
                    getPage().add(celllink);
                    celllink.rendered();
                    ret = new IRenderable()
                    {

                        private static final long serialVersionUID = 1L;

                        public void render(final TreeNode _node,
                                           final Response _response)
                        {
                            final CharSequence url = celllink.urlFor(ILinkListener.INTERFACE);
                            String content = getNodeValue(_node);

                            // escape if necessary
                            if (isEscapeContent()) {
                                content = Strings.escapeMarkup(content).toString();
                            }

                            _response.write("<a");
                            if (isContentAsTooltip()) {
                                _response.write(" title=\"" + content + "\"");
                            }
                            _response.write(" href=\"");
                            _response.write(url);
                            _response.write("\">");
                            _response.write(content);
                            _response.write("</a>");
                        }
                    };
                }
            }
            return ret;
        }

        /**
         * Method to get the value for the node.
         *
         * @param _node node the value will be returned for
         * @return value for the node
         */
        @Override
        public String getNodeValue(final TreeNode _node)
        {
            final UIStructurBrowserTableCell ret = ((UIStructurBrowser) ((DefaultMutableTreeNode) _node)
                            .getUserObject()).getColumnValue(getIndex());
            return ret.getCellValue();
        }

        /**
         * Getter method for the instance variable {@link #index}.
         *
         * @return value of instance variable {@link #index}
         */
        public int getIndex()
        {
            return this.index;
        }
    }

    /**
     * Class for the column containing a select box.
     */
    public class SelectColumn
        extends AbstractRenderableColumn
    {

        /**
         * Needed for serialization.
         */
        private static final long serialVersionUID = 1L;

        /**
         * @param _location Location
         * @param _header header
         */
        public SelectColumn(final ColumnLocation _location,
                            final String _header)
        {
            super(_location, _header);
        }

        /**
         * Method to render a select box.
         *
         * @param _node Node the cell is rendered for
         * @param _level level of the node
         * @return new Cell
         */
        @Override
        public IRenderable newCell(final TreeNode _node,
                                   final int _level)
        {
            return new IRenderable()
            {

                private static final long serialVersionUID = 1L;

                public void render(final TreeNode _node,
                                   final Response _response)
                {
                    final String instanceKey = ((UIStructurBrowser) ((DefaultMutableTreeNode) _node).getUserObject())
                                    .getInstanceKey();
                    final String checkbox = "<input type=\"checkbox\" name=\"selectedRow\" class=\"eFapsCheckboxCell\""
                        + "value=\"" + instanceKey + "\"/>";
                    _response.write(checkbox);
                }
            };
        }

        /**
         * Method to get the value for the node.
         *
         * @param _node node the value will be returned for
         * @return empty String
         */
        @Override
        public String getNodeValue(final TreeNode _node)
        {
            return "";
        }
    }

    /**
     * Class for the column containing a select box.
     */
    public class EditColumn
        extends AbstractColumn
    {

        /**
         * Needed for serialization.
         */
        private static final long serialVersionUID = 1L;
        private final IModel<UIStructurBrowser> model;

        /**
         * @param _location Location
         * @param _header header
         */
        public EditColumn(final ColumnLocation _location,
                          final String _header,
                          final IModel<UIStructurBrowser> _model)
        {
            super(_location, _header);
            this.model = _model;
        }


        /**
         * This method is used to populate the cell for given node in case when
         * {@link IColumn#newCell(TreeNode, int)} returned null.
         *
         * @param _parent    The parent to which the cell must be added.
         * @param _wicketId  The component id
         * @param _node      TreeNode for the cell
         * @param _level     Convenience parameter that indicates how deep the node is in hierarchy
         * @return The populated cell component
         */
        @Override
        public Component newCell(final MarkupContainer _parent,
                                 final String _wicketId,
                                 final TreeNode _node,
                                 final int _level)
        {
            return new AjaxEditRowPanel(_wicketId, this.model, null);
        }

        /**
         * If this method returns null, {@link IColumn#newCell(MarkupContainer, String, TreeNode, int)}
         * is used to popuplate the cell.
         *
         * @param _node  TreeNode for the cell
         * @param _level Convenience parameter that indicates how deep the node is in hierarchy
         * @return The cell renderer
         */
        @Override
        public IRenderable newCell(final TreeNode _node,
                                   final int _level)
        {
            return null;
        }
    }
}
