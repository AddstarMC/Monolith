package au.com.addstar.monolith.internal.lookup;

import java.util.HashMap;
import java.util.Set;

import au.com.addstar.monolith.Monolith;
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
		Material mat = mNameMap.get(name.toLowerCase());
		Monolith.getInstance().DebugMsg("getByName(" + name + ") = " + mat);
		return mat;
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
