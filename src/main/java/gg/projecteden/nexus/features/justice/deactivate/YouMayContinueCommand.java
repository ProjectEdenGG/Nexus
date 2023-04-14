package gg.projecteden.nexus.features.justice.deactivate;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.justice.misc._JusticeCommand;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.freeze.Freeze;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@Aliases("ymc")
@Permission(Group.MODERATOR)
public class YouMayContinueCommand extends _JusticeCommand {

	public YouMayContinueCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player> [warn...]")
	@Description("Unfreeze a player, force them into global, vanish, and optionally warn them")
	void player(@ErasureType(Freeze.class) List<Freeze> players, String reason) {
		for (Freeze freeze : players) {
			freeze.deactivate(uuid());

			Chat.setActiveChannel(freeze, StaticChannel.GLOBAL);

			if (!isNullOrEmpty(reason))
				Punishments.of(freeze).add(Punishment.ofType(PunishmentType.WARN).punisher(uuid()).input(reason));
		}

		line(2);
		Vanish.vanish(player());
		Chat.setActiveChannel(player(), StaticChannel.STAFF);
	}

}
