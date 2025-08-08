package com.soulbind.mixin;


import com.soulbind.SoulBind;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract ServerWorld getWorld();

    @Shadow public abstract ItemEntity dropItem(ItemStack stack, boolean dropAtSelf, boolean retainOwnership);

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        PlayerEntity soulmate = ModUtils.getSoulmate(player);
        if (soulmate != null) {
            soulmate.damage(this.getWorld(), SoulBind.of(getWorld(), SoulBind.SOULMATELESS), Float.MAX_VALUE);
        }
    }


    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath2(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object)this;

        for (ItemStack stack : player.getInventory()) {
            if (stack.isOf(Items.MACE)) {
                this.dropItem(new ItemStack(Items.MACE), false, false);
            }
        }

    }

}
