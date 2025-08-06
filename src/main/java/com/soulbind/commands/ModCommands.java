package com.soulbind.commands;

import com.mojang.brigadier.arguments.StringArgumentType;

import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class ModCommands {
    // commands are registered through events and are basically one long line of code.



    public static void RegisterCommands() {
        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                commandDispatcher.register(CommandManager.literal("writestring")
                                .then(CommandManager.argument("player", EntityArgumentType.player())

                                    .then(CommandManager.argument("string", StringArgumentType.string())
                                            .executes((commandContext -> {

                                                String string = StringArgumentType.getString(commandContext, "string");
                                                PlayerEntity player = EntityArgumentType.getPlayer(commandContext, "player");

                                                ModUtils.writePlayerName(player, string);


                                                return 1; //this is only used by commands like /execute store, for example in the kill command the return is how many it killed.
                                                // that way datapack creators can do stuff with it, but in our case its not needed. Though returning a number based on for example success could.
                                            })))))));


        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->
                commandDispatcher.register(CommandManager.literal("readstring")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes((commandContext -> {


                                    PlayerEntity player = EntityArgumentType.getPlayer(commandContext, "player");


                                    String soulmate = ModUtils.readPlayerName(player);


                                    commandContext.getSource().getPlayer().sendMessage(Text.literal(soulmate));


                                    // now we can save stuff!

                                    return 1; //this is only used by commands like /execute store, for example in the kill command the return is how many it killed.
                                    // that way datapack creators can do stuff with it, but in our case its not needed. Though returning a number based on for example success could.
                                }))))));





    }




}
