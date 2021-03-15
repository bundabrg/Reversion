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

package au.com.grieve.reversion.protocol.education.v363;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource;
import com.nukkitx.protocol.bedrock.v361.BedrockPacketHelper_v361;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationPacketHelper_v363 extends BedrockPacketHelper_v361 {
    public static final BedrockPacketHelper INSTANCE = new EducationPacketHelper_v363();

    @Override
    public boolean isBlockingItem(int id, int lookFor) {
        return id == 513;
    }

    @Override
    protected void registerEntityData() {
        super.registerEntityData();

        this.addEntityData(107, EntityData.AMBIENT_SOUND_INTERVAL);
        this.addEntityData(108, EntityData.AMBIENT_SOUND_INTERVAL_RANGE);
        this.addEntityData(109, EntityData.AMBIENT_SOUND_EVENT_NAME);
    }

    @Override
    public InventorySource readSource(ByteBuf buffer) {
        InventorySource.Type type = InventorySource.Type.byId(VarInts.readUnsignedInt(buffer));

        switch (type) {
            case CONTAINER:
                int containerId = VarInts.readInt(buffer);
                return InventorySource.fromContainerWindowId(containerId);
            case GLOBAL:
                return InventorySource.fromGlobalInventory();
            case WORLD_INTERACTION:
                InventorySource.Flag flag = InventorySource.Flag.values()[VarInts.readUnsignedInt(buffer)];
                return InventorySource.fromWorldInteraction(flag);
            case CREATIVE:
                return InventorySource.fromCreativeInventory();
            case UNTRACKED_INTERACTION_UI:
                return InventorySource.fromUntrackedInteractionUI(VarInts.readInt(buffer));
            case NON_IMPLEMENTED_TODO:
                containerId = VarInts.readInt(buffer);
                return InventorySource.fromNonImplementedTodo(containerId);
            default:
                return InventorySource.fromInvalid();
        }
    }

}
