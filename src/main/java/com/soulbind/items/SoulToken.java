package com.soulbind.items;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.soulbind.packets.ClientBoundOpenRequestSoulmateScreen;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SoulToken extends Item {
    public SoulToken(Settings settings) {
        super(settings);
    }




    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            List<String> stringList = new ArrayList<>();

            world.getServer().getPlayerManager().getPlayerList().forEach((player -> {
                stringList.add(player.getName().getString());
            }));

            ServerPlayNetworking.send((ServerPlayerEntity) user, new ClientBoundOpenRequestSoulmateScreen(stringList));

        }

        return super.use(world, user, hand);


    }






}
