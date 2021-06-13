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

package au.com.addstar.monolith.effects.emitters;

import java.util.concurrent.TimeUnit;

import au.com.addstar.monolith.attachments.Attachment;

public class ContinuousEmitter extends Emitter {
    private boolean isRunning;
    private long emitTime;

    private long delay;
    private long interval;

    public ContinuousEmitter(Attachment attachment) {
        super(attachment);
    }

    /**
     * Sets the time between the start and the first
     * emission
     *
     * @param delay The delay in whatever unit specified
     * @param unit  The time unit for the delay
     */
    public void setDelay(long delay, TimeUnit unit) {
        this.delay = unit.toMillis(delay);
    }

    /**
     * Gets the time between the start and the first
     * emission
     *
     * @param unit The time unit for the delay
     * @return The delay time in {@code unit}
     */
    public long getDelay(TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(delay, unit);
    }

    /**
     * Sets the time between emissions
     *
     * @param interval The delay in whatever unit specified
     * @param unit     The time unit for the delay
     */
    public void setInterval(long interval, TimeUnit unit) {
        this.interval = unit.toMillis(interval);
    }

    /**
     * Gets the time between emissions
     *
     * @param unit The time unit for the delay
     * @return The delay time in {@code unit}
     */
    public long getInterval(TimeUnit unit) {
        return TimeUnit.MILLISECONDS.convert(interval, unit);
    }

    @Override
    protected void onStart() {
        emitTime = System.currentTimeMillis() + delay;
        isRunning = true;
    }

    @Override
    protected void onStop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void onTick() {
        if (System.currentTimeMillis() >= emitTime) {
            emit();
            emitTime = System.currentTimeMillis() + interval;
        }
    }
}
