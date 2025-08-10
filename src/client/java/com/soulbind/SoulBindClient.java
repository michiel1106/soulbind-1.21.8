package com.soulbind;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.soulbind.packets.*;
import com.soulbind.screens.AbilitySelectScreen;
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
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SoulBindClient implements ClientModInitializer {
	private static KeyBinding primary;
	private static KeyBinding secondary;

	@Override
	public void onInitializeClient() {



		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) ->
				commandDispatcher.register(ClientCommandManager.literal("soulmate")
						.then(ClientCommandManager.literal("accept")
								.then(ClientCommandManager.argument("playername", StringArgumentType.string())
										.then(ClientCommandManager.argument("requestid", IntegerArgumentType.integer())
												.executes((commandContext -> {

													String string = StringArgumentType.getString(commandContext, "playername");

													int requestid = IntegerArgumentType.getInteger(commandContext, "requestid");

													CustomPayload payload = new SoulmateAcceptPacketC2S(string, requestid);

													ClientPlayNetworking.send(payload);

													return 1;
												}))))))));

		ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess) ->
				commandDispatcher.register(ClientCommandManager.literal("soulmate")
						.then(ClientCommandManager.literal("deny")
								.then(ClientCommandManager.argument("requestid", IntegerArgumentType.integer())
										.executes(commandContext -> {

											int requestid = IntegerArgumentType.getInteger(commandContext, "requestid");

											SoulmateDenyPacketC2S soulmateDenyPacketC2S = new SoulmateDenyPacketC2S(requestid);

											ClientPlayNetworking.send(soulmateDenyPacketC2S);


											return 1;
										}))))));

		primary = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.soulbind.primary",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"Soulbind"
		));

		secondary = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.soulbind.secondary",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"Soulbind"
		));



		ClientTickEvents.START_CLIENT_TICK.register((minecraftClient -> {
			if (primary.wasPressed()) {
				ClientPlayNetworking.send(new ActivatePrimaryC2S());
			}
			if (secondary.wasPressed()) {
				ClientPlayNetworking.send(new ActivateSecondaryC2S());
			}

		}));



		ClientPlayNetworking.registerGlobalReceiver(ClientBoundOpenRequestSoulmateScreen.ID, (payload, context) -> {


			List<String> stringList = payload.stringList();

		//	stringList.removeIf((string -> string.equals(MinecraftClient.getInstance().player.getName().getString())));

			MinecraftClient.getInstance().setScreen(new OriginDisplayScreen(Text.empty(), stringList));
		});


		ClientPlayNetworking.registerGlobalReceiver(SoulmateInvitePacketS2C.ID, ((payload, context) -> {

			System.out.println(payload.player() + " " + payload.requestid());

			System.out.println("/soulmate accept " + payload.player() + " " + payload.requestid());


			Text message = Text.literal("") // 2 blank lines
					.append(Text.literal(payload.player() + " has requested you to become their soulmate with their chosen ability being: " + payload.ability()))
					.append("\n\n")
					.append(Text.literal("[Accept]").styled(style -> style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent.RunCommand("/soulmate accept " + payload.player() + " " + payload.requestid()))))
					.append(Text.literal("     ")) // some space between buttons
					.append(Text.literal("[Decline]").styled(style -> style.withColor(Formatting.RED).withClickEvent(new ClickEvent.RunCommand("/soulmate deny " + payload.requestid()))))
					.append("\n\n");

			MinecraftClient.getInstance().player.sendMessage(message, false);

		}));


		// probably dont have to touch this but I enabled this just in case.
	}
}