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

package au.com.addstar.monolith.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;

import au.com.addstar.monolith.Monolith;
import au.com.addstar.monolith.lookup.LookupCallback;

public class FutureWaiter<V> implements Runnable {
    private final Future<V> mFuture;
    private final LookupCallback<V> mCallback;
    private long mMaxTime;

    public FutureWaiter(Future<V> future, LookupCallback<V> callback) {
        mFuture = future;
        mCallback = callback;
        mMaxTime = -1;
    }

    public FutureWaiter(Future<V> future, LookupCallback<V> callback, int maxTime, TimeUnit unit) {
        this(future, callback);
        mMaxTime = unit.toMillis(maxTime);
    }

    @Override
    public void run() {
        try {
            V value;
            if (mMaxTime < 0)
                value = mFuture.get();
            else
                value = mFuture.get(mMaxTime, TimeUnit.MILLISECONDS);

            if (value == null)
                onFailure(null);
            else
                onSuccess(value);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            onFailure(e);
        }
    }

    private void onSuccess(final V value) {
        Bukkit.getScheduler().runTask(Monolith.getInstance(), () -> mCallback.onResult(true, value, null));
    }

    private void onFailure(final Throwable error) {
        Bukkit.getScheduler().runTask(Monolith.getInstance(), () -> mCallback.onResult(false, null, error));
    }
}
