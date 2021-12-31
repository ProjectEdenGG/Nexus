package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.Nickname.NicknameHistoryEntry;
import gg.projecteden.nexus.models.nickname.NicknameService;

@RequiredRole("Admins")
@Command("Manage nicknames")
public class NicknameAppCommand extends NexusAppCommand {

	public NicknameAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Deny a nickname request")
	void deny(
		@Desc("Player") Nickname player,
		@Desc("Reason") String reason
	) {
		for (NicknameHistoryEntry entry : player.getNicknameHistory()) {
			if (!entry.isPending())
				continue;

			entry.deny(reason);
			new NicknameService().save(player);
			reply("Successfully denied nickname request");
			return;
		}

		replyEphemeral("No pending nickname request found for " + player.getNickname());
	}

}
