package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.listeners.Tab.Presence;
import gg.projecteden.nexus.features.listeners.Tab.Presence.Modifier;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.afk.AFKUser;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Aliases({"list", "ls", "who", "players", "eonline", "elist", "ewho", "eplayers"})
@Description("List online players and view basic information about them")
public class OnlineCommand extends CustomCommand {

	public OnlineCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	public void help() {
		long vanished = OnlinePlayers.getAll().stream().filter(PlayerUtils::isVanished).count();
		long online = OnlinePlayers.getAll().size() - vanished;
		boolean canSeeVanished = !isPlayer() || player().hasPermission("pv.see");
		String counts = online + ((canSeeVanished && vanished > 0) ? " &3+ &e" + vanished : "");

		line();
		send("&3There are &e" + counts + " &3out of maximum &e" + Bukkit.getMaxPlayers() + " &3players online");

		Rank.REVERSED.forEach(rank -> {
			List<Nerd> nerds = rank.getOnlineNerds().stream().filter(this::canSee).collect(Collectors.toList());
			if (nerds.size() == 0) return;

			JsonBuilder builder = new JsonBuilder(rank.getColoredName() + "s&f: ");

			nerds.forEach(nerd -> getNameWithPresence(nerd, builder));

			send(builder);
		});

		line();
		send("&e&lClick &3on a player's name to open the &eQuickAction &3menu");
		line();
	}

	private boolean canSee(Nerd nerd) {
		return PlayerUtils.canSee(player(), nerd.getOnlinePlayer()) && player().canSee(nerd.getOnlinePlayer());
	}

	void getNameWithPresence(Nerd nerd, JsonBuilder builder) {
		final Presence presence = Presence.of(nerd.getOnlinePlayer());
		final String name = nerd.getColoredName();

		if (!builder.isInitialized())
			builder.initialize();
		else
			builder.next("&f, ").group();

		builder.next((presence.isActive() ? "" : presence.ingame() + " ") + name)
				.command("/quickaction " + nerd.getName())
				.hover(getInfo(nerd, presence))
				.group();
	}

	String getInfo(Nerd nerd, Presence presence) {
		Player player = nerd.getOnlinePlayer();
		Hours hours = new HoursService().get(player.getUniqueId());

		int ping = player.getPing();
		String onlineFor = Timespan.of(nerd.getLastJoin()).format();
		String world = StringUtils.getWorldDisplayName(nerd.getLocation(), nerd.getWorld());
		ShopGroup shopGroup = ShopGroup.of(player);
		if (shopGroup == null)
			shopGroup = ShopGroup.SURVIVAL;
		String balance = new BankerService().getBalanceFormatted(player, shopGroup);
		String totalHours = Timespan.ofSeconds(hours.getTotal()).format();
		String afk = "";

		if (presence.applies(Modifier.AFK)) {
			AFKUser afkUser = AFK.get(player);
			String timeAFK = Timespan.of(afkUser.getTime()).format();
			afk = "&3AFK for: &e" + timeAFK + "\n \n";
		}

		return afk +
				"&3Ping: &e" + ping + "\n" +
				"&3World: &e" + world + "\n" +
				"&3" + camelCase(shopGroup) + " balance: &e" + balance + "\n" +
				"&3Online for: &e" + onlineFor + "\n" +
				"&3Hours: &e" + totalHours;
	}
}
