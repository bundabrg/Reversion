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

package au.com.grieve.reversion.translators.v411be_to_v409be;

import au.com.grieve.reversion.api.RegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.protocol.bedrock.v411.Bedrock_v411;

public class Register_v411be_to_v409be {
    public static RegisteredTranslator TRANSLATOR = RegisteredTranslator.builder()
            .fromEdition("bedrock")
            .fromProtocolVersion(411)
            .toEdition("bedrock")
            .toProtocolVersion(409)
            .codec(Bedrock_v411.V411_CODEC)
            .translator(BedrockTranslator.class)
            .build();
}
