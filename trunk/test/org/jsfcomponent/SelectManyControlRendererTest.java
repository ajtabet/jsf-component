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

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link SelectManyControlRenderer} test.
 *
 * @author Nikolai Holub (nikolai.holub at gmail.com)
 */
public class SelectManyControlRendererTest {

    /** File path for generated HTML. */
    private static final String GENERATED_FILE = "generated/SelectManyControl.html";

    /**
     * Test method.
     *
     * @throws IOException if IOException occurs
     */
    @Test
    public void test() throws IOException {
        // prepare available items
        SelectItem[] items = new SelectItem[8];

        for (int i = 0; i < items.length; i++) {
            items[i] = new SelectItem(i, "item" + i);
        }

        // prepare selected values
        Set<String> selectedValues = new LinkedHashSet<String>();

        selectedValues.add(String.valueOf(items[1].getValue()));
        selectedValues.add(String.valueOf(items[3].getValue()));

        // prepare properties
        Map<String, String> properties = new HashMap<String, String>();

        properties.put("leftListTitle", "Available items");
        properties.put("rightListTitle", "Selected items");
        properties.put("fieldsetStyleClass", "SelectManyControl_fieldset");
        properties.put("legendStyleClass", "SelectManyControl_legend");

        // generate HTML
        String html = new SelectManyControlRenderer(
                "clientId", properties,
                items,
                selectedValues).generateHtml();

        // all template variables should be resolved
        Assert.assertFalse(html.contains("$"));

        // save generated HTML to the file
        FileWriter writer = new FileWriter(GENERATED_FILE);
        writer.write(html);
        writer.close();
    }
}
