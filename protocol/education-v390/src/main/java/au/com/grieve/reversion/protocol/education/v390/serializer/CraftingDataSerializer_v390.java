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

package au.com.grieve.reversion.protocol.education.v390.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerMixData;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingDataType;
import com.nukkitx.protocol.bedrock.data.inventory.PotionMixData;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import com.nukkitx.protocol.bedrock.v361.serializer.CraftingDataSerializer_v361;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CraftingDataSerializer_v390 extends CraftingDataSerializer_v361 {
    public static final CraftingDataSerializer_v390 INSTANCE = new CraftingDataSerializer_v390();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, CraftingDataPacket packet, BedrockSession session) {
        helper.writeArray(buffer, packet.getCraftingData(), (buf, craftingData) -> {
            VarInts.writeInt(buf, craftingData.getType().ordinal());
            switch (craftingData.getType()) {
                case SHAPELESS:
                case SHAPELESS_CHEMISTRY:
                case SHULKER_BOX:
                    this.writeShapelessRecipe(buf, helper, craftingData, session);
                    break;
                case SHAPED:
                case SHAPED_CHEMISTRY:
                    this.writeShapedRecipe(buf, helper, craftingData, session);
                    break;
                case FURNACE:
                case FURNACE_DATA:
                    this.writeFurnaceRecipe(buf, helper, craftingData, session);
                    break;
                case MULTI:
                    this.writeMultiRecipe(buf, helper, craftingData);
                    break;
            }
        });


        // Potions
        helper.writeArray(buffer, packet.getPotionMixData(), (buf, potionMixData) -> {
            VarInts.writeInt(buf, potionMixData.getInputId());
            VarInts.writeInt(buf, potionMixData.getReagentId());
            VarInts.writeInt(buf, potionMixData.getOutputId());
        });

        // Potion Container Change
        helper.writeArray(buffer, packet.getContainerMixData(), (buf, containerMixData) -> {
            VarInts.writeInt(buf, containerMixData.getInputId());
            VarInts.writeInt(buf, containerMixData.getReagentId());
            VarInts.writeInt(buf, containerMixData.getOutputId());
        });

        // Unknown Array
        buffer.writeByte(0);

        buffer.writeBoolean(packet.isCleanRecipes());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, CraftingDataPacket packet, BedrockSession session) {
        helper.readArray(buffer, packet.getCraftingData(), buf -> {
            int typeInt = VarInts.readInt(buf);
            CraftingDataType type = CraftingDataType.byId(typeInt);

            switch (type) {
                case SHAPELESS:
                case SHAPELESS_CHEMISTRY:
                case SHULKER_BOX:
                    return this.readShapelessRecipe(buf, helper, type, session);
                case SHAPED:
                case SHAPED_CHEMISTRY:
                    return this.readShapedRecipe(buf, helper, type, session);
                case FURNACE:
                case FURNACE_DATA:
                    return this.readFurnaceRecipe(buf, helper, type, session);
                case MULTI:
                    return this.readMultiRecipe(buf, helper, type);
                default:
                    throw new IllegalArgumentException("Unhandled crafting data type: " + type);
            }
        });

        // Potions
        helper.readArray(buffer, packet.getPotionMixData(), buf -> {
            int fromPotionId = VarInts.readInt(buf);
            int ingredient = VarInts.readInt(buf);
            int toPotionId = VarInts.readInt(buf);
            return new PotionMixData(fromPotionId, 0, ingredient, 0, toPotionId, 0);
        });

        // Potion Container Change
        helper.readArray(buffer, packet.getContainerMixData(), buf -> {
            int fromItemId = VarInts.readInt(buf);
            int ingredient = VarInts.readInt(buf);
            int toItemId = VarInts.readInt(buf);
            return new ContainerMixData(fromItemId, ingredient, toItemId);
        });

        // I don't really know what this is but its an array{varint, array{varint, varint}}
        // We'll just toss it away for now
        int length = VarInts.readUnsignedInt(buffer);
        for (int i = 0; i < length; i++) {
            VarInts.readInt(buffer);
            int length2 = VarInts.readUnsignedInt(buffer);
            for (int j = 0; j < length2; j++) {
                VarInts.readInt(buffer);
                VarInts.readInt(buffer);
            }
        }
        packet.setCleanRecipes(buffer.readBoolean());
    }
}
