/*
 * MIT License
 *
 * Copyright (c) 2022 Reversion Developers
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
import com.nukkitx.protocol.bedrock.data.inventory.CraftingData;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingDataType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.PotionMixData;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;

import java.util.ArrayList;
import java.util.List;

public class CraftingDataHandler_Bedrock extends PacketHandler<BedrockTranslator, CraftingDataPacket> {

    public CraftingDataHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(CraftingDataPacket packet) {
        List<CraftingData> translatedList = new ArrayList<>();
        outer:
        for (CraftingData craftingData : packet.getCraftingData()) {
            List<ItemData> inputs = new ArrayList<>();
            List<ItemData> outputs = new ArrayList<>();
            if (craftingData.getType() != CraftingDataType.MULTI) {
                for (ItemData itemData : craftingData.getInputs()) {
                    ItemData translated = getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToUpstream(itemData, true);
                    if (translated == null) {
                        continue outer;
                    }
                    inputs.add(translated);
                }

                for (ItemData itemData : craftingData.getOutputs()) {
                    ItemData translated = getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToUpstream(itemData, true);
                    if (translated == null) {
                        continue outer;
                    }
                    outputs.add(translated);
                }
            }

            CraftingData translated = new CraftingData(
                    craftingData.getType(),
                    craftingData.getRecipeId(),
                    craftingData.getWidth(),
                    craftingData.getHeight(),
                    craftingData.getInputId(),
                    craftingData.getInputDamage(),
                    inputs,
                    outputs,
                    craftingData.getUuid(),
                    craftingData.getCraftingTag(),
                    craftingData.getPriority(),
                    craftingData.getNetworkId()
            );
            translatedList.add(translated);
        }

        List<PotionMixData> translatedPotionMixData = new ArrayList<>();
        for (PotionMixData pmd : packet.getPotionMixData()) {
            ItemData input = getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToUpstream(ItemData.of(pmd.getInputId(), (short) pmd.getInputMeta(), 1), true);
            ItemData reagent = getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToUpstream(ItemData.of(pmd.getReagentId(), (short) pmd.getReagentMeta(), 1), true);
            ItemData output = getTranslator().getRegisteredTranslator().getItemMapper().mapItemDataToUpstream(ItemData.of(pmd.getOutputId(), (short) pmd.getOutputMeta(), 1), true);

            if (input != null && reagent != null && output != null) {
                translatedPotionMixData.add(new PotionMixData(input.getId(), input.getDamage(), reagent.getId(), reagent.getDamage(),
                        output.getId(), output.getDamage()));
            }
        }


        packet.getCraftingData().clear();
        packet.getCraftingData().addAll(translatedList);

        packet.getPotionMixData().clear();
        packet.getPotionMixData().addAll(translatedPotionMixData);
        return false;
    }
}
