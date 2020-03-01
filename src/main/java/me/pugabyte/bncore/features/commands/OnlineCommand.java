package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO: Balance hoverable

@Aliases({"list", "ls", "who", "players", "eonline", "elist", "ewho", "eplayers"})
public class OnlineCommand extends CustomCommand {

	public OnlineCommand(CommandEvent event) {
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
		send("&3There are &e" + counts + " &3out of maximum &e" + Bukkit.getMaxPlayers() + " &3players online");

		ranks.forEach(rank -> {
			List<Nerd> nerds = rank.getOnlineNerds();
			if (nerds.size() == 0) return;

			JsonBuilder builder = new JsonBuilder(rank + "s&f: ");

			nerds.stream().filter(this::canSee).forEach(nerd -> getNameWithModifiers(nerd, builder));

			send(builder);
		});

		line();
		send("&e&lClick &3on a player's name to open the &eQuickAction &3menu");
		line();
	}

	private boolean canSee(Nerd nerd) {
		return Utils.canSee(player(), nerd.getPlayer());
	}

	void getNameWithModifiers(Nerd nerd, JsonBuilder builder) {
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

		if (!builder.isInitialized())
			builder.initialize();
		else
			builder.next("&f, ").group();

		builder.next(modifiers + nerd.getRank().getFormat() + nerd.getName())
				.command("/quickaction " + nerd.getName())
				.hover(getInfo(nerd, modifiers))
				.group();
	}

	String getInfo(Nerd nerd, String modifiers) {
		Player player = nerd.getPlayer();
		Hours hours = new HoursService().get(player);

		int ping = Utils.getPing(player);
		String onlineFor = StringUtils.timespanDiff(nerd.getLastJoin());
		WorldGroup world = WorldGroup.get(player.getWorld());
		double balance = 0.0;
		String totalHours = StringUtils.timespanFormat(hours.getTotal());
		String afk = "";

		if (modifiers.contains("AFK")) {
			AFKPlayer afkPlayer = AFK.get(player);
			String timeAFK = StringUtils.timespanDiff(afkPlayer.getTime());
			afk = "&3AFK for: &e" + timeAFK + "\n \n";
		}

		return afk +
				"&3Ping: &e" + ping + "\n" +
				"&3World: &e" + world + "\n" +
//				"&3Balance: &e$" + balance + "\n" +
				"&3Online for: &e" + onlineFor + "\n" +
				"&3Hours: &e" + totalHours;
	}
}
