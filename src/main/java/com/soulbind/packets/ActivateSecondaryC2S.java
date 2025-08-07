package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ActivateSecondaryC2S() implements CustomPayload {
    public static final Identifier ACTIVATE_SECONDARY_C2S = Identifier.of(SoulBind.MOD_ID, "activatesecondaryc2s");
    public static final Id<ActivateSecondaryC2S> ID = new Id<>(ACTIVATE_SECONDARY_C2S);
    public static final PacketCodec<RegistryByteBuf, ActivateSecondaryC2S> CODEC =
            PacketCodec.unit(new ActivateSecondaryC2S());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
