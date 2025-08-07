package com.soulbind.events;

import com.soulbind.abilities.Ability;
import com.soulbind.abilities.importantforregistering.AbilityData;
import com.soulbind.abilities.importantforregistering.AbilityType;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ModEvents {
    private static final ThreadLocal<Set<UUID>> syncingDamage = ThreadLocal.withInitial(HashSet::new);
    private static int ticks = 0;

    public static void activateEvents() {



        // instead of doing the arrow thingie and brackets you can also make a method. which can make things look cleaner.
        // actually, that would be good to do.

        // TODO make every lambda (thats what you call the arrow and bracket thingie) just a direct method reference like mainSoulmateTick.
        ServerTickEvents.START_SERVER_TICK.register(ModEvents::mainSoulmateTick);


        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer -> {
            for (PlayerEntity player : MinecraftServer.getPlayerManager().getPlayerList()) {
                AbilityData data = player.getAttachedOrElse(ModDataAttachments.PLAYER_ABILITY, new AbilityData(AbilityType.EMPTY_ABILITY));
                if (data != null) {
                    AbilityType type = data.type();

                    Ability ability = type.createInstance();

                    PlayerEntity soulmate = ModUtils.getSoulmate(player);


                    ability.Tick(player, MinecraftServer.getPlayerManager().getPlayer(player.getUuid()).getWorld(), soulmate);
                }
            }
        }));

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
            Ability ability = ModUtils.getAbility(newPlayer);

            if (ability != null) {
                ability.onRespawn(newPlayer);
            }
        }));

        UseItemCallback.EVENT.register(((player, world, hand) -> {

            Ability ability = ModUtils.getAbility(player);
            if (ability != null) {
                ability.onUse((ServerWorld) world, player.getMainHandStack(), player);
            }
            return ActionResult.PASS;
        }));



        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((serverWorld, player, livingEntity) -> {



            if (player instanceof PlayerEntity playerEntity) {
                Ability ability = ModUtils.getAbility(playerEntity);
                if (ability != null) {
                    ability.onKill(serverWorld, playerEntity, livingEntity);
                }

            }
        }));

        ServerLivingEntityEvents.AFTER_DAMAGE.register((player, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(player instanceof PlayerEntity playerEntity)) return;

            // Prevent recursive syncing
            Set<UUID> syncSet = syncingDamage.get();
            if (syncSet.contains(playerEntity.getUuid())) return;

            Ability ability = ModUtils.getAbility(playerEntity);
            if (ability != null) {
                ability.onDamage(playerEntity, source, damageTaken);
            }

            PlayerEntity soulmate = ModUtils.getSoulmate(playerEntity);
            if (soulmate != null && !syncSet.contains(soulmate.getUuid())) {
                try {
                    // Mark both players as syncing
                    syncSet.add(playerEntity.getUuid());
                    syncSet.add(soulmate.getUuid());

                    // Forward the damage


                    soulmate.damage((ServerWorld) player.getWorld(), source, damageTaken);

                } finally {
                    // Always clear to prevent memory leaks
                    syncSet.remove(playerEntity.getUuid());
                    syncSet.remove(soulmate.getUuid());
                }
            }
        });





        ServerPlayerEvents.JOIN.register((player -> {
            if (!ModUtils.HasAlreadyJoined(player)) {


                ModUtils.giveAbilityToPlayer(player, AbilityType.EMPTY_ABILITY);
                ModUtils.GivePlayerItem(player);
            }

        }));


    }

    private static void mainSoulmateTick(MinecraftServer server) {
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();

        for (ServerPlayerEntity player : playerList) {


          //  if (soulmate != null) { // getSoulmate uses the name save to the player but if the player with the name doesnt exist or isnt online it returns null. which isnt good.

           // }
        }


    }




}
