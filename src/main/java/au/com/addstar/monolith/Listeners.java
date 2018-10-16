package au.com.addstar.monolith;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener
{

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onPlayerQuit(PlayerQuitEvent event)
	{
		MonoPlayer player = MonoPlayer.getPlayer(event.getPlayer());
		player.onDestroy();
	}
	
}
