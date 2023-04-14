package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;

import java.util.List;
import java.util.stream.Collectors;

// TODO: Map back to OnlineCommand with filter?
public class OnlineStaffCommand extends CustomCommand {

	public OnlineStaffCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View online staff members")
	void onlineStaff() {
		long vanished = OnlinePlayers.getAll().stream().filter(Vanish::isVanished).count();
		long online = Rank.getOnlineStaff().size() - vanished;
		boolean canSeeVanished = player().hasPermission("pv.see");
		String counts = online + ((canSeeVanished && vanished > 0) ? " &3+ &e" + vanished : "");

		line();
		send("&3There are &e" + counts + " &3staff members online");
		Rank.STAFF_RANKS.forEach(rank -> {
			List<Nerd> nerds = rank.getOnlineNerds();
			if (nerds.size() == 0)
				return;

			send(rank.getColoredName() + "s&f: " + nerds.stream().filter(this::canSee).map(this::getNameWithPresence).collect(Collectors.joining("&f, ")));
		});
		line();
		send("&3View a full list of staff members with &c/staff");
		send("&3If you need to request a staff members &ehelp&3, please use &c/ticket <message>");
		line();
	}

	private boolean canSee(Nerd nerd) {
		return PlayerUtils.canSee(player(), nerd.getOnlinePlayer()) && player().canSee(nerd.getOnlinePlayer());
	}

	private String getNameWithPresence(Nerd nerd) {
		final Presence presence = Presence.of(nerd.getOnlinePlayer());
		final String name = nerd.getColoredName();
		return (presence.isActive() ? "" : presence.ingame() + " ") + name;
	}
}
