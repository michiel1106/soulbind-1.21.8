package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SoulmateDenyPacketC2S(int randomID) implements CustomPayload {
    public static final Identifier SOULMATE_DENY_PACKET = Identifier.of(SoulBind.MOD_ID, "soulmatedenypacketc2s");
    public static final Id<SoulmateDenyPacketC2S> ID = new Id<>(SOULMATE_DENY_PACKET);
    public static final PacketCodec<RegistryByteBuf, SoulmateDenyPacketC2S> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, SoulmateDenyPacketC2S::randomID, SoulmateDenyPacketC2S::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
