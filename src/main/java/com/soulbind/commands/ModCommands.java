package com.soulbind.commands;

import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.soulbind.abilities.importantforregistering.AbilityType;
import com.soulbind.util.ModUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Locale;

public class ModCommands {
    // commands are registered through events and are basically one long line of code.



    public static void RegisterCommands() {




        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("soulbind")
                        .then(CommandManager.literal("setjoined")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("true/false", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                    String value = StringArgumentType.getString(ctx, "true/false").toLowerCase(Locale.ENGLISH);

                                                    if (value.equals("true") || value.equals("false")) {
                                                        boolean joined = Boolean.parseBoolean(value);
                                                        ModUtils.SetJoinedAlready(player, joined);
                                                        ModUtils.GivePlayerItem(player);
                                                    } else {
                                                        ctx.getSource().sendMessage(Text.literal("Please use either true or false."));
                                                    }
                                                    return 1;
                                                }))))));



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("soulbind")
                        .then(CommandManager.literal("ability")
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .then(CommandManager.argument("ability", StringArgumentType.word())
                                                        .suggests(ABILITIES)
                                                        .executes(ctx -> {
                                                            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                            String id = StringArgumentType.getString(ctx, "ability");
                                                            AbilityType type = getAbilityTypeById(id);
                                                            if (type == null) {
                                                                ctx.getSource().sendError(Text.literal("Invalid ability id: " + id));
                                                                return 0;
                                                            }
                                                            ModUtils.giveAbilityToPlayer(player, type);
                                                            return 1;
                                                        })))))));


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("soulbind")
                        .then(CommandManager.literal("ability")
                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                    ModUtils.removeAbility(player);
                                                    return 1;
                                                }))))));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("soulbind")
                        .then(CommandManager.literal("ability")
                                .then(CommandManager.literal("get")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                                                    ctx.getSource().sendMessage(Text.literal(ModUtils.getAbilityString(player)));


                                                    return 1;
                                                }))))));


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("soulbind")
                                .then(CommandManager.literal("soulmate")
                        .then(CommandManager.literal("bind")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .then(CommandManager.argument("player1", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity p1 = EntityArgumentType.getPlayer(ctx, "player");
                                                    ServerPlayerEntity p2 = EntityArgumentType.getPlayer(ctx, "player1");

                                                    ModUtils.writePlayerName(p1, p2.getName().getString());
                                                    ModUtils.writePlayerName(p2, p1.getName().getString());

                                                    return 1;
                                                })))))));

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(CommandManager.literal("soulbind")
                .then(CommandManager.literal("soulmate")
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(commandContext -> {
                                            ServerPlayerEntity entity = EntityArgumentType.getPlayer(commandContext, "player");

                                            PlayerEntity soulmate = ModUtils.getSoulmate(entity);

                                            if (soulmate != null) {
                                                ModUtils.writePlayerName(soulmate, "");
                                            }
                                            ModUtils.writePlayerName(entity, "");


                                            return 1;
                                        })))))));

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> commandDispatcher.register(CommandManager.literal("soulbind")
                .then(CommandManager.literal("soulmate")
                        .then(CommandManager.literal("get")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(commandContext -> {
                                            ServerPlayerEntity entity = EntityArgumentType.getPlayer(commandContext, "player");

                                            commandContext.getSource().sendMessage(Text.literal(ModUtils.readPlayerName(entity)));





                                            return 1;
                                        })))))));

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
