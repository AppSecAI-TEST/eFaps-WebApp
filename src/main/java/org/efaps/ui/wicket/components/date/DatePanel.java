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

package org.efaps.ui.wicket.components.date;

import org.apache.wicket.model.Model;
import org.efaps.ui.wicket.models.field.AbstractUIField;
import org.efaps.ui.wicket.models.field.FieldConfiguration;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
public class DatePanel
    extends DateTimePanel
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param _wicketId
     * @param _of
     * @param _fieldConfiguration
     */
    public DatePanel(final String _wicketId,
                     final Model<AbstractUIField> _model,
                     final FieldConfiguration _fieldConfiguration,
                     final Object _object)
        throws EFapsException
    {
        super(_wicketId, _model, _fieldConfiguration, _object, false);
    }
}
