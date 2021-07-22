package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;

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

		long vanished = PlayerUtils.getOnlinePlayers().stream().filter(PlayerUtils::isVanished).count();
		long online = Rank.getOnlineStaff().size() - vanished;
		boolean canSeeVanished = player().hasPermission("pv.see");
		String counts = online + ((canSeeVanished && vanished > 0) ? " &3+ &e" + vanished : "");

		line();
		send("&3There are &e" + counts + " &3staff members online");
		ranks.forEach(rank -> {
			List<Nerd> nerds = rank.getOnlineNerds();
			if (nerds.size() == 0) return;

			send(rank.getColoredName() + "s&f: " + nerds.stream().filter(this::canSee).map(this::getNameWithModifiers).collect(Collectors.joining("&f, ")));
		});
		line();
		send("&3View a full list of staff members with &c/staff");
		send("&3If you need to request a staff members &ehelp&3, please use &c/ticket <message>");
		line();
	}

	private boolean canSee(Nerd nerd) {
		return PlayerUtils.canSee(player(), nerd.getOnlinePlayer()) && player().canSee(nerd.getOnlinePlayer());
	}

	String getNameWithModifiers(Nerd nerd) {
		boolean vanished = PlayerUtils.isVanished(nerd.getOnlinePlayer());
		boolean afk = AFK.get(nerd.getOnlinePlayer()).isAfk();

		String modifiers = "";
		if (vanished)
			if (afk)
				modifiers = "&7[AFK] [V] ";
			else
				modifiers = "&7[V] ";
		else if (afk)
			modifiers = "&7[AFK] ";

		return modifiers + nerd.getRank().getChatColor() + nerd.getName();
	}
}
