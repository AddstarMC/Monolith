package au.com.addstar.monolith.internal.lookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.HashMultimap;

public class ItemDB
{
	private HashMap<String, ItemStack> mNameMap;
	private HashMultimap<ItemStack, String> mIdMap;
	
	public ItemDB()
	{
		mNameMap = new HashMap<String, ItemStack>();
		mIdMap = HashMultimap.create();
	}
	
	public ItemStack getByName(String name)
	{
		return mNameMap.get(name.toLowerCase());
	}
	
	public Set<String> getById(ItemStack item)
	{
		if(item.getAmount() == 0)
			return mIdMap.get(item);
		else
		{
			ItemStack stack = item.clone();
			stack.setAmount(0);
			return mIdMap.get(stack);
		}
	}
	
	public Set<String> getById(Material material, int data)
	{
		return getById(new ItemStack(material, 0, (short)data));
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
	
	@SuppressWarnings( "deprecation" )
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
			if(parts.length != 3)
				continue;
			
			String name = parts[0];
			Material material = null;
			try
			{
				int id = Integer.parseInt(parts[1]);
				material = Material.getMaterial(id);
			}
			catch(NumberFormatException e)
			{
			}
			
			if(material == null)
				material = Material.getMaterial(parts[1]);
			
			if(material == null)
				continue;
			
			byte data = 0;
			try
			{
				data = Byte.parseByte(parts[2]);
				if(data < 0)
					continue;
			}
			catch(NumberFormatException e)
			{
				continue;
			}
			
			ItemStack stack = new ItemStack(material, 0, (short)data);
			mNameMap.put(name.toLowerCase(), stack);
			mIdMap.put(stack, name);
		}
	}
}
