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

    // now that we have this class, we can do a bunch of stuff. If you middle click Item there above after "extends", you can check out what you can @Override. basically add your own functionality.



    // for example this one, super.inventory means that it will do atleast the Item.inventoryTick stuff. sometimes needed, for example in the Mine class in Item:


    /*
        public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        ToolComponent toolComponent = (ToolComponent)stack.get(DataComponentTypes.TOOL);
        if (toolComponent != null && !toolComponent.canDestroyBlocksInCreative()) {
            boolean var10000;
            if (user instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)user;
                if (playerEntity.getAbilities().creativeMode) {
                    var10000 = false;
                    return var10000;
                }
            }

            var10000 = true;
            return var10000;
        } else {
            return true;
        }
    }
     */

    // if I would override it without doing super.canMine, it would not check the item for components. So doing the super is almost always necessary,


    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {

            // screen is rendering stuff and all rendering is client side.
            List<String> stringList = new ArrayList<>();

            world.getServer().getPlayerManager().getPlayerList().forEach((player -> {
                stringList.add(player.getName().getString());
            }));


            ServerPlayNetworking.send((ServerPlayerEntity) user, new ClientBoundOpenRequestSoulmateScreen(stringList));

        }

        return super.use(world, user, hand);


    }

    // this will tick everytime its in an inventory. specifically an entities inventory.
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        // if I want to check if a player is holding the item, I can check if the entity is "an instance of". An instance is making a new thing from a class. you do that by typing "new {class}" so for example new PlayerEntity.
        // and because the player entity extends LivingEntity which extends Entity, I can check entity specifically. I cannot check if stack is an instanceof PlayerEntity because playerentity doesnt extend ItemStack in any way.
        if (entity instanceof PlayerEntity player) {

            // you can also take away the player after PlayerEntity, what player does is exposing the variable. you can do the same by casting.
            //System.out.println("A player is holding this item! their name is " + player.getName().getString());
        }

        // casting example:

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            //System.out.println("a player is holding this item: " + player.getName());
            // casting is usually not really preferred though so I wouldnt recommend using it. just putting a variable after PlayerEntity is way better.

        }

    }




}
