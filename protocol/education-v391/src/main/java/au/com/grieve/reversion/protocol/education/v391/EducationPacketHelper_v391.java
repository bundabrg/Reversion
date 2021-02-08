/*
 * MIT License
 *
 * Copyright (c) 2021 Reversion Developers
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

package au.com.grieve.reversion.protocol.education.v391;

import au.com.grieve.reversion.protocol.education.v390.EducationPacketHelper_v390;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.nukkitx.protocol.bedrock.data.command.CommandParamType.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationPacketHelper_v391 extends EducationPacketHelper_v390 {
    public static final EducationPacketHelper_v391 INSTANCE = new EducationPacketHelper_v391();


    @Override
    protected void registerCommandParams() {
        this.addCommandParam(1, INT);
        this.addCommandParam(2, FLOAT);
        this.addCommandParam(3, VALUE);
        this.addCommandParam(4, WILDCARD_INT);
        this.addCommandParam(5, OPERATOR);
        this.addCommandParam(6, TARGET);
        this.addCommandParam(7, WILDCARD_TARGET); // Unsure
        this.addCommandParam(14, FILE_PATH);
        this.addCommandParam(30, STRING);
        this.addCommandParam(38, BLOCK_POSITION);
        this.addCommandParam(39, POSITION);
        this.addCommandParam(42, MESSAGE);
        this.addCommandParam(44, TEXT);
        this.addCommandParam(48, JSON);
        this.addCommandParam(59, COMMAND);
    }

    @Override
    protected void registerLevelEvents() {
        super.registerLevelEvents();
        this.addLevelEvent(1050, LevelEventType.SOUND_CAMERA);
        this.addLevelEvent(3600, LevelEventType.BLOCK_START_BREAK);
        this.addLevelEvent(3601, LevelEventType.BLOCK_STOP_BREAK);
        this.addLevelEvent(3602, LevelEventType.BLOCK_UPDATE_BREAK);
        this.addLevelEvent(4000, LevelEventType.SET_DATA);
        this.addLevelEvent(9800, LevelEventType.ALL_PLAYERS_SLEEPING);
        int legacy = 16384;
        this.addLevelEvent(68 + legacy, LevelEventType.PARTICLE_BLUE_FLAME);
        this.addLevelEvent(69 + legacy, LevelEventType.PARTICLE_SOUL);
        this.addLevelEvent(70 + legacy, LevelEventType.PARTICLE_OBSIDIAN_TEAR);

        this.addLevelEvent(2023, LevelEventType.PARTICLE_TELEPORT_TRAIL);
        this.addLevelEvent(28 + legacy, LevelEventType.PARTICLE_DRIP_HONEY);
        this.addLevelEvent(29 + legacy, LevelEventType.PARTICLE_FALLING_DUST);
        this.addLevelEvent(30 + legacy, LevelEventType.PARTICLE_MOB_SPELL);
        this.addLevelEvent(31 + legacy, LevelEventType.PARTICLE_MOB_SPELL_AMBIENT);
        this.addLevelEvent(32 + legacy, LevelEventType.PARTICLE_MOB_SPELL_INSTANTANEOUS);
        this.addLevelEvent(33 + legacy, LevelEventType.PARTICLE_INK);
        this.addLevelEvent(34 + legacy, LevelEventType.PARTICLE_SLIME);
        this.addLevelEvent(35 + legacy, LevelEventType.PARTICLE_RAIN_SPLASH);
        this.addLevelEvent(36 + legacy, LevelEventType.PARTICLE_VILLAGER_ANGRY);
        this.addLevelEvent(37 + legacy, LevelEventType.PARTICLE_VILLAGER_HAPPY);
        this.addLevelEvent(38 + legacy, LevelEventType.PARTICLE_ENCHANTMENT_TABLE);
        this.addLevelEvent(39 + legacy, LevelEventType.PARTICLE_TRACKING_EMITTER);
        this.addLevelEvent(40 + legacy, LevelEventType.PARTICLE_NOTE);
        this.addLevelEvent(41 + legacy, LevelEventType.PARTICLE_WITCH_SPELL);
        this.addLevelEvent(42 + legacy, LevelEventType.PARTICLE_CARROT);
        this.addLevelEvent(43 + legacy, LevelEventType.PARTICLE_MOB_APPEARANCE);
        this.addLevelEvent(44 + legacy, LevelEventType.PARTICLE_END_ROD);
        this.addLevelEvent(45 + legacy, LevelEventType.PARTICLE_DRAGONS_BREATH);
        this.addLevelEvent(46 + legacy, LevelEventType.PARTICLE_SPIT);
        this.addLevelEvent(47 + legacy, LevelEventType.PARTICLE_TOTEM);
        this.addLevelEvent(48 + legacy, LevelEventType.PARTICLE_FOOD);
        this.addLevelEvent(49 + legacy, LevelEventType.PARTICLE_FIREWORKS_STARTER);
        this.addLevelEvent(50 + legacy, LevelEventType.PARTICLE_FIREWORKS_SPARK);
        this.addLevelEvent(51 + legacy, LevelEventType.PARTICLE_FIREWORKS_OVERLAY);
        this.addLevelEvent(52 + legacy, LevelEventType.PARTICLE_BALLOON_GAS);
        this.addLevelEvent(53 + legacy, LevelEventType.PARTICLE_COLORED_FLAME);
        this.addLevelEvent(54 + legacy, LevelEventType.PARTICLE_SPARKLER);
        this.addLevelEvent(55 + legacy, LevelEventType.PARTICLE_CONDUIT);
        this.addLevelEvent(56 + legacy, LevelEventType.PARTICLE_BUBBLE_COLUMN_UP);
        this.addLevelEvent(57 + legacy, LevelEventType.PARTICLE_BUBBLE_COLUMN_DOWN);
        this.addLevelEvent(58 + legacy, LevelEventType.PARTICLE_SNEEZE);
        this.addLevelEvent(59 + legacy, LevelEventType.PARTICLE_SHULKER_BULLET);
        this.addLevelEvent(60 + legacy, LevelEventType.PARTICLE_BLEACH);
        this.addLevelEvent(61 + legacy, LevelEventType.PARTICLE_DRAGON_DESTROY_BLOCK);
        this.addLevelEvent(62 + legacy, LevelEventType.PARTICLE_MYCELIUM_DUST);
        this.addLevelEvent(63 + legacy, LevelEventType.PARTICLE_FALLING_RED_DUST);
        this.addLevelEvent(64 + legacy, LevelEventType.PARTICLE_CAMPFIRE_SMOKE);
        this.addLevelEvent(65 + legacy, LevelEventType.PARTICLE_TALL_CAMPFIRE_SMOKE);
        this.addLevelEvent(66 + legacy, LevelEventType.PARTICLE_RISING_DRAGONS_BREATH);
        this.addLevelEvent(67 + legacy, LevelEventType.PARTICLE_DRAGONS_BREATH);
    }

}
