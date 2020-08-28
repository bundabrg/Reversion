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

package au.com.grieve.reversion.protocol.bedrock.v411;

import au.com.grieve.reversion.protocol.bedrock.v409.BedrockPacketHelper_v409;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.nukkitx.protocol.bedrock.data.command.CommandParamType.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BedrockPacketHelper_v411 extends BedrockPacketHelper_v409 {
    public static final BedrockPacketHelper_v411 INSTANCE = new BedrockPacketHelper_v411();

    @Override
    protected void registerCommandParams() {
        this.addCommandParam(1, INT);
        this.addCommandParam(2, FLOAT);
        this.addCommandParam(3, VALUE);
        this.addCommandParam(4, WILDCARD_INT);
        this.addCommandParam(5, OPERATOR);
        this.addCommandParam(6, TARGET);

        this.addCommandParam(8, WILDCARD_TARGET);
        this.addCommandParam(15, FILE_PATH);
        this.addCommandParam(30, STRING);
        this.addCommandParam(38, BLOCK_POSITION);
        this.addCommandParam(39, POSITION);
        this.addCommandParam(42, MESSAGE);
        this.addCommandParam(44, TEXT);
        this.addCommandParam(48, JSON);
        this.addCommandParam(55, COMMAND);
    }

}
