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

package au.com.addstar.monolith.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.google.common.collect.HashBiMap;

public class DynamicEnum<T> implements Iterable<T> {
    private final HashBiMap<T, String> mMembers;

    public DynamicEnum(Class<T> clazz) {
        mMembers = HashBiMap.create();
        registerAll(clazz);
    }

    @SuppressWarnings("unchecked")
    protected void registerAll(Class<T> matching) {
        for (Field field : getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && matching.isAssignableFrom(field.getType())) {
                try {
                    T value = (T) field.get(null);
                    if (value == null)
                        continue;

                    registerMember(value, field.getName());
                } catch (IllegalAccessException e) {
                    // Should not happen
                }
            }
        }
    }

    public void registerMember(T member, String name) {
        Validate.isTrue(!mMembers.inverse().containsKey(name), "Cannot register member with existing name");
        mMembers.put(member, name);
    }

    public boolean isMember(T member) {
        return mMembers.containsKey(member);
    }

    @Override
    public Iterator<T> iterator() {
        return mMembers.keySet().iterator();
    }

    public Set<T> values() {
        return mMembers.keySet();
    }

    public T valueOf(String name) {
        T member = mMembers.inverse().get(name);
        if (member == null)
            throw new IllegalArgumentException("No such member " + name);

        return member;
    }

    public String nameOf(T value) {
        String name = mMembers.get(value);
        if (name == null)
            throw new IllegalArgumentException(value + " is not a member of this dyn enum");
        return name;
    }
}
