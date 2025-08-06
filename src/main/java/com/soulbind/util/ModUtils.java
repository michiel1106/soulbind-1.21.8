package com.soulbind.util;

import com.soulbind.dataattachements.SoulmateData;
import net.minecraft.entity.player.PlayerEntity;

import static com.soulbind.dataattachements.ModDataAttachments.PLAYER_SOULMATE_ATTACHMENT;

@SuppressWarnings("UnstableApiUsage")
public class ModUtils {
    // here im gonna make some methods to make code everywhere else easier.


    public static void writePlayerName(PlayerEntity player, String string) {
        SoulmateData data = player.getAttachedOrElse(PLAYER_SOULMATE_ATTACHMENT, SoulmateData.DEFAULT);

        player.setAttached(PLAYER_SOULMATE_ATTACHMENT, data.writeString(string));
    }

    public static String readPlayerName(PlayerEntity player) {
        SoulmateData data = player.getAttachedOrElse(PLAYER_SOULMATE_ATTACHMENT, SoulmateData.DEFAULT);

        return data.string();
    }




}
