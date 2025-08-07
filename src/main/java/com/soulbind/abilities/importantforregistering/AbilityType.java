package com.soulbind.abilities.importantforregistering;

import com.mojang.serialization.Codec;
import com.soulbind.abilities.Ability;
import com.soulbind.abilities.testabilities.EmptyAbility;
import com.soulbind.abilities.testabilities.TestAbility;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Supplier;

public enum AbilityType implements StringIdentifiable {
    TEST_ABILITY("test_ability", TestAbility::new),
    EMPTY_ABILITY("empty", EmptyAbility::new);



    // if you want to add more abilities for registering, you need to remove the last ; and add a new entry. so for example, say you have the PoisonAbility class. (which extends ability ofcourse). and you have this:

    /*
        TEST_ABILITY("test_ability", TestAbility::new),
        EMPTY_ABILITY("empty", EmptyAbility::new);

    you need to change that to:
        TEST_ABILITY("test_ability", TestAbility::new),
        EMPTY_ABILITY("empty", EmptyAbility::new),     <---- remove the ; and add a comma
        POISON_ABILITY("poison_ability", PoisonAbility::new);  <---- add the ; at the end.
     */


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