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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import com.google.common.collect.HashMultimap;

public class EnchantDB extends FlatDb<Enchantment>
{
	private HashMap<String, Enchantment> mNameMap;
	private HashMultimap<Enchantment, String> mEnchantMap;
	
	public EnchantDB()
	{
		mNameMap = new HashMap<>();
		mEnchantMap = HashMultimap.create();
	}
	
	public Enchantment getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getByEnchant(Enchantment item)
	{
		return mEnchantMap.get(item);
	}

	public void load(InputStream stream) throws IOException
	{
		super.load(stream);
		for(Enchantment enchant: Enchantment.values()){
			String name = enchant.getKey().getKey();
			mNameMap.put(name,enchant);
			if(!mEnchantMap.containsValue(name)) {
				mEnchantMap.put(enchant, name);
			}
		}
	}

	@Override
	Enchantment getObject(String... string) {
		String key = StringUtils.trim(string[0]).toLowerCase();
		String[] keyparts = key.split(":");
		NamespacedKey namekey;
		if (keyparts.length == 2){
			namekey = new NamespacedKey(keyparts[0],keyparts[1]);
		}else{
			namekey = NamespacedKey.minecraft(keyparts[0]);
		}

		Enchantment enchant = Enchantment.getByKey(namekey);
		return enchant;
	}

	@Override
	void saveObject(String string, Enchantment object) {
		mNameMap.put(string.toLowerCase(), object);
		mEnchantMap.put(object, string);
	}
}
