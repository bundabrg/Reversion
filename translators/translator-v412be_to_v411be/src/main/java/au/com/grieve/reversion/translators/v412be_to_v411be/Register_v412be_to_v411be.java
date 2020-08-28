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

package au.com.grieve.reversion.translators.v412be_to_v411be;

import au.com.grieve.reversion.api.RegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.protocol.bedrock.v412.Bedrock_v412;

public class Register_v412be_to_v411be {
    public static RegisteredTranslator TRANSLATOR = RegisteredTranslator.builder()
            .fromEdition("bedrock")
            .fromProtocolVersion(412)
            .toEdition("bedrock")
            .toProtocolVersion(411)
            .codec(Bedrock_v412.V412_CODEC)
            .translator(BedrockTranslator.class)
            .build();
}
