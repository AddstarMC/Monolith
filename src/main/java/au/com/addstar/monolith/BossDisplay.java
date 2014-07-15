package au.com.addstar.monolith;

import java.util.WeakHashMap;

import net.minecraft.server.v1_7_R4.EntityEnderDragon;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BossDisplay
{
	private String mText = "";
	private float mValue = 1;
	private boolean mHasChanged = false;
	
	private WeakHashMap<MonoPlayer, DragonDisplay> mDragons = new WeakHashMap<MonoPlayer, DragonDisplay>();
	
	public BossDisplay()
	{
		
	}
	
	public BossDisplay(String text, float value)
	{
		Validate.notNull(text);
		Validate.isTrue(value >= 0 && value <= 1);
		mText = text;
		mValue = value;
	}
	
	public void setText(String text)
	{
		Validate.notNull(text);
		
		if(text.length() > 64)
			text = text.substring(0,64);
		mText = text;
		mHasChanged = true;
		updateAll();
	}
	
	public String getText()
	{
		return mText;
	}
	
	public float getPercent()
	{
		return mValue;
	}
	
	public void setPercent(float value)
	{
		Validate.isTrue(value >= 0 && value <= 1);
		mValue = value;
		mHasChanged = true;
		updateAll();
	}
	
	void onPlayerAdd(MonoPlayer player)
	{
		DragonDisplay display = new DragonDisplay(player.getPlayer());
		display.spawn();
		mDragons.put(player, display);
	}
	
	void onPlayerRemove(MonoPlayer player)
	{
		DragonDisplay display = mDragons.remove(player);
		if(display != null)
			display.remove();
	}
	
	public void refresh(MonoPlayer player)
	{
		DragonDisplay display = mDragons.remove(player);
		if(display != null)
			display.spawn();
	}
	
	
	public void updateAll()
	{
		for(DragonDisplay display : mDragons.values())
			display.update();
		
		mHasChanged = false;
	}
	
	public void update(MonoPlayer player)
	{
		DragonDisplay display = mDragons.get(player);
		if(display != null)
			display.update();
	}
	
	private int toHealth()
	{
		return (int)Math.min(Math.max(mValue * 200, 1), 200);
	}
	
	private class DragonDisplay
	{
		private static final int ENTITY_DRAGON_ID = 999999999;
		
		private Player mPlayer;
		private Location mLocation;
		private EntityEnderDragon mDragon;
		
		public DragonDisplay(Player player)
		{
			mPlayer = player;
			mLocation = player.getLocation();
		}
		
		public void update()
		{
			Location loc = mPlayer.getLocation();
			
			if(mHasChanged)
				updateStats();
			
			if(mLocation.getWorld() != loc.getWorld())
				spawn();
			else
			{
				double dist = mLocation.distanceSquared(loc);
				
				if(dist >= 10000)
					spawn();
				else if(dist > 500)
				{
					mLocation = loc;
					positionDragon();
				}
			}
		}
		
		public void remove()
		{
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ENTITY_DRAGON_ID);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		public void spawn()
		{
			mLocation = mPlayer.getLocation();
			
			mDragon = new EntityEnderDragon(null);
			mDragon.setCustomName(mText);
			mDragon.setCustomNameVisible(true);
			mDragon.setHealth(toHealth());
			mDragon.setLocation(mLocation.getX(), -500, mLocation.getZ(), 0, 0);
			mDragon.d(ENTITY_DRAGON_ID); // Set entityId
			PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(mDragon);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		private void positionDragon()
		{
			PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(ENTITY_DRAGON_ID, mLocation.getBlockX() * 32, -500 * 32, mLocation.getBlockZ() * 32, (byte)0, (byte)0);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		private void updateStats()
		{
			mDragon.setCustomName(mText);
			mDragon.setCustomNameVisible(true);
			mDragon.setHealth(toHealth());
			PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(ENTITY_DRAGON_ID, mDragon.getDataWatcher(), true);
			((CraftPlayer)mPlayer).getHandle().playerConnection.sendPacket(packet);
		}
		
		@Override
		public String toString()
		{
			return "DDisplay{" + mPlayer.getName() + "}";
		}
	}
}
