/*
 * Copyright 2007 Nikolai Holub.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsfcomponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.faces.renderkit.RenderKitUtils;

/**
 * Represents a multiple-selection component that consists of two lists and add/remove buttons.
 * One list (left list) contains available (not selected) options.
 * Another list (right list) contains selected options.
 * Options can be moved from one list to another with help of add/remove buttons.
 * Each list is rendered as HTML select element which is enclosed in HTML fieldset element.
 * List title is rendered as legend HTML element.
 *
 * @author Nikolai Holub (nikolai.holub at gmail.com)
 */
public class UISelectManyControl extends UIInput {

    /** Log. */
    private static Log log = LogFactory.getLog(UISelectManyControl.class);
    /** Length of array which stores component state. */
    private static final int STATE_ARRAY_LENGTH = 5;

    /** Left list title. */
    private String leftListTitle;
    /** Right list title. */
    private String rightListTitle;
    /** Style class for HTML fieldset element. */
    private String fieldsetStyleClass;
    /** Style class for HTML legend element. */
    private String legendStyleClass;

    /**
     * Create a new {@link UISelectManyControl} instance.
     */
    public UISelectManyControl() {
        if (log.isDebugEnabled()) {
            log.debug("constructor");
        }
    }

    /**
     * Convert the submitted value.
     *
     * @see UIInput#getConvertedValue(FacesContext, Object)
     * @param context {@link FacesContext} for the request we are processing
     * @param newSubmittedValue submitted value
     * @return converted value
     */
    @Override
    protected Object getConvertedValue(final FacesContext context,
            final Object newSubmittedValue) {
        Set<String> selectedValues;

        if (null != newSubmittedValue
                && newSubmittedValue instanceof String) {
            selectedValues = SelectManyControlRenderer.convertValue((String) newSubmittedValue);
        } else {
            selectedValues = Collections.emptySet();
        }

        return selectedValues;
    }

    /**
     * Decode the state from the request.
     *
     * @see UIInput#decode(FacesContext)
     * @param context {@link FacesContext} for the request we are processing
     */
    @Override
    public void decode(final FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();
        Map<String, String> requestParams = externalContext.getRequestParameterMap();

        String clientId = getClientId(context);
        String param = requestParams.get(clientId);

        if (log.isDebugEnabled()) {
            log.debug("decode - clientId = " + clientId);
            log.debug("decode - request parameter = {" + param + "}");
        }

        setSubmittedValue(param);
    }

    /**
     * Render current state.
     *
     * @see UIComponentBase#encodeEnd(FacesContext)
     * @param context {@link FacesContext} for the response we are creating
     * @throws IOException if an input/output error occurs while rendering
     */
    @SuppressWarnings("unchecked")
    @Override
    public void encodeEnd(final FacesContext context) throws IOException {
        String clientId = getClientId(context);

        if (log.isDebugEnabled()) {
            log.debug("encodeEnd - clientId = " + clientId);
            log.debug("encodeEnd - getValue() = " + getValue());
        }

        Iterator items = RenderKitUtils.getSelectItems(context, this);
        ArrayList<SelectItem> availableItems = new ArrayList<SelectItem>();

        while (items.hasNext()) {
            SelectItem item = (SelectItem) items.next();
            availableItems.add(item);
        }

        String html = new SelectManyControlRenderer(
                clientId, getProperties(),
                availableItems.toArray(new SelectItem[] {}),
                (Set<String>) getValue()).generateHtml();

        ResponseWriter out = context.getResponseWriter();
        out.write(html);
    }

    /**
     * Restore the state from the object.
     *
     * @see UIInput#restoreState(FacesContext, Object)
     * @param context {@link FacesContext}
     * @param state the state object used for restoring
     */
    @Override
    public void restoreState(final FacesContext context, final Object state) {
        Object[] values = (Object[]) state;

        int i = 0;
        super.restoreState(context, values[i++]);
        leftListTitle = (String) values[i++];
        rightListTitle = (String) values[i++];
        fieldsetStyleClass = (String) values[i++];
        legendStyleClass = (String) values[i++];
    }

    /**
     * Return the state as an object.
     *
     * @see UIInput#saveState(FacesContext)
     * @param context {@link FacesContext}
     * @return the state as an object
     */
    @Override
    public Object saveState(final FacesContext context) {
        Object[] values = new Object[STATE_ARRAY_LENGTH];

        int i = 0;
        values[i++] = super.saveState(context);
        values[i++] = leftListTitle;
        values[i++] = rightListTitle;
        values[i++] = fieldsetStyleClass;
        values[i++] = legendStyleClass;

        return values;
    }

    /**
     * Return the value of the <code>leftListTitle</code> property.
     *
     * @return the value of the <code>leftListTitle</code> property
     */
    public String getLeftListTitle() {
        return getPropertyValue(leftListTitle, "leftListTitle");
    }

    /**
     * Set the value of the <code>leftListTitle</code> property.
     *
     * @param leftListTitle property value
     */
    public void setLeftListTitle(final String leftListTitle) {
        this.leftListTitle = leftListTitle;
    }

    /**
     * Return the value of the <code>rightListTitle</code> property.
     *
     * @return the value of the <code>rightListTitle</code> property
     */
    public String getRightListTitle() {
        return getPropertyValue(rightListTitle, "rightListTitle");
    }

    /**
     * Set the value of the <code>rightListTitle</code> property.
     *
     * @param rightListTitle property value
     */
    public void setRightListTitle(final String rightListTitle) {
        this.rightListTitle = rightListTitle;
    }

    /**
     * Return the value of the <code>legendStyleClass</code> property.
     *
     * @return the value of the <code>legendStyleClass</code> property
     */
    public String getLegendStyleClass() {
        return getPropertyValue(legendStyleClass, "legendStyleClass");
    }

    /**
     * Set the value of the <code>legendStyleClass</code> property.
     *
     * @param legendStyleClass property value
     */
    public void setLegendStyleClass(final String legendStyleClass) {
        this.legendStyleClass = legendStyleClass;
    }

    /**
     * Return the value of the <code>fieldsetStyleClass</code> property.
     *
     * @return the value of the <code>fieldsetStyleClass</code> property
     */
    public String getFieldsetStyleClass() {
        return getPropertyValue(fieldsetStyleClass, "fieldsetStyleClass");
    }

    /**
     * Set the value of the <code>fieldsetStyleClass</code> property.
     *
     * @param fieldsetStyleClass property value
     */
    public void setFieldsetStyleClass(final String fieldsetStyleClass) {
        this.fieldsetStyleClass = fieldsetStyleClass;
    }

    /**
     * Return property value.
     *
     * @param value the value
     * @param name property name
     * @return property value.
     */
    private String getPropertyValue(final String value, final String name) {
        if (null != value) {
            return value;
        }

        ValueExpression ve = getValueExpression(name);

        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Return properties.
     *
     * @return properties
     */
    private Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<String, String>();

        properties.put("leftListTitle", getLeftListTitle());
        properties.put("rightListTitle", getRightListTitle());
        properties.put("fieldsetStyleClass", getFieldsetStyleClass());
        properties.put("legendStyleClass", getLegendStyleClass());

        return properties;
    }
}
