package com.soulbind.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
@Debug(export = true)
public abstract class PlayerEntityMixin {

	@ModifyReceiver(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V"))
	private PlayerEntity onIncreaseStat(PlayerEntity instance, Identifier stat, int amount) {
		System.out.println("Dealt damage: " + (amount / 10.0F));


		return instance;
	}
	
}