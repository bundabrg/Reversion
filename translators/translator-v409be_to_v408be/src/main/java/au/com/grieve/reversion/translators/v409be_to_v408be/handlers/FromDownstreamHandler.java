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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FromDownstreamHandler implements BedrockPacketHandler {
    private final Translator_v409be_to_v408be translator;

//    @Override
//    public boolean handle(StartGamePacket packet) {
//        // Translate Block Palette
//        List<NbtMap> translatedPalette = new ArrayList<>();
//        for (NbtMap block : packet.getBlockPalette()) {
//            try {
//                translatedPalette.add(Translator_v409be_to_v408be.MAPPER.blockToUpstream(block));
//            } catch (MapperException e) {
//                e.printStackTrace();
//            }
//        }
//        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette.toArray(new NbtMap[0])));
//
//        // Remove codebuilder
//        packet.getGamerules().add(new GameRuleData<>("codebuilder", false));
//
//        return false;
//    }

//    @Override
//    public boolean handle(CreativeContentPacket packet) {
//        InventoryContentPacket inventoryContentPacket = new InventoryContentPacket();
//        //noinspection deprecation
//        inventoryContentPacket.setContainerId(ContainerId.CREATIVE);
//
//        // Translate each item. Any missing items we will complain about to assist debug
//        List<ItemData> translatedItems = new ArrayList<>();
//        for (ItemData item : packet.getContents()) {
//            ItemData translatedItem = Translator_v390ee_to_v408be.MAPPER.itemToUpstream(item);
//
//            // Ignore items that were translated
//            if (item.equals(translatedItem)) {
//                translatedItems.add(translatedItem);
//            }
//        }
//
//        inventoryContentPacket.setContents(translatedItems.toArray(new ItemData[0]));
//        translator.toUpstream(inventoryContentPacket);
//        return true;
//    }

//    @Override
//    public boolean handle(AddItemEntityPacket packet) {
//        packet.setItemInHand(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItemInHand()));
//        return false;
//    }

//    @Override
//    public boolean handle(AddPlayerPacket packet) {
//        packet.setHand(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getHand()));
//        return false;
//    }

//    @Override
//    public boolean handle(InventoryContentPacket packet) {
//        packet.setContents(
//                Arrays.stream(packet.getContents())
//                        .map(i -> Translator_v390ee_to_v408be.MAPPER.itemToUpstream(i))
//                        .toArray(ItemData[]::new)
//        );
//        return false;
//    }

//    @Override
//    public boolean handle(InventorySlotPacket packet) {
//        packet.setItem(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItem()));
//        return false;
//    }

//    @Override
//    public boolean handle(MobArmorEquipmentPacket packet) {
//        packet.setBoots(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getBoots()));
//        packet.setChestplate(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getChestplate()));
//        packet.setHelmet(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getHelmet()));
//        packet.setLeggings(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getLeggings()));
//        return false;
//    }

//    @Override
//    public boolean handle(MobEquipmentPacket packet) {
//        packet.setItem(Translator_v390ee_to_v408be.MAPPER.itemToUpstream(packet.getItem()));
//        return false;
//    }

//    @Override
//    public boolean handle(CraftingDataPacket packet) {
//        List<CraftingData> translatedList = new ArrayList<>();
//        for (CraftingData craftingData : packet.getCraftingData()) {
//            CraftingData translated = new CraftingData(
//                    craftingData.getType(),
//                    craftingData.getRecipeId(),
//                    craftingData.getWidth(),
//                    craftingData.getHeight(),
//                    craftingData.getInputId(),
//                    craftingData.getInputDamage(),
//                    Arrays.stream(craftingData.getInputs())
//                            .map(i -> Translator_v390ee_to_v408be.MAPPER.itemToUpstream(i))
//                            .toArray(ItemData[]::new),
//                    Arrays.stream(craftingData.getOutputs())
//                            .map(i -> Translator_v390ee_to_v408be.MAPPER.itemToUpstream(i))
//                            .toArray(ItemData[]::new),
//                    craftingData.getUuid(),
//                    craftingData.getCraftingTag(),
//                    craftingData.getPriority(),
//                    craftingData.getNetworkId()
//            );
//            translatedList.add(translated);
//        }
//        packet.getCraftingData().clear();
//        packet.getCraftingData().addAll(translatedList);
//        return false;
//    }

//    @Override
//    public boolean handle(AddEntityPacket packet) {
//        EntityMapper.Entity entity = new EntityMapper.Entity();
//        entity.setId(packet.getRuntimeEntityId());
//        entity.setIdentifier(packet.getIdentifier());
//        entity.getEntityData().putAll(packet.getMetadata());
//        entity = Translator_v390ee_to_v408be.MAPPER.getEntityMapper().addEntity(entity);
//
//        packet.setIdentifier(entity.getIdentifier());
//        packet.getMetadata().clear();
//        packet.getMetadata().putAll(entity.getEntityData());
//        return false;
//    }
//
//    @Override
//    public boolean handle(SetEntityDataPacket packet) {
//        EntityMapper.Entity entity = new EntityMapper.Entity();
//        entity.setId(packet.getRuntimeEntityId());
//        entity.getEntityData().putAll(packet.getMetadata());
//        entity = Translator_v390ee_to_v408be.MAPPER.getEntityMapper().toUpstream(entity);
//
//        packet.getMetadata().clear();
//        packet.getMetadata().putAll(entity.getEntityData());
//        return false;
//    }
}
