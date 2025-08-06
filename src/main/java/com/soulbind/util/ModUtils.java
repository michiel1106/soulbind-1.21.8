package com.soulbind.util;

import com.soulbind.dataattachements.AlreadyJoinedData;
import com.soulbind.dataattachements.SoulmateData;
import com.soulbind.items.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import static com.soulbind.dataattachements.ModDataAttachments.PLAYER_JOINED_ATTACHMENT;
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

    public static void SetJoinedAlready(PlayerEntity player, boolean value) {
        AlreadyJoinedData data = player.getAttachedOrElse(PLAYER_JOINED_ATTACHMENT, AlreadyJoinedData.DEFAULT);
        player.setAttached(PLAYER_JOINED_ATTACHMENT, data.writeBoolean(value));
    }

    public static boolean HasAlreadyJoined(PlayerEntity player) {

        AlreadyJoinedData data = player.getAttachedOrElse(PLAYER_JOINED_ATTACHMENT, AlreadyJoinedData.DEFAULT);
        return data.value();

    }

    public static void GivePlayerItem(PlayerEntity player) {
        ItemStack itemStack = new ItemStack(ModItems.SOUL_TOKEN);

        NbtCompound nbt = new NbtCompound();
        nbt.putString("playername", player.getName().getString());

        NbtComponent component = NbtComponent.of(nbt);

        itemStack.set(DataComponentTypes.CUSTOM_DATA, component);

        player.giveItemStack(itemStack);
        ModUtils.SetJoinedAlready(player, true);
    }




}
