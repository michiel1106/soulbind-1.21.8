package com.soulbind.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.soulbind.abilities.Ability;
import com.soulbind.abilities.importantforregistering.AbilityData;
import com.soulbind.abilities.importantforregistering.AbilityType;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.macehandler.MaceHandler;
import com.soulbind.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@SuppressWarnings("UnstableApiUsage")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow
    public abstract @Nullable ItemEntity dropItem(ItemStack stack, boolean retainOwnership);

	@Unique
	private LivingEntity target;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V"))
	private PlayerEntity onIncreaseStat(PlayerEntity instance, Identifier stat, int amount) {
		PlayerEntity player = (PlayerEntity)(Object)this;

		if (getTarget() != null) {
			AbilityData data = player.getAttached(ModDataAttachments.PLAYER_ABILITY);
			if (data != null) {
				if (player instanceof PlayerEntity playerEntity) {
					AbilityType type = data.type();
					Ability ability = ModUtils.getAbility(playerEntity);
					ability.onHit(playerEntity, getTarget(), amount/10.0F);
				}
			}
		}

		return instance;
	}


	// 1) prevent vanilla dropping for soulmate players
	@Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
	private void preventDrops(CallbackInfo ci) {
		PlayerEntity self = (PlayerEntity) (Object) this;
		if (!ModUtils.readPlayerName(self).equals("")) {
			ci.cancel(); // don't drop items
		}
	}


	@WrapOperation(method = "getExperienceToDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
	private boolean experienceDropMixin(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule, Operation<Boolean> original) {

		PlayerEntity player = (PlayerEntity)(Object)this;

		if (!ModUtils.readPlayerName(player).equals("")) {
			return true;
		}
		return instance.getBoolean(rule);
	}


	@Inject(method = "attack", at = @At(value = "TAIL"))
	private void attack(Entity target, CallbackInfo ci) {
		if (target.isAttackable()) {
			PlayerEntity player = (PlayerEntity)(Object)this;
			if (!target.handleAttack(player)) {
				setTarget((LivingEntity) target);
			}
		}
	}

    @Unique
	public LivingEntity getTarget() {
        return target;
    }

    @Unique
	public void setTarget(LivingEntity target) {
        this.target = target;
    }

	@ModifyArgs(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
	private void modifyLavaDamage(Args args) {

		PlayerEntity player = (PlayerEntity)(Object)this;
		Ability ability = ModUtils.getAbility(player);
		if (ability != null) {
			DamageSource source = args.get(1);
			float amount = args.get(2);


			float customDamage = ability.getCustomDamage(source, amount);
			args.set(2, customDamage);
		}

	}







}