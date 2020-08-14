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

/**
 * A translator translates from one version to another. It may chain to another translator and
 * may also deal directly with the upstream client or downstream server.
 */
public interface Translator {

    /**
     * Downstream Translator in chain. If null then we are the closest to the Server
     *
     * @return the next translator
     */
    Translator getDownstreamTranslator();

    /**
     * Set the downstream Translator in the chain
     *
     * @param downstreamTranslator downstream translator
     */
    void setDownstreamTranslator(Translator downstreamTranslator);

    /**
     * Return the upstream Translator in chain. If null then we are the closest to the Client
     *
     * @return the upstream translator
     */
    Translator getUpstreamTranslator();

    /**
     * Set the upstream Translator in the chain
     *
     * @param upstreamTranslator upstream translator
     */
    void setUpstreamTranslator(Translator upstreamTranslator);

    /**
     * Return the Translator closest to the Server
     *
     * @return the translator closest to the server
     */
    Translator getServerTranslator();

    /**
     * Return the Translator closest to the Client
     *
     * @return the translator closest to the client
     */
    Translator getClientTranslator();

    /**
     * Return the Codec to use when we are speaking to the Client
     *
     * @return the codec used
     */
    BedrockPacketCodec getCodec();

    /**
     * Receive a packet from upstream
     *
     * @param packet Packet to receive
     * @return true if handled
     */
    boolean fromUpstream(BedrockPacket packet);

    /**
     * Receive a packet from downstream
     *
     * @param packet Packet to receive
     * @return true if handled
     */
    boolean fromDownstream(BedrockPacket packet);

    /**
     * Receive a packet from the Server
     * This is only executed if this translator is the end in the chain
     *
     * @param packet Packet to receieve
     * @return true if handled
     */
    boolean fromServer(BedrockPacket packet);

    /**
     * Receive a packet from Client
     * This is only executed if this translator is the first in the chain
     *
     * @param packet Packet to receive
     * @return true if handled
     */
    boolean fromClient(BedrockPacket packet);

    /**
     * Send a packet to our upstream
     *
     * @param packet Packet to send
     * @return true if handled
     */
    boolean toUpstream(BedrockPacket packet);

    /**
     * Send a packet to our downstream
     *
     * @param packet Packet to send
     * @return true if handled
     */
    boolean toDownstream(BedrockPacket packet);

    /**
     * Send a packet to Server
     * This is only executed when we are the last in the chain
     *
     * @param packet Packet to send
     * @return true if handled
     */
    boolean toServer(BedrockPacket packet);

    /**
     * Send a packet to the Client
     * This is only executed when we are the first in the chain
     *
     * @param packet Packet to send
     * @return true if handled
     */
    boolean toClient(BedrockPacket packet);
}
