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

package au.com.addstar.monolith.internal.lookup;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.EntityType;

import au.com.addstar.monolith.lookup.EntityDefinition;

import com.google.common.collect.HashMultimap;

public class EntityDB extends FlatDb<EntityDefinition> {
    private final HashMap<String, EntityDefinition> mNameMap;
    private final HashMultimap<EntityDefinition, String> mIdMap;

    public EntityDB() {
        mNameMap = new HashMap<>();
        mIdMap = HashMultimap.create();
    }

    public EntityDefinition getByName(String name) {
        return mNameMap.get(name.toLowerCase());
    }

    public Set<String> getByType(EntityDefinition item) {
        return mIdMap.get(item);
    }

    public Set<String> getAllTypes() {
        return mNameMap.keySet();
    }

    @Override
    EntityDefinition getObject(String... string) {
        try {
            EntityType type = EntityType.valueOf(string[0].toUpperCase().trim());

            String subType = null;
            if (string.length == 2)
                subType = string[1].toUpperCase();

            return new EntityDefinition(type, subType);
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).warning(StringUtils.join(string, ",") + " cannot be parsed to an entity");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    void saveObject(String string, EntityDefinition object) {
        mNameMap.put(string.toLowerCase(), object);
        mIdMap.put(object, string);
    }
}
