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

package au.com.grieve.reversion.api;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public abstract class BaseTranslator implements Translator {
    private final ReversionSession reversionSession;

    @Setter
    private Translator upstreamTranslator;

    @Setter
    private Translator downstreamTranslator;

    @Override
    public Translator getServerTranslator() {
        return downstreamTranslator == null ? this : downstreamTranslator.getServerTranslator();
    }

    @Override
    public Translator getClientTranslator() {
        return upstreamTranslator == null ? this : upstreamTranslator.getClientTranslator();
    }

    @Override
    public boolean fromUpstream(BedrockPacket packet) {
        // Pass to our Downstream
        return toDownstream(packet);
    }

    @Override
    public boolean fromDownstream(BedrockPacket packet) {
        // Pass to our Upstream
        return toUpstream(packet);
    }

    @Override
    public boolean fromServer(BedrockPacket packet) {
        return fromDownstream(packet);
    }

    @Override
    public boolean fromClient(BedrockPacket packet) {
        return fromUpstream(packet);
    }

    @Override
    public boolean toUpstream(BedrockPacket packet) {
        return upstreamTranslator != null ? upstreamTranslator.fromDownstream(packet) : toClient(packet);
    }

    @Override
    public boolean toDownstream(BedrockPacket packet) {
        return downstreamTranslator != null ? downstreamTranslator.fromUpstream(packet) : toServer(packet);
    }

    @Override
    public abstract BedrockPacketCodec getCodec();

    @Override
    public boolean toServer(BedrockPacket packet) {
        return reversionSession.toServer(packet);
    }

    @Override
    public boolean toClient(BedrockPacket packet) {
        return reversionSession.toClient(packet);
    }
}
