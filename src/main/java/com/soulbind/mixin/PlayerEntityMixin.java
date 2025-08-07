package com.soulbind.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.soulbind.abilities.Ability;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityData;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityType;
import com.soulbind.dataattachements.ModDataAttachments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

	@Unique
	private LivingEntity target;

	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V"))
	private PlayerEntity onIncreaseStat(PlayerEntity instance, Identifier stat, int amount) {
		PlayerEntity player = (PlayerEntity)(Object)this;




		if (getTarget() != null) {
			AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
			if (data != null) {
				if (player instanceof PlayerEntity playerEntity) {
					AbilityType type = data.type();
					Ability ability = type.createInstance();
					ability.onHit(playerEntity, getTarget(), amount/10.0F);
				}
			}
		}

		return instance;
	}

	@Inject(method = "attack", at = @At(value = "HEAD"))
	private void attack(Entity target, CallbackInfo ci) {
		if (target.isAttackable()) {
			PlayerEntity player = (PlayerEntity)(Object)this;
			if (!target.handleAttack(player)) {
				setTarget((LivingEntity) target);
			}
		}
	}

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }
}