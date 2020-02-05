/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.util;

import com.google.common.collect.Lists;

import au.com.addstar.monolith.BoundingBox;

import net.minecraft.server.v1_15_R1.AxisAlignedBB;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.List;

public class EntityUtil
{
	public static BoundingBox getBoundingBox(Entity entity)
	{
		AxisAlignedBB rawBB = ((CraftEntity)entity).getHandle().getBoundingBox();
		return new BoundingBox(new Vector(rawBB.minX, rawBB.minY, rawBB.minZ), new Vector(rawBB.maxX, rawBB.maxY, rawBB.maxZ));
	}
	
	public static List<Entity> getEntitiesWithin(World world, BoundingBox bb)
	{
		return getEntitiesWithin(world, bb, Entity.class);
	}
	
	@SuppressWarnings( "unchecked" )
	public static <T extends Entity> List<T> getEntitiesWithin(World world, BoundingBox bb, Class<T> type)
	{
		AxisAlignedBB rawBB = new AxisAlignedBB(bb.getMinCorner().getX(), bb.getMinCorner().getY(), bb.getMinCorner().getZ(), bb.getMaxCorner().getX(), bb.getMaxCorner().getY(), bb.getMaxCorner().getZ());
		
		// Call List<? extends Entity> getEntities(Class<? extends Entity>, AxisAlignedBB, Predicate<? extends Entity>)
		List<net.minecraft.server.v1_15_R1.Entity> rawResults = ((CraftWorld) world).getHandle().a(net.minecraft.server.v1_15_R1.Entity.class, rawBB, null);

		List<T> resolved = Lists.newArrayListWithCapacity(rawResults.size());
		for (net.minecraft.server.v1_15_R1.Entity rawResult : rawResults)
		{
			Entity bukkitEntity = rawResult.getBukkitEntity();
			if (type.isInstance(bukkitEntity))
				resolved.add((T)bukkitEntity);
		}
		
		return resolved;
	}

	public static List<? extends Entity> getEntitiesWithin(World world, BoundingBox bb, EntityType type)
	{
		return getEntitiesWithin(world, bb, type.getEntityClass());
	}
}
