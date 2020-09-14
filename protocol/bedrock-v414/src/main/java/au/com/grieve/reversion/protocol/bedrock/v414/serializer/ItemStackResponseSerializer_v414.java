/*
 * Reversion - Minecraft Protocol Support for Bedrock
 * Copyright (C) 2020 Reversion Developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
