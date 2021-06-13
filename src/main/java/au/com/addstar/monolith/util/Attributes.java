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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.attribute.Attribute;


public class Attributes {
    private static final BiMap<Attribute, String> ids;

    static {
        ids = HashBiMap.create();
        ids.put(Attribute.GENERIC_ARMOR, "generic.armor");
        ids.put(Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage");
        ids.put(Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed");
        ids.put(Attribute.GENERIC_FOLLOW_RANGE, "generic.followRange");
        ids.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, "generic.knockbackResistance");
        ids.put(Attribute.GENERIC_LUCK, "generic.luck");
        ids.put(Attribute.GENERIC_MAX_HEALTH, "generic.maxHealth");
        ids.put(Attribute.GENERIC_MOVEMENT_SPEED, "generic.movementSpeed");
        ids.put(Attribute.HORSE_JUMP_STRENGTH, "horse.jumpStrength");
        ids.put(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, "zombie.spawnReinforcements");
        ids.put(Attribute.GENERIC_FLYING_SPEED, "generic.flySpeed");
        ids.put(Attribute.GENERIC_ARMOR_TOUGHNESS, "generic.armorToughness");
    }

    public static Attribute fromId(String id) {
        return ids.inverse().get(id);
    }

    public static String getId(Attribute attribute) {
        return ids.get(attribute);
    }
}
