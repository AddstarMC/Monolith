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

package au.com.addstar.monolith.lookup;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.inventory.ItemStack;

public class EntityDefinition {
    private final EntityType mType;
    private String mSubType;

    public EntityDefinition(EntityType type, String subtype) {
        mType = type;
        mSubType = subtype;
    }

    public EntityDefinition(Entity entity) {
        mType = entity.getType();

        if (entity instanceof Creeper) {
            if (((Creeper) entity).isPowered())
                mSubType = "POWERED";
        } else if (entity instanceof Rabbit)
            mSubType = ((Rabbit) entity).getRabbitType().name();
        else if (entity instanceof Ocelot)
            mSubType = ((Ocelot) entity).getCatType().name();
    }

    public EntityType getType() {
        return mType;
    }

    public String getSubType() {
        return mSubType;
    }

    public boolean isSpawnable() {
        switch (mType) {
            case UNKNOWN:
            case PLAYER:
            case FISHING_HOOK:
            case SPLASH_POTION:
            case DROPPED_ITEM:
                return false;
            default:
                return true;
        }
    }

    public Entity createEntity(Location location) {
        World world = location.getWorld();
        Entity entity;

        switch (mType) {
            case DROPPED_ITEM:
                entity = world.dropItem(location, new ItemStack(Material.STONE));
                break;
            default:
                entity = world.spawnEntity(location, mType);
                break;
        }

        if (entity == null) {
            return null;
        }

        if (mSubType != null) {
            switch (mType) {
                case CREEPER:
                    if (mSubType.equalsIgnoreCase("powered"))
                        ((Creeper) entity).setPowered(true);
                    break;
                case RABBIT:
                    ((Rabbit) entity).setRabbitType(Type.valueOf(mSubType.toUpperCase()));
                    break;
                case CAT:
                    ((Cat) entity).setCatType(Cat.Type.valueOf(mSubType.toUpperCase()));
                    break;
                // No other subtypes
                default:
                    break;
            }
        }

        return entity;
    }

    @Override
    public String toString() {
        if (mSubType != null)
            return String.format("%s:%s", mType.name(), mSubType);
        else
            return mType.name();
    }
}
