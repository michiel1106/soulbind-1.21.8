package com.soulbind.persistentdata;

import com.mojang.serialization.Codec;
import com.soulbind.SoulBind;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {
    // also followed tutorial for this https://wiki.fabricmc.net/tutorial:persistent_states



    public String soulmate = "";

    public HashMap<UUID, PlayerData> players = new HashMap<>();





    private StateSaverAndLoader() {
    }




    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        return playerState;
    }


    private StateSaverAndLoader(String totalDirtBlocksBroken) {
        this.soulmate = totalDirtBlocksBroken;
    }

    private String getTotalDirtBlocksBroken() {
        return soulmate;
    }

    private static final Codec<StateSaverAndLoader> CODEC = Codec.STRING.fieldOf("soulmate").codec().xmap(
            StateSaverAndLoader::new, // create a new 'StateSaverAndLoader' from the stored number
            StateSaverAndLoader::getTotalDirtBlocksBroken // return the number from the 'StateSaverAndLoader' to be saved
    );

    private static final PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
            (String) SoulBind.MOD_ID,
            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one and refresh variables
            CODEC, // If there is a 'StateSaverAndLoader' NBT, parse it with 'CODEC'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to the codec in our type, using the codec to decode the nbt into our state
        StateSaverAndLoader state = serverWorld.getPersistentStateManager().getOrCreate(type);

        // If state is not marked dirty, nothing will be saved when Minecraft closes.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

}
