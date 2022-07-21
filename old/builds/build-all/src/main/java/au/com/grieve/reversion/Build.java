/*
 * MIT License
 *
 * Copyright (c) 2022 Reversion Developers
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

import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import lombok.experimental.UtilityClass;

/**
 * Utility Class that lists resources included in this build
 */
@UtilityClass
public class Build {
    public static final RegisteredTranslator[] TRANSLATORS = {
//            // Translator from an older Education to a Newer Bedrock Server
//            Register_v390ee_to_v408be.TRANSLATOR,
//            Register_v391ee_to_v408be.TRANSLATOR,
//            Register_v392ee_to_v408be.TRANSLATOR,
//
//            // Translate from an older Bedrock to a Newer Bedrock Server
//            Register_v407be_to_v408be.TRANSLATOR,
//            Register_v408be_to_v419be.TRANSLATOR,
//            Register_v419be_to_v422be.TRANSLATOR,
//
//            // Translate from a newer Bedrock to an Older Bedrock Server
//            Register_v422be_to_v419be.TRANSLATOR,
//            Register_v423be_to_v422be.TRANSLATOR


    };

    public static final BedrockPacketCodec[] PROTOCOLS = {
//            Bedrock_v407.V407_CODEC,
//            Bedrock_v408.V408_CODEC,
//            Bedrock_v419.V419_CODEC,
//            Bedrock_v422.V422_CODEC,
//            Education_v390.V390_CODEC,
//            Education_v391.V391_CODEC,
//            Education_v392.V392_CODEC,
//
//            // Betas
//            Bedrock_v423.V423_CODEC

    };

    public static final Object[] EDITIONS = {
            BedrockReversionServer.class,
//            EducationReversionServer.class
    };
}
