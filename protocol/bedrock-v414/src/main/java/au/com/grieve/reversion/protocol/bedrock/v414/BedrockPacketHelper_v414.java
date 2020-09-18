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

package au.com.grieve.reversion.protocol.bedrock.v414;

import au.com.grieve.reversion.protocol.bedrock.v411.BedrockPacketHelper_v411;
import com.nukkitx.protocol.bedrock.data.ExperimentData;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BedrockPacketHelper_v414 extends BedrockPacketHelper_v411 {
    public static final BedrockPacketHelper_v414 INSTANCE = new BedrockPacketHelper_v414();

    @Override
    public void readExperiments(ByteBuf buffer, List<ExperimentData> experiments) {
        int count = buffer.readIntLE(); // Actually unsigned
        for (int i = 0; i < count; i++) {
            experiments.add(new ExperimentData(
                    this.readString(buffer),
                    buffer.readBoolean() // Hardcoded to true in 414
            ));
        }
    }

    @Override
    public void writeExperiments(ByteBuf buffer, List<ExperimentData> experiments) {
        buffer.writeIntLE(experiments.size());

        for (ExperimentData experiment : experiments) {
            this.writeString(buffer, experiment.getName());
            buffer.writeBoolean(experiment.isEnabled());
        }
    }

//    @Override
//    public SerializedSkin readSkin(ByteBuf buffer) {
//        String skinId = this.readString(buffer);
//        String skinResourcePatch = this.readString(buffer);
//        ImageData skinData = this.readImage(buffer);
//
//        int animationCount = buffer.readIntLE();
//        List<AnimationData> animations = new ObjectArrayList<>(animationCount);
//        for (int i = 0; i < animationCount; i++) {
//            ImageData image = this.readImage(buffer);
//            int type = buffer.readIntLE();
//            float frames = buffer.readFloatLE();
//            animations.add(new AnimationData(image, type, frames));
//        }
//
//        // TODO - 0x01 0x00 0x00 0x00
////
////        byte[] t = new byte[4];
////        ByteBuf b2 = Unpooled.buffer(64);
////        buffer.readBytes(b2, 4);
////        System.err.println(ByteBufUtil.prettyHexDump(b2));
//        buffer.readIntLE();
//
//        ImageData capeData = this.readImage(buffer);
//        String geometryData = this.readString(buffer);
//        String animationData = this.readString(buffer);
//        boolean premium = buffer.readBoolean();
//        boolean persona = buffer.readBoolean();
//        boolean capeOnClassic = buffer.readBoolean();
//        String capeId = this.readString(buffer);
//        String fullSkinId = this.readString(buffer);
//        String armSize = this.readString(buffer);
//        String skinColor = this.readString(buffer);
//
//        List<PersonaPieceData> personaPieces = new ObjectArrayList<>();
//        int piecesLength = buffer.readIntLE();
//        for (int i = 0; i < piecesLength; i++) {
//            String pieceId = this.readString(buffer);
//            String pieceType = this.readString(buffer);
//            String packId = this.readString(buffer);
//            boolean isDefault = buffer.readBoolean();
//            String productId = this.readString(buffer);
//            personaPieces.add(new PersonaPieceData(pieceId, pieceType, packId, isDefault, productId));
//        }
//
//        List<PersonaPieceTintData> tintColors = new ObjectArrayList<>();
//        int tintsLength = buffer.readIntLE();
//        for (int i = 0; i < tintsLength; i++) {
//            String pieceType = this.readString(buffer);
//            List<String> colors = new ObjectArrayList<>();
//            int colorsLength = buffer.readIntLE();
//            for (int i2 = 0; i2 < colorsLength; i2++) {
//                colors.add(this.readString(buffer));
//            }
//            tintColors.add(new PersonaPieceTintData(pieceType, colors));
//        }
//
//        return SerializedSkin.of(skinId, skinResourcePatch, skinData, animations, capeData, geometryData, animationData,
//                premium, persona, capeOnClassic, capeId, fullSkinId, armSize, skinColor, personaPieces, tintColors);
//    }
//
//    @Override
//    public void writeSkin(ByteBuf buffer, SerializedSkin skin) {
//        requireNonNull(skin, "Skin is null");
//
//        this.writeString(buffer, skin.getSkinId());
//        this.writeString(buffer, skin.getSkinResourcePatch());
//        this.writeImage(buffer, skin.getSkinData());
//
//        List<AnimationData> animations = skin.getAnimations();
//        buffer.writeIntLE(animations.size());
//        for (AnimationData animation : animations) {
//            this.writeImage(buffer, animation.getImage());
//            buffer.writeIntLE(animation.getType());
//            buffer.writeFloatLE(animation.getFrames());
//        }
//
//        // TODO - 0x01 0x00 0x00 0x00
//        buffer.writeIntLE(1);
//
//        this.writeImage(buffer, skin.getCapeData());
//        this.writeString(buffer, skin.getGeometryData());
//        this.writeString(buffer, skin.getAnimationData());
//        buffer.writeBoolean(skin.isPremium());
//        buffer.writeBoolean(skin.isPersona());
//        buffer.writeBoolean(skin.isCapeOnClassic());
//        this.writeString(buffer, skin.getCapeId());
//        this.writeString(buffer, skin.getFullSkinId());
//        this.writeString(buffer, skin.getArmSize());
//        this.writeString(buffer, skin.getSkinColor());
//        List<PersonaPieceData> pieces = skin.getPersonaPieces();
//        buffer.writeIntLE(pieces.size());
//        for (PersonaPieceData piece : pieces) {
//            this.writeString(buffer, piece.getId());
//            this.writeString(buffer, piece.getType());
//            this.writeString(buffer, piece.getPackId());
//            buffer.writeBoolean(piece.isDefault());
//            this.writeString(buffer, piece.getProductId());
//        }
//
//        List<PersonaPieceTintData> tints = skin.getTintColors();
//        buffer.writeIntLE(tints.size());
//        for (PersonaPieceTintData tint : tints) {
//            this.writeString(buffer, tint.getType());
//            List<String> colors = tint.getColors();
//            buffer.writeIntLE(colors.size());
//            for (String color : colors) {
//                this.writeString(buffer, color);
//            }
//        }
//    }
}
