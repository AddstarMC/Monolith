package au.com.addstar.monolith;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

@Deprecated
public class BossDisplay
{
	private BossBar handle;
	
	public BossDisplay()
	{
		handle = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
		handle.setProgress(1);
	}
	
	public BossDisplay(String text, float value)
	{
		Validate.notNull(text);
		Validate.isTrue(value >= 0 && value <= 1);
		
		handle = Bukkit.createBossBar(text, BarColor.PURPLE, BarStyle.SOLID);
		handle.setProgress(value);
	}
	
	public void setText(String text)
	{
		Validate.notNull(text);
		
		if(text.length() > 64)
			text = text.substring(0,64);
		handle.setTitle(text);
	}
	
	public String getText()
	{
		return handle.getTitle();
	}
	
	public float getPercent()
	{
		return (float)handle.getProgress();
	}
	
	public void setPercent(float value)
	{
		Validate.isTrue(value >= 0 && value <= 1);
		handle.setProgress(value);
	}
	
	void onPlayerAdd(MonoPlayer player)
	{
		handle.addPlayer(player.getPlayer());
	}
	
	void onPlayerRemove(MonoPlayer player)
	{
		handle.removePlayer(player.getPlayer());
	}
	
	public void refresh(MonoPlayer player)
	{
		// Not needed
	}
	
	
	public void updateAll()
	{
		// Not needed
	}
	
	public void update(MonoPlayer player)
	{
		// Not needed
	}
}
