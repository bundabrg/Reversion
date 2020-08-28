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

package au.com.grieve.reversion.protocol.education.v363;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.v361.BedrockPacketHelper_v361;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationPacketHelper_v363 extends BedrockPacketHelper_v361 {
    public static final BedrockPacketHelper INSTANCE = new EducationPacketHelper_v363();

    @Override
    protected void registerEntityData() {
        super.registerEntityData();

        this.addEntityData(107, EntityData.AMBIENT_SOUND_INTERVAL);
        this.addEntityData(108, EntityData.AMBIENT_SOUND_INTERVAL_RANGE);
        this.addEntityData(109, EntityData.AMBIENT_SOUND_EVENT_NAME);
    }

}
