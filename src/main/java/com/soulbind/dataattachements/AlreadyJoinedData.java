package com.soulbind.dataattachements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record AlreadyJoinedData(Boolean value) {

    public static Codec<AlreadyJoinedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("joined").forGetter(AlreadyJoinedData::value)
    ).apply(instance, AlreadyJoinedData::new));

    public static PacketCodec<ByteBuf, AlreadyJoinedData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public static AlreadyJoinedData DEFAULT = new AlreadyJoinedData(false);


    public AlreadyJoinedData writeBoolean(Boolean value) {


        return new AlreadyJoinedData(value);
    }


    public AlreadyJoinedData setJoined() {
        return new AlreadyJoinedData(true);
    }

    public AlreadyJoinedData setNotJoined() {
        return new AlreadyJoinedData(false);
    }

    public AlreadyJoinedData clear() {
        return DEFAULT;
    }
}
