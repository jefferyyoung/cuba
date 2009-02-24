package com.haulmont.cuba.web.gui.components;

import com.itmill.toolkit.ui.AbstractSelect;
import com.haulmont.cuba.gui.components.List;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;

public abstract class AbstractListComponent<T extends AbstractSelect>
    extends
        AbstractActionOwnerComponent<T>
    implements
        List
{
    protected CollectionDatasource datasource;

    public boolean isMultiSelect() {
        return component.isMultiSelect();
    }

    public void setMultiSelect(boolean multiselect) {
        component.setMultiSelect(multiselect);
    }

    public <T> T getSingleSelected() {
        final Set selected = getSelecetdItemIds();
        return selected == null || selected.isEmpty() ?
                null : (T) datasource.getItem(selected.iterator().next());
    }

    public Set getSelected() {
        final Set<Object> itemIds = getSelecetdItemIds();

        if (itemIds != null) {
            final HashSet<Object> res = new HashSet<Object>();
            for (Object id : itemIds) {
                final Object o = datasource.getItem(id);
                res.add(o);
            }
            return res;
        } else {
            return Collections.emptySet();
        }
    }

    protected Set<Object> getSelecetdItemIds() {
        final Object value = component.getValue();
        if (value == null) {
            return null;
        } else if (value instanceof Collection) {
            return (Set) component.getValue();
        } else {
            return Collections.singleton(value);
        }
    }
}
