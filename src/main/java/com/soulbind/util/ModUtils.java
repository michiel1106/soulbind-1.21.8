package com.soulbind.util;

import com.soulbind.SoulBind;
import com.soulbind.persistentdata.StateSaverAndLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;

public class ModUtils {
    // here im gonna make some methods to make code everywhere else smaller.


    public static void WriteData(String string, PlayerEntity player) {
        StateSaverAndLoader.getPlayerState(player).soulmate = string;
    }
    public static String ReadData(PlayerEntity player) {
        return StateSaverAndLoader.getPlayerState(player).soulmate;
    }




}
