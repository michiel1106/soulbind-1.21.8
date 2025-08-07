package com.soulbind.util;

import com.soulbind.abilities.Ability;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityData;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityType;
import com.soulbind.dataattachements.AlreadyJoinedData;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.dataattachements.SoulmateData;
import com.soulbind.items.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import static com.soulbind.dataattachements.ModDataAttachments.PLAYER_JOINED_ATTACHMENT;
import static com.soulbind.dataattachements.ModDataAttachments.PLAYER_SOULMATE_ATTACHMENT;

@SuppressWarnings("UnstableApiUsage")
public class ModUtils {

    public static void writePlayerName(PlayerEntity player, String string) {
        SoulmateData data = player.getAttachedOrElse(PLAYER_SOULMATE_ATTACHMENT, SoulmateData.DEFAULT);

        player.setAttached(PLAYER_SOULMATE_ATTACHMENT, data.writeString(string));
    }

    public static void giveAbilityToPlayer(PlayerEntity player, AbilityType type) {
        AbilityData abilityData = new AbilityData(type);

        player.setAttached(ModDataAttachments.PLAYER_ABILITY, abilityData);
    }

    public static void removeAbility(PlayerEntity player) {
        AbilityData abilityData = new AbilityData(AbilityType.EMPTY_ABILITY);

        player.setAttached(ModDataAttachments.PLAYER_ABILITY, abilityData);
    }

    public static String readPlayerName(PlayerEntity player) {
        SoulmateData data = player.getAttachedOrElse(PLAYER_SOULMATE_ATTACHMENT, SoulmateData.DEFAULT);

        return data.string();
    }

    public static PlayerEntity getSoulmate(PlayerEntity player) {
        PlayerManager playerManager = player.getWorld().getServer().getPlayerManager();
        ServerPlayerEntity entity = playerManager.getPlayer(readPlayerName(player));

        return entity;
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

    public static Ability getAbility(PlayerEntity player) {
        AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
        if (data != null) {
            AbilityType type = data.type();

            return type.createInstance();
        }
        return null;
    }




}
