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

package au.com.grieve.reversion.translators.v428be_to_v422be;

import au.com.grieve.reversion.editions.bedrock.BedrockRegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.*;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import au.com.grieve.reversion.protocol.bedrock.v428.Bedrock_v428;
import au.com.grieve.reversion.translators.v422be_to_v419be.handlers.StartGameHandler_v422be_to_v419be;
import com.nukkitx.protocol.bedrock.packet.*;

public class Register_v428be_to_v422be {
    public static BedrockRegisteredTranslator TRANSLATOR = BedrockRegisteredTranslator.builder()
            .fromEdition("bedrock")
            .fromProtocolVersion(428)
            .toEdition("bedrock")
            .toProtocolVersion(422)
            .codec(Bedrock_v428.V428_CODEC)
            .translator(BedrockTranslator.class)
            .blockMapper(BlockMapper.DEFAULT)
            .itemMapper(ItemMapper.DEFAULT)
            .entityMapper(EntityMapper.DEFAULT)
            .registerPacketHandler(AddEntityPacket.class, AddEntityHandler_Bedrock.class)
            .registerPacketHandler(AddItemEntityPacket.class, AddItemEntityHandler_Bedrock.class)
            .registerPacketHandler(AddPlayerPacket.class, AddPlayerHandler_Bedrock.class)
            .registerPacketHandler(BlockEntityDataPacket.class, BlockEntityData_Bedrock.class)
            .registerPacketHandler(CraftingDataPacket.class, CraftingDataHandler_Bedrock.class)
            .registerPacketHandler(CreativeContentPacket.class, CreativeContentHandler_Bedrock.class)
            .registerPacketHandler(InventoryContentPacket.class, InventoryContentHandler_Bedrock.class)
            .registerPacketHandler(InventorySlotPacket.class, InventorySlotHandler_Bedrock.class)
            .registerPacketHandler(InventoryTransactionPacket.class, InventoryTransactionHandler_Bedrock.class)
            .registerPacketHandler(LevelChunkPacket.class, LevelChunkHandler_Bedrock.class)
            .registerPacketHandler(LevelEventPacket.class, LevelEventHandler_Bedrock.class)
            .registerPacketHandler(LoginPacket.class, LoginHandler_Bedrock.class)
            .registerPacketHandler(MobArmorEquipmentPacket.class, MobArmorEquipmentHandler_Bedrock.class)
            .registerPacketHandler(MobEquipmentPacket.class, MobEquipmentHandler_Bedrock.class)
            .registerPacketHandler(SetEntityDataPacket.class, SetEntityDataHandler_Bedrock.class)
            .registerPacketHandler(StartGamePacket.class, StartGameHandler_v422be_to_v419be.class)
            .registerPacketHandler(UpdateBlockPacket.class, UpdateBlockHandler_Bedrock.class)
            .build();
}
