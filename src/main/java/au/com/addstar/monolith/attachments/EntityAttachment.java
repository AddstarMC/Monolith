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
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class EntityAttachment<T extends Entity> extends Attachment {
    private final T entity;
    private final Function<? super T, Vector> offsetFunction;

    private final Location locationCache;

    public EntityAttachment(T entity) {
        this(entity, Functions.constant(new Vector(0, 0, 0)));
    }

    public EntityAttachment(T entity, Vector offset) {
        this(entity, Functions.constant(offset));
    }

    public EntityAttachment(T entity, Function<? super T, Vector> offsetFunction) {
        this.entity = entity;
        this.offsetFunction = offsetFunction;

        locationCache = entity.getLocation();
    }

    @Override
    public Location getLocation() {
        entity.getLocation(locationCache);

        Vector offset = offsetFunction.apply(entity);
        if (offset != null) locationCache.add(offset);
        return locationCache;
    }
}
