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

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.google.common.collect.Iterators;

import net.minecraft.server.v1_16_R2.NBTBase;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;

public class PropertyContainerImpl implements PropertyContainer {
    private final NBTTagList root;

    public PropertyContainerImpl(NBTTagList root) {
        this.root = root;
    }

    private static PropertyBase<?> loadProperty(NBTTagCompound tag) {
        switch (tag.getByte("type")) {
            case PropertyBase.TYPE_STRING:
                return new StringProperty(tag);
            case PropertyBase.TYPE_INTEGER:
                return new IntegerProperty(tag);
            case PropertyBase.TYPE_FLOAT:
                return new FloatProperty(tag);
            case PropertyBase.TYPE_CUSTOM:
                return new CustomProperty(tag);
            default:
                return null;
        }
    }

    public NBTTagList getRoot() {
        return root;
    }

    @Override
    public PropertyBase<?> get(String name, UUID owner) throws PropertyClassException {
        for (NBTBase aRoot : root) {
            if (aRoot instanceof NBTTagCompound) {
                NBTTagCompound raw = (NBTTagCompound) aRoot;
                PropertyBase<?> property = loadProperty(raw);
                if (property != null && property.getName() != null) {
                    if (property.getName().equals(name) && property.getOwner().equals(owner))
                        return property;
                }
            } else {
                throw new PropertyClassException(aRoot.toString() + " is not a Compound " +
                        "tag");
            }
        }

        return null;
    }

    @Override
    public String getString(String name, UUID owner) throws ClassCastException {
        PropertyBase<?> prop = get(name, owner);
        if (prop instanceof StringProperty)
            return ((StringProperty) prop).getValue();
        else if (prop == null)
            return null;
        else
            throw new ClassCastException("Property type does not match");
    }

    @Override
    public Integer getInt(String name, UUID owner) throws ClassCastException {
        PropertyBase<?> prop = get(name, owner);
        if (prop instanceof IntegerProperty)
            return ((IntegerProperty) prop).getValue();
        else if (prop == null)
            return null;
        else
            throw new ClassCastException("Property type does not match");
    }

    @Override
    public Double getFloat(String name, UUID owner) throws ClassCastException {
        PropertyBase<?> prop = get(name, owner);
        if (prop instanceof FloatProperty)
            return ((FloatProperty) prop).getValue();
        else if (prop == null)
            return null;
        else
            throw new ClassCastException("Property type does not match");
    }

    @Override
    public ConfigurationSerializable getCustom(String name, UUID owner) throws ClassCastException {
        PropertyBase<?> prop = get(name, owner);
        if (prop instanceof CustomProperty)
            return ((CustomProperty) prop).getValue();
        else if (prop == null)
            return null;
        else
            throw new ClassCastException("Property type does not match");
    }

    @Override
    public void add(PropertyBase<?> property) {
        // Remove any existing one if any
        remove(property.getName(), property.getOwner());
        root.add(property.getTag());
    }

    @Override
    public void remove(String name, UUID owner) {
        for (int i = 0; i < root.size(); ++i) {
            if (root.get(i) instanceof NBTTagCompound) {
                NBTTagCompound raw = (NBTTagCompound) root.get(i);
                PropertyBase<?> property = loadProperty(raw);
                if (property.getName() != null) {
                    if (property.getName().equals(name) && property.getOwner().equals(owner)) {
                        root.remove(i);
                        break;
                    }
                }
            }

        }
    }

    @Override
    public void clear(UUID owner) {
        for (int i = 0; i < root.size(); ++i) {
            if (root.get(i) instanceof NBTTagCompound) {
                NBTTagCompound raw = (NBTTagCompound) root.get(i);
                PropertyBase<?> property = loadProperty(raw);
                if (property.getOwner() != null) {
                    if (property.getOwner().equals(owner)) {
                        root.remove(i);
                        --i;
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        while (!root.isEmpty())
            root.remove(0);
    }

    @Override
    public Iterable<PropertyBase<?>> getAllProperties(final UUID owner) {
        return () -> Iterators.filter(new PropertyIterator(), property -> property.getOwner().equals(owner));
    }

    @Override
    public Iterable<PropertyBase<?>> getAllProperties() {
        return PropertyIterator::new;
    }

    @Override
    public PropertyContainerImpl clone() {
        return new PropertyContainerImpl(root.clone());
    }

    private class PropertyIterator implements Iterator<PropertyBase<?>> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < root.size();
        }

        @Override
        public PropertyBase<?> next() {
            NBTBase base = root.get(index++);
            if (base instanceof NBTTagCompound) {
                NBTTagCompound raw = (NBTTagCompound) base;
                return loadProperty(raw);
            } else {
                throw new PropertyClassException(base.toString() + " is not a compound tag " +
                        "compatible" +
                        " with properties");
            }
        }

        @Override
        public void remove() {
            if (index > 0)
                root.remove(--index);
        }
    }
}
