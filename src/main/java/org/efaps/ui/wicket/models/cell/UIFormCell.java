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

import org.efaps.admin.datamodel.ui.FieldValue;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.db.Instance;
import org.efaps.ui.wicket.models.objects.AbstractUIObject;
import org.efaps.util.EFapsException;

/**
 * This class represents the model wich is used for rendering the components of
 * two cells inside a form. The first is holding the label for the second one
 * which contains the value. <br>
 * It uses a {@link org.efaps.admin.ui.field.Field} as the base for the data.
 *
 * @author The eFaps Team
 * @version $Id$
 */
public class UIFormCell
    extends UITableCell
{

    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instance variable storing the Value for the first cell as a Label.
     */
    private final String cellLabel;

    /**
     * Instance variable storing if in case of edit or create this field is
     * required.
     */
    private final boolean required;

    /**
     * Stored if the label should be hidden.
     */
    private final boolean hideLabel;

    /**
     * Stores the number of rows that must be spanned.
     */
    private final int rowSpan;

    /**
     * Stores the name of the type.
     */
    private final String attrTypeName;

    /**
     * Constructor used on search and create.
     *
     * @param _parent parent ui object
     * @param _fieldvalue FieldValue of the Cell
     * @param _cellValue Value of the Cell
     * @param _cellTitle title for the cell, if null will be set to _cellvalue
     * @param _label Label for the Cell
     * @param _attrTypeName Name of the Type of Attribute
     * @throws EFapsException on error
     */
    public UIFormCell(final AbstractUIObject _parent,
                      final FieldValue _fieldvalue,
                      final String _cellValue,
                      final String _cellTitle,
                      final String _label,
                      final String _attrTypeName)
        throws EFapsException
    {
        this(_parent, _fieldvalue, _fieldvalue.getInstance(), _cellValue, _cellTitle, null, _label, _attrTypeName);
    }

    /**
     * Constructor.
     *
     * @param _parent       parent ui object
     * @param _fieldValue   FieldValue used for this Cell
     * @param _instance     Instance of the Cell
     * @param _cellValue    Value for the Cell
     * @param _cellTitle    title for the cell, if null will be set to _cellvalue
     * @param _icon         icon of the cell
     * @param _label        Label of the Cell
     * @param _attrTypeName Name of the Type of Attribute
     * @throws EFapsException on error
     */
    // CHECKSTYLE:OFF
    public UIFormCell(final AbstractUIObject _parent,
                      final FieldValue _fieldValue,
                      final Instance _instance,
                      final String _cellValue,
                      final String _cellTitle,
                      final String _icon,
                      final String _label,
                      final String _attrTypeName)
        throws EFapsException
    {
     // CHECKSTYLE:ON
        super(_parent, _fieldValue, _instance, _cellValue, _cellTitle, _icon);
        this.required = _fieldValue.getField().isRequired()
                        && _fieldValue.getField().isEditableDisplay(_parent.getMode())
                        && (_parent.getMode().equals(TargetMode.EDIT) || _parent.getMode().equals(TargetMode.CREATE));
        this.cellLabel = DBProperties.getProperty(_label);
        this.hideLabel = _fieldValue.getField().isHideLabel();
        this.rowSpan = _fieldValue.getField().getRowSpan();
        this.attrTypeName = _attrTypeName;
        if (isAutoComplete()) {
            if (_fieldValue.getField().getCols() > 0) {
                getAutoCompleteSetting().setWidth(_fieldValue.getField().getCols());
            }
        }
    }



    /**
     * This is the getter method for the instance variable {@link #attrTypeName}
     * .
     *
     * @return value of instance variable {@link #attrTypeName}
     */
    public String getTypeName()
    {
        return this.attrTypeName;
    }

    /**
     * This is the getter method for the instance variable {@link #cellLabel}.
     *
     * @return value of instance variable {@link #cellLabel}
     */
    public String getCellLabel()
    {
        return this.cellLabel;
    }

    /**
     * This is the getter method for the instance variable {@link #required}.
     *
     * @return value of instance variable {@link #required}
     */
    public boolean isRequired()
    {
        return this.required;
    }

    /**
     * This is the getter method for the instance variable {@link #hideLabel}.
     *
     * @return value of instance variable {@link #hideLabel}
     */
    public boolean isHideLabel()
    {
        return this.hideLabel;
    }

    /**
     * This is the getter method for the instance variable {@link #rowSpan}.
     *
     * @return value of instance variable {@link #rowSpan}
     */
    public int getRowSpan()
    {
        return this.rowSpan;
    }
}
