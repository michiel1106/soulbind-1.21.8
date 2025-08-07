package com.soulbind;

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

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoulBind implements ModInitializer {
	public static final String MOD_ID = "soulbind";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



	// also idk if you know this already but heres a short explanation of what public, static, private, final and void mean.

	/*
	public means that a class outside of this one can access it. Right now I can go to ModItems and use the LOGGER variable because its public. if it was private I would not be able to use it.

	static means that theres only one. Take health for example. If the health variable in pigs was static, then every pig would have the same health, but its not, so each pig can have their own health value.
	you can still make it public though, so if it was public, you could take the pig entity:

	PigEntity pig = new PigEntity();

	pig.health = 0;

	and change their health!

	Next up is final. Final means that you cant change it. Whatever it is assigned is unchangable.
	You cannot do

	LOGGER = LoggerFactory.getLogger("bindsoul")

	because its final.

	then theres void. functions, or methods, I kind of use them interchangably, can return something. below example:

	 */

	public String thisreturnsastring() {
		return "tada";
	}

	/*
	the String argument can be changed to anything. You can change it to SoulToken and make it return the item.
	what void does is basically say, "this returns nothing". which is good for the initialize class because you dont need to return anything.
	its just meant to execute more pieces of code.

	 */



	@Override
	public void onInitialize() {

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