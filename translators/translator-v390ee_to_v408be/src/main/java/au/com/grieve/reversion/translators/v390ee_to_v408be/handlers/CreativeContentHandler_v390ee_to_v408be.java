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

package au.com.grieve.reversion.translators.v390ee_to_v408be.handlers;

import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.CreativeContentHandler_Bedrock;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.packet.CreativeContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;


public class CreativeContentHandler_v390ee_to_v408be extends CreativeContentHandler_Bedrock {
    public static final CreativeContentHandler_v390ee_to_v408be INSTANCE = new CreativeContentHandler_v390ee_to_v408be();

    @Override
    public boolean fromDownstream(BedrockTranslator translator, CreativeContentPacket packet) {
        super.fromDownstream(translator, packet);

        // We use an InventoryContentPacket of type Creative
        InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
        inventoryContentPacket.setContainerId(ContainerId.CREATIVE);
        inventoryContentPacket.setContents(packet.getContents());
        translator.toUpstream(inventoryContentPacket);

        return true;
    }
}
