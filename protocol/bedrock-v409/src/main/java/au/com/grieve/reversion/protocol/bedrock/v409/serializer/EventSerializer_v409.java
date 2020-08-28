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

package au.com.grieve.reversion.protocol.bedrock.v409.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.data.event.AgentCreatedEventData;
import com.nukkitx.protocol.bedrock.data.event.EventData;
import com.nukkitx.protocol.bedrock.data.event.EventDataType;
import com.nukkitx.protocol.bedrock.packet.EventPacket;
import com.nukkitx.protocol.bedrock.v388.serializer.EventSerializer_v388;
import io.netty.buffer.ByteBuf;

public class EventSerializer_v409 extends EventSerializer_v388 {
    public static final EventSerializer_v409 INSTANCE = new EventSerializer_v409();

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, EventPacket packet) {
        packet.setUniqueEntityId(VarInts.readLong(buffer));

        int eventId = VarInts.readInt(buffer);
        Preconditions.checkElementIndex(eventId, VALUES.length, "EventDataType");
        EventDataType type = VALUES[eventId];

        packet.setUsePlayerId(buffer.readByte());

        EventData data;

        switch (type) {
            case ACHIEVEMENT_AWARDED:
                data = this.readAchievementAwarded(buffer, helper);
                break;
            case ENTITY_INTERACT:
                data = this.readEntityInteract(buffer, helper);
                break;
            case PORTAL_BUILT:
                data = this.readPortalBuilt(buffer, helper);
                break;
            case PORTAL_USED:
                data = this.readPortalUsed(buffer, helper);
                break;
            case MOB_KILLED:
                data = this.readMobKilled(buffer, helper);
                break;
            case CAULDRON_USED:
                data = this.readCauldronUsed(buffer, helper);
                break;
            case PLAYER_DIED:
                data = this.readPlayerDied(buffer, helper);
                break;
            case BOSS_KILLED:
                data = this.readBossKilled(buffer, helper);
                break;
            case AGENT_COMMAND:
                data = this.readAgentCommand(buffer, helper);
                break;
            case AGENT_CREATED:
                data = AgentCreatedEventData.INSTANCE;
                break;
            case PATTERN_REMOVED:
                data = this.readPatternRemoved(buffer, helper);
                break;
            case SLASH_COMMAND_EXECUTED:
                data = this.readSlashCommandExecuted(buffer, helper);
                break;
            case FISH_BUCKETED:
                data = this.readFishBucketed(buffer, helper);
                break;
            default:
                throw new UnsupportedOperationException("Unknown event type " + type);
        }
        packet.setEventData(data);
    }

}
