/*
 * Copyright (c) 2020. AddstarMC
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

package au.com.addstar.monolith.properties;

import java.util.UUID;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

/**
 * Represents a property that holds an integer
 */
public class IntegerProperty extends PropertyBase<Integer> {
    /**
     * Creates a new int based property
     *
     * @param name  The name of the property
     * @param owner The UUID of the owner for grouping purposes
     * @param value The value to hold
     */
    public IntegerProperty(String name, UUID owner, int value) {
        super(name, owner);
        tag.setByte("type", TYPE_INTEGER);
        setValue(value);
    }

    IntegerProperty(NBTTagCompound tag) {
        super(tag);
    }

    @Override
    public Integer getValue() {
        return tag.getInt("value");
    }

    @Override
    public void setValue(Integer value) {
        tag.setInt("value", value);
    }
}
