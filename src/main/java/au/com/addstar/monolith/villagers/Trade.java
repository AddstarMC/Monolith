package au.com.addstar.monolith.villagers;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R1.MerchantRecipe;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Trade
{
	private static final Field mFieldRewardXp;
	
	static
	{
		try
		{
			mFieldRewardXp = MerchantRecipe.class.getDeclaredField("rewardExp");
			mFieldRewardXp.setAccessible(true);
		}
		catch(Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	private MerchantRecipe mRecipe;
	
	public Trade(ItemStack input, ItemStack output, int maxUses, boolean rewardXp)
	{
		Validate.notNull(input);
		Validate.notNull(output);
		
		mRecipe = new MerchantRecipe(CraftItemStack.asNMSCopy(input), null, CraftItemStack.asNMSCopy(output), 0, maxUses);
		
		try
		{
			mFieldRewardXp.set(mRecipe, rewardXp);
		}
		catch(Exception e)
		{
			// Should not happen
			throw new AssertionError(e);
		}
	}
	
	public Trade(ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int maxUses, boolean rewardXp)
	{
		Validate.notNull(primaryInput);
		Validate.notNull(secondaryInput);
		Validate.notNull(output);
		
		mRecipe = new MerchantRecipe(CraftItemStack.asNMSCopy(primaryInput), CraftItemStack.asNMSCopy(secondaryInput), CraftItemStack.asNMSCopy(output), 0, maxUses);
		
		try
		{
			mFieldRewardXp.set(mRecipe, rewardXp);
		}
		catch(Exception e)
		{
			// Should not happen
			throw new AssertionError(e);
		}
	}
	
	Trade(MerchantRecipe handle)
	{
		mRecipe = handle;
	}
	
	public ItemStack getPrimary()
	{
		if (mRecipe.getBuyItem1() == null)
			return null;
		
		return CraftItemStack.asBukkitCopy(mRecipe.getBuyItem1());
	}
	
	public ItemStack getSecondary()
	{
		if (mRecipe.getBuyItem2() == null)
			return null;
		
		return CraftItemStack.asBukkitCopy(mRecipe.getBuyItem2());
	}
	
	public boolean hasSecondary()
	{
		return mRecipe.hasSecondItem();
	}
	
	public ItemStack getResult()
	{
		if (mRecipe.getBuyItem3() == null)
			return null;
		
		return CraftItemStack.asBukkitCopy(mRecipe.getBuyItem3());
	}
	
	public int getMaxUses()
	{
		return mRecipe.f();
	}
	
	public int getUses()
	{
		return mRecipe.e();
	}
	
	public boolean doesRewardXp()
	{
		return mRecipe.j();
	}
	
	public void addMaxUses(int uses)
	{
		mRecipe.a(uses);
	}
	
	public boolean canUse()
	{
		return !mRecipe.h();
	}
	
	MerchantRecipe getHandle()
	{
		return mRecipe;
	}
	
	@Override
	public String toString()
	{
		return String.format("Trade{primary=%s,secondary=%s,result=%s,xp=%s,maxUses=%d,uses=%d}", getPrimary(), getSecondary(), getResult(), doesRewardXp(), getMaxUses(), getUses());
	}
}
