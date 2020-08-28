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

package au.com.grieve.reversion.translators.v390ee_to_v408be;

import au.com.grieve.reversion.api.RegisteredTranslator;
import au.com.grieve.reversion.api.ReversionSession;
import au.com.grieve.reversion.api.Translator;
import au.com.grieve.reversion.editions.bedrock.BedrockTranslator;
import au.com.grieve.reversion.editions.bedrock.mappers.BlockMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EnchantmentMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.EntityMapper;
import au.com.grieve.reversion.editions.bedrock.mappers.ItemMapper;
import au.com.grieve.reversion.exceptions.MapperException;
import lombok.Getter;

@Getter
public class Translator_v390be_to_v408be extends BedrockTranslator {
    private static final ItemMapper ITEM_MAPPER;
    private static final BlockMapper BLOCK_MAPPER;
    private static final EntityMapper ENTITY_MAPPER;
    private static final EnchantmentMapper ENCHANTMENT_MAPPER;

    static {
        try {
            ENCHANTMENT_MAPPER = new EnchantmentMapper(Translator.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/enchantment_mapper.json"));
            ITEM_MAPPER = new ItemMapper(Translator.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/item_mapper.json"));
            BLOCK_MAPPER = new BlockMapper(
                    Translator.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/block_mapper.json"),
                    Translator.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/runtime_block_states.dat")
            );
            ENTITY_MAPPER = new EntityMapper(Translator.class.getResourceAsStream("/translators/v390ee_to_v408be/mappings/entity_mapper.json"));
        } catch (MapperException e) {
            throw new RuntimeException(e);
        }
    }

    public Translator_v390be_to_v408be(RegisteredTranslator registeredTranslator, ReversionSession reversionSession) {
        super(registeredTranslator, reversionSession);

        itemMapper = ITEM_MAPPER;
        blockMapper = BLOCK_MAPPER;
        entityMapper = ENTITY_MAPPER;
        enchantmentMapper = ENCHANTMENT_MAPPER;
    }

}
