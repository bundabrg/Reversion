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

package au.com.grieve.reversion.protocol.bedrock.v411;

import au.com.grieve.reversion.protocol.bedrock.v409.BedrockPacketHelper_v409;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.nukkitx.protocol.bedrock.data.command.CommandParamType.*;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BedrockPacketHelper_v411 extends BedrockPacketHelper_v409 {
    public static final BedrockPacketHelper_v411 INSTANCE = new BedrockPacketHelper_v411();

    @Override
    protected void registerCommandParams() {
        this.addCommandParam(1, INT);
        this.addCommandParam(2, FLOAT);
        this.addCommandParam(3, VALUE);
        this.addCommandParam(4, WILDCARD_INT);
        this.addCommandParam(5, OPERATOR);
        this.addCommandParam(6, TARGET);

        this.addCommandParam(8, WILDCARD_TARGET);
        this.addCommandParam(15, FILE_PATH);
        this.addCommandParam(30, STRING);
        this.addCommandParam(38, BLOCK_POSITION);
        this.addCommandParam(39, POSITION);
        this.addCommandParam(42, MESSAGE);
        this.addCommandParam(44, TEXT);
        this.addCommandParam(48, JSON);
        this.addCommandParam(55, COMMAND);
    }

}
