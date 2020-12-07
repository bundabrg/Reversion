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

package au.com.grieve.reversion.translators.v407be_to_v408be.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingData;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingDataType;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;

import java.util.List;
import java.util.stream.Collectors;

public class CraftingDataHandler_v407be_to_v408be extends PacketHandler<BedrockTranslator, CraftingDataPacket> {

    public CraftingDataHandler_v407be_to_v408be(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(CraftingDataPacket packet) {
        // Not sure why but MULTI recipes seem to cause issues with v407 so we will just strip them out
        List<CraftingData> translatedList = packet.getCraftingData().stream()
                .filter(p -> p.getType() != CraftingDataType.MULTI)
                .collect(Collectors.toList());

        packet.getCraftingData().clear();
        packet.getCraftingData().addAll(translatedList);
        return super.fromDownstream(packet);
    }
}
