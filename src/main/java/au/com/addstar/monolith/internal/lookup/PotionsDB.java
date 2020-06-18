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

import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.HashMultimap;

public class PotionsDB extends FlatDb<PotionEffectType> {
    private final HashMap<String, PotionEffectType> mNameMap;
    private final HashMultimap<PotionEffectType, String> mIdMap;

    public PotionsDB() {
        mNameMap = new HashMap<>();
        mIdMap = HashMultimap.create();
    }

    public PotionEffectType getByName(String name) {
        return mNameMap.get(name.toLowerCase());
    }

    public Set<String> getByEffect(PotionEffectType item) {
        return mIdMap.get(item);
    }

    @Override
    PotionEffectType getObject(String... string) {
        return PotionEffectType.getByName(string[0]);
    }

    @Override
    void saveObject(String string, PotionEffectType object) {
        mNameMap.put(string.toLowerCase(), object);
        mIdMap.put(object, string);
    }
}
