package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SoulmateAcceptPacketC2S(String player, int randomID) implements CustomPayload {
    public static final Identifier SOULMATE_ACCEPT_PACKET = Identifier.of(SoulBind.MOD_ID, "soulmateacceptpacketc2s");
    public static final Id<SoulmateAcceptPacketC2S> ID = new Id<>(SOULMATE_ACCEPT_PACKET);
    public static final PacketCodec<RegistryByteBuf, SoulmateAcceptPacketC2S> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SoulmateAcceptPacketC2S::player, PacketCodecs.VAR_INT, SoulmateAcceptPacketC2S::randomID, SoulmateAcceptPacketC2S::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
