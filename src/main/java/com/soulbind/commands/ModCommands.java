package com.soulbind.commands;

import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.soulbind.abilities.nonimportantabilitystuff.AbilityType;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Locale;

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



        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(CommandManager.literal("setjoined")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("true/false", StringArgumentType.string())
                                .executes(commandContext -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(commandContext, "player");
                                    String value = StringArgumentType.getString(commandContext, "true/false");

                                    if (value.toLowerCase(Locale.ENGLISH).equals("true")) {
                                        ModUtils.SetJoinedAlready(player, true);
                                        ModUtils.GivePlayerItem(player);
                                    } else if (value.toLowerCase(Locale.ENGLISH).equals("false")) {
                                        ModUtils.SetJoinedAlready(player, false);
                                        ModUtils.GivePlayerItem(player);
                                    } else {
                                        commandContext.getSource().sendMessage(Text.literal("Please use either true or false."));
                                    }




                                    return 1;
                                }))))));

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(CommandManager.literal("ability")
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("ability", StringArgumentType.word())
                                        .executes(commandContext -> {
                                            PlayerEntity player = EntityArgumentType.getPlayer(commandContext, "player");

                                            String abilityId = StringArgumentType.getString(commandContext, "ability");
                                            AbilityType abilityType = getAbilityTypeById(abilityId);
                                            if (abilityType == null) {
                                                commandContext.getSource().sendError(Text.literal("Invalid ability id: " + abilityId));
                                                return 0;
                                            }
                                            ModUtils.giveAbilityToPlayer(player, abilityType);
                                            return 1;

                                        }).suggests(ABILITIES)))))));

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(CommandManager.literal("ability")
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(commandContext -> {
                                            PlayerEntity player = EntityArgumentType.getPlayer(commandContext, "player");
                                            ModUtils.removeAbility(player);
                                            return 1;
                                        }))))));





    }
    public static SuggestionProvider<ServerCommandSource> ABILITIES = (context, builder) -> {
        return CommandSource.suggestMatching(
                Arrays.stream(AbilityType.values()).map(AbilityType::asString), builder
        );
    };


    private static AbilityType getAbilityTypeById(String id) {
        for (AbilityType type : AbilityType.values()) {
            if (type.asString().equals(id)) {
                return type;
            }
        }
        return null;
    }

}
