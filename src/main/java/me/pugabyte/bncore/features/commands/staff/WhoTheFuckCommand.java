package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.models.hours.Hours;
import me.pugabyte.bncore.models.hours.HoursService;
import me.pugabyte.bncore.models.litebans.LiteBansService;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Utils;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Aliases("whotf")
@Permission("group.staff")
public class WhoTheFuckCommand extends CustomCommand {

	public WhoTheFuckCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void whotf(Nerd nerd) {
		line();
		line();
		send("&3Who the fuck is &6&l" + nerd.getName() + "&3?");
		line();

		HoursService hoursService = new HoursService();
		LiteBansService liteBansService = new LiteBansService();
		GeoIPService geoIpService = new GeoIPService();

		Hours hours = hoursService.get(nerd);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm");

		String rank = nerd.getRank().toString();
		String firstJoin = nerd.getFirstJoin().format(dateTimeFormatter);
		String lastJoinQuitLabel = null;
		String lastJoinQuitDate = null;
		String lastJoinQuitDiff = null;
		if (nerd.getOfflinePlayer().isOnline()) {
			if (nerd.getLastQuit() != null) {
				lastJoinQuitLabel = "Last Quit";
				lastJoinQuitDate = nerd.getLastQuit().format(dateTimeFormatter);
				lastJoinQuitDiff = Utils.timespanDiff(nerd.getLastQuit());
			}
		} else {
			lastJoinQuitLabel = "Last Join";
			lastJoinQuitDate = nerd.getLastJoin().format(dateTimeFormatter);
			lastJoinQuitDiff = Utils.timespanDiff(nerd.getLastJoin());
		}

		int history = liteBansService.getHistory(nerd.getUuid());
		List<String> alts = liteBansService.getAlts(nerd.getUuid());

		JsonBuilder json = json()
				.next("&3Rank: &e" + rank).newline()
				.next("&3First Join: &e" + firstJoin).newline();

		if (lastJoinQuitDate != null)
				json.next("&3" + lastJoinQuitLabel + ": &e" + lastJoinQuitDiff + " ago").hover("&e" + lastJoinQuitDate).newline();

		if (hours.getTotal() > 0)
			json.next("&3Hours: &e" + Utils.timespanFormat(hours.getTotal(), "None")).newline();

		if (history > 0)
			json.next("&3History: &e" + history).command("/history " + nerd.getName()).newline();

		if (alts.size() > 0)
			json.next("&3Alts: &e" + String.join(", ", alts)).newline();

		try {
			GeoIP geoIp = geoIpService.get(nerd);
			json.next("&3Location: &e" + geoIp.getFriendlyLocationString()).hover(geoIp.getIp()).suggest(geoIp.getIp());
		} catch (InvalidInputException ex) {
			json.next("&3Location: &c" + ex.getMessage());
		}

		send(json);
	}

}
