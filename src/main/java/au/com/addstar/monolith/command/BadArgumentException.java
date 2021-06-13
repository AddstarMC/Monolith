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

package au.com.addstar.monolith.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BadArgumentException extends RuntimeException {
    private static final long serialVersionUID = -5099437186563532852L;

    private int mArg;
    private ArrayList<String> mInfoLines;

    public BadArgumentException(int argument) {
        mArg = argument;
    }

    public BadArgumentException(int argument, String reason) {
        super(reason);
        mArg = argument;
    }

    public int getArgument() {
        return mArg;
    }

    public void setArgument(int argument) {
        mArg = argument;
    }

    public BadArgumentException addInfo(String line) {
        if (mInfoLines == null)
            mInfoLines = new ArrayList<>();
        mInfoLines.add(line);

        return this;
    }

    public BadArgumentException addInfo(Collection<String> lines) {
        if (mInfoLines == null)
            mInfoLines = new ArrayList<>();
        mInfoLines.addAll(lines);

        return this;
    }

    public List<String> getInfoLines() {
        if (mInfoLines == null)
            return Collections.emptyList();
        return mInfoLines;
    }
}
