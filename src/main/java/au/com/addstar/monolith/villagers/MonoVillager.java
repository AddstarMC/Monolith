package au.com.addstar.monolith.villagers;

import java.lang.reflect.Field;
import net.minecraft.server.v1_9_R1.EntityVillager;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftVillager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class MonoVillager
{
	private static final Field mFieldCareer;
	private static final Field mFieldCareerStage;
	
	static
	{
		try
		{
			mFieldCareer = EntityVillager.class.getDeclaredField("bH");
			mFieldCareer.setAccessible(true);
			
			mFieldCareerStage = EntityVillager.class.getDeclaredField("bI");
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
	
	public HumanEntity getTradingWith()
	{
		if (!mHandle.getHandle().da()) // Has no trading player
			return null;
		
		// EntityVillager.getTradingPlayer() : EntityHuman
		return mHandle.getHandle().t_().getBukkitEntity();
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
}
