package au.com.addstar.monolith.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.MovingObjectPosition;
import net.minecraft.server.v1_8_R2.Vec3D;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import au.com.addstar.monolith.BoundingBox;
import au.com.addstar.monolith.lookup.EntityDefinition;

public class Raytrace
{
	private boolean mHitAir;
	
	private Set<EntityDefinition> mIgnoreEntities;
	private Set<Entity> mIgnoreSpecificEntities;
	private Set<EntityDefinition> mIncludeEntities;
	private boolean mIgnoreAllEntities;
	
	private Set<MaterialData> mIgnoreBlocks;
	private Set<Block> mIgnoreSpecificBlocks;
	private Set<MaterialData> mIncludeBlocks;
	
	private boolean mIgnoreAllBlocks;
	
	public Raytrace()
	{
		mIgnoreEntities = Sets.newHashSet();
		mIncludeEntities = Sets.newHashSet();
		mIgnoreSpecificEntities = Sets.newHashSet();
		
		mIgnoreBlocks = Sets.newHashSet();
		mIncludeBlocks = Sets.newHashSet();
		mIgnoreSpecificBlocks = Sets.newHashSet();
	}
	
	public Raytrace hitAir(boolean hit)
	{
		mHitAir = hit;
		return this;
	}
	
	public Raytrace ignoreAllEntities()
	{
		mIncludeEntities.clear();
		mIgnoreEntities.clear();
		mIgnoreAllEntities = true;
		return this;
	}
	
	public Raytrace ignoreEntity(EntityType type)
	{
		return ignoreEntity(new EntityDefinition(type, null));
	}
	
	public Raytrace ignoreEntity(EntityDefinition type)
	{
		mIncludeEntities.remove(type);
		mIgnoreEntities.add(type);
		mIgnoreAllEntities = false;
		return this;
	}
	
	public Raytrace ignoreEntities(EntityType... types)
	{
		for (EntityType type : types)
			ignoreEntity(type);
		
		return this;
	}
	
	public Raytrace ignoreEntities(EntityDefinition... types)
	{
		for (EntityDefinition type : types)
			ignoreEntity(type);
		
		return this;
	}
	
	public Raytrace ignoreEntity(Entity ent)
	{
		mIgnoreSpecificEntities.add(ent);
		mIgnoreAllEntities = false;
		
		return this;
	}
	
	public Raytrace ignoreEntities(Entity... ents)
	{
		for (Entity ent : ents)
			ignoreEntity(ent);
		
		return this;
	}
	
	public Raytrace includeEntity(EntityType type)
	{
		mIncludeEntities.add(new EntityDefinition(type, null));
		
		return this;
	}
	
	public Raytrace includeEntity(EntityDefinition type)
	{
		mIncludeEntities.add(type);
		
		return this;
	}
	
	public Raytrace includeEntities(EntityType... types)
	{
		for (EntityType type : types)
			includeEntity(type);
		
		return this;
	}
	
	public Raytrace includeEntities(EntityDefinition... types)
	{
		for (EntityDefinition type : types)
			includeEntity(type);
		
		return this;
	}
	
	public Raytrace ignoreAllBlocks()
	{
		mIncludeBlocks.clear();
		mIgnoreBlocks.clear();
		mIgnoreAllBlocks = true;
		
		return this;
	}
	
	@SuppressWarnings( "deprecation" )
	public Raytrace ignoreBlock(Material type)
	{
		return ignoreBlock(type.getNewData((byte)0));
	}
	
	public Raytrace ignoreBlock(MaterialData type)
	{
		mIncludeBlocks.remove(type);
		mIgnoreBlocks.add(type);
		mIgnoreAllBlocks = false;
		
		return this;
	}
	
	public Raytrace ignoreBlocks(Material... types)
	{
		for (Material type : types)
			ignoreBlock(type);
		
		return this;
	}
	
	public Raytrace ignoreBlocks(MaterialData... types)
	{
		for (MaterialData type : types)
			ignoreBlock(type);
		
		return this;
	}
	
	public Raytrace ignoreBlock(Block block)
	{
		mIgnoreSpecificBlocks.add(block);
		mIgnoreAllBlocks = false;
		
		return this;
	}
	
	public Raytrace ignoreBlocks(Block... blocks)
	{
		for (Block block : blocks)
			ignoreBlocks(block);
		
		return this;
	}
	
	@SuppressWarnings( "deprecation" )
	public Raytrace includeBlock(Material type)
	{
		mIncludeBlocks.add(type.getNewData((byte)0));
		
		return this;
	}
	
	public Raytrace includeBlock(MaterialData type)
	{
		mIncludeBlocks.add(type);
		
		return this;
	}
	
	public Raytrace includeBlocks(Material... types)
	{
		for (Material type : types)
			includeBlock(type);
		
		return this;
	}
	
	public Raytrace includeBlocks(MaterialData... types)
	{
		for (MaterialData type : types)
			includeBlock(type);
		
		return this;
	}
	
	private boolean canHitBlocks()
	{
		return (!mIgnoreAllBlocks || !mIncludeBlocks.isEmpty());
	}
	
	private boolean canHitBlock(Block block)
	{
		MaterialData type = block.getState().getData();
		
		if (!mIncludeBlocks.contains(type))
		{
			if (mIgnoreAllBlocks || mIgnoreBlocks.contains(type) || mIgnoreSpecificBlocks.contains(block))
				return false;
		}
		
		return true;
	}
	
	private boolean canHitEntities()
	{
		return (!mIgnoreAllEntities || !mIncludeEntities.isEmpty());
	}
	
	private boolean canHitEntity(Entity ent)
	{
		EntityDefinition type = new EntityDefinition(ent);
		if (!mIncludeEntities.contains(type))
		{
			if (mIgnoreAllEntities || mIgnoreEntities.contains(type) || mIgnoreSpecificEntities.contains(ent))
				return false;
		}
		
		return true;
	}
	
	public Hit traceOnce(Location start, Vector direction, double maxDistance)
	{
		List<Hit> hits = traceSome(start, direction, maxDistance, 1);
		if (hits.isEmpty())
			return null;
		
		return hits.get(0);
	}
	
	public List<Hit> traceAll(Location start, Vector direction, double maxDistance)
	{
		return traceSome(start, direction, maxDistance, Integer.MAX_VALUE);
	}
	
	public Hit traceOnce(Location start, Location end)
	{
		Validate.isTrue(start.getWorld() == end.getWorld(), "Unable to raytrace across worlds");
		Vector direction = end.toVector().subtract(start.toVector());
		
		List<Hit> hits = traceSome(start, direction, direction.length(), 1);
		if (hits.isEmpty())
			return null;
		
		return hits.get(0);
	}
	
	public List<Hit> traceAll(Location start, Location end)
	{
		Validate.isTrue(start.getWorld() == end.getWorld(), "Unable to raytrace across worlds");
		Vector direction = end.toVector().subtract(start.toVector());
		
		return traceSome(start, direction, direction.length(), Integer.MAX_VALUE);
	}
	
	public List<Hit> traceSome(Location start, Location end, int maxHits)
	{
		Validate.isTrue(start.getWorld() == end.getWorld(), "Unable to raytrace across worlds");
		Vector direction = end.toVector().subtract(start.toVector());
		
		return traceSome(start, direction, direction.length(), maxHits);
	}
	
	public List<Hit> traceSome(Location start, Vector direction, double maxDistance, int maxHits)
	{
		Vector startVec = start.toVector();
		Vector endVec = new Vector(start.getX() + direction.getX() * maxDistance, start.getY() + direction.getY() * maxDistance, start.getZ() + direction.getZ() * maxDistance);
		
		List<Hit> blockHits = Collections.emptyList();
		List<Hit> entityHits = Collections.emptyList();
		
		if (canHitBlocks())
			blockHits = traceBlocks(start, endVec, maxHits);
		
		if (canHitEntities())
			entityHits = traceEntities(start, endVec, maxHits);
		
		// lists are already limited to maxHits so if one is empty, the other one contains the real ordered list
		if (!blockHits.isEmpty())
		{
			if (entityHits.isEmpty())
				return blockHits;
			else
			{
				// Compute the real order
				List<Hit> hits = Lists.newArrayListWithCapacity(Math.min(maxHits, blockHits.size() + entityHits.size()));
				hits.addAll(blockHits);
				hits.addAll(entityHits);
				Collections.sort(hits);
				
				// Trim to size
				for (int i = hits.size() - 1; i >= maxHits; --i)
					hits.remove(i);
				return hits;
			}
		}
		else if (!entityHits.isEmpty())
			return entityHits;
		else
		{
			if (mHitAir)
				return Arrays.asList(new Hit(endVec.toLocation(start.getWorld()), endVec.distance(startVec)));
			else
				return Collections.emptyList();
		}
	}
	
	private List<Hit> traceBlocks(Location start, Vector end, int maxHits)
	{
		BlockVector startVec = new BlockVector(start.getBlockX(), start.getBlockY(), start.getBlockZ());
		BlockVector endVec = end.toBlockVector();
		
		BlockVector current = startVec.clone();
		
		double maxDistance = end.distanceSquared(start.toVector());
		
		// Get the sign of the direction
		int signX = (endVec.getX() > startVec.getX() ? 1 : (endVec.getX() < startVec.getX() ? -1 : 0));
		int signY = (endVec.getY() > startVec.getY() ? 1 : (endVec.getY() < startVec.getY() ? -1 : 0));
		int signZ = (endVec.getZ() > startVec.getZ() ? 1 : (endVec.getZ() < startVec.getZ() ? -1 : 0));
	    
		// Planes for each axis that we will next cross
		int planeX = startVec.getBlockX() + (signX > 0 ? 1 : 0);
		int planeY = startVec.getBlockY() + (signY > 0 ? 1 : 0);
		int planeZ = startVec.getBlockZ() + (signZ > 0 ? 1 : 0);
		
		// Only used for multiplying up the error margins
		double vx = end.getX() == start.getX() ? 1 : end.getX() - start.getX();
		double vy = end.getY() == start.getY() ? 1 : end.getY() - start.getY();
		double vz = end.getZ() == start.getZ() ? 1 : end.getZ() - start.getZ();
		
	    // Error is normalized to vx * vy * vz so we only have to multiply up
	    double vxvy = vx * vy;
	    double vxvz = vx * vz;
	    double vyvz = vy * vz;
		
		// Error from the next plane accumulators, scaled up by vx*vy*vz
	    double errorX = (planeX - start.getX()) * vyvz;
	    double errorY = (planeY - start.getY()) * vxvz;
	    double errorZ = (planeZ - start.getZ()) * vxvy;
		
		double dErrorX = signX * vyvz;
		double dErrorY = signY * vxvz;
		double dErrorZ = signZ * vxvy;

		List<Hit> hits = Lists.newArrayList();
		
		while(true)
		{
			Block block = start.getWorld().getBlockAt(current.getBlockX(), current.getBlockY(), current.getBlockZ());
			
			Hit hit = tryHitBlock(block, start, end);
			if (hit != null)
			{
				hits.add(hit);
				if (hits.size() >= maxHits)
					break;
			}
			
			// Are we done?
			if (current.getBlockX() == endVec.getBlockX() && current.getBlockY() == endVec.getBlockY() && current.getBlockZ() == endVec.getBlockZ())
				break;
			// Fallback case. This only comes into play when the direction vector is almost aligned with an axis.
			if (current.distanceSquared(startVec) > maxDistance)
				break;
			
			// Which plane do we cross first?
			double xr = Math.abs(errorX);
			double yr = Math.abs(errorY);
			double zr = Math.abs(errorZ);
	        
			if (signX != 0 && (signY == 0 || xr < yr) && (signZ == 0 || xr < zr))
			{
				current.setX(current.getBlockX() + signX);
				errorX += dErrorX;
			}
			else if (signX != 0 && (signZ == 0 || yr < zr))
			{
				current.setY(current.getBlockY() + signY);
				errorY += dErrorY;
			}
			else if (signZ != 0)
			{
				current.setZ(current.getBlockZ() + signZ);
				errorZ += dErrorZ;
			}
		}
		
		return hits;
	}
	
	private Hit tryHitBlock(Block block, Location start, Vector end)
	{
		if (block.getType() == Material.AIR)
			return null;
		
		// Check that we are allowed to hit this type
		if (!canHitBlock(block))
			return null;
		
		// We are allowed to hit this type, begin
		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		net.minecraft.server.v1_8_R2.Block type = CraftMagicNumbers.getBlock(block);
		
		// Expand the search area around the block to ensure that it passes completely through the block
		Vec3D srcVec = new Vec3D(start.getX(), start.getY(), start.getZ());
		Vec3D dstVec = new Vec3D(end.getX(), end.getY(), end.getZ());
		
		// Do the trace
		MovingObjectPosition hitPos = type.a(((CraftWorld)block.getWorld()).getHandle(), pos, srcVec, dstVec);
		if (hitPos == null)
			return null;
		
		// Translate the hit face
		BlockFace hitFace;
		switch (hitPos.direction)
		{
		case DOWN:
			hitFace = BlockFace.DOWN;
			break;
		case EAST:
			hitFace = BlockFace.EAST;
			break;
		case NORTH:
			hitFace = BlockFace.NORTH;
			break;
		case SOUTH:
			hitFace = BlockFace.SOUTH;
			break;
		case UP:
			hitFace = BlockFace.UP;
			break;
		case WEST:
			hitFace = BlockFace.WEST;
			break;
		default:
			hitFace = BlockFace.SELF;
			break;
		}
		
		// Find the exact hit location
		Location translatedPos = new Location(block.getWorld(), hitPos.pos.a, hitPos.pos.b, hitPos.pos.c);
		return new Hit(translatedPos, block, hitFace, translatedPos.distance(start));
	}
	
	private List<Hit> traceEntities(Location start, Vector end, int maxHits)
	{
		Vector startVec = start.toVector();
		BoundingBox box = new BoundingBox(startVec, end);
		List<Entity> entities = EntityUtil.getEntitiesWithin(start.getWorld(), box);
		
		List<Hit> hits = Lists.newArrayList();
		for (Entity entity : entities)
		{
			// Check that we are allowed to hit this type
			if (!canHitEntity(entity))
				break;
			
			BoundingBox bb = EntityUtil.getBoundingBox(entity);
			Vector hitLocation = bb.getIntersectionPoint(startVec, end);
			if (hitLocation != null)
			{
				Hit hit = new Hit(hitLocation.toLocation(start.getWorld()), entity, hitLocation.distance(startVec));
				// Insert the hit
				int pos = Collections.binarySearch(hits, hit);
				if (pos < 0)
					pos = -pos-1;
				
				hits.add(pos, hit);
				// Keep only maxHits amount of hits
				while (hits.size() > maxHits)
					hits.remove(hits.size()-1);
			}
		}
		
		return hits;
	}
	
	
	public class Hit implements Comparable<Hit>
	{
		private Location mPosition;
		private double mDistance;
		private HitType mType;
		
		private Entity mEntity;
		
		private Block mBlock;
		private BlockFace mBlockFace;
		
		private Hit(Location hitLocation, Block block, BlockFace face, double distance)
		{
			mPosition = hitLocation;
			mType = HitType.Block;
			mBlock = block;
			mBlockFace = face;
			mDistance = distance;
		}
		
		private Hit(Location hitLocation, Entity ent, double distance)
		{
			mPosition = hitLocation;
			mType = HitType.Entity;
			mEntity = ent;
			mDistance = distance;
		}
		
		private Hit(Location hitLocation, double distance)
		{
			mPosition = hitLocation;
			mType = HitType.Air;
		}
		public HitType getType()
		{
			return mType;
		}
		
		public Location getLocation()
		{
			return mPosition;
		}
		
		public double getDistance()
		{
			return mDistance;
		}
		
		public Entity getHitEntity()
		{
			return mEntity;
		}
		
		public Block getHitBlock()
		{
			return mBlock;
		}
		
		public BlockFace getBlockFace()
		{
			Validate.isTrue(mType == HitType.Block);
			
			return mBlockFace;
		}
		
		@Override
		public String toString()
		{
			switch (mType)
			{
			default:
			case Air:
				return String.format("Hit AIR at %.0f,%.0f,%.0f dist %.2f", mPosition.getX(), mPosition.getY(), mPosition.getZ(), mDistance);
			case Block:
				return String.format("Hit BLOCK %s on face %s at %.0f,%.0f,%.0f dist %.2f", mBlock.getType(), mBlockFace, mPosition.getX(), mPosition.getY(), mPosition.getZ(), mDistance);
			case Entity:
				return String.format("Hit ENTITY %s at %.0f,%.0f,%.0f dist %.2f", mEntity.getType(), mPosition.getX(), mPosition.getY(), mPosition.getZ(), mDistance);
			}
		}

		@Override
		public int compareTo( Hit other )
		{
			return Double.compare(mDistance, other.mDistance);
		}
	}
	
	public enum HitType
	{
		Block,
		Entity,
		Air
	}
}
