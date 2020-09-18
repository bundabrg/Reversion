/*
 * MIT License
 *
 * Copyright (c) 2020 Reversion Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package au.com.grieve.reversion.protocol.bedrock.v414.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerSlotType;
import com.nukkitx.protocol.bedrock.packet.ItemStackResponsePacket;
import com.nukkitx.protocol.bedrock.v407.serializer.ItemStackResponseSerializer_v407;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemStackResponseSerializer_v414 extends ItemStackResponseSerializer_v407 {

    public static final ItemStackResponseSerializer_v414 INSTANCE = new ItemStackResponseSerializer_v414();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ItemStackResponsePacket packet) {
        helper.writeArray(buffer, packet.getEntries(), (buf, response) -> {
            buf.writeBoolean(!response.isSuccess());
            VarInts.writeInt(buffer, response.getRequestId());

            if (!response.isSuccess())
                return;

            helper.writeArray(buf, response.getContainers(), (buf2, containerEntry) -> {
                buf2.writeByte(containerEntry.getContainer().ordinal());

                helper.writeArray(buf2, containerEntry.getItems(), (byteBuf, itemEntry) -> {
                    byteBuf.writeByte(itemEntry.getSlot());
                    byteBuf.writeByte(itemEntry.getHotbarSlot());
                    byteBuf.writeByte(itemEntry.getCount());
                    VarInts.writeInt(byteBuf, itemEntry.getStackNetworkId());
                });
            });
        });
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ItemStackResponsePacket packet) {
        List<ItemStackResponsePacket.Response> entries = packet.getEntries();
        helper.readArray(buffer, entries, buf -> {
            boolean error = buf.readBoolean();
            int requestId = VarInts.readInt(buf);

            if (error)
                return new ItemStackResponsePacket.Response(false, requestId, Collections.emptyList());

            List<ItemStackResponsePacket.ContainerEntry> containerEntries = new ArrayList<>();
            helper.readArray(buf, containerEntries, buf2 -> {
                ContainerSlotType container = ContainerSlotType.values()[buf2.readByte()];

                List<ItemStackResponsePacket.ItemEntry> itemEntries = new ArrayList<>();
                helper.readArray(buf2, itemEntries, byteBuf -> new ItemStackResponsePacket.ItemEntry(
                        byteBuf.readByte(),
                        byteBuf.readByte(),
                        byteBuf.readByte(),
                        VarInts.readInt(byteBuf)
                ));
                return new ItemStackResponsePacket.ContainerEntry(container, itemEntries);
            });
            return new ItemStackResponsePacket.Response(true, requestId, containerEntries);
        });
    }
}
