package com.soulbind.abilities;

import com.soulbind.SoulBind;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FallDashAbility extends Ability{
    private int cooldown = 0;

    Random random = new Random();
    @Override
    public List<OrderedText> getDescription() {
        List<OrderedText> orderedTexts = new ArrayList<>();

        orderedTexts.add(Text.literal("Use primary to dash and").asOrderedText());
        orderedTexts.add(Text.literal("you dont take fall damage").asOrderedText());

        return orderedTexts;
    }

    @Override
    public String getName() {
        return "Fall Dash";
    }

    @Override
    public String getId() {
        return "falldash";
    }

    @Override
    public void Tick(PlayerEntity player, ServerWorld world, @Nullable PlayerEntity soulmate) {
        super.Tick(player, world, soulmate);

        if (cooldown != 0) {
            cooldown--;
        }
    }

    @Override
    public void usePrimary(PlayerEntity player, ServerWorld world) {

        if (cooldown != 0) {
            dashPlayer(player, 3);
        }
        cooldown = 100;

        super.usePrimary(player, world);
    }



    public static void dashPlayer(PlayerEntity player, double speed) {
        // get current yaw/pitch (degrees)
        double yaw = player.getYaw();
        double pitch = player.getPitch();

        // convert to radians
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        // compute forward vector from yaw/pitch
        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        // normalize and scale by speed
        Vec3d dir = new Vec3d(x, y, z).normalize().multiply(speed);

        // apply velocity
        player.setVelocity(dir);
        player.velocityModified = true;

        // on a dedicated server you will normally want to sync to the client:
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sp = (ServerPlayerEntity) player;

            EntityVelocityUpdateS2CPacket packet = new EntityVelocityUpdateS2CPacket(player);

            sp.networkHandler.sendPacket(packet);

        }
    }

    @Override
    public float getCustomDamage(DamageSource damageSource, float amount) {
        if (damageSource.isIn(DamageTypeTags.IS_FALL)) {
            return 0;
        }
        return super.getCustomDamage(damageSource, amount);
    }

    @Override
    public Identifier getImage() {
        return SoulBind.identifier("falldash.png");
    }


}
