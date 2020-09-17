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

package au.com.grieve.reversion.translators.v390ee_to_v408be.handlers;

import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.InventoryTransactionHandler_Bedrock;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.data.inventory.InventoryActionData;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;

import java.util.List;

public class InventoryTransactionHandler_v390ee_to_v408be extends InventoryTransactionHandler_Bedrock {
    private InventoryActionData cachedAction;

    public InventoryTransactionHandler_v390ee_to_v408be(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromUpstream(InventoryTransactionPacket packet) {
        super.fromUpstream(packet);

        List<InventoryActionData> originalActions = packet.getActions();

        if (originalActions.size() >= 2) {
            // Downstream expects a crafting packet to to go from output slot to the destination slot whereas we
            // use 2 packets to transition from Output slot to the UI output slot to the UI cursor slot so we will
            // cache part of the first packet and when the next comes we build a new action skipping the middle step.
            if (originalActions.get(1).getSource().getContainerId() == ContainerId.UI && originalActions.get(1).getSlot() == 50) {
                cachedAction = originalActions.get(0);
                return true;
            }

            if (cachedAction != null && originalActions.get(0).getSource().getContainerId() == ContainerId.UI
                    && originalActions.get(0).getSlot() == 50) {
                InventoryActionData action = originalActions.get(1);
                originalActions.clear();
                originalActions.add(cachedAction);
                originalActions.add(action);
                cachedAction = null;
            }
        }

        return false;
    }
}
