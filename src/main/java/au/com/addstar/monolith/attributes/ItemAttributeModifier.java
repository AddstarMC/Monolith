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

import java.util.Map;
import java.util.UUID;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.NumberConversions;

/**
 * An attribute modifier for items.
 * Has an extra EquipmentSlot specification
 * to limit where the modifier will apply.
 * This class, just like the parent class, is immutable
 */
public class ItemAttributeModifier extends AttributeModifier implements ConfigurationSerializable {
    private final EquipmentSlot slot;

    public ItemAttributeModifier(String name, double amount, Operation operation) {
        this(UUID.randomUUID(), name, amount, operation, null);
    }

    public ItemAttributeModifier(String name, double amount, Operation operation, EquipmentSlot slot) {
        this(UUID.randomUUID(), name, amount, operation, slot);
    }

    public ItemAttributeModifier(UUID uuid, String name, double amount, Operation operation) {
        this(uuid, name, amount, operation, null);
    }

    public ItemAttributeModifier(UUID uuid, String name, double amount, Operation operation, EquipmentSlot slot) {
        super(uuid, name, amount, operation);

        // Slot can be null
        this.slot = slot;
    }

    public static ItemAttributeModifier deserialize(Map<String, Object> args) {
        EquipmentSlot slot = null;
        if (args.containsKey("slot"))
            slot = EquipmentSlot.valueOf((String) args.get("slot"));

        return new ItemAttributeModifier((UUID) args.get("uuid"), (String) args.get("name"), NumberConversions.toDouble(args.get("amount")), Operation.values()[NumberConversions.toInt(args.get("operation"))], slot);
    }

    /**
     * Gets the slot where this modifier applies.
     *
     * @return The slot, or null if it applies everywhere
     */
    public EquipmentSlot getSlot() {
        return slot;
    }

    /**
     * Checks if this attribute modifier is specific to an equipment slot
     *
     * @return True if it is restricted
     */
    public boolean isSlotSpecific() {
        return slot != null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        if (slot != null)
            map.put("slot", slot.name());

        return map;
    }
}
