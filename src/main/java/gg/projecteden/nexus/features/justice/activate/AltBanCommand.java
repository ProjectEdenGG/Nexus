package gg.projecteden.nexus.features.justice.activate;

import gg.projecteden.nexus.features.justice.misc._PunishmentCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import lombok.NonNull;

import java.util.List;

@Permission(Group.MODERATOR)
public class AltBanCommand extends _PunishmentCommand {

	public AltBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [time/reason...] [--now]")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input, @Switch boolean now) {
		punish(players, input, now);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.ALT_BAN;
	}

	@Permission(Group.ADMIN)
	@Path("bots <names> [--dryrun]")
	void bots(@Arg(type = String.class) List<String> names, @Switch boolean dryrun) {
		int banned = 0;
		int ignored = 0;
		for (String name : names) {
			try {
				Nerd nerd = Nerd.of(name);
				if (!nerd.getPastNames().contains(name))
					throw new PlayerNotFoundException(name);

				++banned;
				if (dryrun)
					send("Banning " + name);
				else
					Punishments.of(nerd).add(Punishment.ofType(PunishmentType.ALT_BAN).punisher(uuid()).input("Spam bot").now(true));

			} catch (PlayerNotFoundException ex) {
				send("Ignoring " + name);
				++ignored;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		send("Banned " + banned + ", ignored " + ignored);
	}

}
