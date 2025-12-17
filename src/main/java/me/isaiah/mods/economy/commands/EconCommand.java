package me.isaiah.mods.economy.commands;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

public class EconCommand implements com.mojang.brigadier.Command<CommandSourceStack>, Predicate<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {

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
        ServerPlayer plr = context.getSource().getPlayerOrException();
        msg(plr, "Economy Fabric Mod - by Isaiah");
        return 0;
    }
    
    public void msg(ServerPlayer cs, String message) {
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

}