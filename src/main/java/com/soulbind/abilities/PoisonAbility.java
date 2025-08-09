package com.soulbind.abilities;

import com.soulbind.SoulBind;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PoisonAbility extends Ability{
    Random random = new Random();
    @Override
    public List<OrderedText> getDescription() {
        List<OrderedText> orderedTexts = new ArrayList<>();

        orderedTexts.add(Text.literal("Have a small chance to poison").asOrderedText());
        orderedTexts.add(Text.literal("players when you hit them!").asOrderedText());

        return orderedTexts;
    }

    @Override
    public String getName() {
        return "Poison";
    }

    @Override
    public Identifier getImage() {
        return SoulBind.identifier("poison.png");
    }

    @Override
    public void onHit(PlayerEntity player, Entity target, float damage) {

        System.out.println(target);

        if (random.nextInt(10) == 1) {
            if (target instanceof PlayerEntity playerEntity) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 0, true, false));
            }
        }


        super.onHit(player, target, damage);
    }
}
