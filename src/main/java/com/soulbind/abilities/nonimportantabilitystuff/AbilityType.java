package com.soulbind.abilities.nonimportantabilitystuff;

import com.mojang.serialization.Codec;
import com.soulbind.abilities.Ability;
import com.soulbind.abilities.EmptyAbility;
import com.soulbind.abilities.TestAbility;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Supplier;

public enum AbilityType implements StringIdentifiable {
    TEST_ABILITY("test_ability", TestAbility::new),
    EMPTY_ABILITY("empty", EmptyAbility::new);


    private final String id;
    private final Supplier<Ability> supplier;

    AbilityType(String id, Supplier<Ability> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    @Override
    public String asString() {
        return id;
    }

    public Ability createInstance() {
        return supplier.get();
    }

    public static final Codec<AbilityType> CODEC = StringIdentifiable.createCodec(AbilityType::values);
}