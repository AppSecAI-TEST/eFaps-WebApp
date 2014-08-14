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

package org.efaps.ui.wicket.models.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.efaps.admin.datamodel.ui.UIValue;
import org.efaps.db.Instance;
import org.efaps.ui.wicket.components.values.LabelField;
import org.efaps.ui.wicket.models.AbstractInstanceObject;
import org.efaps.ui.wicket.models.cell.FieldConfiguration;
import org.efaps.ui.wicket.models.field.factories.BitEnumUIFactory;
import org.efaps.ui.wicket.models.field.factories.BooleanUIFactory;
import org.efaps.ui.wicket.models.field.factories.DateTimeUIFactory;
import org.efaps.ui.wicket.models.field.factories.DateUIFactory;
import org.efaps.ui.wicket.models.field.factories.DecimalUIFactory;
import org.efaps.ui.wicket.models.field.factories.EnumUIFactory;
import org.efaps.ui.wicket.models.field.factories.IComponentFactory;
import org.efaps.ui.wicket.models.field.factories.JaxbUIFactory;
import org.efaps.ui.wicket.models.field.factories.LinkWithRangesUIFactory;
import org.efaps.ui.wicket.models.field.factories.NumberUIFactory;
import org.efaps.ui.wicket.models.field.factories.StringUIFactory;
import org.efaps.ui.wicket.models.field.factories.UserUIFactory;
import org.efaps.ui.wicket.models.objects.AbstractUIModeObject;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public abstract class AbstractUIField
    extends AbstractInstanceObject
{
    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The factories used to construct the components.
     */
    private static final List<IComponentFactory> FACTORIES = new ArrayList<IComponentFactory>();

    static {
        AbstractUIField.FACTORIES.add(StringUIFactory.get());
        AbstractUIField.FACTORIES.add(LinkWithRangesUIFactory.get());
        AbstractUIField.FACTORIES.add(BooleanUIFactory.get());
        AbstractUIField.FACTORIES.add(DateUIFactory.get());
        AbstractUIField.FACTORIES.add(DateTimeUIFactory.get());
        AbstractUIField.FACTORIES.add(DecimalUIFactory.get());
        AbstractUIField.FACTORIES.add(NumberUIFactory.get());
        AbstractUIField.FACTORIES.add(UserUIFactory.get());
        AbstractUIField.FACTORIES.add(EnumUIFactory.get());
        AbstractUIField.FACTORIES.add(BitEnumUIFactory.get());
        AbstractUIField.FACTORIES.add(JaxbUIFactory.get());
    }
    /**
     * Configuration of the related field.
     */
    private FieldConfiguration fieldConfiguration;

    /**
     * Parent Object.
     */
    private final AbstractUIModeObject parent;

    /**
     * UserInterface Value.
     */
    private UIValue value;

    /**
     * @param _instanceKey key to the instance
     * @param _parent       parent object
     * @param _value        value
     * @throws EFapsException on error
     */
    public AbstractUIField(final String _instanceKey,
                           final AbstractUIModeObject _parent,
                           final UIValue _value)
        throws EFapsException
    {
        super(_instanceKey);
        this.parent = _parent;
        this.value = _value;
        this.fieldConfiguration = getNewFieldConfiguration();
    }

    /**
     * Getter method for the instance variable {@link #parent}.
     *
     * @return value of instance variable {@link #parent}
     */
    public AbstractUIModeObject getParent()
    {
        return this.parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasInstanceManager()
        throws EFapsException
    {
        return getParent() != null ? getParent().hasInstanceManager() : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instance getInstanceFromManager()
        throws EFapsException
    {
        return getParent().getInstanceFromManager();
    }

    /**
     * Getter method for the instance variable {@link #value}.
     *
     * @return value of instance variable {@link #value}
     */
    public UIValue getValue()
    {
        return this.value;
    }

    /**
     * Setter method for instance variable {@link #value}.
     *
     * @param _value value for instance variable {@link #value}
     */
    public void setValue(final UIValue _value)
    {
        this.value = _value;
    }

    /**
     * Getter method for the instance variable {@link #fieldConfiguration}.
     *
     * @return value of instance variable {@link #fieldConfiguration}
     */
    public FieldConfiguration getFieldConfiguration()
    {
        return this.fieldConfiguration;
    }

    /**
     * Setter method for instance variable {@link #fieldConfiguration}.
     *
     * @param _fieldConfiguration value for instance variable {@link #fieldConfiguration}
     */

    protected void setFieldConfiguration(final FieldConfiguration _fieldConfiguration)
    {
        this.fieldConfiguration = _fieldConfiguration;
    }

    /**
     * @return a new FieldConfiguration
     * @throws EFapsException on error
     */
    protected FieldConfiguration getNewFieldConfiguration()
        throws EFapsException
    {
        FieldConfiguration ret;
        if (getValue() == null) {
            ret = null;
        } else {
            ret = new FieldConfiguration(getValue().getField().getId());
        }
        return ret;
    }

    /**
     * @return is this value editable
     */
    public boolean editable()
    {
        return getValue().getField().isEditableDisplay(getParent().getMode());
    }

    /**
     * @return the List of Factories used for this Field on construction of the component.
     */
    protected List<IComponentFactory> getFactories()
    {
        return AbstractUIField.FACTORIES;
    }

    /**
     * @param _wicketId wicket id
     * @return Component
     * @throws EFapsException on error
     */
    public Component getComponent(final String _wicketId)
        throws EFapsException
    {
        Component ret = null;
        for (final IComponentFactory factory : getFactories()) {
            if (editable()) {
                ret = factory.getEditable(_wicketId, this);
            } else {
                ret = factory.getReadOnly(_wicketId, this);
            }
            if (ret != null) {
                break;
            }
        }

        if (ret == null) {
            ret = new LabelField(_wicketId, "No Factory was applied successfully", this.fieldConfiguration, "NONE");
        }
        return ret;
    }

    @Override
    public String toString()
    {
        return getValue().toString();
    }
}
