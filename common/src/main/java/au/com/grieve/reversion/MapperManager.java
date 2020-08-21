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

package au.com.grieve.reversion;

import au.com.grieve.reversion.exceptions.MapperException;
import au.com.grieve.reversion.mappers.BlockMapper;
import au.com.grieve.reversion.mappers.EnchantmentMapper;
import au.com.grieve.reversion.mappers.ItemMapper;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.Builder;
import lombok.Getter;

/**
 * Provides a central point to register mappers
 */
@Getter
@Builder
public class MapperManager {
    protected final ItemMapper itemMapper;
    protected final EnchantmentMapper enchantmentMapper;
    protected final BlockMapper blockMapper;

    /**
     * Map ItemData from downstream to upstream
     *
     * @param original the original ItemData
     * @return the mapped ItemData
     */
    public ItemData itemToUpstream(ItemData original) {
        // Translate Item
        ItemData translated = itemMapper.toUpstream(original);

        // Translate enchantments on item
        translated = enchantmentMapper.toUpstream(translated);

        return translated;
    }

    /**
     * Map Block NBT from downstream to upstream
     *
     * @param original the original nbt
     * @return the mapped nbt
     */
    public NbtMap blockToUpstream(NbtMap original) throws MapperException {
        // Translate NBT

        return blockMapper.toUpstream(original);
    }
}
