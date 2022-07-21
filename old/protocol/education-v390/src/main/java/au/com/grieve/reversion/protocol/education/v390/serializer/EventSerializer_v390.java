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

package au.com.grieve.reversion.protocol.education.v390.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.data.event.AgentCreatedEventData;
import com.nukkitx.protocol.bedrock.data.event.EventData;
import com.nukkitx.protocol.bedrock.data.event.EventDataType;
import com.nukkitx.protocol.bedrock.packet.EventPacket;
import com.nukkitx.protocol.bedrock.v388.serializer.EventSerializer_v388;
import io.netty.buffer.ByteBuf;

public class EventSerializer_v390 extends EventSerializer_v388 {
    public static final EventSerializer_v390 INSTANCE = new EventSerializer_v390();

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
