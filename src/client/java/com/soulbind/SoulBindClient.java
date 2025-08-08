package com.soulbind;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.soulbind.packets.ActivatePrimaryC2S;
import com.soulbind.packets.ActivateSecondaryC2S;
import com.soulbind.packets.ClientBoundOpenRequestSoulmateScreen;
import com.soulbind.screens.OriginDisplayScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SoulBindClient implements ClientModInitializer {
	private static KeyBinding primary;
	private static KeyBinding secondary;

	@Override
	public void onInitializeClient() {

		primary = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.soulbind.primary", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_G, // The keycode of the key
				"Soulbind" // The translation key of the keybinding's category.
		));

		secondary = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.soulbind.secondary", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_R, // The keycode of the key
				"Soulbind" // The translation key of the keybinding's category.
		));



		ClientTickEvents.START_CLIENT_TICK.register((minecraftClient -> {
			if (primary.wasPressed()) {
				ClientPlayNetworking.send(new ActivatePrimaryC2S());
			}
			if (secondary.wasPressed()) {
				ClientPlayNetworking.send(new ActivateSecondaryC2S());
			}

		}));

		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) -> commandDispatcher.register((ClientCommandManager.literal("openscreen").executes(commandContext -> {
			List<String> stringList = new ArrayList<>();

			MinecraftClient.getInstance().setScreen(new OriginDisplayScreen(Text.literal(""), stringList));
			return 1;
		})))));

		ClientPlayNetworking.registerGlobalReceiver(ClientBoundOpenRequestSoulmateScreen.ID, (payload, context) -> {

			System.out.println(payload.stringList());
			MinecraftClient.getInstance().setScreen(new OriginDisplayScreen(Text.empty(), payload.stringList()));
		});


		// probably dont have to touch this but I enabled this just in case.
	}
}