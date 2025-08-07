package com.soulbind.mixin;


import com.soulbind.abilities.Ability;
import com.soulbind.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract void heal(float amount);

    // Add guard
    private static final ThreadLocal<Boolean> syncingHealing = ThreadLocal.withInitial(() -> false);

    @Inject(method = "heal", at = @At("TAIL"))
    private void onPlayerHeal(float amount, CallbackInfo ci) {
        if (syncingHealing.get()) return; // prevent loop

        LivingEntity livingEntity = (LivingEntity)(Object)this;
        if (!(livingEntity instanceof PlayerEntity player)) return;

        PlayerEntity soulmate = ModUtils.getSoulmate(player);
        if (soulmate == null || soulmate.isDead()) return;

        try {
            syncingHealing.set(true);
            soulmate.heal(amount); // sync healing
        } finally {
            syncingHealing.set(false); // always clear
        }
    }

}
