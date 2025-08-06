package com.soulbind.events;

import com.soulbind.items.ModItems;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class ModEvents {
    private static int ticks = 0;

    // in fabric theres events, they are incredibly useful. you can find all the events here: https://wiki.fabricmc.net/tutorial:event_index

    // imo theyre constructed kind of weirdly but oh well.
    public static void activateEvents() {






        ServerTickEvents.END_SERVER_TICK.register((serverWorld -> {
            // the serverWorld you see above is something you can use.

            List<ServerPlayerEntity> players = serverWorld.getPlayerManager().getPlayerList();

            // this event is called every server tick. so if we want to say, track how long two people are apart by using a timer that increases we can do that. heres a simple example:

            // "=" assigns stuff, "==" checks for stuff.
            if (ticks == 40) {
                System.out.println("it has been 2 seconds.");
                System.out.println("resetting timer..");
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
