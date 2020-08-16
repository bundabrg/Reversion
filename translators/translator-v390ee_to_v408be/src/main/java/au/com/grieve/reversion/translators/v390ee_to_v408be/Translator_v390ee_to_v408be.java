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

package au.com.grieve.reversion.translators.v390ee_to_v408be;

import au.com.grieve.reversion.annotations.ReversionTranslator;
import au.com.grieve.reversion.api.BaseTranslator;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.translators.v390ee_to_v408be.handlers.FromUpstreamHandler;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.education.v390.Education_v390;
import lombok.Getter;

@ReversionTranslator(
        fromEdition = "education",
        fromVersion = 390,
        toEdition = "bedrock",
        toVersion = 408
)
public class Translator_v390ee_to_v408be extends BaseTranslator {
    @Getter
    private final BedrockPacketCodec codec = Education_v390.V390_CODEC;

    private final BedrockPacketHandler fromUpstreamHandler;

    public Translator_v390ee_to_v408be(ReversionSession reversionSession) {
        super(reversionSession);

        fromUpstreamHandler = new FromUpstreamHandler(this);
    }

    @Override
    public boolean fromUpstream(BedrockPacket packet) {
        if (packet.handle(fromUpstreamHandler)) {
            return true;
        }
        return super.fromUpstream(packet);
    }
}
