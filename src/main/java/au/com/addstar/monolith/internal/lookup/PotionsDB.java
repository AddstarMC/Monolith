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

public class PotionsDB
{
	private HashMap<String, PotionEffectType> mNameMap;
	private HashMultimap<PotionEffectType, String> mIdMap;
	
	public PotionsDB()
	{
		mNameMap = new HashMap<String, PotionEffectType>();
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
	
	public void load(File file) throws IOException
	{
		FileInputStream stream = new FileInputStream(file);
		
		try
		{
			load(stream);
		}
		finally
		{
			stream.close();
		}
	}
	
	public void load(InputStream stream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		mNameMap.clear();
		mIdMap.clear();
		
		while(reader.ready())
		{
			String line = reader.readLine();
			if(line.startsWith("#"))
				continue;
			
			String[] parts = line.split(",");
			if(parts.length != 2)
				continue;
			
			String name = parts[0];
			PotionEffectType enchant = PotionEffectType.getByName(parts[1]);
			if(enchant == null)
				continue;
			
			mNameMap.put(name.toLowerCase(), enchant);
			mIdMap.put(enchant, name);
		}
	}
}
