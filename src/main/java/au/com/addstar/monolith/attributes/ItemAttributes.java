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

package au.com.addstar.monolith.attributes;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.attribute.Attribute;

/**
 * Represents attribute modifiers stored on an item
 */
public interface ItemAttributes {
    /**
     * Gets all modifiers that modify the given attribute
     *
     * @param attribute The modified attribute
     * @return A collection of ItemAttributeModifier objects
     */
    Collection<ItemAttributeModifier> getModifiers(Attribute attribute);

    /**
     * Adds a modifier to this item
     *
     * @param attribute The attribute to modify
     * @param modifier  The modifier to add. The UUID of this modifier MUST be unique
     * @throws IllegalArgumentException Thrown if the uuid is not unique
     */
    void addModifier(Attribute attribute, ItemAttributeModifier modifier) throws IllegalArgumentException;

    /**
     * Removes a modifier from this item
     *
     * @param modifier The modifier to remove
     */
    void removeModifier(ItemAttributeModifier modifier);

    /**
     * Clears all modifiers that modify the given attribute
     *
     * @param attribute The attribute to clear modifiers for
     */
    void clearModifiers(Attribute attribute);

    /**
     * Clears all modifiers from the item
     */
    void clearModifiers();

    /**
     * Gets a modifier by its UUID
     *
     * @param id The id of the modifier
     * @return The modifier or null
     */
    ItemAttributeModifier getModifier(UUID id);

    /**
     * Gets all modifiers that have the given name
     *
     * @param name The name, case sensitive
     * @return A collection of ItemAttributeModifier instances
     */
    Collection<ItemAttributeModifier> getModifiers(String name);
}
