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

package au.com.addstar.monolith.attachments;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.google.common.base.Function;

public final class AttachmentFunctions {
    private AttachmentFunctions() {
    }

    /**
     * Provides a function that computes the offset based on the entities look vector
     *
     * @param offset The offset relative to the look direction
     * @param <T>    extends @link{org.bukkit.entity.LivingEntity}
     * @return The Function
     */
    public static <T extends LivingEntity> Function<T, Vector> lookRelativeOffset(Vector offset) {
        return new LookRelativeOffset<>(offset);
    }

    private static class LookRelativeOffset<T extends LivingEntity> implements Function<T, Vector> {
        private Location location;
        private final Vector offset;
        private final Vector staging;

        public LookRelativeOffset(Vector offset) {
            this.offset = offset;
            staging = new Vector();
        }

        @Override
        public Vector apply(T entity) {
            if (location == null)
                location = entity.getLocation();
            else
                entity.getLocation(location);

            staging.setX(0);
            staging.setY(0);
            staging.setZ(0);

            // Location should be facing the entities facing dir
            Vector forward = location.getDirection();

            // Compute other axis
            Vector up = new Vector(0, 1, 0);
            Vector left = up.crossProduct(forward).normalize();
            up = forward.clone().crossProduct(left).normalize();

            // Final compute
            staging.setX(
                    (forward.getX() * offset.getZ()) +
                            (left.getX() * -offset.getX()) +
                            (up.getX() * offset.getY())
            );
            staging.setY(
                    (forward.getY() * offset.getZ()) +
                            (left.getY() * -offset.getX()) +
                            (up.getY() * offset.getY())
            );
            staging.setZ(
                    (forward.getZ() * offset.getZ()) +
                            (left.getZ() * -offset.getX()) +
                            (up.getZ() * offset.getY())
            );

            return staging;
        }
    }
}
