package com.soulbind.mixin;


import com.soulbind.macehandler.MaceHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.crypto.Mac;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {


    @Shadow public abstract Slot getOutputSlot();


    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void onQuickMove(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {

        if (index == 0) {
            Slot slot = this.getOutputSlot();
            if (slot instanceof CraftingResultSlot) {
                ItemStack stack = slot.getStack();
                if (stack.getItem() instanceof MaceItem) {

                    if (MaceHandler.maceActive) {
                        System.out.println("Prevent quick-moving of MaceItem from crafting output.");
                        cir.setReturnValue(ItemStack.EMPTY);
                        cir.cancel();
                    }
                    MaceHandler.maceCrafted();
                }
            }
        }
    }
}
