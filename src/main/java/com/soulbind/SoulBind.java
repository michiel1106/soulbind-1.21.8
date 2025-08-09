package com.soulbind;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soulbind.abilities.Ability;
import com.soulbind.commands.ModCommands;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.events.ModEvents;
import com.soulbind.items.ModItems;
import com.soulbind.items.SoulToken;
import com.soulbind.packets.*;
import com.soulbind.util.ModUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class SoulBind implements ModInitializer {
	public static final String MOD_ID = "soulbind";


	public static final Predicate<Entity> IS_TOKEN = entity -> {
		if (entity instanceof ItemEntity item) {
			if (item.getStack().getItem() instanceof SoulToken) {
				return true;
			}
		}
		return false;
	};


	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final RegistryKey<DamageType> SOULMATELESS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "soulmateless"));




	public static DamageSource of(World world, RegistryKey<DamageType> key) {
		return new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(key));
	}

	@Override
	public void onInitialize() {


		PayloadTypeRegistry.playS2C().register(ClientBoundOpenRequestSoulmateScreen.ID, ClientBoundOpenRequestSoulmateScreen.CODEC);
		PayloadTypeRegistry.playS2C().register(SoulmateInvitePacketS2C.ID, SoulmateInvitePacketS2C.CODEC);

		PayloadTypeRegistry.playC2S().register(ActivatePrimaryC2S.ID, ActivatePrimaryC2S.CODEC);
		PayloadTypeRegistry.playC2S().register(ActivateSecondaryC2S.ID, ActivateSecondaryC2S.CODEC);
		PayloadTypeRegistry.playC2S().register(SoulmateInvitePacketC2S.ID, SoulmateInvitePacketC2S.CODEC);


		ServerPlayNetworking.registerGlobalReceiver(SoulmateInvitePacketC2S.ID, ((soulmateInvitePacketC2S, context) -> {
			String player1 = soulmateInvitePacketC2S.player();
			String ability = soulmateInvitePacketC2S.ability();
			CustomPayload payload = new SoulmateInvitePacketS2C(player1, ability);
			ServerPlayNetworking.send(context.server().getPlayerManager().getPlayer(player1), payload);
		}));



		ServerPlayNetworking.registerGlobalReceiver(ActivatePrimaryC2S.ID, ((payload, context) ->  {
			ServerPlayerEntity player = context.player();


			Ability ability = ModUtils.getAbility(player);

			if (ability != null) {
				ability.usePrimary(player, player.getWorld());
			}


		}));

		ServerPlayNetworking.registerGlobalReceiver(ActivateSecondaryC2S.ID, ((payload, context) ->  {
			ServerPlayerEntity player = context.player();

			Ability ability = ModUtils.getAbility(player);

			if (ability != null) {
				ability.useSecondary(player, player.getWorld());
			}


		}));





		ModItems.initialize();
		ModEvents.activateEvents();
		ModCommands.RegisterCommands();
		ModDataAttachments.init();
		// btw if you already know stuff from the comments or have read them then you can delete them and commit to the repo, github keeps a history of everything anyway so they wouldnt be completely gone.

		LOGGER.info("Starting Soulbind Mod!");
	}
}