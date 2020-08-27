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
