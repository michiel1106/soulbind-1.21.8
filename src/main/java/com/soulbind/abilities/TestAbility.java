package com.soulbind.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class TestAbility extends Ability {
    @Override
    public void Tick(PlayerEntity player, ServerWorld world, PlayerEntity soulmate) {
        super.Tick(player, world, soulmate);
        //       System.out.println("ticked!");
    }

    @Override
    public void onKill(ServerWorld world, PlayerEntity attacker, Entity target) {
        super.onKill(world, attacker, target);
        System.out.println("killed");
    }

    @Override
    public void onHit(PlayerEntity player, Entity target, float damage) {
        super.onHit(player, target, damage);

        System.out.println("damaged: " + damage);

    }

    @Override
    public void onUse(ServerWorld world, ItemStack stack, PlayerEntity player) {
        super.onUse(world, stack, player);
        System.out.println("onuse + " + stack.toString());
    }

    @Override
    public void onDamage(PlayerEntity player, DamageSource damageSource, float damage) {
        super.onDamage(player, damageSource, damage);
        System.out.println(damage);
    }
}
