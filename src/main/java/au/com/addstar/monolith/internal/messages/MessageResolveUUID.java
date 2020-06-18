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

package au.com.addstar.monolith.internal.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import au.com.addstar.monolith.lookup.PlayerDefinition;

import com.google.common.collect.Lists;

public class MessageResolveUUID extends Message<List<PlayerDefinition>> {
    private int id;
    private Iterable<UUID> ids;
    private List<PlayerDefinition> result;

    public MessageResolveUUID(int id, Iterable<UUID> names) {
        this.id = id;
        this.ids = names;
    }

    public MessageResolveUUID() {
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        id = in.readInt();
        String strList = in.readUTF();
        result = Lists.newArrayList();

        if (strList.isEmpty())
            return;
        String[] pairList = strList.split(";");

        for (String pair : pairList) {
            String[] parts = pair.split(":");
            UUID id = UUID.fromString(parts[0]);
            result.add(new PlayerDefinition(id, parts[1]));
        }
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeUTF(StringUtils.join(ids.iterator(), ';'));
    }

    @Override
    public boolean isSource(Message<?> message) {
        if (message instanceof MessageResolveUUID) {
            return ((MessageResolveUUID) message).id == id;
        }
        return false;
    }

    @Override
    public List<PlayerDefinition> getReply() {
        return result;
    }
}
