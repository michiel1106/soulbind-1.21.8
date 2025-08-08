package com.soulbind.mixin;


import com.soulbind.macehandler.MaceHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeCache;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin extends BlockWithEntity {

    protected CrafterBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public static Optional<RecipeEntry<CraftingRecipe>> getCraftingRecipe(ServerWorld world, CraftingRecipeInput input) {
        return RECIPE_CACHE.getRecipe(world, input);
    }

    @Shadow @Final private static RecipeCache RECIPE_CACHE;

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private void onCraft(BlockState state, ServerWorld world, BlockPos pos, CallbackInfo ci) {
        if (world.getBlockEntity(pos) instanceof CrafterBlockEntity crafterBlockEntity) {
            CraftingRecipeInput var11 = crafterBlockEntity.createRecipeInput();
            Optional<RecipeEntry<CraftingRecipe>> optional = getCraftingRecipe(world, var11);

            if (optional.isPresent()) {

                String path = optional.get().id().getValue().getPath();


                    if (path.equals("mace")) {

                        MaceHandler.maceCrafted();

                        if (MaceHandler.MaceActive) {
                        ci.cancel();

                        }

                    }

            }
        }

    }

}
