package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.models.godmode.Godmode;
import me.pugabyte.bncore.models.godmode.GodmodeService;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.litebans.LiteBansService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.getLocationString;
import static me.pugabyte.bncore.utils.StringUtils.shortDateTimeFormat;
import static me.pugabyte.bncore.utils.StringUtils.timespanDiff;
import static me.pugabyte.bncore.utils.Utils.getLocation;

@Aliases("whotf")
@Permission("group.staff")
public class WhoTheFuckCommand extends CustomCommand {

	public WhoTheFuckCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<player>")
	void whotf(Nerd nerd) {
		line();
		line();
		send("&3Who the fuck is &6&l" + nerd.getName() + "&3?");

		HoursService hoursService = new HoursService();
		LiteBansService liteBansService = new LiteBansService();
		GeoIPService geoIpService = new GeoIPService();

		Hours hours = hoursService.get(nerd);
		String rank = nerd.getRank().withFormat();
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

		int history = liteBansService.getHistory(nerd.getUuid());
		List<String> alts = liteBansService.getAlts(nerd.getUuid());
		List<String> pastNames = new NerdService().getPastNames(offlinePlayer.getUniqueId());
		Godmode godmode = new GodmodeService().get(player());

		JsonBuilder json = json()
				.newline().next("&3Rank: &e" + rank)
				.newline().next("&3First Join: &e" + firstJoin);

		if (lastJoinQuitDate != null)
			json.newline().next("&3" + lastJoinQuitLabel + ": &e" + lastJoinQuitDiff + " ago").hover("&e" + lastJoinQuitDate);

		if (hours.getTotal() > 0)
			json.newline().next("&3Hours: &e" + StringUtils.timespanFormat(hours.getTotal(), "None"));

		if (history > 0)
			json.newline().next("&3History: &e" + history).command("/history " + nerd.getName());

		if (alts.size() > 0)
			json.newline().next("&3Alts: &e" + String.join(", ", alts));

		if (pastNames.size() > 1)
			json.newline().next("&3Past Names: &e" + String.join("&3, &e", pastNames));

		try {
			GeoIP geoIp = geoIpService.get(nerd);
			json.newline().next("&3GeoIP: &e" + geoIp.getFriendlyLocationString()).hover("&e" + geoIp.getIp()).suggest(geoIp.getIp());
		} catch (InvalidInputException ex) {
			json.newline().next("&3GeoIP: &c" + ex.getMessage());
		}

		try {
			json.newline().next("&3Location: &e" + getLocationString(getLocation(offlinePlayer)));
		} catch (InvalidInputException ex) {
			json.newline().next("&3Location: &c" + ex.getMessage());
		}

		json.newline().next("&3Balance: &e" + NumberFormat.getCurrencyInstance().format(BNCore.getEcon().getBalance(offlinePlayer)));

		if (offlinePlayer.isOnline() && player != null) {
			json.newline().next("&3Gamemode: &e" + camelCase(player.getGameMode().name()));

			json.newline().next("&3God mode: &e" + godmode.isEnabledRaw());

			json.newline().next("&3Fly mode: &e" + player.getAllowFlight() + " &3(" + (player.isFlying() ? "flying" : "not flying") + ")");
		}

		json.newline().next("&3OP: &e" + offlinePlayer.isOp());

		send(json);
	}

}
