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

package au.com.addstar.monolith.internal.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import au.com.addstar.monolith.Monolith;

public abstract class Message<T> {

    public static Message<?> load(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        DataInputStream in = new DataInputStream(stream);

        try {
            Message<?> message = MessageType.newMessage(in.readUTF());
            if (message == null)
                return null;

            message.read(in);
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] save(Message<?> message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            String id = MessageType.getId(message);
            if (id == null) {
                Monolith.getInstance().getLogger().warning("Attempted to send unregistered plugin message type " + message.getClass());
                return null;
            }

            out.writeUTF(id);
            message.write(out);
        } catch (IOException e) {
            // Should not happen
            throw new AssertionError(e);
        }

        return stream.toByteArray();
    }

    public abstract void read(DataInputStream in) throws IOException;

    public abstract void write(DataOutputStream out) throws IOException;

    public boolean isSource(Message<?> message) {
        return false;
    }

    public T getReply() {
        return null;
    }
}
