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

package au.com.grieve.reversion.editions.bedrock.handlers;

import au.com.grieve.reversion.api.PacketHandler;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StartGameHandler_Bedrock extends PacketHandler<BedrockTranslator, StartGamePacket> {
    protected boolean initialized = false;

    public StartGameHandler_Bedrock(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(StartGamePacket packet) {
        // Reorder the BlockPalette
        NbtList<NbtMap> originalPalette = packet.getBlockPalette();
        List<NbtMap> translatedPalette = new ArrayList<>();
        for (int id = 0; id < originalPalette.size(); id++) {
            translatedPalette.add(getTranslator().getRegisteredTranslator().getBlockMapper().getBlockFromUpstreamPalette(originalPalette, id));
        }
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, translatedPalette));

        // Remap ItemPalette
        packet.setItemEntries(packet.getItemEntries().stream()
                .map(i -> new StartGamePacket.ItemEntry(i.getIdentifier(), getTranslator().getRegisteredTranslator().getItemMapper().mapItemIdToUpstream(i.getId())))
                .collect(Collectors.toList())
        );
        return false;
    }

    @Override
    public boolean fromServer(StartGamePacket packet) {
        // Generate runtime Id from palette as we can't assume the server is sending them in canonical order
        if (!initialized) {
            initialized = true;

            NbtList<NbtMap> geyserTags = packet.getBlockPalette();

            getTranslator().getRegisteredTranslator().getBlockMapper().initRuntimeIdMapFromPalette(geyserTags);
        }

        return super.fromServer(packet);
    }

}
