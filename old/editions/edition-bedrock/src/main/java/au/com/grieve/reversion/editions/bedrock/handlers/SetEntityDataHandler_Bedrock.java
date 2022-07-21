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

package au.com.grieve.reversion.editions.bedrock.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;

public class SetEntityDataHandler_Bedrock extends PacketHandler<BedrockTranslator, SetEntityDataPacket> {

    public SetEntityDataHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(SetEntityDataPacket packet) {
        EntityMapper.Entity entity = new EntityMapper.Entity();
        entity.setId(packet.getRuntimeEntityId());
        entity.getEntityData().putAll(packet.getMetadata());
        entity = getTranslator().getRegisteredTranslator().getEntityMapper().mapEntitytoUpstream(getTranslator(), entity);

        packet.getMetadata().clear();
        packet.getMetadata().putAll(entity.getEntityData());
        return false;
    }
}
