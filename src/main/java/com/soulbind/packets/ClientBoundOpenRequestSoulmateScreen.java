package com.soulbind.packets;

import com.soulbind.SoulBind;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record ClientBoundOpenRequestSoulmateScreen(List<String> stringList) implements CustomPayload {
    public static final Identifier OPEN_SOULMATE_SCREEN_PAYLOAD_ID = Identifier.of(SoulBind.MOD_ID, "openrequestsoulmatescreen");
    public static final CustomPayload.Id<ClientBoundOpenRequestSoulmateScreen> ID = new CustomPayload.Id<>(OPEN_SOULMATE_SCREEN_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, List<String>> STRING_LIST_CODEC =
            new PacketCodec<>() {
                @Override
                public void encode(RegistryByteBuf buf, List<String> value) {
                    buf.writeVarInt(value.size());
                    for (String str : value) {
                        buf.writeString(str);
                    }
                }

                @Override
                public List<String> decode(RegistryByteBuf buf) {
                    int size = buf.readVarInt();
                    List<String> result = new java.util.ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        result.add(buf.readString());
                    }
                    return result;
                }
            };


    public static final PacketCodec<RegistryByteBuf, ClientBoundOpenRequestSoulmateScreen> CODEC =
            PacketCodec.tuple(
                    STRING_LIST_CODEC,
                    ClientBoundOpenRequestSoulmateScreen::stringList,
                    ClientBoundOpenRequestSoulmateScreen::new
            );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
