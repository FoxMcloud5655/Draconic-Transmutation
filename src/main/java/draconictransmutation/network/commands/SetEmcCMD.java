package draconictransmutation.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import draconictransmutation.config.CustomEMCParser;
import draconictransmutation.network.commands.argument.NSSItemArgument;
import draconictransmutation.network.commands.parser.NSSItemParser.NSSItemResult;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class SetEmcCMD {

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("setemc")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(Commands.argument("emc", LongArgumentType.longArg(0, Long.MAX_VALUE))
						.then(Commands.argument("item", new NSSItemArgument())
								.executes(ctx -> setEmc(ctx, NSSItemArgument.getNSS(ctx, "item"), LongArgumentType.getLong(ctx, "emc"))))
						.executes(ctx -> setEmc(ctx, RemoveEmcCMD.getHeldStack(ctx), LongArgumentType.getLong(ctx, "emc"))));

	}

	private static int setEmc(CommandContext<CommandSource> ctx, NSSItemResult stack, long emc) {
		String toSet = stack.getStringRepresentation();
		CustomEMCParser.addToFile(toSet, emc);
		ctx.getSource().sendFeedback(DTLang.COMMAND_SET_SUCCESS.translate(toSet, emc), true);
		ctx.getSource().sendFeedback(DTLang.RELOAD_NOTICE.translate(), true);
		return Command.SINGLE_SUCCESS;
	}
}