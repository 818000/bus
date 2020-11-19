/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.image.galaxy;

import org.aoju.bus.core.lang.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
public class ConfigurationChange {

    private final List<ModifiedObject> objects = new ArrayList<>();
    private final boolean verbose;

    public ConfigurationChange(boolean verbose) {
        this.verbose = verbose;
    }

    public static <T> T nullifyIfNotVerbose(ConfigurationChange diffs, T obj) {
        return diffs != null && diffs.isVerbose() ? obj : null;
    }

    public static ModifiedObject addModifiedObjectIfVerbose(ConfigurationChange diffs, String dn, ChangeType changeType) {
        if (diffs == null || !diffs.isVerbose())
            return null;

        ModifiedObject object = new ModifiedObject(dn, changeType);
        diffs.add(object);
        return object;
    }

    public static ModifiedObject addModifiedObject(ConfigurationChange diffs, String dn, ChangeType changeType) {
        if (diffs == null)
            return null;

        ModifiedObject object = new ModifiedObject(dn, changeType);
        diffs.add(object);
        return object;
    }

    public static void removeLastIfEmpty(ConfigurationChange diffs, ModifiedObject obj) {
        if (obj != null && obj.isEmpty())
            diffs.removeLast();
    }

    private void removeLast() {
        objects.remove(objects.size() - 1);
    }

    public List<ModifiedObject> modifiedObjects() {
        return objects;
    }

    public void add(ModifiedObject object) {
        objects.add(object);
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public boolean isVerbose() {
        return verbose;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder(objects.size() * 64);
        for (ModifiedObject obj : objects) {
            sb.append(obj.changeType).append(Symbol.C_SPACE).append(obj.dn).append(Symbol.C_LF);
            if (obj.attributes != null) {
                for (ModifiedAttribute attr : obj.attributes) {
                    sb.append(Symbol.SPACE).append(attr.name).append(": ")
                            .append(attr.removedValues).append("=>")
                            .append(attr.addedValues).append(Symbol.C_LF);
                }
            }
        }
        return sb.toString();
    }

    public enum ChangeType {C, U, D}

    public static class ModifiedAttribute {
        private final String name;
        private final List<Object> addedValues = new ArrayList<>(1);
        private final List<Object> removedValues = new ArrayList<>(1);

        public ModifiedAttribute(String name) {
            this.name = name;
        }

        public ModifiedAttribute(String name, Object prev, Object val) {
            this.name = name;
            removeValue(prev);
            addValue(val);
        }

        public String name() {
            return name;
        }

        public List<Object> addedValues() {
            return addedValues;
        }

        public List<Object> removedValues() {
            return removedValues;
        }

        public void addValue(Object value) {
            if (value != null && !removedValues.remove(value))
                addedValues.add(value);
        }

        public void removeValue(Object value) {
            if (value != null && !addedValues.remove(value))
                removedValues.add(value);
        }

    }

    public static class ModifiedObject {
        private final String dn;
        private final ChangeType changeType;
        private final List<ModifiedAttribute> attributes = new ArrayList<>();

        public ModifiedObject(String dn, ChangeType changeType) {
            this.dn = dn;
            this.changeType = changeType;
        }

        public String dn() {
            return dn;
        }

        public ChangeType changeType() {
            return changeType;
        }

        public boolean isEmpty() {
            return attributes.isEmpty();
        }

        public List<ModifiedAttribute> modifiedAttributes() {
            return attributes;
        }

        public void add(ModifiedAttribute attribute) {
            this.attributes.add(attribute);
        }
    }

}
