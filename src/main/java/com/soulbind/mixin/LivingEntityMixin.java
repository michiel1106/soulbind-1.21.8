package com.soulbind.mixin;


import com.soulbind.util.ModUtils;
import net.minecraft.entity.LivingEntity;
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

    @Inject(method = "heal", at = @At("TAIL"))
    private void onPlayerHeal(float amount, CallbackInfo ci) {


        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (livingEntity instanceof PlayerEntity playerEntity) {
            PlayerEntity soulmate = ModUtils.getSoulmate(playerEntity);

            if (soulmate != null) {


                soulmate.heal(amount);
            }
        }
    }

}
