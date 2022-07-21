/*
 * MIT License
 *
 * Copyright (c) 2022 Reversion Developers
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
