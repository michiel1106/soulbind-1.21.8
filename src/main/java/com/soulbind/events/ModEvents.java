package com.soulbind.events;

import com.soulbind.SoulBind;
import com.soulbind.abilities.Ability;
import com.soulbind.abilities.importantforregistering.AbilityData;
import com.soulbind.abilities.importantforregistering.AbilityType;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.items.ModItems;
import com.soulbind.macehandler.MaceHandler;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

            for (ServerWorld world : MinecraftServer.getWorlds()) {
                List<? extends ItemEntity> worldTokenItems = world.getEntitiesByType(EntityType.ITEM, SoulBind.IS_TOKEN);

                worldTokenItems.forEach((Entity::discard));

            }


            for (PlayerEntity player : MinecraftServer.getPlayerManager().getPlayerList()) {

                for (ItemStack itemStack : player.getInventory()) {

                    if (itemStack.isOf(ModItems.SOUL_TOKEN)) {
                        NbtComponent customData = itemStack.get(DataComponentTypes.CUSTOM_DATA);
                        if (customData != null) {
                            String myValue = customData.copyNbt().getString("playername").orElseGet(() -> "d");

                            if (!player.getName().getString().equals(myValue)) {
                                itemStack.decrement(1);
                            }
                        }
                    }


                    if (!hasToken(player)) {
                        ModUtils.GivePlayerItem(player);
                    }
                }



                AbilityData data = player.getAttachedOrElse(ModDataAttachments.PLAYER_ABILITY, new AbilityData(AbilityType.EMPTY_ABILITY));
                if (data != null) {
                    AbilityType type = data.type();



                    Ability ability = ModUtils.getAbility(player);


                    PlayerEntity soulmate = ModUtils.getSoulmate(player);


                    ability.Tick(player, MinecraftServer.getPlayerManager().getPlayer(player.getUuid()).getWorld(), soulmate);
                }
            }
        }));





        UseItemCallback.EVENT.register(((player, world, hand) -> {

            Ability ability = ModUtils.getAbility(player);
            if (ability != null) {
                ability.onUse((ServerWorld) world, player.getMainHandStack(), player);
            }
            return ActionResult.PASS;
        }));



        ServerTickEvents.START_SERVER_TICK.register(MaceHandler::Tick);


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



        ServerPlayerEvents.LEAVE.register((ModUtils::removeAbility));


        ServerPlayerEvents.JOIN.register((player -> {

            ServerWorld world = player.getWorld();

            PlayerEntity maceOwnerPlayer = ModUtils.getMaceOwnerPlayer(world);

            if (maceOwnerPlayer != null) {
                if (!MaceHandler.hasMace(player)) {
                    if (maceOwnerPlayer.getName().getString().equals(player.getName().getString())) {
                        ModUtils.setMaceOwnerString(world, "");
                    }
                }
            }

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


    public static boolean hasToken(PlayerEntity player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack.isOf(ModItems.SOUL_TOKEN)) {
                return true;
            }
        }
        return false;
    }



}
