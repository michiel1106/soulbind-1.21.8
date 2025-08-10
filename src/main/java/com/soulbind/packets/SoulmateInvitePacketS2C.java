package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SoulmateInvitePacketS2C(String player, String ability, int requestid) implements CustomPayload {
    public static final Identifier SOULMATE_INVITE_SCREED = Identifier.of(SoulBind.MOD_ID, "s2csoulmatinvitepacket");
    public static final Id<SoulmateInvitePacketS2C> ID = new Id<>(SOULMATE_INVITE_SCREED);
    public static final PacketCodec<RegistryByteBuf, SoulmateInvitePacketS2C> CODEC = PacketCodec.tuple(PacketCodecs.STRING, SoulmateInvitePacketS2C::player, PacketCodecs.STRING, SoulmateInvitePacketS2C::ability, PacketCodecs.VAR_INT, SoulmateInvitePacketS2C::requestid, SoulmateInvitePacketS2C::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
