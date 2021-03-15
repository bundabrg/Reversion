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

package au.com.grieve.reversion.protocol.education.v390;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.data.command.CommandParam;
import com.nukkitx.protocol.bedrock.data.inventory.InventorySource;
import com.nukkitx.protocol.bedrock.v388.BedrockPacketHelper_v388;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EducationPacketHelper_v390 extends BedrockPacketHelper_v388 {
    public static final EducationPacketHelper_v390 INSTANCE = new EducationPacketHelper_v390();

    @Override
    public boolean isBlockingItem(int id, int lookFor) {
        return id == 513;
    }

    @Override
    protected void registerCommandParams() {
        this.addCommandParam(1, CommandParam.INT);
        this.addCommandParam(2, CommandParam.FLOAT);
        this.addCommandParam(3, CommandParam.VALUE);
        this.addCommandParam(4, CommandParam.WILDCARD_INT);
        this.addCommandParam(5, CommandParam.OPERATOR);
        this.addCommandParam(6, CommandParam.TARGET);
        this.addCommandParam(7, CommandParam.WILDCARD_TARGET);
        this.addCommandParam(14, CommandParam.FILE_PATH);
        this.addCommandParam(29, CommandParam.STRING);
        this.addCommandParam(37, CommandParam.BLOCK_POSITION);
        this.addCommandParam(38, CommandParam.POSITION);
        this.addCommandParam(41, CommandParam.MESSAGE);
        this.addCommandParam(43, CommandParam.TEXT);
        this.addCommandParam(47, CommandParam.JSON);
        this.addCommandParam(54, CommandParam.COMMAND);
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
