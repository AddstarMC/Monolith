package au.com.addstar.monolith;

import java.util.WeakHashMap;

import org.bukkit.World;
import au.com.addstar.monolith.chat.Title;

public class MonoWorld
{
	private static WeakHashMap<World, MonoWorld> mWorlds = new WeakHashMap<>();
	
	public static MonoWorld getWorld(World world)
	{
		MonoWorld mworld = mWorlds.get(world);
		if(mworld == null)
		{
			mworld = new MonoWorld(world);
			mWorlds.put(world, mworld);
		}
		
		return mworld;
	}
	
	private World mWorld;
	
	private MonoWorld(World world)
	{
		mWorld = world;
	}
	
	public void showTitle(Title title)
	{
		title.show(mWorld.getPlayers());
	}
}
