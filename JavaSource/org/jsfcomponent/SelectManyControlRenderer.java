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

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Generates HTML for {@link UISelectManyControl}.
 * Velocity is used for HTML generation.
 *
 * @author Nikolai Holub (nikolai.holub at gmail.com)
 */
class SelectManyControlRenderer {
    /** Log. */
    private static Log log = LogFactory.getLog(SelectManyControlRenderer.class);
    /** VelocityEngine. */
    private static VelocityEngine engine;
    /** Delimiter for selected values. */
    private static final String SELECTED_VALUES_DELIMITER = ";";
    /** Delimiter for JavaScript array. */
    private static final String JAVASCRIPT_ARRAY_DELIMITER = ",";
    /** HTML template path. */
    private static final String TEMPLATE = "org/jsfcomponent/SelectManyControl.vm";

    static {
        engine = new VelocityEngine();

        Properties properties = new Properties();
        properties.setProperty("resource.loader", "classpath");
        properties.setProperty(
                "classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        try {
            engine.init(properties);
        } catch (Exception e) {
            engine = null;
            log.error("error in Velocity initialization", e);
        }
    }

    /** UISelectManyControl clientId. */
    private String clientId;
    /** UISelectManyControl properties. */
    private Map<String, String> properties;

    /** "valuesArrayString" property. */
    private StringBuilder valuesArrayString;
    /** "leftListOptions" property. */
    private StringBuilder leftListOptions;
    /** "rightListOptions" property. */
    private StringBuilder rightListOptions;
    /** "selectedValuesString" property. */
    private StringBuilder selectedValuesString;

    /**
     * Create a new {@link SelectManyControlRenderer} instance.
     *
     * @param clientId UISelectManyControl clientId
     * @param properties UISelectManyControl properties
     * @param items Array of SelectItem objects
     * @param selectedValues Set of selected values
     */
    SelectManyControlRenderer(final String clientId,
            final Map<String, String> properties,
            final SelectItem[] items, final Set<String> selectedValues) {
        this.clientId = clientId;
        this.properties = properties;

        if (engine != null) {
            prepareProperties(items, selectedValues);
        }
    }

    /**
     * Generate HTML.
     *
     * @return generated HTML.
     */
    String generateHtml() {
        String content = "";

        if (engine != null) {
            try {
                VelocityContext context = new VelocityContext();

                context.put("clientId", clientId);
                context.put("valuesArrayString", valuesArrayString);
                context.put("leftListOptions", leftListOptions);
                context.put("rightListOptions", rightListOptions);
                context.put("selectedValuesString", selectedValuesString);

                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    context.put(entry.getKey(), entry.getValue());
                }

                Template template = engine.getTemplate(TEMPLATE);

                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                content = writer.toString();
            } catch (Exception e) {
                log.error("error in applying Velocity template", e);
            }
        }

        return content;
    }

    /**
     * Convert values string to a Set of values.
     *
     * @param valuesString values string
     * @return converted value
     */
    static Set<String> convertValue(final String valuesString) {
        Set<String> retVal;

        if (valuesString.length() > 0) {
            String[] values = valuesString.split(SELECTED_VALUES_DELIMITER);

            retVal = new LinkedHashSet<String>(Arrays.asList(values));
        } else {
            retVal = Collections.emptySet();
        }

        return retVal;
    }

    /**
     * Prepare properties for Velocity template.
     *
     * @param items Array of SelectItem objects
     * @param selectedValues Set of selected values
     */
    private void prepareProperties(
            final SelectItem[] items, final Set<String> selectedValues) {
        valuesArrayString = createValuesArrayString(items);

        leftListOptions = new StringBuilder();
        rightListOptions = new StringBuilder();
        selectedValuesString = new StringBuilder();

        for (int i = 0; i < items.length; i++) {
            String option = MessageFormat.format(
                    "<option value=\"{0}\">{1}</option>", i, items[i].getLabel());

            String itemStringValue = String.valueOf(items[i].getValue());

            if (selectedValues.contains(itemStringValue)) {
                rightListOptions.append(option);
                selectedValuesString.append(itemStringValue);
                selectedValuesString.append(SELECTED_VALUES_DELIMITER);
            } else {
                leftListOptions.append(option);
            }
        }
    }

    /**
     * Create "valuesArrayString" property.
     *
     * @param items Array of SelectItem objects
     * @return created "valuesArrayString" property
     */
    private static StringBuilder createValuesArrayString(final SelectItem[] items) {
        StringBuilder retVal = new StringBuilder();

        for (int i = 0; i < items.length - 1; i++) {
            retVal.append(items[i].getValue());
            retVal.append(JAVASCRIPT_ARRAY_DELIMITER);
        }

        if (items.length > 0) {
            retVal.append(items[items.length - 1].getValue());
        }

        return retVal;
    }
}
