package au.com.addstar.monolith.villagers;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.EntityVillager;
import net.minecraft.server.v1_13_R2.VillageDoor;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftIronGolem;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftVillager;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;

import com.google.common.collect.Lists;

public class Village
{
	private static final Field mFieldWorld;
	private static final Field mFieldVillage;
	
	static
	{
		try
		{
			mFieldWorld = net.minecraft.server.v1_13_R2.Village.class.getDeclaredField("a");
			mFieldWorld.setAccessible(true);
			
			mFieldVillage = EntityVillager.class.getDeclaredField("village");
			mFieldVillage.setAccessible(true);
		}
		catch(Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}

	private net.minecraft.server.v1_13_R2.Village mHandle;

	private Village(net.minecraft.server.v1_13_R2.Village handle)
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
			net.minecraft.server.v1_13_R2.World world = (net.minecraft.server.v1_13_R2.World) mFieldWorld.get(mHandle);
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
		List<VillageDoor> doors = mHandle.f();
		
		final World world = getWorld();
		List<BlockState> states = doors.stream().map(door -> {
			BlockPosition pos = door.d();
			return world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getState();
		}).collect(Collectors.toList());

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
			net.minecraft.server.v1_13_R2.Village handle = (net.minecraft.server.v1_13_R2.Village) mFieldVillage.get(((CraftVillager) villager).getHandle());
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
			net.minecraft.server.v1_13_R2.Village handle = (net.minecraft.server.v1_13_R2.Village) mFieldVillage.get(((CraftVillager) villager).getHandle());
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
		net.minecraft.server.v1_13_R2.Village handle = ((CraftIronGolem) golem).getHandle().l();
		if (handle == null)
			return null;
		
		return new Village(handle); 
	}
}
