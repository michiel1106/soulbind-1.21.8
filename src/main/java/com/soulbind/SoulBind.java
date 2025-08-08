package com.soulbind;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soulbind.abilities.Ability;
import com.soulbind.commands.ModCommands;
import com.soulbind.dataattachements.ModDataAttachments;
import com.soulbind.events.ModEvents;
import com.soulbind.items.ModItems;
import com.soulbind.packets.ActivatePrimaryC2S;
import com.soulbind.packets.ActivateSecondaryC2S;
import com.soulbind.packets.ClientBoundOpenRequestSoulmateScreen;
import com.soulbind.util.ModUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
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

public class SoulBind implements ModInitializer {
	public static final String MOD_ID = "soulbind";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final RegistryKey<DamageType> SOULMATELESS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "soulmateless"));




	public static DamageSource of(World world, RegistryKey<DamageType> key) {
		return new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(key));
	}

	@Override
	public void onInitialize() {





		CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(CommandManager.literal("damagesoul")
				.executes(commandContext -> {
					commandContext.getSource().getPlayer().damage(commandContext.getSource().getWorld(), of(commandContext.getSource().getWorld(), SOULMATELESS), 1000f);
					return 1;
				}))));



		PayloadTypeRegistry.playS2C().register(ClientBoundOpenRequestSoulmateScreen.ID, ClientBoundOpenRequestSoulmateScreen.CODEC);
		PayloadTypeRegistry.playC2S().register(ActivatePrimaryC2S.ID, ActivatePrimaryC2S.CODEC);
		PayloadTypeRegistry.playC2S().register(ActivateSecondaryC2S.ID, ActivateSecondaryC2S.CODEC);


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