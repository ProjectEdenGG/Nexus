package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@RequiredRole("Staff")
public abstract class _PunishmentAppCommand extends NexusAppCommand {

	public _PunishmentAppCommand(AppCommandEvent event) {
		super(event);
	}

	protected PunishmentType getType() {
		return PunishmentType.valueOf(this.getClass().getSimpleName()
				.replaceFirst("AppCommand", "")
				.replaceFirst("Un", "")
				.toUpperCase());
	}

	abstract protected void execute(DiscordUser author, Punishments player, String reason, boolean now);

	@Command(value = "Punish user", literals = false)
	void run(
		@Desc("Player") Punishments player,
		@Desc("Reason/Time") @Optional String reason
	) {
		boolean now = false;
		if (!isNullOrEmpty(reason) && reason.contains(" --now")) {
			now = true;
			reason = reason.replaceFirst(" --now", "");
		}

		execute(user(), player, reason, now);
		thumbsupEphemeral();
	}

}
