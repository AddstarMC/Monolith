package au.com.addstar.monolith.villagers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_8_R2.EntityVillager;
import net.minecraft.server.v1_8_R2.MerchantRecipe;
import net.minecraft.server.v1_8_R2.MerchantRecipeList;

import org.bukkit.craftbukkit.v1_8_R2.entity.CraftVillager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import com.google.common.base.Converter;
import com.google.common.collect.Maps;

public class MonoVillager
{
	private static final Field mFieldCareer;
	private static final Field mFieldCareerStage;
	
	static
	{
		try
		{
			mFieldCareer = EntityVillager.class.getDeclaredField("bx");
			mFieldCareer.setAccessible(true);
			
			mFieldCareerStage = EntityVillager.class.getDeclaredField("by");
			mFieldCareerStage.setAccessible(true);
		}
		catch(Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	private CraftVillager mHandle;
	private Village mVillage;
	
	private MonoVillager(Villager villager)
	{
		mHandle = (CraftVillager)villager;
	}
	
	/**
	 * Gets the villager entity this wrapper uses
	 */
	public Villager getEntity()
	{
		return mHandle;
	}

	/**
	 * A shortcut method for getting the profession of the vilager
	 */
	public Profession getProfession()
	{
		return mHandle.getProfession();
	}
	
	/**
	 * Gets a list o
	 * @return
	 */
	public List<Trade> getTrades()
	{
		MerchantRecipeList handleList = mHandle.getHandle().getOffers(null);
		List<MerchantRecipe> typedList = (List<MerchantRecipe>)handleList;
		
		return new TransformingList<Trade, MerchantRecipe>(typedList, new MerchantConverter());
	}
	
	private int getCareer()
	{
		try
		{
			return mFieldCareer.getInt(mHandle.getHandle());
		}
		catch(IllegalAccessException e)
		{
			// Should not happen
			throw new AssertionError(e);
		}
	}
	
	private void setCareer(int num)
	{
		try
		{
			mFieldCareer.setInt(mHandle.getHandle(), num);
		}
		catch(IllegalAccessException e)
		{
			// Should not happen
			throw new AssertionError(e);
		}
	}
	
	private void setCareerStage(int num)
	{
		try
		{
			mFieldCareerStage.setInt(mHandle.getHandle(), num);
		}
		catch(IllegalAccessException e)
		{
			// Should not happen
			throw new AssertionError(e);
		}
	}
	
	/**
	 * Locks the trade generation preventing new trades from appearing. This cannot be undone
	 */
	public void lockTrades()
	{
		if (getCareer() == 0)
			setCareer(1);
		setCareerStage(128);
	}
	
	public void setTrades(Collection<Trade> trades)
	{
		MerchantRecipeList handleList = mHandle.getHandle().getOffers(null);
		handleList.clear();
		
		for (Trade trade : trades)
			handleList.add(trade.getHandle());
	}
	
	public void addTrade(Trade trade)
	{
		MerchantRecipeList handleList = mHandle.getHandle().getOffers(null);
		handleList.add(trade.getHandle());
	}
	
	public HumanEntity getTradingWith()
	{
		if (!mHandle.getHandle().cm()) // Has no trading player
			return null;
		
		// EntityVillager.getTradingPlayer() : EntityHuman
		return mHandle.getHandle().v_().getBukkitEntity();
	}
	
	public Village getVillage()
	{
		mVillage = Village.getVillage(mHandle, mVillage);
		
		return mVillage;
	}
	
	public static MonoVillager getVillager(Villager villager)
	{
		return new MonoVillager(villager);
	}
	
	private static class MerchantConverter extends Converter<MerchantRecipe, Trade>
	{
		private Map<MerchantRecipe, Trade> mCache;
		
		public MerchantConverter()
		{
			mCache = Maps.newIdentityHashMap();
		}
		
		@Override
		protected Trade doForward( MerchantRecipe handle )
		{
			if (handle == null)
				return null;
			
			Trade trade = mCache.get(handle);
			if (trade == null)
			{
				trade = new Trade(handle);
				mCache.put(handle, trade);
			}
			
			return trade;
		}

		@Override
		protected MerchantRecipe doBackward( Trade trade )
		{
			return trade.getHandle();
		}
	}
}
