package gg.projecteden.nexus.features.commands.staff;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Set;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.getLocationString;

@Aliases({"whotf", "whothefuck"})
@Permission(Group.STAFF)
public class WhoIsCommand extends CustomCommand {

	public WhoIsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@NoLiterals
	@Description("View information about a player such as their rank, history, alts, playtime, and more")
	void run(Nerd player) {
		if (Minigamer.of(player()).isPlaying())
			error("Cannot use in minigames");

		line();
		line();
		send("&3Who the fuck is &6&l" + player.getNickname() + "&3?");

		HoursService hoursService = new HoursService();
		GeoIPService geoipService = new GeoIPService();

		Punishments punishments = Punishments.of(player);
		boolean history = punishments.hasHistory();
		JsonBuilder alts = punishments.getAltsMessage();

		Hours hours = hoursService.get(player);
		String rank = player.getRank().getColoredName();
		String firstJoin = shortDateTimeFormat(player.getFirstJoin());
		String lastJoinQuitLabel = null;
		String lastJoinQuitDate = null;
		String lastJoinQuitDiff = null;

		if (player.isOnline()) {
			if (player.getLastQuit() != null) {
				lastJoinQuitLabel = "Last Quit";
				lastJoinQuitDate = shortDateTimeFormat(player.getLastQuit());
				lastJoinQuitDiff = Timespan.of(player.getLastQuit()).format();
			}
		} else {
			lastJoinQuitLabel = "Last Join";
			lastJoinQuitDate = shortDateTimeFormat(player.getLastQuit());
			lastJoinQuitDiff = Timespan.of(player.getLastJoin()).format();
		}
		Set<String> pastNames = player.getPastNames();
		Godmode godmode = new GodmodeService().get(player);

		JsonBuilder json = json();

		if (player.hasNickname())
			json.newline().next("&3Real Name: &e" + player.getName()).group();

		json.newline().next("&3Rank: &e" + rank).group();
		json.newline().next("&3First Join: &e" + firstJoin).group();

		if (lastJoinQuitDate != null)
			json.newline().next("&3" + lastJoinQuitLabel + ": &e" + lastJoinQuitDiff + " ago").hover("&e" + lastJoinQuitDate).group();

		if (hours.getTotal() > 0)
			json.newline().next("&3Hours: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format()).group();

		if (history)
			json.newline().next("&3History: &e" + punishments.getPunishments().size()).command("/history " + player.getName()).hover("&eClick to view history").group();

		if (alts != null)
			json.newline().next("&3Alts: &e").next(alts).group();

		if (!pastNames.isEmpty())
			json.newline().next("&3Past Names: &e" + String.join("&3, &e", pastNames)).group();

		try {
			GeoIP geoip = geoipService.get(player);
			if (!isNullOrEmpty(geoip.getIp()))
				json.newline().next("&3GeoIP: &e" + geoip.getFriendlyLocationString()).hover("&e" + geoip.getIp()).suggest(geoip.getIp()).group();
		} catch (InvalidInputException ex) {
			json.newline().next("&3GeoIP: &c" + ex.getMessage()).group();
		}

		try {
			json.newline().next("&3Location: &e" + getLocationString(player.getLocation())).hover("&eClick to TP").command("/tp " + player.getName()).group();
		} catch (InvalidInputException ex) {
			json.newline().next("&3Location: &c" + ex.getMessage()).group();
		}

		json.newline().next("&3Balances:");
		for (ShopGroup shopGroup : ShopGroup.values())
			if (new BankerService().getBalance(player, shopGroup) != 500)
				json.newline().next("  &3" + camelCase(shopGroup) + ": &e" + new BankerService().getBalanceFormatted(player, shopGroup)).group();

		if (player.isOnline()) {
			Player onlinePlayer = player.getOnlinePlayer();

			json.newline().next("&3Minecraft Version: &e" + Hook.VIAVERSION.getPlayerVersion(onlinePlayer));

			json.newline().next("&3Client Brand Name: &e" + onlinePlayer.getClientBrandName()).group();

			final LocalResourcePackUser packUser = new LocalResourcePackUserService().get(player);
			json.newline().next("&3Saturn: &e" + packUser.getSaturnStatus()).group();
			json.newline().next("&3Titan: &e" + packUser.getTitanStatus()).group();

			json.newline().next("&3Gamemode: &e" + camelCase(onlinePlayer.getGameMode())).group();

			json.newline().next("&3God mode: &e" + godmode.isEnabled()).group();

			json.newline().next("&3Fly mode: &e" + onlinePlayer.getAllowFlight() + " &3(" + (onlinePlayer.isFlying() ? "flying" : "not flying") + ")").group();

			final ChatVisibility chatVisibility = onlinePlayer.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (chatVisibility != ChatVisibility.FULL)
				json.newline().next("&3Chat Visibility: &e" + camelCase(chatVisibility));
		}

		json.newline().next("&3OP: &e" + player.getOfflinePlayer().isOp()).group();

		send(json);
	}

}
