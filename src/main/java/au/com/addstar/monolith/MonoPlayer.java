package au.com.addstar.monolith;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;


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
}
