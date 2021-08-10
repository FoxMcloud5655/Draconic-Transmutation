package draconictransmutation.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import draconictransmutation.config.CustomEMCParser;
import draconictransmutation.network.commands.argument.NSSItemArgument;
import draconictransmutation.network.commands.parser.NSSItemParser.NSSItemResult;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public class RemoveEmcCMD {

	private static final SimpleCommandExceptionType EMPTY_STACK = new SimpleCommandExceptionType(DTLang.COMMAND_NO_ITEM.translate());

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("removeemc")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(Commands.argument("item", new NSSItemArgument())
						.executes(ctx -> removeEmc(ctx, NSSItemArgument.getNSS(ctx, "item"))))
				.executes(ctx -> removeEmc(ctx, getHeldStack(ctx)));
	}

	private static int removeEmc(CommandContext<CommandSource> ctx, NSSItemResult stack) {
		String toRemove = stack.getStringRepresentation();
		CustomEMCParser.addToFile(toRemove, 0);
		ctx.getSource().sendFeedback(DTLang.COMMAND_REMOVE_SUCCESS.translate(toRemove), true);
		ctx.getSource().sendFeedback(DTLang.RELOAD_NOTICE.translate(), true);
		return Command.SINGLE_SUCCESS;
	}

	public static NSSItemResult getHeldStack(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = ctx.getSource().asPlayer();
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.isEmpty()) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.isEmpty()) {
			throw EMPTY_STACK.create();
		}
		return new NSSItemResult(stack);
	}
}