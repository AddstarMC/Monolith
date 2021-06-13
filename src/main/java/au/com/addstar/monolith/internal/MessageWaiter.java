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

package au.com.addstar.monolith.internal;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;

import au.com.addstar.monolith.internal.messages.Message;

public class MessageWaiter implements PluginMessageListener {
    private final List<WaitFuture<?>> mWaiting;

    public MessageWaiter() {
        mWaiting = Lists.newLinkedList();
    }

    public <T> ListenableFuture<T> waitForReply(Message<T> source) {
        WaitFuture<T> future = new WaitFuture<>(source);
        mWaiting.add(future);

        return future;
    }

    private void checkMessage(Message<?> message) {
        Iterator<WaitFuture<?>> it = mWaiting.iterator();

        while (it.hasNext()) {
            WaitFuture<?> future = it.next();

            if (future.isReply(message)) {
                it.remove();
                future.done(message);
            } else if (future.isExpired()) {
                it.remove();
                future.done(new TimeoutException());
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        Message<?> message = Message.load(data);
        if (message != null)
            checkMessage(message);
    }

    private class WaitFuture<T> extends AbstractFuture<T> {
        private final Message<T> mSource;
        private final long mExpireTime;

        public WaitFuture(Message<T> source) {
            mSource = source;
            mExpireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        }

        @SuppressWarnings("unchecked")
        public void done(Message<?> message) {
            set((T) message.getReply());
        }

        public void done(Throwable error) {
            setException(error);
        }

        public boolean isReply(Message<?> message) {
            return message.isSource(mSource);
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= mExpireTime;
        }
    }
}
