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

package au.com.grieve.reversion.translators.v411be_to_v409be;

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
import au.com.grieve.reversion.protocol.bedrock.v411.Bedrock_v411;
import au.com.grieve.reversion.translators.v411be_to_v409be.handlers.StartGameHandler_v411be_to_v409be;
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

public class Register_v411be_to_v409be {
    public static BedrockRegisteredTranslator TRANSLATOR = BedrockRegisteredTranslator.builder()
            .fromEdition("bedrock")
            .fromProtocolVersion(411)
            .toEdition("bedrock")
            .toProtocolVersion(409)
            .codec(Bedrock_v411.V411_CODEC)
            .translator(BedrockTranslator.class)
            .blockMapper(BlockMapper.builder()
                    .upstreamPalette(() -> Register_v411be_to_v409be.class.getResourceAsStream("/protocol/bedrock-v409/blockpalette.nbt"))
                    .build()
            )
            .itemMapper(ItemMapper.DEFAULT)
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
            .registerPacketHandler(StartGamePacket.class, StartGameHandler_v411be_to_v409be.class)
            .registerPacketHandler(UpdateBlockPacket.class, UpdateBlockHandler_Bedrock.class)
            .build();
}
