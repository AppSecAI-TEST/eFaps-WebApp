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

package org.efaps.ui.wicket.pages.dashboard;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.efaps.bpm.BPM;
import org.efaps.ui.wicket.models.objects.UITaskSummary;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id: SortableTaskSummaryDataProvider.java 9249 2013-04-23 15:50:17Z
 *          jan@moxter.net $
 */
public class SortableTaskSummaryDataProvider
    extends SortableDataProvider<UITaskSummary, String>
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of TaskSummary to be displayed.
     */
    private final List<UITaskSummary> summaries;

    /**
     * Constructor.
     */
    public SortableTaskSummaryDataProvider()
    {
        // set default sort
        setSort("description", SortOrder.ASCENDING);
        this.summaries = UITaskSummary.getUITaskSummary(BPM.getTasksAssignedAsPotentialOwner());
    }

    @Override
    public Iterator<UITaskSummary> iterator(final long _first,
                                          final long _count)
    {
        final String sortprop = getSort().getProperty();
        final boolean asc = getSort().isAscending();
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
                }else if ("owner".equals(sortprop)) {
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
}
