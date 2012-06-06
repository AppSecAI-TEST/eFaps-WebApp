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

package org.efaps.ui.wicket.behaviors.dojo;


import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

/**
 * This class turns a Component into a Dojo-ContentPane.
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class ContentPaneBehavior
    extends AbstractDojoBehavior
{

    /**
     * Enum is used when this ContentPaneBehavior is used as a child inside a
     * BorderContainer. The BorderContainer is widget is partitioned into up to
     * five regions: left (or leading), right (or trailing), top, and bottom
     * with a mandatory center to fill in any remaining space. Each edge region
     * may have an optional splitter user interface for manual resizing.
     */
    public enum Region
    {
        /** center region. */
        CENTER("center"),
        /** top region. */
        TOP("top"),
        /** bottom region. */
        BOTTOM("bottom"),
        /** leading region. */
        LEADING("leading"),
        /** trailing region. */
        TRAILING("trailing"),
        /** left region. */
        LEFT("left"),
        /** right region. */
        RIGHT("right");

        /**
         * Stores the key of the Region.
         */
        private final String key;

        /**
         * Private Constructor.
         *
         * @param _key Key
         */
        private Region(final String _key)
        {
            this.key = _key;
        }

        /**
         * Getter method for instance variable {@link #key}.
         *
         * @return value of instance variable {@link #key}
         */
        public String getKey()
        {
            return this.key;
        }
    }

    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Region of this ContenPane.
     */
    private final Region region;

    /**
     * Width of this ContenPane.
     */
    private String width;

    /**
     * Height of this ContenPane.
     */
    private final String height;

    /**
     * Sould a splitter be added.
     */
    private final Boolean splitter;

    private final String splitterState;

    /**
     * Constructor.
     *
     * @param _region region of this ContentPaneBehavior
     * @param _splitter should a splitter be rendered
     *
     */
    public ContentPaneBehavior(final Region _region,
                               final boolean _splitter)
    {
        this(_region, _splitter, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param _region region of this ContentPaneBehavior
     * @param _splitter should a splitter be rendered
     * @param _width width of this ContentPaneBehavior
     * @param _height height of this ContentPaneBehavior
     *
     */
    public ContentPaneBehavior(final Region _region,
                               final boolean _splitter,
                               final String _width,
                               final String _height,
                               final String _splitterState)
    {
        super();
        this.region = _region;
        this.width = _width;
        this.height = _height;
        this.splitter = _splitter;
        this.splitterState = _splitterState;
    }

    /**
     * The tag of the related component must be set, so that a dojo
     * BorderContainer will be rendered.
     *
     * @param _component component this Behavior belongs to
     * @param _tag Tag to write to
     */
    @Override
    public void onComponentTag(final Component _component,
                               final ComponentTag _tag)
    {
        super.onComponentTag(_component, _tag);
        _tag.put("data-dojo-type", "dijit.layout.ContentPane");
        _tag.append("data-dojo-props", "region: '" + this.region.getKey() + "'", ",");
        if (this.splitter) {
            _tag.append("data-dojo-props", "splitter: true", ",");
        }
        if (this.splitterState != null) {
            _tag.append("data-dojo-props", "toggleSplitterState: \"" + this.splitterState + "\"", ",");
        }

        if (this.width != null) {
            _tag.append("style", "width: " + this.width, ";");
        }
        if (this.height != null) {
            _tag.append("style", "height: " + this.height, ";");
        }

    }

    /**
     * Getter method for instance variable {@link #width}.
     *
     * @return value of instance variable {@link #width}
     */
    public String getWidth()
    {
        return this.width;
    }

    /**
     * Setter method for instance variable {@link #width}.
     *
     * @param _width value for instance variable {@link #width}
     */
    public void setWidth(final String _width)
    {
        this.width = _width;
    }

    /**
     * Render the links for the head.
     *
     * @param _component component the header will be rendered for
     * @param _response resonse to add
     */
    @Override
    public void renderHead(final Component _component,
                           final IHeaderResponse _response)
    {
        super.renderHead(_component, _response);
        _response.render(JavaScriptHeaderItem.forScript(
                        "require([\"dijit/layout/ContentPane\", \"dojo/parser\"]);",
                        ContentPaneBehavior.class.getName()));
    }

}
