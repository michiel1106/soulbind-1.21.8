package com.soulbind.events;

import com.soulbind.abilities.Ability;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityData;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityType;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ModEvents {
    private static int ticks = 0;

    // in fabric theres events, they are incredibly useful. you can find all the events here: https://wiki.fabricmc.net/tutorial:event_index

    // imo theyre constructed kind of weirdly but oh well.
    public static void activateEvents() {

        // onuse done | works sort of. Does not fire when placing blocks for example
        // onkill done | works.
        // onhit
        // useprimary
        // usesecondary
        // tick done | works
        // getcustomdamage
        // onDamage done | works.


        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer -> {
            for (PlayerEntity player : MinecraftServer.getPlayerManager().getPlayerList()) {
                AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
                if (data != null) {
                    AbilityType type = data.type();

                    Ability ability = type.createInstance();

                    PlayerEntity soulmate = ModUtils.getSoulmate(player);
                    ability.Tick(player, (ServerWorld) player.getWorld(), soulmate);
                }
            }
        }));

        UseItemCallback.EVENT.register(((player, world, hand) -> {
            AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
            if (data != null) {
                AbilityType type = data.type();

                Ability ability = type.createInstance();

                ability.onUse((ServerWorld) world, player.getMainHandStack(), player);
            }
            return ActionResult.PASS;
        }));

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((serverWorld, player, livingEntity) -> {
            AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
            if (data != null) {
                if (player instanceof PlayerEntity playerEntity) {
                    AbilityType type = data.type();

                    Ability ability = type.createInstance();

                    ability.onKill(serverWorld, playerEntity, livingEntity);
                }
            }
        }));

        ServerLivingEntityEvents.AFTER_DAMAGE.register(((player, source, baseDamageTaken, damageTaken, blocked) ->  {

            AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
            if (data != null) {
                if (player instanceof PlayerEntity playerEntity) {
                    AbilityType type = data.type();
                    Ability ability = type.createInstance();
                    ability.onDamage(playerEntity, source, damageTaken);
                }
            }



        }));






        ServerTickEvents.END_SERVER_TICK.register((serverWorld -> {
            // the serverWorld you see above is something you can use.

            List<ServerPlayerEntity> players = serverWorld.getPlayerManager().getPlayerList();

            // this event is called every server tick. so if we want to say, track how long two people are apart by using a timer that increases we can do that. heres a simple example:

            // "=" assigns stuff, "==" checks for stuff.
            if (ticks == 40) {
            //    System.out.println("it has been 2 seconds.");
            //    System.out.println("resetting timer..");
                ticks = 0;
            }

            // increases ticks by one. ticks-- decreases by one
            ticks++;
            // this example prints the above every 40 ticks. with minecraft running at 20 TPS it prints it every 2 seconds.


            // we can even make our own events. for example when the player receives damage, theyre really nice.
        }));


        ServerPlayerEvents.JOIN.register((player -> {
            if (!ModUtils.HasAlreadyJoined(player)) {


                ModUtils.GivePlayerItem(player);
            }

        }));


    }



}
