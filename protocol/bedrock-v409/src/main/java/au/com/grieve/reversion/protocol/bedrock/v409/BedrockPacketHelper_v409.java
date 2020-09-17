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

package au.com.grieve.reversion.protocol.bedrock.v409;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.network.VarInts;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.v407.BedrockPacketHelper_v407;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

import static java.util.Objects.requireNonNull;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BedrockPacketHelper_v409 extends BedrockPacketHelper_v407 {
    public static final BedrockPacketHelper_v409 INSTANCE = new BedrockPacketHelper_v409();

    public boolean isBlockingItem(int id) {
        return id == 354;
    }

    @Override
    public ItemData readItem(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int id = VarInts.readInt(buffer);
        if (id == 0) {
            // We don't need to read anything extra.
            return ItemData.AIR;
        }
        int aux = VarInts.readInt(buffer);
        short damage = (short) (aux >> 8);
        if (damage == Short.MAX_VALUE) damage = -1;
        int count = aux & 0xff;
        int nbtSize = buffer.readShortLE();
        NbtMap compoundTag = null;
        if (nbtSize > 0) {
            try (NBTInputStream reader = NbtUtils.createReaderLE(new ByteBufInputStream(buffer.readSlice(nbtSize)))) {
                compoundTag = (NbtMap) reader.readTag();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        } else if (nbtSize == -1) {
            try (NBTInputStream reader = NbtUtils.createNetworkReader(new ByteBufInputStream(buffer))) {
                int nbtTagCount = VarInts.readUnsignedInt(buffer);
                if (nbtTagCount == 1) {
                    compoundTag = (NbtMap) reader.readTag();
                } else {
                    throw new IllegalArgumentException("Expected 1 tag but got " + nbtTagCount);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }
        String[] canPlace = readArray(buffer, new String[0], this::readString);
        String[] canBreak = readArray(buffer, new String[0], this::readString);

        long blockingTicks = 0;
        if (isBlockingItem(id)) {
            blockingTicks = VarInts.readLong(buffer);
        }
        return ItemData.of(id, damage, count, compoundTag, canPlace, canBreak, blockingTicks);
    }

    @Override
    public void writeItem(ByteBuf buffer, ItemData item) {
        requireNonNull(item, "item is null!");

        // Write id
        int id = item.getId();
        if (id == 0) {
            // We don't need to write anything extra.
            buffer.writeByte(0);
            return;
        }
        VarInts.writeInt(buffer, id);
        // Write damage and count
        short damage = item.getDamage();
        if (damage == -1) damage = Short.MAX_VALUE;
        VarInts.writeInt(buffer, (damage << 8) | (item.getCount() & 0xff));
        if (item.getTag() != null) {
            buffer.writeShortLE(-1);
            VarInts.writeUnsignedInt(buffer, 1); // Hardcoded in current version
            this.writeTag(buffer, item.getTag());
        } else {
            buffer.writeShortLE(0);
        }
        writeArray(buffer, item.getCanPlace(), this::writeString);
        writeArray(buffer, item.getCanBreak(), this::writeString);

        if (isBlockingItem(item.getId())) {
            VarInts.writeLong(buffer, item.getBlockingTicks());
        }
    }
}
