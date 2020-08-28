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
import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;

import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionHandler_Bedrock extends PacketHandler<BedrockTranslator, InventoryTransactionPacket> {

    public InventoryTransactionHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromUpstream(InventoryTransactionPacket packet) {
        packet.setItemInHand(getTranslator().getItemMapper().mapItemDataToDownstream(getTranslator(), packet.getItemInHand()));

        List<InventoryActionData> originalActions = packet.getActions();
        List<InventoryActionData> translatedList = new ArrayList<>();

        for (InventoryActionData actionData : originalActions) {
            InventoryActionData translated = new InventoryActionData(
                    actionData.getSource(),
                    actionData.getSlot(),
                    getTranslator().getItemMapper().mapItemDataToDownstream(getTranslator(), actionData.getFromItem()),
                    getTranslator().getItemMapper().mapItemDataToDownstream(getTranslator(), actionData.getToItem()),
                    actionData.getStackNetworkId()
            );
            translatedList.add(translated);
        }

        packet.getActions().clear();
        packet.getActions().addAll(translatedList);
        return false;
    }
}
