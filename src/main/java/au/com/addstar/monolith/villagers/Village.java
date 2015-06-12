package au.com.addstar.monolith.villagers;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.VillageDoor;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftIronGolem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Village
{
	private static final Field mFieldWorld;
	private static final Field mFieldVillage;
	
	static
	{
		try
		{
			mFieldWorld = net.minecraft.server.v1_8_R3.Village.class.getDeclaredField("a");
			mFieldWorld.setAccessible(true);
			
			mFieldVillage = EntityVillager.class.getDeclaredField("village");
			mFieldVillage.setAccessible(true);
		}
		catch(Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	
	private net.minecraft.server.v1_8_R3.Village mHandle;
	
	private Village(net.minecraft.server.v1_8_R3.Village handle)
	{
		mHandle = handle;
	}
	
	public int getPopulationSize()
	{
		return mHandle.e();
	}
	
	public int getRadius()
	{
		return mHandle.b();
	}
	
	public Location getLocation()
	{
		return new Location(getWorld(), mHandle.a().getX(), mHandle.a().getY(), mHandle.a().getZ());
	}
	
	public World getWorld()
	{
		try
		{
			net.minecraft.server.v1_8_R3.World world = (net.minecraft.server.v1_8_R3.World)mFieldWorld.get(mHandle);
			return world.getWorld();
		}
		catch(Exception e)
		{
			// Should not happen
			throw new AssertionError(e);
		}
	}
	
	public List<BlockState> getDoors()
	{
		List<VillageDoor> doors = (List<VillageDoor>)mHandle.f();
		
		final World world = getWorld();
		List<BlockState> states = Lists.transform(doors, new Function<VillageDoor, BlockState>()
		{
			@Override
			public BlockState apply( VillageDoor door )
			{
				BlockPosition pos = door.d();
				return world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState();
			}
		});

		return Collections.unmodifiableList(states);
	}
	
	public int getReputation(OfflinePlayer player)
	{
		return mHandle.a(player.getName());
	}
	
	public void addReputation(OfflinePlayer player, int rep)
	{
		mHandle.a(player.getName(), rep);
	}
	
	public void setReputation(OfflinePlayer player, int rep)
	{
		int toAdd = rep - getReputation(player);
		mHandle.a(player.getName(), toAdd);
	}
	
	static Village getVillage(Villager villager, Village cached)
	{
		try
		{
			net.minecraft.server.v1_8_R3.Village handle = (net.minecraft.server.v1_8_R3.Village) mFieldVillage.get(((CraftVillager)villager).getHandle());
			if (handle == null)
				return null;
			
			if (cached != null && cached.mHandle == handle)
				return cached;
			
			return new Village(handle);
		}
		catch(Exception e)
		{
			// Shouldnt happen
			throw new AssertionError(e);
		}
	}
	
	public static Village getVillage(Villager villager)
	{
		try
		{
			net.minecraft.server.v1_8_R3.Village handle = (net.minecraft.server.v1_8_R3.Village) mFieldVillage.get(((CraftVillager)villager).getHandle());
			if (handle == null)
				return null;
			
			return new Village(handle);
		}
		catch(Exception e)
		{
			// Shouldnt happen
			throw new AssertionError(e);
		}
	}
	
	public static Village getVillage(IronGolem golem)
	{
		net.minecraft.server.v1_8_R3.Village handle = ((CraftIronGolem)golem).getHandle().n();
		if (handle == null)
			return null;
		
		return new Village(handle); 
	}
}
