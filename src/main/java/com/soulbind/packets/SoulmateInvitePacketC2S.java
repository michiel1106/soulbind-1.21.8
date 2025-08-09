package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record SoulmateInvitePacketC2S(String player, String ability) implements CustomPayload {
    public static final Identifier SOULMATE_INVITE_SCREED = Identifier.of(SoulBind.MOD_ID, "c2ssoulmatinvitepacket");
    public static final Id<SoulmateInvitePacketC2S> ID = new Id<>(SOULMATE_INVITE_SCREED);
    public static final PacketCodec<RegistryByteBuf, SoulmateInvitePacketC2S> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SoulmateInvitePacketC2S::player, PacketCodecs.STRING, SoulmateInvitePacketC2S::ability, SoulmateInvitePacketC2S::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
