package draconictransmutation.network.commands;

import java.util.Collection;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import draconictransmutation.api.ProjectEAPI;
import draconictransmutation.api.capabilities.IKnowledgeProvider;
import draconictransmutation.network.PacketHandler;
import draconictransmutation.network.packets.to_client.knowledge.KnowledgeClearPKT;
import draconictransmutation.utils.text.DTLang;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;

public class ClearKnowledgeCMD {

	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("clearknowledge")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(Commands.argument("targets", EntityArgument.players())
						.executes(cs -> execute(cs, EntityArgument.getPlayers(cs, "targets"))));
	}

	private static int execute(CommandContext<CommandSource> ctx, Collection<ServerPlayerEntity> targets) {
		CommandSource source = ctx.getSource();
		for (ServerPlayerEntity player : targets) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge);
			PacketHandler.sendTo(new KnowledgeClearPKT(), player);
			source.sendFeedback(DTLang.CLEAR_KNOWLEDGE_SUCCESS.translate(player.getDisplayName()), true);
			if (player != source.getEntity()) {
				player.sendMessage(DTLang.CLEAR_KNOWLEDGE_NOTIFY.translateColored(TextFormatting.RED, source.getDisplayName()), Util.DUMMY_UUID);
			}
		}
		return targets.size();
	}
}