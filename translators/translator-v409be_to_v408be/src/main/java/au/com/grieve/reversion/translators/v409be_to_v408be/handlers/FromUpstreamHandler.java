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

package au.com.grieve.reversion.translators.v409be_to_v408be.handlers;

import au.com.grieve.reversion.translators.v409be_to_v408be.Translator_v409be_to_v408be;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FromUpstreamHandler implements BedrockPacketHandler {
    private final Translator_v409be_to_v408be translator;

    @Override
    public boolean handle(LoginPacket packet) {
        packet.setProtocolVersion(408);
        return false;
    }

//    @Override
//    public boolean handle(InventoryTransactionPacket packet) {
//        packet.setItemInHand(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItemInHand()));
//
//        List<InventoryActionData> originalActions = packet.getActions();
//        List<InventoryActionData> translatedList = new ArrayList<>();
//
//        if (originalActions.size() >= 2) {
//            // Downstream expects a crafting packet to to go from output slot to the destination slot whereas we
//            // use 2 packets to transition from Output slot to the UI output slot to the UI cursor slot so we will
//            // cache part of the first packet and when the next comes we build a new action skipping the middle step.
//            if (originalActions.get(1).getSource().getContainerId() == ContainerId.UI && originalActions.get(1).getSlot() == 50) {
//                cacheUISlot = originalActions.get(0);
//                return true;
//            }
//
//            if (cacheUISlot != null && originalActions.get(0).getSource().getContainerId() == ContainerId.UI
//                    && originalActions.get(0).getSlot() == 50) {
//                InventoryActionData action = originalActions.get(1);
//                originalActions.clear();
//                originalActions.add(cacheUISlot);
//                originalActions.add(action);
//                cacheUISlot = null;
//            }
//        }
//
//        for (InventoryActionData actionData : originalActions) {
//            InventoryActionData translated = new InventoryActionData(
//                    actionData.getSource(),
//                    actionData.getSlot(),
//                    Translator_v390ee_to_v408be.MAPPER.itemToUpstream(actionData.getFromItem()),
//                    Translator_v390ee_to_v408be.MAPPER.itemToUpstream(actionData.getToItem()),
//                    actionData.getStackNetworkId()
//            );
//            translatedList.add(translated);
//        }
//
//        packet.getActions().clear();
//        packet.getActions().addAll(translatedList);
//
//        return false;
//    }
}
