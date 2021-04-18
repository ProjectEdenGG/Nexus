package me.pugabyte.nexus.features.commands.staff.moderator.justice.freeze;

import de.myzelyam.api.vanish.VanishAPI;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.freeze.Freeze;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;

import java.util.List;

@Aliases("ymc")
@Permission("group.moderator")
public class YouMayContinueCommand extends CustomCommand {

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
		VanishAPI.showPlayer(player());
		Chat.setActiveChannel(player(), StaticChannel.STAFF);
	}

}
