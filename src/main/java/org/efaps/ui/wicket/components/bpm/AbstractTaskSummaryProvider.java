/*
 * Copyright 2003 - 2013 The eFaps Team
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


package org.efaps.ui.wicket.components.bpm;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.efaps.admin.user.Role;
import org.efaps.db.Context;
import org.efaps.ui.wicket.models.objects.UITaskSummary;
import org.efaps.ui.wicket.util.Configuration;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
public abstract class AbstractTaskSummaryProvider
    extends SortableDataProvider<UITaskSummary, String>
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logging instance used in this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskSummaryProvider.class);

    /**
     * List of TaskSummary to be displayed.
     */
    private final List<UITaskSummary> summaries;

    /**
     * Constructor.
     */
    public AbstractTaskSummaryProvider()
    {
        String property = null;
        SortOrder order = null;
        try {
            property = Context.getThreadContext().getUserAttribute(getUserAttributeKey4SortProperty());
            final String orderTmp = Context.getThreadContext().getUserAttribute(getUserAttributeKey4SortOrder());
            if (orderTmp != null) {
                order = SortOrder.valueOf(orderTmp);
            }
        } catch (final EFapsException e) {
            // only UserAttributes ==> logging only
            AbstractTaskSummaryProvider.LOG.error("error on retrieving UserAttributes", e);
        }
        setSort(property == null ? "description" : property, order == null ? SortOrder.ASCENDING : order);
        this.summaries = getUITaskSummary();
    }

    @Override
    public Iterator<UITaskSummary> iterator(final long _first,
                                            final long _count)
    {
        final String sortprop = getSort().getProperty();
        final boolean asc = getSort().isAscending();

        try {
            Context.getThreadContext().setUserAttribute(getUserAttributeKey4SortOrder(),
                            asc ? SortOrder.ASCENDING.name() : SortOrder.DESCENDING.name());
            Context.getThreadContext().setUserAttribute(getUserAttributeKey4SortProperty(), sortprop);
        } catch (final EFapsException e) {
            // only UserAttributes ==> logging only
            AbstractTaskSummaryProvider.LOG.error("error on setting UserAttributes", e);
        }

        Collections.sort(this.summaries, new Comparator<UITaskSummary>()
        {

            @Override
            public int compare(final UITaskSummary _task0,
                               final UITaskSummary _task1)
            {
                final UITaskSummary task0;
                final UITaskSummary task1;
                if (asc) {
                    task0 = _task0;
                    task1 = _task1;
                } else {
                    task1 = _task0;
                    task0 = _task1;
                }
                int ret = 0;
                if ("description".equals(sortprop)) {
                    ret = task0.getDescription().compareTo(task1.getDescription());
                } else if ("activationTime".equals(sortprop)) {
                    ret = task0.getActivationTime().compareTo(task1.getActivationTime());
                } else if ("name".equals(sortprop)) {
                    ret = task0.getName().compareTo(task1.getName());
                } else if ("id".equals(sortprop)) {
                    ret = _task0.getId().compareTo(task1.getId());
                } else if ("status".equals(sortprop)) {
                    ret = task0.getStatus().compareTo(task1.getStatus());
                } else if ("owner".equals(sortprop)) {
                    ret = task0.getOwner().compareTo(task1.getOwner());
                }
                return ret;
            }
        });
        return this.summaries.subList(Long.valueOf(_first).intValue(), Long.valueOf(_first + _count).intValue())
                        .iterator();
    }

    /**
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     * @return size of the list of TaskSummary
     */
    @Override
    public long size()
    {
        return this.summaries.size();
    }

    /**
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
     * @param _object TaskSummary the model is wanted for
     * @return Model of TaskSummary
     */
    @Override
    public IModel<UITaskSummary> model(final UITaskSummary _object)
    {
        return Model.of(_object);
    }

    /**
     * Requery the data.
     */
    public void requery()
    {
        this.summaries.clear();
        this.summaries.addAll(getUITaskSummary());
    }

    /**
     * @return list of UITaskSummary.
     */
    protected abstract List<UITaskSummary> getUITaskSummary();

    /**
     * @return the key used to store the sort property as a UserAttribute.
     */
    protected abstract String getUserAttributeKey4SortProperty();

    /**
     * @return the key used to store the sort order as a UserAttribute.
     */
    protected abstract String getUserAttributeKey4SortOrder();

    /**
     * @return the number of rows presented per page
     */
    public abstract int getRowsPerPage();


    /**
     * @return true if the OID columns should be shown
     * @throws EFapsException on error
     */
    public boolean showOid()
        throws EFapsException
    {
        // Administration Role
        return Configuration.getAttributeAsBoolean(Configuration.ConfigAttribute.SHOW_OID)
                        && Context.getThreadContext().getPerson()
                                        .isAssigned(Role.get(UUID
                                                        .fromString("1d89358d-165a-4689-8c78-fc625d37aacd")));
    }
}