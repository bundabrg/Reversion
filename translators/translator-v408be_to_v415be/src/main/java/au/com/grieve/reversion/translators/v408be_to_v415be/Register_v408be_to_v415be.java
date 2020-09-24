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

package au.com.grieve.reversion.translators.v408be_to_v415be;

import au.com.grieve.reversion.editions.bedrock.BedrockRegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.AddEntityHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.AddItemEntityHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.AddPlayerHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.CraftingDataHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.CreativeContentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.InventoryContentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.InventorySlotHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.InventoryTransactionHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.LevelChunkHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.LoginHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.MobArmorEquipmentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.MobEquipmentHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.SetEntityDataHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.handlers.UpdateBlockHandler_Bedrock;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import au.com.grieve.reversion.translators.v408be_to_v415be.handlers.StartGameHandler_v408be_to_v415be;
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
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;

public class Register_v408be_to_v415be {
    public static BedrockRegisteredTranslator TRANSLATOR = BedrockRegisteredTranslator.builder()
            .fromEdition("bedrock")
            .fromProtocolVersion(408)
            .toEdition("bedrock")
            .toProtocolVersion(415)
            .codec(Bedrock_v408.V408_CODEC)
            .translator(BedrockTranslator.class)
            .blockMapper(BlockMapper.builder()
                            .upstreamPalette(() -> Register_v408be_to_v415be.class.getResourceAsStream("/protocol/bedrock-v408/blockpalette.nbt"))
                            .downstreamPalette(() -> Register_v408be_to_v415be.class.getResourceAsStream("/protocol/bedrock-v415/blockpalette.nbt"))
                            .runtimeMapper(() -> Register_v408be_to_v415be.class.getResourceAsStream("/translators/v408be_to_v415be/mappings/runtime_block_mapper.json"))

//                    .blockMapper(() -> Register_v408be_to_v415be.class.getResourceAsStream("/translators/v408be_to_v415be/mappings/block_mapper.json"))
//                    .debugName("v408be_to_v415be")
//                    .debug(true)

                            .build()
            )
            .itemMapper(ItemMapper.builder()
                            .upstreamPalette(() -> Register_v408be_to_v415be.class.getResourceAsStream("/protocol/bedrock-v408/itempalette.json"))
                            .downstreamPalette(() -> Register_v408be_to_v415be.class.getResourceAsStream("/protocol/bedrock-v415/itempalette.json"))
                            .runtimeMapper(() -> Register_v408be_to_v415be.class.getResourceAsStream("/translators/v408be_to_v415be/mappings/runtime_item_mapper.json"))

//                    .itemMapper(() -> Register_v408be_to_v415be.class.getResourceAsStream("/translators/v408be_to_v415be/mappings/item_mapper.json"))
//                    .debugName("v408be_to_v415be")
//                    .debug(true)

                            .build()
            )
            .entityMapper(EntityMapper.DEFAULT)
            .registerPacketHandler(AddEntityPacket.class, AddEntityHandler_Bedrock.class)
            .registerPacketHandler(AddItemEntityPacket.class, AddItemEntityHandler_Bedrock.class)
            .registerPacketHandler(AddPlayerPacket.class, AddPlayerHandler_Bedrock.class)
            .registerPacketHandler(CraftingDataPacket.class, CraftingDataHandler_Bedrock.class)
            .registerPacketHandler(CreativeContentPacket.class, CreativeContentHandler_Bedrock.class)
            .registerPacketHandler(InventoryContentPacket.class, InventoryContentHandler_Bedrock.class)
            .registerPacketHandler(InventorySlotPacket.class, InventorySlotHandler_Bedrock.class)
            .registerPacketHandler(InventoryTransactionPacket.class, InventoryTransactionHandler_Bedrock.class)
            .registerPacketHandler(LevelChunkPacket.class, LevelChunkHandler_Bedrock.class)
            .registerPacketHandler(LoginPacket.class, LoginHandler_Bedrock.class)
            .registerPacketHandler(MobArmorEquipmentPacket.class, MobArmorEquipmentHandler_Bedrock.class)
            .registerPacketHandler(MobEquipmentPacket.class, MobEquipmentHandler_Bedrock.class)
            .registerPacketHandler(SetEntityDataPacket.class, SetEntityDataHandler_Bedrock.class)
            .registerPacketHandler(StartGamePacket.class, StartGameHandler_v408be_to_v415be.class)
            .registerPacketHandler(UpdateBlockPacket.class, UpdateBlockHandler_Bedrock.class)
            .build();
}