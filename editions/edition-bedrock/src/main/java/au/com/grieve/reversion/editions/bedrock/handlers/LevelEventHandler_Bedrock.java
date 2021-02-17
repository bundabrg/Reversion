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

package au.com.grieve.reversion.editions.bedrock.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;

public class LevelEventHandler_Bedrock extends PacketHandler<BedrockTranslator, LevelEventPacket> {

    public LevelEventHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(LevelEventPacket packet) {
        int id;
        int face;
        int damage;
        switch (packet.getType()) {
            case PARTICLE_DESTROY_BLOCK: // block
            case PARTICLE_DESTROY_BLOCK_NO_SOUND:
            case PARTICLE_FALLING_DUST:
                packet.setData(getTranslator().getRegisteredTranslator().getBlockMapper().mapRuntimeIdToUpstream(packet.getData()));
                break;
            case PARTICLE_CRACK_BLOCK: // (face << 24) | block
                id = packet.getData() & 16777215;
                face = packet.getData() >> 24;
                packet.setData(getTranslator().getRegisteredTranslator().getBlockMapper().mapRuntimeIdToUpstream(id) | (face << 24));
                break;
            case PARTICLE_ITEM_BREAK: // (id << 16) | damage
                id = packet.getData() >> 16;
                damage = packet.getData() & 65535;
                packet.setData((getTranslator().getRegisteredTranslator().getItemMapper().mapRuntimeIdToUpstream(id) << 16) | damage);
                break;
        }
        return false;
    }
}
