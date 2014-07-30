package au.com.addstar.monolith.lookup;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialDefinition
{
	private Material mMaterial;
	private short mData;
	
	public MaterialDefinition(Material material, short data)
	{
		mMaterial = material;
		mData = data;
	}
	
	public ItemStack asItemStack(int size)
	{
		return new ItemStack(mMaterial, size, mData);
	}
	
	@SuppressWarnings( "deprecation" )
	public MaterialData asMaterialData()
	{
		return mMaterial.getNewData((byte)mData);
	}
	
	public Material getMaterial()
	{
		return mMaterial;
	}
	
	public short getData()
	{
		return mData;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof MaterialDefinition))
			return false;
		
		return ((MaterialDefinition)obj).mData == mData && ((MaterialDefinition)obj).mMaterial == mMaterial;
	}
	
	@SuppressWarnings( "deprecation" )
	@Override
	public int hashCode()
	{
		return mMaterial.getId() << 16 | mData;
	}
	
	@Override
	public String toString()
	{
		return mMaterial.name() + ":" + mData;
	}
	
	public static MaterialDefinition from(ItemStack item)
	{
		return new MaterialDefinition(item.getType(), item.getDurability());
	}
}
