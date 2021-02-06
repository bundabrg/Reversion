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

package au.com.grieve.reversion.translators.v408be_to_v419be.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.protocol.bedrock.packet.BlockEntityDataPacket;

public class BlockEntityData_v408be_to_v419be extends PacketHandler<BedrockTranslator, BlockEntityDataPacket> {

    public BlockEntityData_v408be_to_v419be(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(BlockEntityDataPacket packet) {
        // Convert from name to ID if we have an Item key
        if (packet.getData().containsKey("Item")) {
            packet.setData(packet.getData().toBuilder()
                    .putCompound("Item", getTranslator().getRegisteredTranslator().getItemMapper()
                            .mapBlockEntityDataToUpstream(packet.getData().getCompound("Item")))
                    .build());
        }
        return false;
    }
}
