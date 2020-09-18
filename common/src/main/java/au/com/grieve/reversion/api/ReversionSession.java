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

import com.nukkitx.network.raknet.RakNetSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.wrapper.BedrockWrapperSerializer;
import io.netty.channel.EventLoop;

public abstract class ReversionSession extends BedrockServerSession {

    public ReversionSession(RakNetSession connection, EventLoop eventLoop, BedrockWrapperSerializer serializer) {
        super(connection, eventLoop, serializer);
    }

    /**
     * Send a packet to the Client.
     *
     * @param packet packet to send
     * @return true if successful
     */
    public abstract boolean toClient(BedrockPacket packet);

    /**
     * Send a packet to the Server
     *
     * @param packet packet to send
     * @return true if successful
     */
    public abstract boolean toServer(BedrockPacket packet);

    /**
     * Set the session translator
     *
     * @param translator head of translator chain
     */
    public abstract void setTranslator(Translator translator);

    /**
     * Return the logindata
     *
     * @return the logindata
     */
    public abstract LoginData getLoginData();
}
