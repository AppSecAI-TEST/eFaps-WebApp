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

package org.efaps.ui.wicket.models.cell;

import java.util.List;
import java.util.Map;

import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.ui.field.FieldCommand;
import org.efaps.db.Instance;
import org.efaps.ui.wicket.models.objects.AbstractUIObject;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class UIFormCellCmd

{
    /**
     * Enum is used to set for this UIFormCellCmd which status of execution it
     * is in.
     */
    public enum ExecutionStatus {
        /** Method evaluateRenderedContent is executed. */
        RENDER,
        /** Method execute is executed. */
        EXECUTE;
    }

    /**
     * Must a button be rendered.
     */
    private final boolean renderButton;

    /**
     * Stores the actual execution status.
     */
    private ExecutionStatus executionStatus;

    /**
     * Must the field be appended.
     */
    private final boolean append;

    /**
     * Target field for the value.
     */
    private final String targetField;

    /**
     * The icon for the button.
     */
    private final String buttonIcon;

    /**
     * @param _parent   Parent object
     * @param _field    field this cellbelongs to
     * @param _instance instance this field belongs to
     * @param _label    label of the field
     * @throws EFapsException   on error
     */
    public UIFormCellCmd(final AbstractUIObject _parent,
                         final FieldCommand _field,
                         final Instance _instance,
                         final String _label)
        throws EFapsException
    {
        this.renderButton = _field.isRenderButton();
        this.append = _field.isAppend();
        this.targetField = _field.getTargetField();
        this.buttonIcon =  _field.getButtonIcon();
    }

    /**
     * Execute the underlying events.
     * @param _others others
     * @return list of returns
     * @throws EFapsException on error
     */
    public List<Return> executeEvents(final Object _others,
                                      final Map<String, String> _uiID2Oid)
        throws EFapsException
    {
        if (this.executionStatus == null) {
            this.executionStatus = UIFormCellCmd.ExecutionStatus.EXECUTE;
        }
        //final List<Return> ret = executeEvents(EventType.UI_FIELD_CMD, _others, _uiID2Oid);

        if (this.executionStatus == UIFormCellCmd.ExecutionStatus.EXECUTE) {
            this.executionStatus = null;
        }
        return null;
    }

    /**
     * Getter method for instance variable {@link #renderButton}.
     *
     * @return value of instance variable {@link #renderButton}
     */
    public boolean isRenderButton()
    {
        return this.renderButton;
    }

    /**
     * Getter method for instance variable {@link #append}.
     *
     * @return value of instance variable {@link #append}
     */
    public boolean isAppend()
    {
        return this.append;
    }

    /**
     * Get the script to render the content for the UserInterface in
     * case that not a standard button should be rendered.
     *
     * @param _script additional script from the UserInterface
     * @return html snipplet
     * @throws EFapsException on error
     *
     */
    public String getRenderedContent(final String _script,
                                     final Map<String, String> _uiID2Oid)
        throws EFapsException
    {
        this.executionStatus = UIFormCellCmd.ExecutionStatus.RENDER;
        final StringBuilder snip = new StringBuilder();
        final List<Return> returns = executeEvents(_script, _uiID2Oid);
        for (final Return oneReturn : returns) {
            if (oneReturn.contains(ReturnValues.SNIPLETT)) {
                snip.append(oneReturn.get(ReturnValues.SNIPLETT));
            }
        }
        this.executionStatus = null;
        return snip.toString();
    }

    /**
     * Getter method for instance variable {@link #executionStatus}.
     *
     * @return value of instance variable {@link #executionStatus}
     */
    public ExecutionStatus getExecutionStatus()
    {
        return this.executionStatus;
    }

    /**
     * Get the field this UIFormCellCmd belongs to.
     * @return fieldcommand
     */
    public FieldCommand getFieldCommand()
    {
        return null;
    }

    /**
     * Getter method for the instance variable {@link #buttonIcon}.
     *
     * @return value of instance variable {@link #buttonIcon}
     */
    public String getButtonIcon()
    {
        return this.buttonIcon;
    }

    /**
     * @return true if not null
     */
    public boolean isTargetField()
    {
        return this.targetField != null;
    }

    /**
     * {@inheritDoc}
     */

    public boolean isHideLabel()
    {
        return this.renderButton;
    }

    /**
     * Getter method for instance variable {@link #targetField}.
     *
     * @return value of instance variable {@link #targetField}
     */
    public String getTargetField()
    {
        return this.targetField;
    }
}
