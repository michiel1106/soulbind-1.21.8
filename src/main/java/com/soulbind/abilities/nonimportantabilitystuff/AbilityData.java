package com.soulbind.abilities.nonimportantabilitystuff;

import com.mojang.serialization.Codec;

public record AbilityData(AbilityType type) {
    public static final AbilityData DEFAULT = new AbilityData(AbilityType.TEST_ABILITY);
    public static final Codec<AbilityData> CODEC = AbilityType.CODEC.xmap(AbilityData::new, AbilityData::type);
}
