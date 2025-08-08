package com.soulbind.dataattachements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record MaceOwner(String string) {

    public static Codec<MaceOwner> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("maceowner").forGetter(MaceOwner::string)
    ).apply(instance, MaceOwner::new));

    public static PacketCodec<ByteBuf, MaceOwner> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public static MaceOwner DEFAULT = new MaceOwner("");

    public MaceOwner writeString(String value) {
        return new MaceOwner(value);
    }

    public MaceOwner removeOwner() {
        return new MaceOwner("");
    }

    public MaceOwner clear() {
        return DEFAULT;
    }
}

