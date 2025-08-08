package com.soulbind.mixin;


import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow public abstract int size();

    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Shadow public abstract void setSelectedSlot(int slot);

    @Inject(method = "clone", at = @At("HEAD"), cancellable = true)
    private void onClone(PlayerInventory other, CallbackInfo ci) {
        for (int i = 0; i < this.size(); i++) {

            if (!other.getStack(i).isOf(Items.MACE)) {
                this.setStack(i, other.getStack(i));
            }
        }

        this.setSelectedSlot(other.getSelectedSlot());

        ci.cancel();
    }

}
