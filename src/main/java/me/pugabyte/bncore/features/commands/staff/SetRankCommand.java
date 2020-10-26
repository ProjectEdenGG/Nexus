package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Rank;
import org.bukkit.OfflinePlayer;

@Permission("group.seniorstaff")
public class SetRankCommand extends CustomCommand {

	public SetRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <rank>")
	void set(OfflinePlayer player, Rank rank) {
		runCommandAsConsole("lp user " + player.getName() + " parent set " + rank.name());
		send(PREFIX + "Set " + player.getName() + "'s rank to " + rank.withFormat());
	}

}
