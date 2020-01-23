package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// TODO: QuickAction and hover info

//@Aliases({"ls", "who", "online", "players", "eonline", "elist", "ewho", "eplayers"})
public class JListCommand extends CustomCommand {

	public JListCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		List<Rank> ranks = Arrays.asList(Rank.values());
		Collections.reverse(ranks);

		long vanished = Bukkit.getOnlinePlayers().stream().filter(Utils::isVanished).count();
		long online = Bukkit.getOnlinePlayers().size() - vanished;
		boolean canSeeVanished = player().hasPermission("vanish.see");
		String counts = online + ((canSeeVanished && vanished > 0) ? " &3+ &e" + vanished : "");

		line();
		send("&3There are &e" + counts + " &3out of maximum &e " + Bukkit.getMaxPlayers() + " &3players online");

		ranks.forEach(rank -> {
			List<Nerd> nerds = rank.getOnlineNerds();
			if (nerds.size() == 0) return;

			send(rank + "s&f: " + nerds.stream().map(this::getNameWithModifiers).collect(Collectors.joining("&f, ")));
		});
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
		else
			if (afk)
				modifiers = "&7[AFK] ";

		return modifiers + nerd.getRank().getFormat() + nerd.getName();
	}
}
