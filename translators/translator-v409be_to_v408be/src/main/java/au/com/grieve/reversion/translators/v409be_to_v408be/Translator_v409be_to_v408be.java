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

package au.com.grieve.reversion.translators.v409be_to_v408be;

import au.com.grieve.reversion.MapperManager;
import au.com.grieve.reversion.annotations.ReversionTranslator;
import au.com.grieve.reversion.api.BaseTranslator;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.translators.v409be_to_v408be.handlers.FromDownstreamHandler;
import au.com.grieve.reversion.translators.v409be_to_v408be.handlers.FromUpstreamHandler;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.v409.Bedrock_v409;
import lombok.Getter;

@ReversionTranslator(
        fromEdition = "bedrock",
        fromVersion = 409,
        toEdition = "bedrock",
        toVersion = 408
)
@Getter
public class Translator_v409be_to_v408be extends BaseTranslator {
    public static MapperManager MAPPER;

    @Getter
    private final BedrockPacketCodec codec = Bedrock_v409.V409_CODEC;

    private final BedrockPacketHandler fromUpstreamHandler;
    private final BedrockPacketHandler fromDownstreamHandler;

    public Translator_v409be_to_v408be(ReversionSession reversionSession) {
        super(reversionSession);

        fromUpstreamHandler = new FromUpstreamHandler(this);
        fromDownstreamHandler = new FromDownstreamHandler(this);

//        // Only load when needed
//        if (MAPPER == null) {
//            try {
//                MAPPER = MapperManager.builder()
//                        .itemMapper(new ItemMapper(getClass().getResourceAsStream("/mappings/item_mapper.json")))
//                        .enchantmentMapper(new EnchantmentMapper(getClass().getResourceAsStream("/mappings/enchantment_mapper.json")))
//                        .blockMapper(new BlockMapper(
//                                getClass().getResourceAsStream("/mappings/blocks_mapper.json"),
//                                getClass().getResourceAsStream("/mappings/runtime_block_states.dat")
//                        ))
//                        .entityMapper(new EntityMapper(getClass().getResourceAsStream("/mappings/entity_mapper.json")))
//                        .build();
//            } catch (MapperException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public boolean fromUpstream(BedrockPacket packet) {
        if (packet.handle(fromUpstreamHandler)) {
            return true;
        }
        return super.fromUpstream(packet);
    }

    @Override
    public boolean fromDownstream(BedrockPacket packet) {
        if (packet.handle(fromDownstreamHandler)) {
            return true;
        }
        return super.fromDownstream(packet);
    }
}
