/*
 * Copyright (c) 2021. AddstarMC
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

package au.com.addstar.monolith;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BoundingBox {
    private Vector mMinCorner;
    private Vector mMaxCorner;

    public BoundingBox(Vector corner1, Vector corner2) {
        mMinCorner = Vector.getMinimum(corner1, corner2);
        mMaxCorner = Vector.getMaximum(corner1, corner2);
    }

    public Vector getMinCorner() {
        return mMinCorner;
    }

    public Vector getMaxCorner() {
        return mMaxCorner;
    }

    public boolean contains(Vector vector) {
        return vector.isInAABB(mMinCorner, mMaxCorner);
    }

    public void include(BoundingBox box) {
        mMinCorner = Vector.getMinimum(mMinCorner, box.getMinCorner());
        mMaxCorner = Vector.getMaximum(mMaxCorner, box.getMaxCorner());
    }

    public void include(Vector point) {
        mMinCorner = Vector.getMinimum(mMinCorner, point);
        mMaxCorner = Vector.getMaximum(mMaxCorner, point);
    }

    public Vector getIntersectionPoint(Vector start, Vector end) {
        // Quick tests to rule it out
        if (start.getX() < mMinCorner.getX() && end.getX() < mMinCorner.getX())
            return null;
        if (start.getX() > mMaxCorner.getX() && end.getX() > mMaxCorner.getX())
            return null;
        if (start.getY() < mMinCorner.getY() && end.getY() < mMinCorner.getY())
            return null;
        if (start.getY() > mMaxCorner.getY() && end.getY() > mMaxCorner.getY())
            return null;
        if (start.getZ() < mMinCorner.getZ() && end.getZ() < mMinCorner.getZ())
            return null;
        if (start.getZ() > mMaxCorner.getZ() && end.getZ() > mMaxCorner.getZ())
            return null;
        // Line starts inside box
        if (start.getX() > mMinCorner.getX() && start.getX() < mMaxCorner.getX()
                && start.getY() > mMinCorner.getY() && start.getY() < mMaxCorner.getY()
                && start.getZ() > mMinCorner.getZ() && start.getZ() < mMaxCorner.getZ())
            return start;

        Vector direction = end.clone().subtract(start);

        // Do intersection tests
        Vector hit = null;
        if ((hit = getFaceIntersection(start.getX() - mMinCorner.getX(), end.getX() - mMinCorner.getX(), start, direction, BlockFace.WEST)) != null)
            return hit;
        if ((hit = getFaceIntersection(start.getX() - mMaxCorner.getX(), end.getX() - mMaxCorner.getX(), start, direction, BlockFace.WEST)) != null)
            return hit;
        if ((hit = getFaceIntersection(start.getY() - mMinCorner.getY(), end.getY() - mMinCorner.getY(), start, direction, BlockFace.DOWN)) != null)
            return hit;
        if ((hit = getFaceIntersection(start.getY() - mMaxCorner.getY(), end.getY() - mMaxCorner.getY(), start, direction, BlockFace.DOWN)) != null)
            return hit;
        if ((hit = getFaceIntersection(start.getZ() - mMinCorner.getZ(), end.getZ() - mMinCorner.getZ(), start, direction, BlockFace.NORTH)) != null)
            return hit;
        if ((hit = getFaceIntersection(start.getZ() - mMaxCorner.getZ(), end.getZ() - mMaxCorner.getZ(), start, direction, BlockFace.NORTH)) != null)
            return hit;

        // No hit
        return null;
    }

    private Vector getFaceIntersection(double dist1, double dist2, Vector start, Vector direction, BlockFace side) {
        if (dist1 * dist2 >= 0.0)
            return null;

        if (dist1 == dist2)
            return null;

        Vector hit = start.clone().add(direction.clone().multiply((-dist1) / (dist2 - dist1)));

        // Check that it is on the face
        switch (side) {
            case DOWN:
            case UP:
                if (hit.getZ() > mMinCorner.getZ() && hit.getZ() < mMaxCorner.getZ() && hit.getX() > mMinCorner.getX() && hit.getX() < mMaxCorner.getX())
                    return hit;
                break;
            case NORTH:
            case SOUTH:
                if (hit.getX() > mMinCorner.getX() && hit.getX() < mMaxCorner.getX() && hit.getY() > mMinCorner.getY() && hit.getY() < mMaxCorner.getY())
                    return hit;
                break;
            case WEST:
            case EAST:
                if (hit.getZ() > mMinCorner.getZ() && hit.getZ() < mMaxCorner.getZ() && hit.getY() > mMinCorner.getY() && hit.getY() < mMaxCorner.getY())
                    return hit;
                break;
            default:
                throw new AssertionError("No other values should be used here");
        }

        // Not in the face
        return null;
    }
}
