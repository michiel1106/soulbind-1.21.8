package com.soulbind.dataattachements;

import com.soulbind.SoulBind;
import com.soulbind.abilities.importantforregistering.AbilityData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

@SuppressWarnings("ALL")
public class ModDataAttachments {
    // following example: https://gist.github.com/Linguardium/cebcd41c6bbcd74eaa1f8b40ec2bbec8


    public static final AttachmentType<SoulmateData> PLAYER_SOULMATE_ATTACHMENT = AttachmentRegistry.create(
                    Identifier.of(SoulBind.MOD_ID,"soulmate"),
                    builder->builder
                            .initializer(()-> SoulmateData.DEFAULT)
                            .persistent(SoulmateData.CODEC)
                            .copyOnDeath()
 );

    public static final AttachmentType<AlreadyJoinedData> PLAYER_JOINED_ATTACHMENT = AttachmentRegistry.create(
            Identifier.of(SoulBind.MOD_ID,"joined"),
            builder->builder
                    .initializer(()-> AlreadyJoinedData.DEFAULT)
                    .persistent(AlreadyJoinedData.CODEC)
                    .copyOnDeath()
    );

    public static final AttachmentType<AbilityData> PLAYER_ABILITY = AttachmentRegistry.create(
            Identifier.of(SoulBind.MOD_ID, "ability"),
            builder -> builder
                    .initializer(() -> AbilityData.DEFAULT)
                    .persistent(AbilityData.CODEC)
                    .copyOnDeath()
    );

    public static void init() {
    }
}
