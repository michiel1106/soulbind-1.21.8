package com.soulbind.mixin;

import com.soulbind.macehandler.MaceHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingResultSlot.class)
@Debug(export = true)
public abstract class ResultSlotMixin extends Slot {


    public ResultSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "takeStack", at = @At("HEAD"), cancellable = true)
    private void takeStack(int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.getStack() != null) {
            if (this.getStack().getItem() instanceof MaceItem) {
                if (MaceHandler.MaceActive) {
                    System.out.println("tried to craft mace item. canceling");
                    cir.setReturnValue(ItemStack.EMPTY);
                    cir.cancel();
                }
            }
        }
    }


    @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;I)V", at = @At("HEAD"), cancellable = true)
    private void stack(ItemStack stack, int amount, CallbackInfo cir) {
        if (this.getStack() != null) {
            if (this.getStack().getItem() instanceof MaceItem) {
                MaceHandler.maceCrafted();
                if (MaceHandler.MaceActive) {
                    System.out.println("tried to craft mace item. canceling");
                    this.setStack(ItemStack.EMPTY);
                    cir.cancel();
                }
            }
        }
    }
}