package me.isaiah.mods.economy.commands;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BalCommand implements com.mojang.brigadier.Command<CommandSourceStack>, Predicate<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {

    public LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> dispatcher, String label) {
        return dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(label).requires(this).executes(this)
                .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("args", StringArgumentType.greedyString()).suggests(this).executes(this))
        );
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);
        return builder.buildFuture();
    }

    @Override
    public boolean test(CommandSourceStack t) {
        return true;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        try {
            String msg = "&aBalance: &f$" + Economy.getMoneyExact(player.getName().getString());
            msg_plr(player, msg);
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
        }
        return 1;
    }
    
    public void msg_plr(ServerPlayer cs, String message) {
		try {
			cs.displayClientMessage(Component.nullToEmpty(translate_alternate_color_codes('&', message)), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private static final char COLOR_CHAR = '\u00A7';
    private static String translate_alternate_color_codes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    
    /*public void message(ServerPlayerEntity cs, Formatting color, String message) {
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
	}*/

}