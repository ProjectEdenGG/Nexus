package gg.projecteden.nexus.features.commands.staff;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.hooks.Hook;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Aliases({"whotf", "whothefuck"})
@Permission(Group.STAFF)
public class WhoIsCommand extends CustomCommand {

	public WhoIsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<player>")
	@Description("View information about a player such as their rank, history, alts, playtime, and more")
	void run(Nerd nerd) {
		if (isPlayer() && Minigamer.of(player()).isPlaying())
			error("Cannot use in minigames");

		line();
		line();
		send("&3Who the fuck is &6&l" + nerd.getNickname() + "&3?");

		HoursService hoursService = new HoursService();
		GeoIPService geoipService = new GeoIPService();

		Punishments punishments = Punishments.of(nerd);
		boolean history = punishments.hasHistory();
		JsonBuilder alts = punishments.getAltsMessage();

		Hours hours = hoursService.get(nerd);
		String rank = nerd.getRank().getColoredName();
		String firstJoin = TimeUtils.shortDateTimeFormat(nerd.getFirstJoin());
		String lastJoinQuitLabel = null;
		String lastJoinQuitDate = null;
		String lastJoinQuitDiff = null;

		if (nerd.isOnline()) {
			if (nerd.getLastQuit() != null) {
				lastJoinQuitLabel = "Last Quit";
				lastJoinQuitDate = TimeUtils.shortDateTimeFormat(nerd.getLastQuit());
				lastJoinQuitDiff = Timespan.of(nerd.getLastQuit()).format();
			}
		} else {
			lastJoinQuitLabel = "Last Join";
			lastJoinQuitDate = TimeUtils.shortDateTimeFormat(nerd.getLastQuit());
			lastJoinQuitDiff = Timespan.of(nerd.getLastJoin()).format();
		}
		Set<String> pastNames = nerd.getPastNames();
		Godmode godmode = new GodmodeService().get(nerd);

		JsonBuilder json = json();

		if (nerd.hasNickname())
			json.newline().next("&3Real Name: &e" + nerd.getName()).group();

		json.newline().next("&3Rank: &e" + rank).group();
		json.newline().next("&3First Join: &e" + firstJoin).group();

		if (lastJoinQuitDate != null)
			json.newline().next("&3" + lastJoinQuitLabel + ": &e" + lastJoinQuitDiff + " ago").hover("&e" + lastJoinQuitDate).group();

		if (hours.getTotal() > 0)
			json.newline().next("&3Hours: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format()).group();

		if (history)
			json.newline().next("&3History: &e" + punishments.getPunishments().size()).command("/history " + nerd.getName()).hover("&eClick to view history").group();

		if (alts != null)
			json.newline().next("&3Alts: &e").next(alts).group();

		if (!pastNames.isEmpty())
			json.newline().next("&3Past Names: &e" + String.join("&3, &e", pastNames)).group();

		try {
			GeoIP geoip = geoipService.get(nerd);
			if (!Nullables.isNullOrEmpty(geoip.getIp()))
				json.newline().next("&3GeoIP: &e" + geoip.getFriendlyLocationString()).hover("&e" + geoip.getIp()).suggest(geoip.getIp()).group();
		} catch (InvalidInputException ex) {
			json.newline().next("&3GeoIP: &c" + ex.getMessage()).group();
		}

		try {
			json.newline().next("&3Location: &e" + StringUtils.getLocationString(nerd.getLocation())).hover("&eClick to TP").command("/tp " + nerd.getName()).group();
		} catch (InvalidInputException ex) {
			json.newline().next("&3Location: &c" + ex.getMessage()).group();
		}

		List<String> balances = new ArrayList<>();
		for (ShopGroup shopGroup : ShopGroup.values()) {
			if (new BankerService().getBalance(nerd, shopGroup) != 500)
				balances.add("  &3" + camelCase(shopGroup) + ": &e" + new BankerService().getBalanceFormatted(nerd, shopGroup));
		}

		if (!balances.isEmpty()) {
			json.newline().next("&3Balances:");
			for (String balance : balances) {
				json.newline().next(balance).group();
			}
		} else {
			json.newline().next("&3Balances: &cN/A");
		}

		if (nerd.isOnline()) {
			Player player = nerd.getOnlinePlayer();

			json.newline().next("&3Minecraft Version: &e" + Hook.VIAVERSION.getPlayerVersion(player));

			json.newline().next("&3Client Brand Name: &e" + player.getClientBrandName()).group();

			final LocalResourcePackUser packUser = new LocalResourcePackUserService().get(nerd);
			json.newline().next("&3Saturn: &e" + packUser.getSaturnStatus()).group();
			json.newline().next("&3Titan: &e" + packUser.getTitanStatus()).group();

			json.newline().next("&3Gamemode: &e" + camelCase(player.getGameMode())).group();

			json.newline().next("&3God mode: &e" + godmode.isEnabled()).group();

			json.newline().next("&3Fly mode: &e" + player.getAllowFlight() + " &3(" + (player.isFlying() ? "flying" : "not flying") + ")").group();

			final ChatVisibility chatVisibility = player.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (chatVisibility != ChatVisibility.FULL)
				json.newline().next("&3Chat Visibility: &c" + camelCase(chatVisibility));
		}

		json.newline().next("&3OP: &e" + nerd.getOfflinePlayer().isOp()).group();

		send(json);
	}

}
