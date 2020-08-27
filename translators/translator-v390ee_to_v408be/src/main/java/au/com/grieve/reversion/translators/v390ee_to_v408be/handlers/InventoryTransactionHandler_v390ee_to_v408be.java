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
import au.com.grieve.reversion.editions.bedrock.handlers.InventoryTransactionHandler_Bedrock;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;

import java.util.List;

public class InventoryTransactionHandler_v390ee_to_v408be extends InventoryTransactionHandler_Bedrock {
    public static final InventoryTransactionHandler_v390ee_to_v408be INSTANCE = new InventoryTransactionHandler_v390ee_to_v408be();

    private static final String CACHE_UI = "v390ee_to-v408be.cacheUI";

    @Override
    public boolean fromUpstream(BedrockTranslator translator, InventoryTransactionPacket packet) {
        super.fromUpstream(translator, packet);

        List<InventoryActionData> originalActions = packet.getActions();

        if (originalActions.size() >= 2) {
            // Downstream expects a crafting packet to to go from output slot to the destination slot whereas we
            // use 2 packets to transition from Output slot to the UI output slot to the UI cursor slot so we will
            // cache part of the first packet and when the next comes we build a new action skipping the middle step.
            if (originalActions.get(1).getSource().getContainerId() == ContainerId.UI && originalActions.get(1).getSlot() == 50) {
                translator.getStorage().put(CACHE_UI, originalActions.get(0));
                return true;
            }

            InventoryActionData cacheUISlot = (InventoryActionData) translator.getStorage().get(CACHE_UI);

            if (cacheUISlot != null && originalActions.get(0).getSource().getContainerId() == ContainerId.UI
                    && originalActions.get(0).getSlot() == 50) {
                InventoryActionData action = originalActions.get(1);
                originalActions.clear();
                originalActions.add(cacheUISlot);
                originalActions.add(action);
                translator.getStorage().remove(CACHE_UI);
            }
        }

        return false;
    }
}
