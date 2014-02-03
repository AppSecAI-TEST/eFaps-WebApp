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

package org.efaps.ui.wicket.behaviors.update;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;

/**
 * @author The eFaps Team
 * @version $Id$
 */
public abstract class AbstractAjaxUpdateBehavior
    extends AbstractDefaultAjaxBehavior
    implements UpdateInterface
{

    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * This instance variable stores the TargetMode of this behavior.
     */
    private TargetMode mode;

    /**
     * This instance variable stores the oid of this behavior.
     */
    private String instanceKey;

    /**
     * @return String
     */
    public String getAjaxCallback()
    {
        return getCallbackScript().toString();
    }

    /**
     * Get the if of this class.
     *
     * @see org.efaps.ui.wicket.behaviors.update.UpdateInterface#getId()
     * @return classname as id
     */
    public String getId()
    {
        return getClass().toString();
    }

    /**
     * Does this callback use ajax.
     *
     * @see org.efaps.ui.wicket.behaviors.update.UpdateInterface#isAjaxCallback()
     * @return true
     */
    public boolean isAjaxCallback()
    {
        return true;
    }

    /**
     * Setter method for instance variable {@link #mode}.
     *
     * @param _mode value for instance variable {@link #mode}
     */
    public void setMode(final TargetMode _mode)
    {
        this.mode = _mode;
    }

    /**
     * This is the getter method for the instance variable {@link #mode}.
     *
     * @return value of instance variable {@link #mode}
     */
    public TargetMode getMode()
    {
        return this.mode;
    }

    /**
     * Setter method for instance variable {@link #instanceKey}.
     *
     * @param _instanceKey value for instance variable {@link #instanceKey}
     */
    public void setInstanceKey(final String _instanceKey)
    {
        this.instanceKey = _instanceKey;
    }

    /**
     * This is the getter method for the instance variable {@link #instanceKey}.
     *
     * @return value of instance variable {@link #instanceKey}
     */
    public String getInstanceKey()
    {
        return this.instanceKey;
    }

    /**
     * Get the call back script.
     *
     * @see org.apache.wicket.ajax.AbstractDefaultAjaxBehavior#getCallbackScript()
     * @return JavaScript
     */
    @Override
    public CharSequence getCallbackScript()
    {
        return new StringBuilder().append("function findFrame(_current, _target) {")
                        .append("  var ret = _current.frames[_target];")
                        .append("  if (!ret) {")
                        .append("    for (var i=0; i < _current.frames.length && !ret; i++)  {")
                        .append("      ret = findFrame(_current.frames[i], _target);")
                        .append("    }")
                        .append("  }")
                        .append("  return ret;")
                        .append("}")
                        .append("var fen = findFrame(top,\"eFapsFrameContent\");")
                        .append("if(!fen){")
                        .append("  fen = top;")
                        .append("}")
                        .append("fen.setTimeout(function(){ fen.childCallBack(\"javascript:")
                        //.append(getCallbackFunction("wicketAjaxGet('" + getCallbackUrl() + "'"))
                        .append("\");},0);");
        // the timeout is needed due to a bug in Firefox, that does not close the
        // nsIXMLHttpRequest and therefore throws an error that disables any
        // further JavaScript. The timeout is a workaround for this bug.
    }
}
