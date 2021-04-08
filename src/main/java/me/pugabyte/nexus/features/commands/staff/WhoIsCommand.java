package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.geoip.GeoIP;
import me.pugabyte.nexus.models.geoip.GeoIPService;
import me.pugabyte.nexus.models.godmode.Godmode;
import me.pugabyte.nexus.models.godmode.GodmodeService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.litebans.LiteBansService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils.Timespan;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.utils.StringUtils.getLocationString;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;
import static me.pugabyte.nexus.utils.StringUtils.timespanDiff;

@Aliases({"whotf", "whothefuck"})
@Permission("group.staff")
public class WhoIsCommand extends CustomCommand {

	public WhoIsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<player>")
	void run(Nerd nerd) {
		line();
		line();
		send("&3Who the fuck is &6&l" + nerd.getName() + "&3?");

		HoursService hoursService = new HoursService();
		GeoIPService geoIpService = new GeoIPService();

		int history = 0;
		List<String> alts = new ArrayList<>();
		try {
			LiteBansService liteBansService = new LiteBansService();
			history = liteBansService.getHistory(nerd.getUuid().toString());
			alts = liteBansService.getAlts(nerd.getUuid().toString());
		} catch (Exception ignore) {}

		String nickname = Nickname.of(nerd);
		Hours hours = hoursService.get(nerd);
		String rank = nerd.getRank().withColor();
		String firstJoin = shortDateTimeFormat(nerd.getFirstJoin());
		String lastJoinQuitLabel = null;
		String lastJoinQuitDate = null;
		String lastJoinQuitDiff = null;
		OfflinePlayer offlinePlayer = nerd.getOfflinePlayer();
		Player player = offlinePlayer.getPlayer();

		if (offlinePlayer.isOnline()) {
			if (nerd.getLastQuit() != null) {
				lastJoinQuitLabel = "Last Quit";
				lastJoinQuitDate = shortDateTimeFormat(nerd.getLastQuit());
				lastJoinQuitDiff = timespanDiff(nerd.getLastQuit());
			}
		} else {
			lastJoinQuitLabel = "Last Join";
			lastJoinQuitDate = shortDateTimeFormat(nerd.getLastQuit());
			lastJoinQuitDiff = timespanDiff(nerd.getLastJoin());
		}
		Set<String> pastNames = nerd.getPastNames();
		Godmode godmode = new GodmodeService().get(nerd);

		JsonBuilder json = json();

		if (nerd.hasNickname())
			json.newline().next("&3Nickname: &e" + nickname);

		json.newline().next("&3Rank: &e" + rank);
		json.newline().next("&3First Join: &e" + firstJoin);

		if (lastJoinQuitDate != null)
			json.newline().next("&3" + lastJoinQuitLabel + ": &e" + lastJoinQuitDiff + " ago").hover("&e" + lastJoinQuitDate);

		if (hours.getTotal() > 0)
			json.newline().next("&3Hours: &e" + Timespan.of(hours.getTotal()).noneDisplay(true).format());

		if (history > 0)
			json.newline().next("&3History: &e" + history).command("/history " + nerd.getName()).hover("&eClick to view history");

		if (!alts.isEmpty())
			json.newline().next("&3Alts: &e" + String.join(", ", alts));

		if (!pastNames.isEmpty())
			json.newline().next("&3Past Names: &e" + String.join("&3, &e", pastNames));

		try {
			GeoIP geoIp = geoIpService.get(nerd);
			json.newline().next("&3GeoIP: &e" + geoIp.getFriendlyLocationString()).hover("&e" + geoIp.getIp()).suggest(geoIp.getIp());
		} catch (InvalidInputException ex) {
			json.newline().next("&3GeoIP: &c" + ex.getMessage());
		}

		try {
			json.newline().next("&3Location: &e" + getLocationString(nerd.getLocation())).hover("&eClick to TP").command("/tp " + offlinePlayer.getName());
		} catch (InvalidInputException ex) {
			json.newline().next("&3Location: &c" + ex.getMessage());
		}

		json.newline().next("&3Balances:");
		for (ShopGroup shopGroup : ShopGroup.values())
			if (new BankerService().getBalance(offlinePlayer, shopGroup) != 500)
				json.newline().next("  &3" + camelCase(shopGroup) + ": &e" + new BankerService().getBalanceFormatted(offlinePlayer, shopGroup));

		if (offlinePlayer.isOnline() && player != null) {
			json.newline().next("&3Gamemode: &e" + camelCase(player.getGameMode()));

			json.newline().next("&3God mode: &e" + godmode.isEnabledRaw());

			json.newline().next("&3Fly mode: &e" + player.getAllowFlight() + " &3(" + (player.isFlying() ? "flying" : "not flying") + ")");
		}

		json.newline().next("&3OP: &e" + offlinePlayer.isOp());

		send(json);
	}

}
