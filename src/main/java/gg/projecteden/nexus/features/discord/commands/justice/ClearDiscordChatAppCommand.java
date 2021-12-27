package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.Optional;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import net.dv8tion.jda.api.entities.Message;

@RequiredRole("Staff")
@HandledBy(Bot.KODA)
public class ClearDiscordChatAppCommand extends NexusAppCommand {

	public ClearDiscordChatAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Clear chat", literals = false)
	void clear(
		@Desc("Messages to delete") int amount,
		@Desc("Messages to skip") @Optional int skip
	) {
		int skipIndex = 0;
		int deleteIndex = 0;
		for (Message message : channel().getIterableHistory()) {
			if (++skipIndex <= skip)
				continue;

			if (++deleteIndex > amount)
				return;

			message.delete().queue();
		}

		replyEphemeral("Deleted " + amount + " messages");
	}

}
