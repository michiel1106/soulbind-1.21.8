package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ClientBoundOpenRequestSoulmateScreen() implements CustomPayload {
    public static final Identifier OPEN_SOULMATE_SCREEN_PAYLOAD_ID = Identifier.of(SoulBind.MOD_ID, "openrequestsoulmatescreen");
    public static final CustomPayload.Id<ClientBoundOpenRequestSoulmateScreen> ID = new CustomPayload.Id<>(OPEN_SOULMATE_SCREEN_PAYLOAD_ID);


    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
