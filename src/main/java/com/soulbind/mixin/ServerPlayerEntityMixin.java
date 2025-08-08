package com.soulbind.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.soulbind.SoulBind;
import com.soulbind.abilities.Ability;
import com.soulbind.util.ModUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
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

        Ability ability = ModUtils.getAbility(player);

        if (ability != null) {
            ability.onRespawn(player);
        }
    }

    @WrapOperation(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean copyFromMixin(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule, Operation<Boolean> original) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        if (!ModUtils.readPlayerName(player).equals("")) {
            return true;
        }

        return instance.getBoolean(rule);
    }


    // 2) after vanilla's copyFrom runs, copy items from old player -> new player if soulmate
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyInventoryAfterRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;

        // only apply soulmate keep-inv behavior
        if (ModUtils.readPlayerName(oldPlayer).equals("")) return;

        PlayerInventory oldInv = oldPlayer.getInventory();
        PlayerInventory newInv = newPlayer.getInventory();

        // copy every slot (hotbar, main, armor, offhand — PlayerInventory.size() covers them)
        for (int i = 0; i < oldInv.size(); i++) {
            ItemStack stack = oldInv.getStack(i);

            if (!stack.isOf(Items.MACE)) {
                newInv.setStack(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            }
        }

        // mark dirty and sync to client
        newInv.markDirty();

        // ensure client sees the new inventory immediately
        // use whichever field/getter your mappings expose; playerScreenHandler/currentScreenHandler both are common
        try {
            newPlayer.playerScreenHandler.sendContentUpdates();
        } catch (NoSuchFieldError e) {
            // fallback if your mappings use a different name; you can also call newPlayer.currentScreenHandler.sendContentUpdates()
        }
    }

}
