package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Aliases({"list", "ls", "who", "players", "eonline", "elist", "ewho", "eplayers"})
public class OnlineCommand extends CustomCommand {

	public OnlineCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	@Description("List online players and view basic information about them")
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

	List<String> getInfo(Nerd nerd, Presence presence) {
		Player player = nerd.getOnlinePlayer();
		Hours hours = new HoursService().get(player.getUniqueId());

		int ping = player.getPing();
		final String onlineFor = Timespan.of(nerd.getLastJoin()).format();
		final String world = StringUtils.getWorldDisplayName(nerd.getLocation(), nerd.getWorld());
		final ShopGroup shopGroup = ShopGroup.of(player, ShopGroup.SURVIVAL);
		final String balance = new BankerService().getBalanceFormatted(player, shopGroup);
		final String totalHours = Timespan.ofSeconds(hours.getTotal()).format();

		final String afk;

		if (presence.applies(Modifier.AFK)) {
			AFKUser afkUser = AFK.get(player);
			String timeAFK = Timespan.of(afkUser.getTime()).format();
			afk = "&3AFK for: &e" + timeAFK;
		} else
			afk = "";

		return new ArrayList<>() {{
			if (afk.length() > 0) {
				add(afk);
				add("");
			}

			add("&3Ping: &e" + ping);
			add("&3World: &e" + world);
			add("&3" + camelCase(shopGroup) + " balance: &e" + balance);
			add("&3Online for: &e" + onlineFor);
			add("&3Hours: &e" + totalHours);
		}};
	}
}
