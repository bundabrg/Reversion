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

import au.com.grieve.reversion.exceptions.MapperException;
import au.com.grieve.reversion.translators.v390ee_to_v408be.Translator_v390ee_to_v408be;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingData;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import com.nukkitx.protocol.bedrock.packet.CreativeContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class FromDownstreamHandler implements BedrockPacketHandler {
    private final Translator_v390ee_to_v408be translator;

    @Override
    public boolean handle(StartGamePacket packet) {
        // Translate Block Palette
        List<NbtMap> translatedPalette = new ArrayList<>();
        for (NbtMap block : packet.getBlockPalette()) {
            try {
                translatedPalette.add(Translator_v390ee_to_v408be.MAPPER.blockToUpstream(block));
            } catch (MapperException e) {
                e.printStackTrace();
            }
        }
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette.toArray(new NbtMap[0])));
        return false;
    }

    /**
     * Minecraft Education uses an inventory packet of type Creative
     *
     * @param packet CreativeContent packet
     * @return true if processed
     */
    @Override
    public boolean handle(CreativeContentPacket packet) {
        InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
        //noinspection deprecation
        inventoryContentPacket.setContainerId(ContainerId.CREATIVE);

        // Translate each item. Any missing items we will complain about to assist debug
        List<ItemData> translatedItems = new ArrayList<>();
        for (ItemData item : packet.getContents()) {
            ItemData translatedItem = Translator_v390ee_to_v408be.MAPPER.itemToUpstream(item);

            // Ignore items that were translated
            if (item.equals(translatedItem)) {
                translatedItems.add(translatedItem);
            }
        }

        inventoryContentPacket.setContents(translatedItems.toArray(new ItemData[0]));
        translator.toUpstream(inventoryContentPacket);
        return true;
    }

    @Override
    public boolean handle(AddItemEntityPacket packet) {
        packet.setItemInHand(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItemInHand()));
        return false;
    }

    @Override
    public boolean handle(AddPlayerPacket packet) {
        packet.setHand(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getHand()));
        return false;
    }

    @Override
    public boolean handle(InventoryContentPacket packet) {
        packet.setContents(
                Arrays.stream(packet.getContents())
                        .map(i -> Translator_v390ee_to_v408be.MAPPER.itemToUpstream(i))
                        .toArray(ItemData[]::new)
        );
        return false;
    }

    @Override
    public boolean handle(InventorySlotPacket packet) {
        packet.setItem(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItem()));
        return false;
    }

    @Override
    public boolean handle(MobArmorEquipmentPacket packet) {
        packet.setBoots(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getBoots()));
        packet.setChestplate(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getChestplate()));
        packet.setHelmet(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getHelmet()));
        packet.setLeggings(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getLeggings()));
        return false;
    }

    @Override
    public boolean handle(MobEquipmentPacket packet) {
        packet.setItem(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItem()));
        return false;
    }

    @Override
    public boolean handle(CraftingDataPacket packet) {
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
                            .map(i -> Translator_v390ee_to_v408be.MAPPER.itemToUpstream(i))
                            .toArray(ItemData[]::new),
                    Arrays.stream(craftingData.getOutputs())
                            .map(i -> Translator_v390ee_to_v408be.MAPPER.itemToUpstream(i))
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
