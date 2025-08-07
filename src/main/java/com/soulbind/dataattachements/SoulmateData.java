package com.soulbind.dataattachements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SoulmateData(String string) {

    public static Codec<SoulmateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("string").forGetter(SoulmateData::string)
    ).apply(instance, SoulmateData::new));

    public static PacketCodec<ByteBuf, SoulmateData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public static SoulmateData DEFAULT = new SoulmateData("");

    public SoulmateData writeString(String value) {
        String string1;
        string1 = value;

        return new SoulmateData(string1);
    }

    public SoulmateData removeSoulmate() {
        return new SoulmateData("");
    }

    public SoulmateData clear() {
        return DEFAULT;
    }
}
