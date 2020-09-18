/*
 * MIT License
 *
 * Copyright (c) 2020 Reversion Developers
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
     * Receive a packet from upstream
     *
     * @param packet Packet to receive
     * @return true if handled
     */
    <T extends BedrockPacket> boolean fromUpstream(T packet);

    /**
     * Receive a packet from downstream
     *
     * @param packet Packet to receive
     * @return true if handled
     */
    <T extends BedrockPacket> boolean fromDownstream(T packet);

    /**
     * Receive a packet from the Server
     * This is only executed if this translator is the end in the chain
     *
     * @param packet Packet to receieve
     * @return true if handled
     */
    <T extends BedrockPacket> boolean fromServer(T packet);

    /**
     * Receive a packet from Client
     * This is only executed if this translator is the first in the chain
     *
     * @param packet Packet to receive
     * @return true if handled
     */
    <T extends BedrockPacket> boolean fromClient(T packet);

    /**
     * Send a packet to our upstream
     *
     * @param packet Packet to send
     * @return true if handled
     */
    <T extends BedrockPacket> boolean toUpstream(T packet);

    /**
     * Send a packet to our downstream
     *
     * @param packet Packet to send
     * @return true if handled
     */
    <T extends BedrockPacket> boolean toDownstream(T packet);

    /**
     * Send a packet to Server
     * This is only executed when we are the last in the chain
     *
     * @param packet Packet to send
     * @return true if handled
     */
    <T extends BedrockPacket> boolean toServer(T packet);

    /**
     * Send a packet to the Client
     * This is only executed when we are the first in the chain
     *
     * @param packet Packet to send
     * @return true if handled
     */
    <T extends BedrockPacket> boolean toClient(T packet);

    /**
     * Return the Translator Codec
     *
     * @return the translator codec
     */
    BedrockPacketCodec getCodec();
}
