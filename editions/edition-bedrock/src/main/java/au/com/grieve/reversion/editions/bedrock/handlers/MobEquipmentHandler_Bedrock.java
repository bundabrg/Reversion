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

package au.com.grieve.reversion.editions.bedrock.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;

public class MobEquipmentHandler_Bedrock extends PacketHandler<BedrockTranslator, MobEquipmentPacket> {
    public static final MobEquipmentHandler_Bedrock INSTANCE = new MobEquipmentHandler_Bedrock();

    @Override
    public boolean fromDownstream(BedrockTranslator translator, MobEquipmentPacket packet) {
        packet.setItem(translator.getItemMapper().mapItemDataToUpstream(packet.getItem()));
        return false;
    }
}
