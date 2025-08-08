package com.soulbind.mixin;


import com.soulbind.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class ItemStackMixin {

    @Shadow private World world;

    @Inject(method = "discard", at = @At("HEAD"))
    private void onDiscard(CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;


        if (entity instanceof ItemEntity itemEntity) {
            if (itemEntity.getStack().getItem() instanceof MaceItem) {
                if (this.world != null) {
                    if (this.world.getServer() != null) {
                        ModUtils.setMaceOwnerString(this.world.getServer().getOverworld(), "");
                    }
                }
            }
        }
    }

}
