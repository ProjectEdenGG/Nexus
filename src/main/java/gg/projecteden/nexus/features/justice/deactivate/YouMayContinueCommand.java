package gg.projecteden.nexus.features.justice.deactivate;

import de.myzelyam.api.vanish.VanishAPI;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.justice.misc._JusticeCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.freeze.Freeze;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;

import java.util.List;

@Aliases("ymc")
@Permission("group.moderator")
public class YouMayContinueCommand extends _JusticeCommand {

	public YouMayContinueCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player> [warn...]")
	void player(@Arg(type = Freeze.class) List<Freeze> players, String reason) {
		for (Freeze freeze : players) {
			freeze.deactivate(uuid());

			Chat.setActiveChannel(freeze, StaticChannel.GLOBAL);

			if (!isNullOrEmpty(reason))
				Punishments.of(freeze).add(Punishment.ofType(PunishmentType.WARN).punisher(uuid()).input(reason));
		}

		line(2);
		VanishAPI.hidePlayer(player());
		Chat.setActiveChannel(player(), StaticChannel.STAFF);
	}

}
