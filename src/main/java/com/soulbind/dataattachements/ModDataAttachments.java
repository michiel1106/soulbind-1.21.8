package com.soulbind.dataattachements;

import com.soulbind.SoulBind;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

@SuppressWarnings("ALL")
public class ModDataAttachments {
    // following example: https://gist.github.com/Linguardium/cebcd41c6bbcd74eaa1f8b40ec2bbec8


    public static final AttachmentType<SoulmateData> PLAYER_SOULMATE_ATTACHMENT = AttachmentRegistry.create(
                    Identifier.of(SoulBind.MOD_ID,"soulmate"),
                    builder->builder // we are using a builder chain here to configure the attachment data type
                            .initializer(()-> SoulmateData.DEFAULT) // a default value to provide if you dont supply one
                            .persistent(SoulmateData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
 );

    public static final AttachmentType<AlreadyJoinedData> PLAYER_JOINED_ATTACHMENT = AttachmentRegistry.create(
            Identifier.of(SoulBind.MOD_ID,"joined"),
            builder->builder // we are using a builder chain here to configure the attachment data type
                    .initializer(()-> AlreadyJoinedData.DEFAULT) // a default value to provide if you dont supply one
                    .persistent(AlreadyJoinedData.CODEC) // how to save and load the data when the object it is attached to is saved or loaded
    );

    public static void init() {
        // This empty method can be called from the mod initializer to ensure our component type is registered at mod initialization time
        // ModAttachmentTypes.init();
    }
}
