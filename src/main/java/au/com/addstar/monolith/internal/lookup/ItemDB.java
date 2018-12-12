package au.com.addstar.monolith.internal.lookup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;


import com.google.common.collect.HashMultimap;

public class ItemDB extends FlatDb<Material>
{
	private HashMap<String, Material> mNameMap;
	private HashMultimap<Material, String> mIdMap;
	
	public ItemDB()
	{
		mNameMap = new HashMap<>();
		mIdMap = HashMultimap.create();
	}
	
	public Material getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getbyMaterial(Material mat)
	{
		return mIdMap.get(mat);
	}

	@Override
	Material getObject(String... string) {
		return Material.matchMaterial(string[0]);
	}

	@Override
	void saveObject(String string, Material object) {
		mNameMap.put(string.toLowerCase(), object);
		mIdMap.put(object, string);
	}
}
