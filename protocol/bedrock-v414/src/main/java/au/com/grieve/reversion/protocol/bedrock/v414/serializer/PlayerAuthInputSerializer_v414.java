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

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import com.nukkitx.protocol.bedrock.v388.serializer.PlayerAuthInputSerializer_v388;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerAuthInputSerializer_v414 extends PlayerAuthInputSerializer_v388 {

    public static final PlayerAuthInputSerializer_v414 INSTANCE = new PlayerAuthInputSerializer_v414();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, PlayerAuthInputPacket packet) {
        super.serialize(buffer, helper, packet);

        // TODO UVarInt Clock?
        VarInts.writeUnsignedInt(buffer, 0);

        // TODO Vector3f moment?
        helper.writeVector3f(buffer, Vector3f.ZERO);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, PlayerAuthInputPacket packet) {
        super.deserialize(buffer, helper, packet);

        // TODO UVarInt Clock?
        VarInts.readUnsignedInt(buffer);

        // TODO Vector3f moment?
        helper.readVector3f(buffer);
    }
}
