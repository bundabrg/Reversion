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

package au.com.grieve.reversion.editions.bedrock;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.api.RegisteredTranslator;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Builder
@Getter
public class BedrockRegisteredTranslator implements RegisteredTranslator {
    private final String fromEdition;
    private final int fromProtocolVersion;
    private final String toEdition;
    private final int toProtocolVersion;
    private final BedrockPacketCodec codec;
    private final Class<? extends Translator> translator;

    // Mappers
    private final BlockMapper blockMapper;
    private final EntityMapper entityMapper;
    private final ItemMapper itemMapper;

    @Singular("registerPacketHandler")
    private final Map<Class<? extends BedrockPacket>, Class<? extends PacketHandler<? extends Translator, ? extends BedrockPacket>>> packetHandlers;


    /**
     * Get a name to identify this translator
     *
     * @return a string name
     */
    public String getName() {
        return String.format("(%s:%d) -> (%s:%d)", fromEdition, fromProtocolVersion, toEdition, toProtocolVersion);
    }
}
