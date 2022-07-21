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

package au.com.grieve.reversion.translators.v390ee_to_v408be;

import au.com.grieve.reversion.editions.bedrock.BedrockRegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.*;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import au.com.grieve.reversion.editions.education.handlers.StartGameHandler_Education;
import au.com.grieve.reversion.protocol.education.v390.Education_v390;
import au.com.grieve.reversion.translators.v390ee_to_v408be.handlers.CreativeContentHandler_v390ee_to_v408be;
import au.com.grieve.reversion.translators.v390ee_to_v408be.handlers.InventoryTransactionHandler_v390ee_to_v408be;
import au.com.grieve.reversion.translators.v390ee_to_v408be.handlers.PlayerActionHandlerHandler_v390ee_to_v408be;
import com.nukkitx.protocol.bedrock.packet.*;

public class Register_v390ee_to_v408be {
    public static BedrockRegisteredTranslator TRANSLATOR = BedrockRegisteredTranslator.builder()
            .fromEdition("education")
            .fromProtocolVersion(390)
            .toEdition("bedrock")
            .toProtocolVersion(408)
            .codec(Education_v390.V390_CODEC)
            .translator(BedrockTranslator.class)
            .blockMapper(BlockMapper.builder()
                            .upstreamPalette(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/protocol/education-v390/blockpalette.nbt"))
                            .downstreamPalette(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/protocol/bedrock-v408/blockpalette.nbt"))
                            .runtimeMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/runtime_block_mapper.json"))

//                    .blockMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/block_mapper.json"))
//                    .debugName("v309ee_to_v408be")
//                    .debug(true)

                            .build()
            )
            .entityMapper(EntityMapper.builder()
                    .entityMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/entity_mapper.json"))
                    .build()
            )
            .itemMapper(ItemMapper.builder()
                            .enchantmentMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/enchantment_mapper.json"))
                            .upstreamPalette(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/protocol/education-v390/itempalette.json"))
                            .downstreamPalette(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/protocol/bedrock-v408/itempalette.json"))
                            .runtimeMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/runtime_item_mapper.json"))

//                    .itemMapper(() -> Register_v390ee_to_v408be.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/item_mapper.json"))
//                    .debugName("v390ee_to_v408be")
//                    .debug(true)

                            .build()
            )
            .registerPacketHandler(AddEntityPacket.class, AddEntityHandler_Bedrock.class)
            .registerPacketHandler(AddItemEntityPacket.class, AddItemEntityHandler_Bedrock.class)
            .registerPacketHandler(AddPlayerPacket.class, AddPlayerHandler_Bedrock.class)
            .registerPacketHandler(BlockEntityDataPacket.class, BlockEntityData_Bedrock.class)
            .registerPacketHandler(CraftingDataPacket.class, CraftingDataHandler_Bedrock.class)
            .registerPacketHandler(CreativeContentPacket.class, CreativeContentHandler_v390ee_to_v408be.class)
            .registerPacketHandler(InventoryContentPacket.class, InventoryContentHandler_Bedrock.class)
            .registerPacketHandler(InventorySlotPacket.class, InventorySlotHandler_Bedrock.class)
            .registerPacketHandler(InventoryTransactionPacket.class, InventoryTransactionHandler_v390ee_to_v408be.class)
            .registerPacketHandler(LevelChunkPacket.class, LevelChunkHandler_Bedrock.class)
            .registerPacketHandler(LevelEventPacket.class, LevelEventHandler_Bedrock.class)
            .registerPacketHandler(LoginPacket.class, LoginHandler_Bedrock.class)
            .registerPacketHandler(MobArmorEquipmentPacket.class, MobArmorEquipmentHandler_Bedrock.class)
            .registerPacketHandler(MobEquipmentPacket.class, MobEquipmentHandler_Bedrock.class)
            .registerPacketHandler(PlayerActionPacket.class, PlayerActionHandlerHandler_v390ee_to_v408be.class)
            .registerPacketHandler(SetEntityDataPacket.class, SetEntityDataHandler_Bedrock.class)
            .registerPacketHandler(StartGamePacket.class, StartGameHandler_Education.class)
            .registerPacketHandler(UpdateBlockPacket.class, UpdateBlockHandler_Bedrock.class)
            .build();
}
