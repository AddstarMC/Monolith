package au.com.addstar.monolith;

import java.util.WeakHashMap;

import net.minecraft.server.v1_8_R2.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MonoWorld
{
	private static WeakHashMap<World, MonoWorld> mWorlds = new WeakHashMap<World, MonoWorld>();
	
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
	
	public void playParticleEffect(Location location, ParticleEffect effect, float speed, int count)
	{
		playParticleEffect(location, effect, speed, count, new Vector());
	}
	
	public void playParticleEffect(Location location, ParticleEffect effect, float speed, int count, Vector offset)
	{
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(effect.getEffect(), false, (float)location.getX(), (float)location.getY(), (float)location.getZ(), (float)offset.getX(), (float)offset.getY(), (float)offset.getZ(), speed, count);
		Location temp = new Location(null, 0, 0 ,0);
		for(Player player : mWorld.getPlayers())
		{
			player.getLocation(temp);
			if(temp.distanceSquared(location) < 256)
				((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
		}
	}
}
