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
        entity = getTranslator().getEntityMapper().mapEntitytoUpstream(getTranslator(), entity);

        packet.getMetadata().clear();
        packet.getMetadata().putAll(entity.getEntityData());
        return false;
    }
}
