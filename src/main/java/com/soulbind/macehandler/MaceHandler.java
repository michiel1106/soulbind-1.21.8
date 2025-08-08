package com.soulbind.macehandler;

import com.soulbind.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@SuppressWarnings("RedundantIfStatement")
public class MaceHandler {

    public static volatile boolean maceActive = false;
    public static List<? extends ItemEntity> maceEntities;

    public static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static int ticks = 0;


    public static final Predicate<Entity> IS_MACE = entity -> {

        if (entity instanceof ItemEntity item) {
            if (item.getStack().getItem() instanceof MaceItem) {
                return true;
            }
        }
        return false;
    };


    public static void maceCrafted() {


        executorService.submit(() -> {
            try {
                Thread.sleep(0, 100);
            } catch (InterruptedException ignored) {}

            maceActive = true;
        });

    }





    public static void Tick(MinecraftServer server) {

        //maceActive = false;  // reset to false, will be set true if found in any world

        for (ServerWorld world : server.getWorlds()) {
            List<? extends ItemEntity> worldMaceEntities = world.getEntitiesByType(EntityType.ITEM, IS_MACE);
            isMaceActive(server.getPlayerManager().getPlayerList(), world);
            ticks++;

            if (ticks == 2) {
                ticks = 0;

                if (worldMaceEntities.isEmpty()) {
                    if (!isMaceActive(server.getPlayerManager().getPlayerList(), world)) {

                    } else {
                        maceActive = true;
                    }
                } else {
                    maceActive = true;
                }
            }

            if (worldMaceEntities.size() >= 2) {

                ItemEntity first = worldMaceEntities.get(0);
                ItemEntity last = worldMaceEntities.get(worldMaceEntities.size() - 1);

                if (Math.max(first.age, last.age) == first.age) {
                    last.discard();
                } else {
                    first.discard();
                }
            }

            if (!worldMaceEntities.isEmpty()) {
                maceActive = true;
            }
        }
    }

    public static boolean isMace(ItemStack itemStack) {
        return itemStack.isOf(Items.MACE);
    }


    public static boolean isMaceActive(List<ServerPlayerEntity> playerEntities, ServerWorld world) {
        for (ServerPlayerEntity player : playerEntities) {


            for (ItemStack itemStack : player.getInventory()) {
                if (isMace(itemStack)) {
                    ModUtils.setMaceOwnerString(world.getServer().getOverworld(), player.getName().getString());
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean hasMace(PlayerEntity player) {
        for (ItemStack itemStack : player.getInventory()) {
            if (isMace(itemStack)) {
                ModUtils.setMaceOwnerString(player.getWorld().getServer().getOverworld(), player.getName().getString());
                return true;
            }
        }
        return false;
    }



}