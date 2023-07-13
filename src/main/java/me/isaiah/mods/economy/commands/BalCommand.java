package me.isaiah.mods.economy.commands;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.isaiah.mods.economy.api.Economy;
import me.isaiah.mods.economy.api.UserDoesNotExistException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BalCommand implements com.mojang.brigadier.Command<ServerCommandSource>, Predicate<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {

    public LiteralCommandNode<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> dispatcher, String label) {
        return dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal(label).requires(this).executes(this)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("args", StringArgumentType.greedyString()).suggests(this).executes(this))
        );
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);
        return builder.buildFuture();
    }

    @Override
    public boolean test(ServerCommandSource t) {
        return true;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        try {
            String msg = "Balance: $" + Economy.getMoneyExact(player.getName().asString());
            message(player, Formatting.GREEN, msg);
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void message(ServerPlayerEntity cs, Formatting color, String message) {
		try {
			if (null == color) {
				cs.sendMessage(Text.of(message), false);
				return;
			}

			List<Text> txts = Text.of(message).getWithStyle(Style.EMPTY.withColor(color));
			for (Text t : txts) {
				cs.sendMessage(t, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}