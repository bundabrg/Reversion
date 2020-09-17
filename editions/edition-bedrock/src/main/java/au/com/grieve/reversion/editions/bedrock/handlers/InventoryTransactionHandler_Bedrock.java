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
        packet.setItemInHand(getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToDownstream(packet.getItemInHand()));

        List<InventoryActionData> originalActions = packet.getActions();
        List<InventoryActionData> translatedList = new ArrayList<>();

        for (InventoryActionData actionData : originalActions) {
            InventoryActionData translated = new InventoryActionData(
                    actionData.getSource(),
                    actionData.getSlot(),
                    getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToDownstream(actionData.getFromItem()),
                    getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToDownstream(actionData.getToItem()),
                    actionData.getStackNetworkId()
            );
            translatedList.add(translated);
        }

        packet.getActions().clear();
        packet.getActions().addAll(translatedList);
        return false;
    }
}
