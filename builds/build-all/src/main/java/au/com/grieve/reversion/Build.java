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

package au.com.grieve.reversion;

import au.com.grieve.reversion.api.RegisteredTranslator;
import au.com.grieve.reversion.editions.bedrock.BedrockReversionServer;
import au.com.grieve.reversion.editions.education.EducationReversionServer;
import au.com.grieve.reversion.protocol.bedrock.v409.Bedrock_v409;
import au.com.grieve.reversion.protocol.bedrock.v411.Bedrock_v411;
import au.com.grieve.reversion.protocol.bedrock.v412.Bedrock_v412;
import au.com.grieve.reversion.protocol.bedrock.v416.Bedrock_v416;
import au.com.grieve.reversion.protocol.education.v390.Education_v390;
import au.com.grieve.reversion.translators.v390ee_to_v408be.Register_v390ee_to_v408be;
import au.com.grieve.reversion.translators.v408be_to_v415be.Register_v408be_to_v415be;
import au.com.grieve.reversion.translators.v409be_to_v408be.Register_v409be_to_v408be;
import au.com.grieve.reversion.translators.v411be_to_v409be.Register_v411be_to_v409be;
import au.com.grieve.reversion.translators.v412be_to_v411be.Register_v412be_to_v411be;
import au.com.grieve.reversion.translators.v414be_to_v412be.Register_v414be_to_v412be;
import au.com.grieve.reversion.translators.v415be_to_v408be.Register_v415be_to_v408be;
import au.com.grieve.reversion.translators.v416be_to_v408be.Register_v416be_to_v408be;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import com.nukkitx.protocol.bedrock.v408.Bedrock_v408;
import com.nukkitx.protocol.bedrock.v414.Bedrock_v414;
import com.nukkitx.protocol.bedrock.v415.Bedrock_v415;
import lombok.experimental.UtilityClass;

/**
 * Utility Class that lists resources included in this build
 */
@UtilityClass
public class Build {
    public static final RegisteredTranslator[] TRANSLATORS = {
            Register_v409be_to_v408be.TRANSLATOR,
            Register_v411be_to_v409be.TRANSLATOR,
            Register_v390ee_to_v408be.TRANSLATOR,
            Register_v412be_to_v411be.TRANSLATOR,
            Register_v414be_to_v412be.TRANSLATOR,
            Register_v415be_to_v408be.TRANSLATOR,
            Register_v408be_to_v415be.TRANSLATOR,
            Register_v416be_to_v408be.TRANSLATOR
    };

    public static final BedrockPacketCodec[] PROTOCOLS = {
            Bedrock_v408.V408_CODEC,
            Bedrock_v409.V409_CODEC,
            Bedrock_v411.V411_CODEC,
            Bedrock_v412.V412_CODEC,
            Bedrock_v414.V414_CODEC,
            Bedrock_v415.V415_CODEC,
            Bedrock_v416.V416_CODEC,
            Education_v390.V390_CODEC
    };

    public static final Object[] EDITIONS = {
            BedrockReversionServer.class,
            EducationReversionServer.class
    };
}
