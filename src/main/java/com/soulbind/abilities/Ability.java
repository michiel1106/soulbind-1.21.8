package com.soulbind.abilities;

import com.soulbind.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class Ability {
    // this is gonna be the base ability. Other abilities will extend this class.

    public PlayerEntity soulmate;
    public PlayerEntity mainPlayer;
    public ServerWorld world;


    public void UsePrimary(PlayerEntity player, ServerWorld world, Hand hand) {

    }

    public void UseSecondary(PlayerEntity player, ServerWorld world, Hand hand) {

    }

    public void Tick(PlayerEntity player, ServerWorld world, PlayerEntity soulmate) {
        this.soulmate = soulmate;
        this.mainPlayer = player;
        this.world = world;


    }

    public PlayerEntity getSoulmate() {
        return world.getServer().getPlayerManager().getPlayer(ModUtils.readPlayerName(mainPlayer));
    }

    public void onHit(PlayerEntity player, Entity target, float damage) {

    }

    public float getCustomDamage(DamageSource damageSource, float amount) {
        return amount;
    }

    public void onUse(ServerWorld world, ItemStack stack, PlayerEntity player) {

    }

    public void onKill(ServerWorld world, PlayerEntity attacker, Entity target) {

    }

    public void onDamage(PlayerEntity player, DamageSource damageSource, float damage) {

    }


}
