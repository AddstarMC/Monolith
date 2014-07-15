package au.com.addstar.monolith;

import java.util.WeakHashMap;

import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.addstar.monolith.chat.ChatMessage;


public class MonoPlayer
{
	private static final WeakHashMap<Player, MonoPlayer> mPlayers = new WeakHashMap<Player, MonoPlayer>();
	
	public static MonoPlayer getPlayer(Player player)
	{
		MonoPlayer mplayer = mPlayers.get(player);
		if(mplayer == null)
		{
			mplayer = new MonoPlayer(player);
			mPlayers.put(player, mplayer);
		}
		
		return mplayer;
	}
	
	private Player mPlayer;
	private BossDisplay mBossDisplay;
	
	private MonoPlayer(Player player)
	{
		mPlayer = player;
	}
	
	public Player getPlayer()
	{
		return mPlayer;
	}
	
	public String getLocale()
	{
		return ((CraftPlayer)mPlayer).getHandle().locale;
	}
	
	public BossDisplay getBossBarDisplay()
	{
		return mBossDisplay;
	}
	
	public void setBossBarDisplay(BossDisplay display)
	{
		if(mBossDisplay != null)
			mBossDisplay.onPlayerRemove(this);
		
		mBossDisplay = display;
		
		if(mBossDisplay != null)
			mBossDisplay.onPlayerAdd(this);
	}
	
	public void sendMessage(ChatMessage... message)
	{
		for(ChatMessage m : message)
			m.send(mPlayer);
	}
	
	protected void onUpdate()
	{
		if(mBossDisplay != null)
			mBossDisplay.update(this);
	}
	
	protected void onRespawn()
	{
		if(mBossDisplay != null)
		{
			Bukkit.getScheduler().runTaskLater(Monolith.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					if(mBossDisplay != null)
						mBossDisplay.refresh(MonoPlayer.this);
				}
			}, 5);
		}
	}
	
	public void playParticleEffect(Location location, ParticleEffect effect, float speed, int count)
	{
		playParticleEffect(location, effect, speed, count, new Vector());
	}
	
	public void playParticleEffect(Location location, ParticleEffect effect, float speed, int count, Vector offset)
	{
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(effect.getId(), (float)location.getX(), (float)location.getY(), (float)location.getZ(), (float)offset.getX(), (float)offset.getY(), (float)offset.getZ(), speed, count);
		if(mPlayer.getLocation().distanceSquared(location) < 256)
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
	}
	
	@Override
	public int hashCode()
	{
		return mPlayer.hashCode();
	}
	
	@Override
	public boolean equals( Object obj )
	{
		return mPlayer.equals(obj);
	}
	
	@Override
	public String toString()
	{
		return "MonoPlayer: " + mPlayer.getName();
	}
	
}
