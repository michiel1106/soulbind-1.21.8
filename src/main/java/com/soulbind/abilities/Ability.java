package com.soulbind.abilities;

import com.soulbind.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("DataFlowIssue")
public class Ability {
    // this is going to be the base ability. Other abilities will extend this class.

    public PlayerEntity soulmate;
    public PlayerEntity mainPlayer;
    public ServerWorld world;


    public void usePrimary(PlayerEntity player, ServerWorld world) {
    }

    public void useSecondary(PlayerEntity player, ServerWorld world) {

    }

    public void Tick(PlayerEntity player, ServerWorld world, @Nullable PlayerEntity soulmate) {
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

        if (target instanceof PlayerEntity) {


            PlayerEntity soulmate1 = ModUtils.getSoulmate(attacker);
            if (soulmate1 != null) {
                double baseValue1 = soulmate1.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue();
                soulmate1.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(baseValue1 + 2);
            }

            double baseValue = attacker.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue();
            attacker.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(baseValue + 2);
        }
    }

    public void onDamage(PlayerEntity player, DamageSource damageSource, float damage) {

    }

    public void onRespawn(PlayerEntity player) {
        double baseValue = player.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue();
        player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(baseValue - 2);
    }



}
