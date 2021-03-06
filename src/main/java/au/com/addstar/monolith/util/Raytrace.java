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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.BlockVector;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import au.com.addstar.monolith.BoundingBox;
import au.com.addstar.monolith.lookup.EntityDefinition;

@Deprecated
public class Raytrace {
    private boolean mHitAir;

    private final Set<EntityDefinition> mIgnoreEntities;
    private final Set<Entity> mIgnoreSpecificEntities;
    private final Set<EntityDefinition> mIncludeEntities;
    private boolean mIgnoreAllEntities;

    private final Set<Material> mIgnoreBlocks;
    private final Set<Block> mIgnoreSpecificBlocks;
    private final Set<Material> mIncludeBlocks;

    private boolean mIgnoreAllBlocks;

    public Raytrace() {
        mIgnoreEntities = Sets.newHashSet();
        mIncludeEntities = Sets.newHashSet();
        mIgnoreSpecificEntities = Sets.newHashSet();

        mIgnoreBlocks = Sets.newHashSet();
        mIncludeBlocks = Sets.newHashSet();
        mIgnoreSpecificBlocks = Sets.newHashSet();
    }

    public Raytrace hitAir(boolean hit) {
        mHitAir = hit;
        return this;
    }

    public Raytrace ignoreAllEntities() {
        mIncludeEntities.clear();
        mIgnoreEntities.clear();
        mIgnoreAllEntities = true;
        return this;
    }

    public Raytrace ignoreEntity(EntityType type) {
        return ignoreEntity(new EntityDefinition(type, null));
    }

    public Raytrace ignoreEntity(EntityDefinition type) {
        mIncludeEntities.remove(type);
        mIgnoreEntities.add(type);
        mIgnoreAllEntities = false;
        return this;
    }

    public Raytrace ignoreEntities(EntityType... types) {
        for (EntityType type : types)
            ignoreEntity(type);

        return this;
    }

    public Raytrace ignoreEntities(EntityDefinition... types) {
        for (EntityDefinition type : types)
            ignoreEntity(type);

        return this;
    }

    public Raytrace ignoreEntity(Entity ent) {
        mIgnoreSpecificEntities.add(ent);
        mIgnoreAllEntities = false;

        return this;
    }

    public Raytrace ignoreEntities(Entity... ents) {
        for (Entity ent : ents)
            ignoreEntity(ent);

        return this;
    }

    public Raytrace includeEntity(EntityType type) {
        mIncludeEntities.add(new EntityDefinition(type, null));

        return this;
    }

    public Raytrace includeEntity(EntityDefinition type) {
        mIncludeEntities.add(type);

        return this;
    }

    public Raytrace includeEntities(EntityType... types) {
        for (EntityType type : types)
            includeEntity(type);

        return this;
    }

    public Raytrace includeEntities(EntityDefinition... types) {
        for (EntityDefinition type : types)
            includeEntity(type);

        return this;
    }

    public Raytrace ignoreAllBlocks() {
        mIncludeBlocks.clear();
        mIgnoreBlocks.clear();
        mIgnoreAllBlocks = true;

        return this;
    }

    public Raytrace ignoreBlock(Material type) {
        mIncludeBlocks.remove(type);
        mIgnoreBlocks.add(type);
        mIgnoreAllBlocks = false;

        return this;
    }

    public Raytrace ignoreBlocks(Material... types) {

        for (Material type : types)
            ignoreBlock(type);

        return this;
    }

    public Raytrace ignoreBlock(Block block) {
        mIgnoreSpecificBlocks.add(block);
        mIgnoreAllBlocks = false;

        return this;
    }

    public Raytrace ignoreBlocks(Block... blocks) {
        for (Block block : blocks)
            ignoreBlocks(block);

        return this;
    }

    public Raytrace includeBlock(Material type) {
        mIncludeBlocks.add(type);

        return this;
    }

    public Raytrace includeBlocks(Material... types) {
        for (Material type : types)
            includeBlock(type);

        return this;
    }


    private boolean canHitBlocks() {
        return (!mIgnoreAllBlocks || !mIncludeBlocks.isEmpty());
    }

    private boolean canHitBlock(Block block) {
        Material type = block.getState().getType();

        if (!mIncludeBlocks.contains(type)) {
            return !mIgnoreAllBlocks && !mIgnoreBlocks.contains(type) && !mIgnoreSpecificBlocks.contains(block);
        }

        return true;
    }

    private boolean canHitEntities() {
        return (!mIgnoreAllEntities || !mIncludeEntities.isEmpty());
    }

    private boolean canHitEntity(Entity ent) {
        EntityDefinition type = new EntityDefinition(ent);
        if (!mIncludeEntities.contains(type)) {
            return !mIgnoreAllEntities && !mIgnoreEntities.contains(type) && !mIgnoreSpecificEntities.contains(ent);
        }

        return true;
    }

    public Hit traceOnce(Location start, Vector direction, double maxDistance) {
        List<Hit> hits = traceSome(start, direction, maxDistance, 1);
        if (hits.isEmpty())
            return null;

        return hits.get(0);
    }

    public List<Hit> traceAll(Location start, Vector direction, double maxDistance) {
        return traceSome(start, direction, maxDistance, Integer.MAX_VALUE);
    }

    public Hit traceOnce(Location start, Location end) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Unable to raytrace across worlds");
        Vector direction = end.toVector().subtract(start.toVector());

        List<Hit> hits = traceSome(start, direction, direction.length(), 1);
        if (hits.isEmpty())
            return null;

        return hits.get(0);
    }

    public List<Hit> traceAll(Location start, Location end) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Unable to raytrace across worlds");
        Vector direction = end.toVector().subtract(start.toVector());

        return traceSome(start, direction, direction.length(), Integer.MAX_VALUE);
    }

    public List<Hit> traceSome(Location start, Location end, int maxHits) {
        Validate.isTrue(start.getWorld() == end.getWorld(), "Unable to raytrace across worlds");
        Vector direction = end.toVector().subtract(start.toVector());

        return traceSome(start, direction, direction.length(), maxHits);
    }

    public List<Hit> traceSome(Location start, Vector direction, double maxDistance, int maxHits) {
        Vector startVec = start.toVector();
        Vector endVec = new Vector(start.getX() + direction.getX() * maxDistance, start.getY() + direction.getY() * maxDistance, start.getZ() + direction.getZ() * maxDistance);

        List<Hit> blockHits = Collections.emptyList();
        List<Hit> entityHits = Collections.emptyList();

        if (canHitBlocks())
            blockHits = traceBlocks(start, endVec, maxHits);

        if (canHitEntities())
            entityHits = traceEntities(start, endVec, maxHits);

        // lists are already limited to maxHits so if one is empty, the other one contains the real ordered list
        if (!blockHits.isEmpty()) {
            if (entityHits.isEmpty())
                return blockHits;
            else {
                // Compute the real order
                List<Hit> hits = Lists.newArrayListWithCapacity(Math.min(maxHits, blockHits.size() + entityHits.size()));
                hits.addAll(blockHits);
                hits.addAll(entityHits);
                Collections.sort(hits);

                // Trim to size
                if (hits.size() > maxHits) {
                    hits.subList(maxHits, hits.size()).clear();
                }
                return hits;
            }
        } else if (!entityHits.isEmpty())
            return entityHits;
        else {
            if (mHitAir)
                return Collections.singletonList(new Hit(endVec.toLocation(start.getWorld()), endVec.distance(startVec)));
            else
                return Collections.emptyList();
        }
    }

    private List<Hit> traceBlocks(Location start, Vector end, int maxHits) {
        BlockVector startVec = new BlockVector(start.getBlockX(), start.getBlockY(), start.getBlockZ());
        BlockVector endVec = end.toBlockVector();

        BlockVector current = startVec.clone();

        double maxDistance = end.distanceSquared(start.toVector());

        // Get the sign of the direction
        int signX = (Double.compare(endVec.getX(), startVec.getX()));
        int signY = (Double.compare(endVec.getY(), startVec.getY()));
        int signZ = (Double.compare(endVec.getZ(), startVec.getZ()));

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

        while (true) {
            Block block = start.getWorld().getBlockAt(current.getBlockX(), current.getBlockY(), current.getBlockZ());

            Hit hit = tryHitBlock(block, start, end, maxDistance);
            if (hit != null) {
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

            if (signX != 0 && (signY == 0 || xr < yr) && (signZ == 0 || xr < zr)) {
                current.setX(current.getBlockX() + signX);
                errorX += dErrorX;
            } else if (signX != 0 && (signZ == 0 || yr < zr)) {
                current.setY(current.getBlockY() + signY);
                errorY += dErrorY;
            } else if (signZ != 0) {
                current.setZ(current.getBlockZ() + signZ);
                errorZ += dErrorZ;
            }
        }

        return hits;
    }

    private Hit tryHitBlock(Block block, Location start, Vector end, Double distance) {
        if (block.getType() == Material.AIR || !canHitBlock(block))
            return null;
        RayTraceResult result;
        if (mIncludeBlocks.contains(Material.WATER))
            result = block.rayTrace(start, end, distance, FluidCollisionMode.ALWAYS);
        else
            result = block.rayTrace(start, end, distance, FluidCollisionMode.NEVER);
        if (result != null) {
            Hit hit = new Hit(result.getHitBlock().getLocation(), result.getHitBlock(), result.getHitBlockFace(), result.getHitPosition().distance(start.toVector()));
            return hit;
        } else
            return null;

    }

    private List<Hit> traceEntities(Location start, Vector end, int maxHits) {
        Vector startVec = start.toVector();
        BoundingBox box = new BoundingBox(startVec, end);
        List<Entity> entities = EntityUtil.getEntitiesWithin(start.getWorld(), box);

        List<Hit> hits = Lists.newArrayList();
        for (Entity entity : entities) {
            // Check that we are allowed to hit this type
            if (!canHitEntity(entity))
                break;

            BoundingBox bb = EntityUtil.getBoundingBox(entity);
            Vector hitLocation = bb.getIntersectionPoint(startVec, end);
            if (hitLocation != null) {
                Hit hit = new Hit(hitLocation.toLocation(start.getWorld()), entity, hitLocation.distance(startVec));
                // Insert the hit
                int pos = Collections.binarySearch(hits, hit);
                if (pos < 0)
                    pos = -pos - 1;

                hits.add(pos, hit);
                // Keep only maxHits amount of hits
                while (hits.size() > maxHits)
                    hits.remove(hits.size() - 1);
            }
        }

        return hits;
    }


    public enum HitType {
        Block,
        Entity,
        Air
    }

    public class Hit implements Comparable<Hit> {
        private final Location mPosition;
        private double mDistance;
        private final HitType mType;

        private Entity mEntity;

        private Block mBlock;
        private BlockFace mBlockFace;

        private Hit(Location hitLocation, Block block, BlockFace face, double distance) {
            mPosition = hitLocation;
            mType = HitType.Block;
            mBlock = block;
            mBlockFace = face;
            mDistance = distance;
        }

        private Hit(Location hitLocation, Entity ent, double distance) {
            mPosition = hitLocation;
            mType = HitType.Entity;
            mEntity = ent;
            mDistance = distance;
        }

        private Hit(Location hitLocation, double distance) {
            mPosition = hitLocation;
            mType = HitType.Air;
        }

        public HitType getType() {
            return mType;
        }

        public Location getLocation() {
            return mPosition;
        }

        public double getDistance() {
            return mDistance;
        }

        public Entity getHitEntity() {
            return mEntity;
        }

        public Block getHitBlock() {
            return mBlock;
        }

        public BlockFace getBlockFace() {
            Validate.isTrue(mType == HitType.Block);

            return mBlockFace;
        }

        @Override
        public String toString() {
            switch (mType) {
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
        public int compareTo(Hit other) {
            return Double.compare(mDistance, other.mDistance);
        }
    }
}
