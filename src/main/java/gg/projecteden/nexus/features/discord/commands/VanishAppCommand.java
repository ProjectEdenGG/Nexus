package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.discord.commands.common.annotations.Verify;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import gg.projecteden.nexus.utils.StringUtils;

@Verify
@RequiredRole("Staff")
@Command("Toggle vanish on next login")
public class VanishAppCommand extends NexusAppCommand {

	public VanishAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Toggle vanish on next login", literals = false)
	void toggle() {
		if (Rank.of(user()).isSeniorStaff()) {
			replyEphemeral("Senior Staff always vanish on login");
			return;
		}

		new VanishUserService().edit(user(), user -> {
			user.setVanishOnLogin(!user.isVanishOnLogin());
			reply("%sVanish on next login %s".formatted(StringUtils.getDiscordPrefix("Vanish"), user.isVanishOnLogin() ? "enabled" : "disabled"));
		});
	}

}
