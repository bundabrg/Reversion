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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .map(i -> new StartGamePacket.ItemEntry(i.getIdentifier(), getTranslator().getRegisteredTranslator().getItemMapper().mapRuntimeIdToUpstream(i.getId())))
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

            Map<Integer, NbtMap> translatedTags = IntStream.range(0, getTranslator().getRegisteredTranslator().getBlockMapper().getUpstreamPalette().size())
                    .boxed()
                    .collect(Collectors.toMap(i -> i, i -> getTranslator().getRegisteredTranslator().getBlockMapper().getUpstreamPalette().get(i).getCompound("block")));

            for (int geyserId = 0; geyserId < geyserTags.size(); geyserId++) {
                NbtMap geyserTag = geyserTags.get(geyserId).getCompound("block");
                boolean found = false;
                for (Map.Entry<Integer, NbtMap> entry : translatedTags.entrySet()) {
                    if (geyserTag.equals(entry.getValue())) {
                        getTranslator().getRegisteredTranslator().getBlockMapper().registerRuntimeIdMapping(geyserId, entry.getKey());
                        found = true;
                        translatedTags.remove(entry.getKey());
                        break;
                    }
                }
                if (!found) {
                    // TODO Log unfound for debugging?
//                    GeyserReversionExtension.getInstance().getLogger().error("Unable to find upstream palette entry: " + geyserTag);
                    getTranslator().getRegisteredTranslator().getBlockMapper().registerRuntimeIdMapping(geyserId, 0); // Set to 0 for now
                }
                if (translatedTags.size() == 0) {
                    break;
                }
            }

            // TODO - Add extra in
//            if (translatedTags.size() > 0) {
//                GeyserReversionExtension.getInstance().getLogger().error("Extra upstream unmatched palette entries: \n" + translatedTags);
//            }
        }

        return super.fromServer(packet);
    }

}
