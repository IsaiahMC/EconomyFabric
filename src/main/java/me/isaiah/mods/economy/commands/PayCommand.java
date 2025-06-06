package me.isaiah.mods.economy.commands;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;

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

import me.isaiah.mods.economy.FabricEconomyMod;
import me.isaiah.mods.economy.api.Economy;
import me.isaiah.mods.economy.api.EconomyUser;
import me.isaiah.mods.economy.api.UserDoesNotExistException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PayCommand implements com.mojang.brigadier.Command<ServerCommandSource>, Predicate<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {

	private static final String USAGE_PAY = "&4Usage: &c/pay <player> <amount>";
	private static final String PAY_NEGATIVE = "&4Error: &cYou can not send negative amounts!";
	
    public LiteralCommandNode<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> dispatcher, String label) {
        return dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal(label).requires(this).executes(this)
                .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("args", StringArgumentType.greedyString()).suggests(this).executes(this))
        );
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        builder = builder.createOffset(builder.getInput().lastIndexOf(' ') + 1);
        
        String input = builder.getInput();
        String[] cmds = input.trim().split(" ");
        
        if (cmds.length < 1) {
        	return builder.buildFuture();
        }

        // Suggest Player Names
        if (cmds.length <= 1 || (cmds.length <= 2 && !input.endsWith(" "))) {
        	String[] names = FabricEconomyMod.MINECRAFT_SERVER.getPlayerNames();
        	for (String s : names) {
        		builder.suggest(s);
        	}
        }
        
        return builder.buildFuture();
    }

    @Override
    public boolean test(ServerCommandSource t) {
        return true;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity plr = context.getSource().getPlayerOrThrow();

        String text = context.getInput();
        String[] args = text.split(" ");

        
        if (args.length < 3) {
        	msg(plr, USAGE_PAY); // /pay <player> <amount>
            return 0;
        }
        
        String money = args[2];
        if (money.startsWith("$")) {
        	money = money.substring(1);
        }

        BigDecimal d = new BigDecimal(money);

        if (d.compareTo( BigDecimal.ZERO ) == -1) {
        	msg(plr, PAY_NEGATIVE);
        	return 1;
        }

        try {
	        EconomyUser user = Economy.getUser(plr.getName().getString());
	        EconomyUser target = Economy.getUser( args[1] );

	        if (user.getMoney().compareTo(d) == -1) {
	        	// No funds
	        	msg(plr, "&4Error: &cYou do not have the required balance of \"" + d + "\" required.");
	        	return 1;
	        }

	        user.setMoney( user.getMoney().subtract(d) );
	        target.setMoney(target.getMoney().add(d));
	        
	        msg(plr, "&a[Economy]: Sent &f\"" + d + "\"&a to " + args[1]);
	        
	        target.economymod$send_message("&a[Economy]: Received &f$" + d + "&a from " + plr.getName().getString());
	        
	        return 1;
        } catch (UserDoesNotExistException e) {
			e.printStackTrace();
			msg(plr, "&4UserDoesNotExistException:&c Player " + args[1] + " does not exist");
		}
        return 0;
    }
    
    public void msg(ServerPlayerEntity cs, String message) {
		try {
			cs.sendMessage(Text.of(translate_alternate_color_codes('&', message)), false);
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