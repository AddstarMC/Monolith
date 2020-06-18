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

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Provides methods for converting objects into strings that can be read by the methods in {@link Parser}
 */
public final class Stringifier {
    private Stringifier() {
    }

    public static String toString(EulerAngle angle) {
        return String.format("%.1f:%.1f:%.1f", angle.getX(), angle.getY(), angle.getZ());
    }

    public static String toString(Vector vector) {
        return String.format("%.1f:%.1f:%.1f", vector.getX(), vector.getY(), vector.getZ());
    }

    public static String toString(Object object) {
        if (object instanceof EulerAngle)
            return toString((EulerAngle) object);
        else if (object instanceof Vector)
            return toString((Vector) object);
        else
            return String.valueOf(object);
    }
}
