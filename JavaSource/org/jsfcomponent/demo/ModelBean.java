package org.jsfcomponent.demo;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Model bean.
 *
 * @author Nikolai Holub (nikolai.holub at gmail.com)
 */
public class ModelBean {

    /** Log. */
    private Log log = LogFactory.getLog(ModelBean.class);

    /** Set of selected values. */
    private Set<String> selectedValues;

    /** Array of SelectItem objects. */
    private SelectItem[] items;

    /**
     * Create a new {@link ModelBean} instance.
     */
    public ModelBean() {
        log.debug("constructor");

        items = new SelectItem[8];

        for (int i = 0; i < items.length; i++) {
            items[i] = new SelectItem(i, "item" + i);
        }

        selectedValues = new LinkedHashSet<String>();
        selectedValues.add(String.valueOf(items[1].getValue()));
        selectedValues.add(String.valueOf(items[3].getValue()));
    }

    /**
     * Return Set of selected values.
     *
     * @return Set of selected values
     */
    public Set<String> getSelectedValues() {
        log.debug("getSelectedValues " + selectedValues);

        return selectedValues;
    }

    /**
     * Set selected values.
     *
     * @param selectedValues Set of selected values.
     */
    public void setSelectedValues(final Set<String> selectedValues) {
        log.debug("setSelectedValues " + selectedValues);

        this.selectedValues = selectedValues;
    }

    /**
     * Return Array of SelectItem objects.
     *
     * @return Array of SelectItem objects
     */
    public SelectItem[] getItems() {
        log.debug("getItems");

        return items;
    }
}
