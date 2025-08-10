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
import com.soulbind.util.SoulRequest;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SoulBind implements ModInitializer {
	public static final String MOD_ID = "soulbind";

	public static Map<Integer, SoulRequest> soulRequests = new HashMap<>();


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
		PayloadTypeRegistry.playC2S().register(SoulmateAcceptPacketC2S.ID, SoulmateAcceptPacketC2S.CODEC);
		PayloadTypeRegistry.playC2S().register(SoulmateDenyPacketC2S.ID, SoulmateDenyPacketC2S.CODEC);


		ServerPlayNetworking.registerGlobalReceiver(SoulmateInvitePacketC2S.ID, ((soulmateInvitePacketC2S, context) -> {
			String player1 = soulmateInvitePacketC2S.player();
			String ability = soulmateInvitePacketC2S.ability();

			SoulRequest soulRequest = new SoulRequest(context.player().getName().getString(), player1);

			soulRequests.put(soulRequest.getRandomID(), soulRequest);

			CustomPayload payload = new SoulmateInvitePacketS2C(player1, ability, soulRequest.getRandomID());
			ServerPlayNetworking.send(context.server().getPlayerManager().getPlayer(player1), payload);
		}));


		ServerPlayNetworking.registerGlobalReceiver(SoulmateAcceptPacketC2S.ID, ((payload, context) -> {

			int i = payload.randomID();
			String player = payload.player();



			SoulRequest soulRequest = soulRequests.get(i);

			if (soulRequest != null) {

				String requestingPlayer = soulRequest.getRequestingPlayer();

				String receivingPlayer = soulRequest.getReceivingPlayer();

				PlayerManager playerManager = context.server().getPlayerManager();

				PlayerEntity requestingPlayerObj = null;
				PlayerEntity receivingPlayerObj = null;

				if (playerManager != null) {
					requestingPlayerObj = playerManager.getPlayer(requestingPlayer);
					receivingPlayerObj = playerManager.getPlayer(receivingPlayer);
				}


				if (receivingPlayer.equals(player)) {
					if (receivingPlayerObj != null && requestingPlayerObj != null) {
						ModUtils.writePlayerName(requestingPlayerObj, receivingPlayer);
						ModUtils.writePlayerName(receivingPlayerObj, requestingPlayer);
						soulRequests.remove(i);

						receivingPlayerObj.sendMessage(Text.literal("Accepted " + requestingPlayer + " their soulmate request!"), false);

						requestingPlayerObj.sendMessage(Text.literal(receivingPlayer + " has accepted your soulmate request!"), false);


					}
				}
			} else {
				context.player().sendMessage(Text.literal("That request is not valid anymore."));
			}

        }));


		ServerPlayNetworking.registerGlobalReceiver(SoulmateDenyPacketC2S.ID, ((payload, context) -> {
			int i = payload.randomID();

			SoulRequest soulRequest = soulRequests.get(i);

			ServerPlayerEntity entity = context.server().getPlayerManager().getPlayer(soulRequest.getRequestingPlayer());

			if (entity != null) {
				entity.sendMessage(Text.literal(soulRequest.getReceivingPlayer() + " has declined your request.").formatted(Formatting.RED));
			}

			soulRequests.remove(i);

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