package au.com.addstar.monolith.villagers;

import java.lang.reflect.Field;

import net.minecraft.server.v1_12_R1.EntityVillager;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftVillager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

/**
 * A Villager defined by Monolith
 */
public class MonoVillager
{
	private static final Field mFieldCareer;
	private static final Field mFieldCareerStage;
	
	static
	{
		try
		{
			mFieldCareer = EntityVillager.class.getDeclaredField("bK");
			mFieldCareer.setAccessible(true);

			mFieldCareerStage = EntityVillager.class.getDeclaredField("bL");
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
	 * @return Gets the villager entity this wrapper uses
	 *
	 */
	public Villager getEntity()
	{
		return mHandle;
	}

	/**
	 * @return A shortcut method for getting the profession of the vilager
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
		if (!mHandle.getHandle().dl()) // Has no trading player
			return null;
		
		// EntityVillager.getTradingPlayer() : EntityHuman
		return mHandle.getHandle().getTrader().getBukkitEntity();
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
