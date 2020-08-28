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
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;

public class MobArmorEquipmentHandler_Bedrock extends PacketHandler<BedrockTranslator, MobArmorEquipmentPacket> {

    public MobArmorEquipmentHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(MobArmorEquipmentPacket packet) {
        packet.setBoots(getTranslator().getItemMapper().mapItemDataToUpstream(getTranslator(), packet.getBoots()));
        packet.setChestplate(getTranslator().getItemMapper().mapItemDataToUpstream(getTranslator(), packet.getChestplate()));
        packet.setHelmet(getTranslator().getItemMapper().mapItemDataToUpstream(getTranslator(), packet.getHelmet()));
        packet.setLeggings(getTranslator().getItemMapper().mapItemDataToUpstream(getTranslator(), packet.getLeggings()));
        return false;
    }
}
