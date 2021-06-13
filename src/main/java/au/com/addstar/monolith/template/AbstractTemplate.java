/*
 * Copyright (c) 2021. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.template;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;

import com.google.common.collect.Maps;

public abstract class AbstractTemplate<HolderType, Holder> {
    private final Map<TemplateSetting<HolderType, Holder, Object>, Object> mSettings;

    public AbstractTemplate() {
        mSettings = Maps.newIdentityHashMap();
    }

    public abstract HolderType getType();

    @SuppressWarnings("unchecked")
    public <T> AbstractTemplate<HolderType, Holder> set(TemplateSetting<HolderType, Holder, T> setting, T value) {
        Validate.notNull(setting);
        if (!setting.appliesTo(getType()))
            throw new IllegalArgumentException("Setting " + setting.getNames()[0] + " does not apply to " + getType());

        mSettings.put((TemplateSetting<HolderType, Holder, Object>) setting, value);
        return this;
    }

    public AbstractTemplate<HolderType, Holder> clear(TemplateSetting<HolderType, Holder, ?> setting) {
        Validate.notNull(setting);

        mSettings.remove(setting);

        return this;
    }

    public boolean isSet(TemplateSetting<HolderType, Holder, ?> setting) {
        return mSettings.containsKey(setting);
    }

    protected void applyTemplate(Holder object) {
        // Apply specified settings
        for (Entry<TemplateSetting<HolderType, Holder, Object>, Object> entry : mSettings.entrySet())
            entry.getKey().apply(object, entry.getValue());
    }
}
