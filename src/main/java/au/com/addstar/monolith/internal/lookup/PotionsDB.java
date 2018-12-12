package au.com.addstar.monolith.internal.lookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.HashMultimap;

public class PotionsDB extends FlatDb<PotionEffectType>
{
	private HashMap<String, PotionEffectType> mNameMap;
	private HashMultimap<PotionEffectType, String> mIdMap;
	
	public PotionsDB()
	{
		mNameMap = new HashMap<>();
		mIdMap = HashMultimap.create();
	}
	
	public PotionEffectType getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getByEffect(PotionEffectType item)
	{
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
