package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Map back to OnlineCommand with filter?
public class OnlineStaffCommand extends CustomCommand {

	public OnlineStaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void onlineStaff() {
		List<Rank> ranks = Rank.getStaff();
		Collections.reverse(ranks);

		long vanished = Bukkit.getOnlinePlayers().stream().filter(Utils::isVanished).count();
		long online = Rank.getOnlineStaff().size() - vanished;
		boolean canSeeVanished = player().hasPermission("pv.see");
		String counts = online + ((canSeeVanished && vanished > 0) ? " &3+ &e" + vanished : "");

		line();
		send("&3There are &e" + counts + " &3staff members online");
		ranks.forEach(rank -> {
			List<Nerd> nerds = rank.getOnlineNerds();
			if (nerds.size() == 0) return;

			send(rank.withFormat() + "s&f: " + nerds.stream().filter(this::canSee).map(this::getNameWithModifiers).collect(Collectors.joining("&f, ")));
		});
		line();
		send("&3View a full list of staff members with &c/staff");
		send("&3If you need to request a staff members &ehelp&3, please use &c/ticket <message>");
		line();
	}

	private boolean canSee(Nerd nerd) {
		return Utils.canSee(player(), nerd.getPlayer()) && player().canSee(nerd.getPlayer());
	}

	String getNameWithModifiers(Nerd nerd) {
		boolean vanished = Utils.isVanished(nerd.getPlayer());
		boolean afk = AFK.get(nerd.getPlayer()).isAfk();

		String modifiers = "";
		if (vanished)
			if (afk)
				modifiers = "&7[AFK] [V] ";
			else
				modifiers = "&7[V] ";
		else if (afk)
			modifiers = "&7[AFK] ";

		return modifiers + nerd.getRank().getFormat() + nerd.getName();
	}
}
