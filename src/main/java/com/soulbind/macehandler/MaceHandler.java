package com.soulbind.macehandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MaceItem;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@SuppressWarnings("RedundantIfStatement")
public class MaceHandler {

    public static boolean maceActive = false;
    public static List<? extends ItemEntity> maceEntities;

    public static ExecutorService executorService = Executors.newSingleThreadExecutor();

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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("macecrafted has been called. it is now: " + maceActive);
            maceActive = true;
        });



    }



    public static void Tick(MinecraftServer server) {

       maceEntities = server.getOverworld().getEntitiesByType(EntityType.ITEM, IS_MACE);

        ticks++;

        System.out.println(maceActive);




        if (ticks == 3) {
            ticks = 0;



            if (maceEntities.size() >= 2) {


                ItemEntity first = maceEntities.getFirst();
                ItemEntity last = maceEntities.getLast();

                if (Math.max(first.age, last.age) == first.age) {
                    last.discard();
                } else {
                    first.discard();
                }

            }
        }




    }

}
