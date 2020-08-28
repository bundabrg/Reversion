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
import com.nukkitx.protocol.bedrock.data.inventory.CraftingData;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CraftingDataHandler_Bedrock extends PacketHandler<BedrockTranslator, CraftingDataPacket> {

    public CraftingDataHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(CraftingDataPacket packet) {
        List<CraftingData> translatedList = new ArrayList<>();
        for (CraftingData craftingData : packet.getCraftingData()) {
            CraftingData translated = new CraftingData(
                    craftingData.getType(),
                    craftingData.getRecipeId(),
                    craftingData.getWidth(),
                    craftingData.getHeight(),
                    craftingData.getInputId(),
                    craftingData.getInputDamage(),
                    Arrays.stream(craftingData.getInputs())
                            .map(i -> getTranslator().getItemMapper().mapItemDataToUpstream(getTranslator(), i))
                            .toArray(ItemData[]::new),
                    Arrays.stream(craftingData.getOutputs())
                            .map(i -> getTranslator().getItemMapper().mapItemDataToUpstream(getTranslator(), i))
                            .toArray(ItemData[]::new),
                    craftingData.getUuid(),
                    craftingData.getCraftingTag(),
                    craftingData.getPriority(),
                    craftingData.getNetworkId()
            );
            translatedList.add(translated);
        }
        packet.getCraftingData().clear();
        packet.getCraftingData().addAll(translatedList);
        return false;
    }
}