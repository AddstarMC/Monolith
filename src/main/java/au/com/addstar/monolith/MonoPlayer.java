package au.com.addstar.monolith;

import java.util.HashMap;

import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import au.com.addstar.monolith.chat.ChatMessage;
import au.com.addstar.monolith.chat.Title;

public class MonoPlayer
{
	private static final HashMap<Player, MonoPlayer> mPlayers = new HashMap<Player, MonoPlayer>();
	
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
	
	protected void onDestroy()
	{
		mPlayers.remove(mPlayer);
	}
	
	@Deprecated
	public void playParticleEffect(Location location, ParticleEffect effect, float speed, int count)
	{
		playParticleEffect(location, effect, speed, count, new Vector());
	}
	
	@Deprecated
	public void playParticleEffect(Location location, ParticleEffect effect, float speed, int count, Vector offset)
	{
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(effect.getEffect(), false, (float)location.getX(), (float)location.getY(), (float)location.getZ(), (float)offset.getX(), (float)offset.getY(), (float)offset.getZ(), speed, count);
		if(mPlayer.getLocation().distanceSquared(location) < 256)
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
	}
	
	public void showTitle(Title title)
	{
		title.show(mPlayer);
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
