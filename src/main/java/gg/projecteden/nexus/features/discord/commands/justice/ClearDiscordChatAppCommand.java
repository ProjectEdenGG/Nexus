package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import net.dv8tion.jda.api.entities.Message;

@RequiredRole("Staff")
@Command("Clear Discord chat")
public class ClearDiscordChatAppCommand extends NexusAppCommand {

	public ClearDiscordChatAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Clear Discord chat", literals = false)
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
