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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PacketHandler<T extends Translator, P extends BedrockPacket> {

    private final T translator;

    /**
     * Process packet directly from Client
     *
     * @param packet The packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromClient(P packet) {
        return false;
    }

    /**
     * Process packet from upstream translator or client
     *
     * @param packet the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromUpstream(P packet) {
        return false;
    }

    /**
     * Process packet directly from Server
     *
     * @param packet the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromServer(P packet) {
        return false;
    }

    /**
     * Process packet from downstream or server
     *
     * @param packet the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromDownstream(P packet) {
        return false;
    }

    /**
     * Process packet directly to Client
     *
     * @param packet The packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toClient(P packet) {
        return false;
    }

    /**
     * Process packet from upstream translator or client
     *
     * @param packet the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toUpstream(P packet) {
        return false;
    }

    /**
     * Process packet directly from Server
     *
     * @param packet the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toServer(P packet) {
        return false;
    }

    /**
     * Process packet from downstream or server
     *
     * @param packet the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toDownstream(P packet) {
        return false;
    }
}
