package au.com.addstar.monolith;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import au.com.addstar.monolith.chat.ChatMessage;
import au.com.addstar.monolith.chat.ChatMessageType;
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
	
	@Deprecated
	public BossDisplay getBossBarDisplay()
	{
		return mBossDisplay;
	}
	
	@Deprecated
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
	
	public void sendMessage(ChatMessageType type, ChatMessage... message)
	{
		for(ChatMessage m : message)
			m.send(mPlayer, type);
	}
	
	public void sendMessage(ChatMessageType type, String message)
	{
		ChatMessage.begin(message).send(mPlayer, type);
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
