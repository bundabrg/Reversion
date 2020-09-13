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

package au.com.grieve.reversion.translators.v390ee_to_v408be;

import au.com.grieve.reversion.editions.bedrock.BedrockRegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.AddEntityHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.AddItemEntityHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.AddPlayerHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.CraftingDataHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.InventoryContentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.InventorySlotHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.LevelChunkHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.LoginHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.MobArmorEquipmentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.MobEquipmentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.SetEntityDataHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.UpdateBlockHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import au.com.grieve.reversion.editions.education.handlers.StartGameHandler_Education;
import au.com.grieve.reversion.protocol.education.v390.Education_v390;
import au.com.grieve.reversion.translators.v390ee_to_v408be.handlers.CreativeContentHandler_v390ee_to_v408be;
import au.com.grieve.reversion.translators.v390ee_to_v408be.handlers.InventoryTransactionHandler_v390ee_to_v408be;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import com.nukkitx.protocol.bedrock.packet.CreativeContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import com.nukkitx.protocol.bedrock.packet.InventoryTransactionPacket;
import com.nukkitx.protocol.bedrock.packet.LevelChunkPacket;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.packet.MobArmorEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import com.nukkitx.protocol.bedrock.packet.UpdateBlockPacket;

public class Register_v390ee_to_v408be {
    public static BedrockRegisteredTranslator TRANSLATOR = BedrockRegisteredTranslator.builder()
            .fromEdition("education")
            .fromProtocolVersion(390)
            .toEdition("bedrock")
            .toProtocolVersion(408)
            .codec(Education_v390.V390_CODEC)
            .translator(BedrockTranslator.class)
            .blockMapper(BlockMapper.builder()
                            .palette(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/protocol/education-v390/blockpalette.nbt"))
                            .runtimeMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/block_runtime_mapper.json"))
                            .blockMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/block_mapper.json"))
//                    .downstreamPalette(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/protocol/bedrock-v408/blockpalette.nbt"))
//                    .debugName("v309ee_to_v408be")
                            .build()
            )
            .entityMapper(EntityMapper.builder()
                    .entityMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/entity_mapper.json"))
                    .build()
            )
            .itemMapper(ItemMapper.builder()
                    .itemMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/item_mapper.json"))
                    .enchantmentMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/enchantment_mapper.json"))
                    .build()
            )
            .registerPacketHandler(AddEntityPacket.class, AddEntityHandler_Bedrock.class)
            .registerPacketHandler(AddItemEntityPacket.class, AddItemEntityHandler_Bedrock.class)
            .registerPacketHandler(AddPlayerPacket.class, AddPlayerHandler_Bedrock.class)
            .registerPacketHandler(CraftingDataPacket.class, CraftingDataHandler_Bedrock.class)
            .registerPacketHandler(CreativeContentPacket.class, CreativeContentHandler_v390ee_to_v408be.class)
            .registerPacketHandler(InventoryContentPacket.class, InventoryContentHandler_Bedrock.class)
            .registerPacketHandler(InventorySlotPacket.class, InventorySlotHandler_Bedrock.class)
            .registerPacketHandler(InventoryTransactionPacket.class, InventoryTransactionHandler_v390ee_to_v408be.class)
            .registerPacketHandler(LevelChunkPacket.class, LevelChunkHandler_Bedrock.class)
            .registerPacketHandler(LoginPacket.class, LoginHandler_Bedrock.class)
            .registerPacketHandler(MobArmorEquipmentPacket.class, MobArmorEquipmentHandler_Bedrock.class)
            .registerPacketHandler(MobEquipmentPacket.class, MobEquipmentHandler_Bedrock.class)
            .registerPacketHandler(SetEntityDataPacket.class, SetEntityDataHandler_Bedrock.class)
            .registerPacketHandler(StartGamePacket.class, StartGameHandler_Education.class)
            .registerPacketHandler(UpdateBlockPacket.class, UpdateBlockHandler_Bedrock.class)
            .build();
}
