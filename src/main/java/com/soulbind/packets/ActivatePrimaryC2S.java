package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ActivatePrimaryC2S() implements CustomPayload {
    public static final Identifier ACTIVATE_PRIMARY_C2S = Identifier.of(SoulBind.MOD_ID, "c2sactivateprimary");
    public static final CustomPayload.Id<ActivatePrimaryC2S> ID = new CustomPayload.Id<>(ACTIVATE_PRIMARY_C2S);
    public static final PacketCodec<RegistryByteBuf, ActivatePrimaryC2S> CODEC =
            PacketCodec.unit(new ActivatePrimaryC2S());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
