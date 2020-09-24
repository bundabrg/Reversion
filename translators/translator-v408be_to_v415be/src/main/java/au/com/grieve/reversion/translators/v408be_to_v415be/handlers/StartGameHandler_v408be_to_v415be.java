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

package au.com.grieve.reversion.translators.v408be_to_v415be.handlers;

import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.handlers.StartGameHandler_Bedrock;
import com.nukkitx.nbt.NbtList;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;

import java.util.stream.Collectors;

/*
    The StartGame packet now does not send the block palette for vanilla blocks. This means when translating to a higher
    version we need to provide the block palette plus any extras that are added
 */

public class StartGameHandler_v408be_to_v415be extends StartGameHandler_Bedrock {

    public StartGameHandler_v408be_to_v415be(BedrockTranslator translator) {
        super(translator);
    }

    @Override
    public boolean fromDownstream(StartGamePacket packet) {
        // Update Canonical Block Palette
        packet.setBlockPalette(new NbtList<>(NbtType.COMPOUND, getTranslator().getRegisteredTranslator().getBlockMapper().getUpstreamPalette()));

        // TODO - Add updates

        // Send ItemPalette

        packet.setItemEntries(getTranslator().getRegisteredTranslator().getItemMapper().getUpstreamPalette().stream()
                .map(i -> new StartGamePacket.ItemEntry(i.getString("name"), (short) i.getInt("id")))
                .collect(Collectors.toList())
        );
        return false;
    }

    @Override
    public boolean fromServer(StartGamePacket packet) {
        return false;
    }
}
