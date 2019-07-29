package au.com.addstar.monolith;

import java.util.HashMap;

import org.bukkit.entity.Player;
import au.com.addstar.monolith.chat.ChatMessage;
import au.com.addstar.monolith.chat.ChatMessageType;
import au.com.addstar.monolith.chat.Title;

public class MonoPlayer
{
	private static final HashMap<Player, MonoPlayer> mPlayers = new HashMap<>();
	
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
		return mPlayer.getLocale();
	}

	
	
	protected void onDestroy()
	{
		mPlayers.remove(mPlayer);
	}

	@Deprecated
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
		if (obj instanceof MonoPlayer)return mPlayer.equals(obj);
		return false;
	}
	
	@Override
	public String toString()
	{
		return "MonoPlayer: " + mPlayer.getName();
	}
	
}
