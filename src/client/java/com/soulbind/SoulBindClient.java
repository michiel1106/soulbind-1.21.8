package com.soulbind;

import com.soulbind.packets.ClientBoundOpenRequestSoulmateScreen;
import com.soulbind.screens.RequestSoulmateScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class SoulBindClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.


		ClientPlayNetworking.registerGlobalReceiver(ClientBoundOpenRequestSoulmateScreen.ID, (payload, context) -> {

			System.out.println(payload.stringList());
			MinecraftClient.getInstance().setScreen(new RequestSoulmateScreen(Text.empty(), payload.stringList()));
		});


		// probably dont have to touch this but I enabled this just in case.
	}
}