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

public abstract class PacketHandler<T extends Translator, P extends BedrockPacket> {
    /**
     * Process packet directly from Client
     *
     * @param translator The associated Translator
     * @param packet     The packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromClient(T translator, P packet) {
        return false;
    }

    /**
     * Process packet from upstream translator or client
     *
     * @param translator The associated translator
     * @param packet     the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromUpstream(T translator, P packet) {
        return false;
    }

    /**
     * Process packet directly from Server
     *
     * @param translator the associated translator
     * @param packet     the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromServer(T translator, P packet) {
        return false;
    }

    /**
     * Process packet from downstream or server
     *
     * @param translator the associated translator
     * @param packet     the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean fromDownstream(T translator, P packet) {
        return false;
    }

    /**
     * Process packet directly to Client
     *
     * @param translator The associated Translator
     * @param packet     The packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toClient(T translator, P packet) {
        return false;
    }

    /**
     * Process packet from upstream translator or client
     *
     * @param translator The associated translator
     * @param packet     the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toUpstream(T translator, P packet) {
        return false;
    }

    /**
     * Process packet directly from Server
     *
     * @param translator the associated translator
     * @param packet     the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toServer(T translator, P packet) {
        return false;
    }

    /**
     * Process packet from downstream or server
     *
     * @param translator the associated translator
     * @param packet     the packet to translate
     * @return true if handled and the packet should not be sent on
     */
    public boolean toDownstream(T translator, P packet) {
        return false;
    }
}
