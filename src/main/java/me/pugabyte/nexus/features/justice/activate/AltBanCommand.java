package me.pugabyte.nexus.features.justice.activate;

import lombok.NonNull;
import me.pugabyte.nexus.features.justice.misc._PunishmentCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Switch;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;

import java.util.List;

@Permission("group.moderator")
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

	@Permission(value = "group.admin", absolute = true)
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
