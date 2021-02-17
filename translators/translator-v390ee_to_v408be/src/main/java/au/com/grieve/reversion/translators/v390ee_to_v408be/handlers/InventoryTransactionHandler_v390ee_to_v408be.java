/*
 * MIT License
 *
 * Copyright (c) 2021 Reversion Developers
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
import com.nukkitx.protocol.bedrock.data.inventory.TransactionType;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InventoryTransactionHandler_v390ee_to_v408be extends InventoryTransactionHandler_Bedrock {
    private InventoryActionData cachedAction;

    @Getter
    private List<PendingUseItem> pendingUseItems = new ArrayList<>();

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

        // If using an Item with action on a block we will wait up to 5ms to see if we also have an action on the air
        // If so, we will send both packets upstream. If not, we send this packet + a block_interact upstream
        if (packet.getTransactionType() == TransactionType.ITEM_USE && packet.getBlockPosition() != null) {
            // Action on the block
            if (packet.getActionType() == 0) {
                PendingUseItem pending = new PendingUseItem();
                pending.setFirstPacket(packet);
                pending.setScheduledFuture(getTranslator().getReversionSession().getServer().getEventLoopGroup().schedule(() -> {
                    if (getTranslator().getDownstreamTranslator() != null) {

                        getTranslator().getDownstreamTranslator().fromUpstream(pending.getFirstPacket());
                        if (pending.getActionPacket() != null) {
                            getTranslator().getDownstreamTranslator().fromUpstream(pending.getActionPacket());
                        }
                    } else {
                        getTranslator().toServer(pending.getFirstPacket());
                        if (pending.getActionPacket() != null) {
                            getTranslator().toServer(pending.getActionPacket());
                        }
                    }
                    pendingUseItems.remove(pending);
                }, 5, TimeUnit.MILLISECONDS));

                pendingUseItems.add(pending);

                return true;
            }

            // Action on the air
            if (packet.getActionType() == 1) {
                PendingUseItem pending = getPendingUseItems().stream()
                        .filter(i -> i.getFirstPacket().getBlockPosition().equals(packet.getBlockPosition()))
                        .findFirst()
                        .orElse(null);

                if (pending != null) {
                    pendingUseItems.remove(pending);
                    pending.getScheduledFuture().cancel(false);
                    if (getTranslator().getDownstreamTranslator() != null) {
                        getTranslator().getDownstreamTranslator().fromUpstream(pending.getFirstPacket());
                    } else {
                        getTranslator().toServer(pending.getFirstPacket());
                    }
                }
            }
        }

        return false;
    }

    @Data
    public static class PendingUseItem {
        private InventoryTransactionPacket firstPacket;
        private PlayerActionPacket actionPacket;
        private ScheduledFuture<?> scheduledFuture;
    }
}
